package com.dreamer.matholympappv1.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import androidx.navigation.NavController;

import com.dreamer.matholympappv1.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class MyMenuInflater {

    public static void inflateMenu(Menu menu, Context context, NavController navController, String zadacha_hint) {
        MenuItem menuScroll = menu.add(R.string.scrollFrgMenuAddHintTitle);
        menuScroll.setTitle(R.string.scrollFrgMenuTitle);
        menuScroll.setTitleCondensed(context.getString(R.string.scrollFrgMenuCondesedTitle));
        menuScroll.setOnMenuItemClickListener(v -> {
            Snackbar.make(((Activity) context).findViewById(android.R.id.content), zadacha_hint, Snackbar.LENGTH_LONG).show();
            return true;
        });

        menuScroll = menu.add(context.getString(R.string.allertDialogNegativeButton));
        menuScroll.setTitle(context.getString(R.string.allertDialogNegativeButton));
        menuScroll.setTitleCondensed(context.getString(R.string.allertDialogNegativeButton));
        menuScroll.setOnMenuItemClickListener(v -> {
            // 1. Сначала очищаем бэкстек до loginFragment
            navController.popBackStack(R.id.loginFragment, false);
            // 2. Затем выполняем навигацию на loginFragment
            navController.navigate(R.id.loginFragment);
            // 3. И только после этого выходим из аккаунта
            FirebaseAuth.getInstance().signOut();
            return true;
        });

        menuScroll = menu.add("settings");
        menuScroll.setTitle("settings");
        menuScroll.setTitleCondensed("settings");
        menuScroll.setOnMenuItemClickListener(v -> {
//            FirebaseAuth.getInstance().signOut();
//            navController.clearBackStack(R.id.scrollingfragment);
            navController.navigate(R.id.action_scrollingFragment2_to_settingsFragment);
            return true;
        });
    }


    public static void RazdelinflateMenu(Menu menu, Context context, NavController navController) {
//        MenuItem menuScroll = menu.add(R.string.scrollFrgMenuAddHintTitle);
//        menuScroll.setTitle(R.string.scrollFrgMenuTitle);
//        menuScroll.setTitleCondensed(context.getString(R.string.scrollFrgMenuCondesedTitle));
//        menuScroll.setOnMenuItemClickListener(v -> {
//            Snackbar.make(((Activity) context).findViewById(android.R.id.content), zadacha_hint, Snackbar.LENGTH_LONG).show();
//            return true;
//        });

        MenuItem menuScroll = menu.add(context.getString(R.string.allertDialogNegativeButton));
        menuScroll.setTitle(context.getString(R.string.allertDialogNegativeButton));
        menuScroll.setTitleCondensed(context.getString(R.string.allertDialogNegativeButton));
        menuScroll.setOnMenuItemClickListener(v -> {
            // 1. Сначала очищаем бэкстек до loginFragment
            navController.popBackStack(R.id.loginFragment, false);
            // 2. Затем выполняем навигацию на loginFragment
            navController.navigate(R.id.loginFragment);
            // 3. И только после этого выходим из аккаунта
            FirebaseAuth.getInstance().signOut();
            return true;
        });

        menuScroll = menu.add("settings");
        menuScroll.setTitle("settings");
        menuScroll.setTitleCondensed("settings");
        menuScroll.setOnMenuItemClickListener(v -> {
//            FirebaseAuth.getInstance().signOut();
//            navController.clearBackStack(R.id.scrollingfragment);
            navController.navigate(R.id.action_RAZDELFragment_to_settingsFragment);
            return true;
        });
    }
}
