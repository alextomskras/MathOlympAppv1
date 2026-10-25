import re

TRAILING = re.compile(r"(\d+)$")


def trailing_number(key: str) -> int:
    m = TRAILING.search(key)
    return int(m.group(1)) if m else -1


def compute_global(export: dict, section: str, task_key: str) -> int:
    """Стабильное поле global задачи; фолбэк — старая формула offset+task_num."""
    task = export[section][task_key]
    if isinstance(task, dict) and task.get("global") is not None:
        return int(task["global"])
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


exp = {"razdel_2": {"zadacha_1": {}, "zadacha_2": {}},
       "razdel_3": {"zadacha_1": {"global": 3}, "zadacha_2": {}}}
# field priority: global=3 записан -> возвращаем 3
assert compute_global(exp, "razdel_3", "zadacha_1") == 3
# fallback: offset(2 задачи в razdel_2) + 2 = 4
assert compute_global(exp, "razdel_3", "zadacha_2") == 4
# .meta-фильтрация (как в main): служебный узел не раздел
filtered = {k: v for k, v in exp.items() if not k.startswith(".")}
assert list(filtered) == ["razdel_2", "razdel_3"]
print("compute_global: field priority + fallback OK")
