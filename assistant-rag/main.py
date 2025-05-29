import os
import zipfile

from flask import Flask, request, jsonify
from flask_cors import CORS
from werkzeug.utils import secure_filename

from config import DOCS_PATH
from rag_engine import answer_question, index_documents

app = Flask(__name__)
CORS(app)

os.makedirs(DOCS_PATH, exist_ok=True)


@app.route("/ask", methods=["POST"])  # Эндпоинт для приёма вопросов от Spring
def ask():
    data = request.get_json()  # Получаем JSON-тело запроса
    question = data.get("question")  # Извлекаем поле 'question'
    if not question:
        return jsonify({"error": "Missing 'question' field"}), 400  # Возвращаем ошибку, если нет вопроса
    response = answer_question(question)  # Отправляем вопрос в движок RAG и получаем ответ
    return jsonify({"response": response})  # Возвращаем результат в виде JSON


@app.route("/upload", methods=["POST"])  # Эндпоинт для загрузки zip-архива с документами
def upload():
    if 'file' not in request.files:
        return jsonify({"error": "No file part"}), 400  # Если в запросе нет файла — ошибка
    file = request.files['file']  # Извлекаем файл
    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400  # Если имя файла пустое — ошибка

    filename = secure_filename(file.filename)  # Защищаем имя файла от уязвимостей
    filepath = os.path.join(DOCS_PATH, filename)  # Формируем путь для сохранения
    file.save(filepath)  # Сохраняем файл во временное хранилище

    # Распаковываем zip-файл
    with zipfile.ZipFile(filepath, 'r') as zip_ref:
        zip_ref.extractall(DOCS_PATH)  # Извлекаем содержимое в папку документов

    os.remove(filepath)  # Удаляем оригинальный zip-файл после распаковки
    index_documents(DOCS_PATH)  # Запускаем индексирование всех документов в папке
    return jsonify({"message": "Documents indexed successfully"})  # Возвращаем сообщение об успехе


@app.route("/health", methods=["GET"])  # Простой эндпоинт для проверки состояния сервиса
def health():
    return jsonify({"status": "ok"})  # Возвращаем статус 'ok'


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)  # Запускаем Flask на 0.0.0.0:5000 с включённым debug
