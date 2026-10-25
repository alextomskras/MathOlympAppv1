"""
FastAPI-прокси картинок MinIO (пример, который принимает подпись Android-клиента).

Android (MinioProxyImageLoader) слал POST:
  тело:   {"global": N, "kind": "answer" | "solution"}
  заголовок X-Auth: "<unix-секунд>:<hex HMAC-SHA256(secret, ts + '\n' + body)>"

Скрипт проверяет подпись и окно времени, берёт объект
  tasks/{N}/{kind}.jpg
из MinIO и возвращает presigned GET-ссылку. Неподписанный POST не принимается.

Запуск на Render:
  pip install fastapi uvicorn minio
  uvicorn fastapi_minio_proxy:app --host 0.0.0.0 --port 8088
ENV:
  MINIO_PROXY_SECRET   — общий секрет (совпадает с секретом из настроек приложения)
  MINIO_ENDPOINT       — host:port MinIO (напримерplay.min.io)
  MINIO_ACCESS_KEY / MINIO_SECRET_KEY
  MINIO_BUCKET         — бакет с картинками
  TS_WINDOW            — сек., допустимое расхождение часового метки (по умолчанию 300)

Cron с Render/внешнего сервера раз в минуту стучится POST /ping (подписанным),
чтобы контейнер не засыпал.
"""
import hashlib
import hmac
import json
import os
import time

from fastapi import FastAPI, Header, HTTPException
from minio import Minio

app = FastAPI()

SECRET = os.environ.get("MINIO_PROXY_SECRET", "").encode()
TS_WINDOW = int(os.environ.get("TS_WINDOW", "300"))
MINIO_ENDPOINT = os.environ["MINIO_ENDPOINT"]
MINIO_BUCKET = os.environ["MINIO_BUCKET"]

minio_client = Minio(
    MINIO_ENDPOINT,
    access_key=os.environ["MINIO_ACCESS_KEY"],
    secret_key=os.environ["MINIO_SECRET_KEY"],
    secure=os.environ.get("MINIO_SECURE", "true").lower() == "true",
)


def sign(secret: bytes, ts: str, body: bytes) -> str:
    return hmac.new(secret, ts.encode() + b"\n" + body, hashlib.sha256).hexdigest()


def check_auth(x_auth: str, body: bytes) -> None:
    """Разбирает 'ts:hex_sig', сверяет подпись и окно времени."""
    if not SECRET:
        raise HTTPException(500, "секрет не задан (MINIO_PROXY_SECRET)")
    if not x_auth or ":" not in x_auth:
        raise HTTPException(401, "X-Auth заголовок отсутствует")
    ts, sig = x_auth.split(":", 1)
    try:
        ts_val = int(ts)
    except ValueError:
        raise HTTPException(401, "X-Auth: метка времени не числовая")
    if abs(time.time() - ts_val) > TS_WINDOW:
        raise HTTPException(401, "X-Auth: метка времени вне окна")
    expected = sign(SECRET, ts, body)
    if not hmac.compare_digest(expected, sig):
        raise HTTPException(401, "X-Auth: подпись не совпадает")


@app.post("/ping")
async def ping(x_auth: str = Header(None), request_body: bytes = b""):
    # Cron keep-alive: достаточно подписанного POST, содержимое тела любое.
    check_auth(x_auth, request_body)
    return {"ok": True}


from fastapi import Request


@app.post("/")
async def post_image(request: Request, x_auth: str = Header(None)):
    body = await request.body()
    check_auth(x_auth, body)
    try:
        data = json.loads(body)
        global_num = int(data["global"])
        kind = data["kind"]
    except Exception:
        raise HTTPException(400, "тело: {\"global\": N, \"kind\": \"answer|solution\"}")
    if kind not in ("answer", "solution"):
        raise HTTPException(400, "kind: answer или solution")
    object_name = f"tasks/{global_num}/{kind}.jpg"
    if not minio_client.stat_object(MINIO_BUCKET, object_name):
        raise HTTPException(404, f"нет объекта {object_name}")
    url = minio_client.get_presigned_url("GET", MINIO_BUCKET, object_name, expires=3600)
    return {"url": url, "global": global_num, "kind": kind}
