            ---
            name: menu-actionbar
            title: menu-actionbar
            description: menu-actionbar
            ---

            ---

name: menu-actionbar
description: Options menu, ActionBar, Toolbar configuration
metadata:
author: android-team
version: "1.0"
---

# Menu & ActionBar Expert

## Options Menu:

- menu/ XML resources
- onCreateOptionsMenu(menu: Menu)
- onOptionsItemSelected(item: MenuItem)
- PopupMenu для контекстных меню

## ActionBar / Toolbar:

- setSupportActionBar(toolbar)
- supportActionBar?.setDisplayHomeAsUpEnabled(true)
- setTitle, setSubtitle
- Custom views в ActionBar

## Navigation Drawer:

- DrawerLayout с NavigationView
- ActionBarDrawerToggle
- MenuItem.OnNavigationItemSelectedListener
- SavedStateHandle для сохранения состояния

## Bottom Navigation:

- BottomNavigationView
- OnItemSelectedListener
- Fragment replacement при смене tab'ов
- BadgeDrawable для notifications
