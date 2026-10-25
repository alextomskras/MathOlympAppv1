"""
Загрузка картинок задач в MinIO со сквозной нумерацией (global).

Сквозной номер = offset(все разделы с номером меньше текущего, по числу задач)
+ номер задачи внутри раздела. Тот же расчёт, что в приложении
(ScrollingFragmentViewModel.setRemoteImage), поэтому номера совпадут.

Формат входа:
  --export export.json  — выгрузка RTDB: каталог "Zadachi" из Firebase Console
                          (JSON), вид: {"razdel_3": {"zadacha_1": {...}, ...}, ...}
  --dir  ./images       — локальные картинки по схеме
                          <dir>/<section_key>/answer_<task_num>.jpg
                          <dir>/<section_key>/solution_<task_num>.jpg
  объектами кладём плоско: tasks/<global>/answer.jpg, tasks/<global>/solution.jpg

ENV:
  MINIO_ENDPOINT, MINIO_ACCESS_KEY, MINIO_SECRET_KEY, MINIO_BUCKET, MINIO_SECURE

Запуск:
  python3 upload_images.py --export export.json --dir ./images [--dry-run]
"""
import argparse
import json
import os
import re

from minio import Minio

TRAILING = re.compile(r"(\d+)$")


def trailing_number(key: str) -> int:
    m = TRAILING.search(key)
    return int(m.group(1)) if m else -1


def compute_global(export: dict, section: str, task_key: str) -> int:
    section_num = trailing_number(section)
    task_num = trailing_number(task_key)
    if section_num < 0 or task_num < 0:
        raise ValueError(f"нет цифры в ключе {section}/{task_key}")
    offset = 0
    for sec, tasks in export.items():
        n = trailing_number(sec)
        if n >= 0 and n < section_num:
            offset += len(tasks)
    return offset + task_num


def main() -> None:
    ap = argparse.ArgumentParser()
    ap.add_argument("--export", required=True)
    ap.add_argument("--dir", required=True)
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()

    with open(args.export, encoding="utf-8") as fh:
        export = json.load(fh)
    if "Zadachi" in export:
        export = export["Zadachi"]

    bucket = os.environ.get("MINIO_BUCKET", "")
    client = None
    if not args.dry_run:
        client = Minio(
            os.environ["MINIO_ENDPOINT"],
            access_key=os.environ["MINIO_ACCESS_KEY"],
            secret_key=os.environ["MINIO_SECRET_KEY"],
            secure=os.environ.get("MINIO_SECURE", "true").lower() == "true",
        )

    uploaded, missing = 0, 0
    for section, tasks in sorted(export.items()):
        for task_key in sorted(tasks):
            global_num = compute_global(export, section, task_key)
            for kind in ("answer", "solution"):
                src = os.path.join(args.dir, section, f"{kind}_{trailing_number(task_key)}.jpg")
                dest = f"tasks/{global_num}/{kind}.jpg"
                if not os.path.exists(src):
                    missing += 1
                    continue
                if args.dry_run:
                    print(f"[dry] {src} -> {dest}")
                    uploaded += 1
                else:
                    client.put_object(bucket, dest, src, length=os.path.getsize(src))
                    uploaded += 1
    print(f"загружено: {uploaded}, пропущено (нет файлов): {missing}")


if __name__ == "__main__":
    main()
