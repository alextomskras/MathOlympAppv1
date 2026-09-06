            ---
            name: permissions-runtime
            title: permissions-runtime
            description: permissions-runtime
            ---

            ---

name: permissions-runtime
description: Runtime permissions handling for old UI
metadata:
author: android-team
version: "1.0"
---

# Permissions & Runtime Requests

## Requesting Permissions:

- ActivityCompat.checkSelfPermission
- requestPermissions(arrayOf(...), REQUEST_CODE)
- onRequestPermissionsResult callback

## Permission Logic:

- shouldShowRequestPermissionRationale
- Объяснение пользователю зачем нужно разрешение
- Обработка "Never ask again"
- Redirect to app settings

## Common Permissions:

- READ/WRITE_EXTERNAL_STORAGE
- CAMERA
- ACCESS_FINE_LOCATION
- RECORD_AUDIO
- READ_CONTACTS

## Best Practices:

- Запрашивай разрешения по мере необходимости (не все сразу)
- Обрабатывай отказ gracefully
- Используй Activity Result API (new approach)
- Проверяй permissions перед выполнением действия
