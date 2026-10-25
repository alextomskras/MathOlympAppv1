"""
Назначение стабильных сквозных номеров (global) задачам в Firebase RTDB.

Поле global записывается ОДИН РАЗ и никогда не меняется: добавление/удаление
задач ничего не сдвигает, картинки переливать не нужно. Приложение читает поле;
если поля нет, считает старую формулу offset (обратная совместимость).

Кто проставляет: этот скрипт (администратор), через Admin SDK. Приложение номера
не генерирует.

Схема:
  Zadachi/.meta/global_counter        — последний выданный номер
  Zadachi/<раздел>/zadacha_N/global   — стабильный номер задачи

Правила скрипта (идемпотентен, повторный запуск безопасен):
  - поле global уже есть: сверяем, что оно <= counter; дубликаты глобалов ->
    падение (ничего не пишем).
  - поля нет: назначаем counter+1, обновляем counter.
  - порядок назначения: по номеру раздела, затем по номеру задачи — при первой
    миграции номера совпадают со старой формулой offset+task_num, поэтому
    объекты tasks/<global>/answer.jpg в MinIO переливать НЕ нужно.

Запуск (нужен serviceAccount.json — аккаунт с правами на запись):
  python3 assign_global.py --service-account serviceAccount.json [--dry-run]
"""
import argparse
import json
import re

from firebase_admin import credentials, db, initialize_app

TRAILING = re.compile(r"(\d+)$")
META_PATH = "Zadachi/.meta/global_counter"


def trailing_number(key: str) -> int:
    m = TRAILING.search(key)
    return int(m.group(1)) if m else -1


def main() -> None:
    ap = argparse.ArgumentParser()
    ap.add_argument("--service-account", required=True)
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()

    initialize_app(credentials.ServiceAccount(args.service_account))
    ref = db.reference("Zadachi")
    export = ref.get()
    if not isinstance(export, dict):
        raise SystemExit("Zadachi пуст или недоступен")

    # .meta — служебный узел, не раздел задач
    sections = {k: v for k, v in export.items() if not k.startswith(".")}
    counter = 0
    meta = ref.child(".meta").get()
    if isinstance(meta, dict) and "global_counter" in meta:
        counter = int(meta["global_counter"])

    seen = {}
    assignments = []
    for section in sorted(sections, key=trailing_number):
        tasks = sections[section]
        if not isinstance(tasks, dict):
            continue
        for task_key in sorted(tasks, key=trailing_number):
            g = tasks[task_key].get("global") if isinstance(tasks[task_key], dict) else None
            if g is not None:
                g = int(g)
                if g > counter:
                    raise SystemExit(f"сверка: global={g} в {section}/{task_key} > counter={counter}")
                if g in seen:
                    raise SystemExit(f"коллизия: global={g} в {section}/{task_key} и {seen[g]}")
                seen[g] = f"{section}/{task_key}"
            else:
                counter += 1
                seen[counter] = f"{section}/{task_key}"
                assignments.append((section, task_key, counter))

    print(f"задач: {len(seen)}, полей global уже есть: {len(seen) - len(assignments)}, назначаем: {len(assignments)}")
    for section, task_key, g in assignments:
        print(f"[{'dry' if args.dry_run else 'write'}] {section}/{task_key} -> global={g}")

    if args.dry_run or not assignments:
        return
    # Одна многопутевая запись: поля задач + счётчик атомарно одним запросом.
    updates = {".meta/global_counter": counter}
    for section, task_key, g in assignments:
        updates[f"{section}/{task_key}/global"] = g
    ref.update(updates)
    print(f"записано {len(assignments)} полей, counter={counter}")


if __name__ == "__main__":
    main()
