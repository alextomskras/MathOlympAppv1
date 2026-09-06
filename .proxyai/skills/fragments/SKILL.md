            ---
            name: fragments
            title: fragments
            description: fragments
            ---

            ---

name: fragments
description: Fragment lifecycle, navigation and communication
metadata:
author: android-team
version: "1.0"
---

# Fragment Management Expert

## Fragment Lifecycle:

- onAttach, onCreate, onCreateView
- onViewCreated, onStart, onResume
- onPause, onStop, onDestroyView, onDestroy
- onDetach

## Fragment Transactions:

- FragmentManager и FragmentTransaction
- add, replace, remove, attach, detach
- addToBackStack для навигации
- commit vs commitAllowingStateLoss

## Communication:

- ViewModel для обмена данными между Fragment'ами
- Interface callbacks (onAttach context)
- FragmentResult API
- Navigation Component (Safe Args)

## Best Practices:

- Избегай new Fragment(), используй newInstance pattern
- Сохраняй состояние в onSaveInstanceState
- Очищай ресурсы в onDestroyView
- Используй viewLifecycleOwner для LiveData наблюдения
