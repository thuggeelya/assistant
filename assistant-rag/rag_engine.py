import hashlib
import os

import fitz  # PyMuPDF
import requests
import torch
from docx import Document
from qdrant_client import QdrantClient
from qdrant_client.http.models import Distance, VectorParams, PointStruct
from sentence_transformers import SentenceTransformer
from transformers import AutoModelForSequenceClassification, AutoTokenizer

from config import OLLAMA_URL, OLLAMA_MODEL, EMBEDDING_MODEL, RERANKER_MODEL, QDRANT_HOST

# Загружаем модель эмбеддингов
embedding_model = SentenceTransformer(EMBEDDING_MODEL)

# Загружаем модель ранжировщика (cross-encoder)
rerank_tokenizer = AutoTokenizer.from_pretrained(RERANKER_MODEL)
rerank_model = AutoModelForSequenceClassification.from_pretrained(RERANKER_MODEL)
rerank_model.eval()

# Подключаемся к Qdrant
qdrant = QdrantClient(QDRANT_HOST)
COLLECTION_NAME = "rag-docs"

# Создаём коллекцию, если не существует
if not qdrant.collection_exists(COLLECTION_NAME):
    qdrant.create_collection(
        collection_name=COLLECTION_NAME,
        vectors_config=VectorParams(size=embedding_model.get_sentence_embedding_dimension(), distance=Distance.COSINE)
    )


def get_text_hash(text):
    # Вычисляем уникальный хеш ID текста
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def extract_text_from_pdf(filepath):
    doc = fitz.open(filepath)
    return "\n".join(page.get_text() for page in doc)


def extract_text_from_docx(filepath):
    doc = Document(filepath)
    return "\n".join(p.text for p in doc.paragraphs)


def index_documents(folder_path):
    # Индексируем документы (txt, pdf, docx) из папки
    docs = []

    for filename in os.listdir(folder_path):
        filepath = os.path.join(folder_path, filename)
        if filename.endswith(".txt"):
            with open(filepath, "r", encoding="utf-8") as f:
                text = f.read()
        elif filename.endswith(".pdf"):
            text = extract_text_from_pdf(filepath)
        elif filename.endswith(".docx"):
            text = extract_text_from_docx(filepath)
        else:
            continue
        docs.append((filename, text))

    points = []

    for fname, text in docs:
        text_id = get_text_hash(text)
        existing_id_set = set(p.id for p in qdrant.scroll(collection_name=COLLECTION_NAME, limit=10_000)[0])

        if text_id not in existing_id_set:
            vector = embedding_model.encode(text)
            point = PointStruct(
                id=text_id,
                vector=vector,
                payload={"filename": fname, "text": text}
            )
            points.append(point)

    qdrant.upsert(collection_name=COLLECTION_NAME, points=points)


def retrieve_documents(query, top_k=5):
    # Выполняем поиск по вектору запроса
    query_vector = embedding_model.encode(query)
    hits = qdrant.search(collection_name=COLLECTION_NAME, query_vector=query_vector, limit=top_k)
    return [hit.payload["text"] for hit in hits]


def rerank_documents(query, docs):
    # Ранжируем документы по степени релевантности к запросу
    scored = []
    for doc in docs:
        inputs = rerank_tokenizer(query, doc, return_tensors="pt", truncation=True)
        with torch.no_grad():
            score = rerank_model(**inputs).logits[0].item()
        scored.append((doc, score))
    return [doc for doc, _ in sorted(scored, key=lambda x: x[1], reverse=True)]


def build_prompt(query, docs):
    # Формируем промпт с контекстом
    context = "\n".join(docs)
    return f"Контекст:\n{context}\n\nВопрос: {query}\nОтвет:"


def call_ollama(prompt):
    # Отправляем промпт в Ollama и возвращаем результат
    response = requests.post(
        f"{OLLAMA_URL}/api/generate",
        json={"model": OLLAMA_MODEL, "prompt": prompt, "stream": False}
    )
    return response.json()["response"]


def answer_question(query):
    docs = retrieve_documents(query)
    ranked_docs = rerank_documents(query, docs)
    prompt = build_prompt(query, ranked_docs)
    answer = call_ollama(prompt)
    return {"response": answer, "prompt": prompt}
