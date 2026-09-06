            ---
            name: dialogs-bottomsheet
            title: dialogs-bottomsheet
            description: dialogs-bottomsheet
            ---

            ---

name: dialogs-bottomsheet
description: DialogFragments, BottomSheets, Alerts
metadata:
author: android-team
version: "1.0"
---

# Dialogs & BottomSheets Expert

## DialogFragment:

- extends DialogFragment (не AlertDialog напрямую)
- onCreateDialog vs onCreateView
- show(fragmentManager, tag)
- dismiss() и dismissAllowingStateLoss()

## AlertDialog:

- AlertDialog.Builder
- setTitle, setMessage, setItems
- setPositiveButton, setNegativeButton
- setNeutralButton

## BottomSheetDialogFragment:

- extends BottomSheetDialogFragment
- BottomSheetBehavior
- STATE_COLLAPSED, STATE_EXPANDED
- isDraggable, skipCollapsed

## Custom Dialogs:

- Custom layout через onCreateView
- DialogFragment.setStyle(STYLE_NORMAL, R.style.Theme)
- Dialog width (wrap_content, match_parent, percent)
- Back button handling
