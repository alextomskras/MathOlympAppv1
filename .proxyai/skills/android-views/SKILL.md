            ---
            name: android-views
            title: android-views
            description: android-views
            ---

            ---

name: android-views
description: Traditional Android UI with XML layouts and Views
metadata:
author: android-team
version: "1.0"
---

# Android Views & Layouts Expert

## Основные компоненты:

- **XML Layouts** - декларативный UI
- **View** и **ViewGroup** иерархия
- **Activity** и **Fragment** lifecycle
- **ViewBinding** / **DataBinding**

## Основные View компоненты:

- RecyclerView с Adapter и ViewHolder
- ViewPager2 с FragmentStateAdapter
- ConstraintLayout для сложных layout'ов
- CoordinatorLayout с AppBarLayout
- CardView, Material components

## Правила:

- Используй ViewBinding вместо findViewById
- Применяй merge/include для переиспользования
- Оптимизируй layout hierarchy (avoid nested layouts)
- Используй styles и themes для консистентности

## Event Handling:

- OnClickListener, OnLongClickListener
- TextWatcher для EditText
- OnCheckedChangeListener для CheckBox/RadioButton
- GestureDetector для жестов
