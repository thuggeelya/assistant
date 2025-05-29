import os
from dotenv import load_dotenv

load_dotenv()

OLLAMA_URL = os.getenv("OLLAMA_URL")
OLLAMA_MODEL = os.getenv("OLLAMA_MODEL")
EMBEDDING_MODEL = os.getenv("EMBEDDING_MODEL")
RERANKER_MODEL = os.getenv("RERANKER_MODEL")
VECTOR_DB = os.getenv("VECTOR_DB")
QDRANT_HOST = os.getenv("QDRANT_HOST")
DOCS_PATH = os.getenv("DOCS_PATH", "./documents")
