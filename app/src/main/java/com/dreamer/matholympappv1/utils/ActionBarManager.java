package com.dreamer.matholympappv1.utils;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.dreamer.matholympappv1.R;

/**
 * Универсальный помощник для управления ActionBar
 * Работает с любым фрагментом и корректно очищается
 */
public class ActionBarManager {

    private final AppCompatActivity activity;
    private final ActionBar actionBar;
    private View customView;
    private boolean isCustomViewEnabled = false;

    public ActionBarManager(@NonNull AppCompatActivity activity) {
        this.activity = activity;
        this.actionBar = activity.getSupportActionBar();
    }

    /**
     * Настраивает ActionBar с кастомным view
     * @param inflater LayoutInflater из фрагмента или активности
     * @param title Заголовок (например, название задачи или раздела)
     * @param subtitle Подзаголовок (например, счет пользователя)
     */
    public void setupActionBar(@NonNull LayoutInflater inflater, 
                               @NonNull String title, 
                               @Nullable String subtitle) {
        if (actionBar == null) {
            return;
        }

        // Очищаем предыдущее состояние
        cleanupActionBar();

        // Включаем отображение кастомного view
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        // Создаем кастомный view
        customView = inflater.inflate(R.layout.actionbar, null);

        TextView titleTextView = customView.findViewById(R.id.appBarTVtitle);
        TextView scoreTextView = customView.findViewById(R.id.appBarTVscore);

        if (titleTextView != null) {
            titleTextView.setText(title != null ? title : "");
        }

        if (scoreTextView != null) {
            if (subtitle != null && !subtitle.isEmpty()) {
                scoreTextView.setText(subtitle);
                scoreTextView.setVisibility(View.VISIBLE);
            } else {
                scoreTextView.setVisibility(View.GONE);
            }
        }

        // Устанавливаем layout params для центрирования
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT
        );
        layout.gravity = Gravity.CENTER_HORIZONTAL;

        actionBar.setCustomView(customView, layout);

        // Устанавливаем флаги отображения
        int displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP;
        actionBar.setDisplayOptions(displayOptions);

        isCustomViewEnabled = true;
    }

    /**
     * Очищает кастомный ActionBar и возвращает к стандартному виду
     * Вызывать в onDestroyView фрагмента
     */
    public void cleanupActionBar() {
        if (actionBar == null) {
            return;
        }

        if (isCustomViewEnabled && customView != null) {
            actionBar.setCustomView(null);
            customView = null;
        }

        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("");
        actionBar.setSubtitle("");
        
        isCustomViewEnabled = false;
    }

    /**
     * Обновляет заголовок ActionBar
     */
    public void updateTitle(String title) {
        if (customView != null) {
            TextView titleTextView = customView.findViewById(R.id.appBarTVtitle);
            if (titleTextView != null) {
                titleTextView.setText(title != null ? title : "");
            }
        }
    }

    /**
     * Обновляет подзаголовок (счет) ActionBar
     */
    public void updateSubtitle(String subtitle) {
        if (customView != null) {
            TextView scoreTextView = customView.findViewById(R.id.appBarTVscore);
            if (scoreTextView != null) {
                if (subtitle != null && !subtitle.isEmpty()) {
                    scoreTextView.setText(subtitle);
                    scoreTextView.setVisibility(View.VISIBLE);
                } else {
                    scoreTextView.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * Статический метод для быстрой очистки ActionBar из фрагмента
     * Удобно вызывать в onDestroyView
     */
    public static void clearActionBar(@Nullable Fragment fragment) {
        if (fragment == null || fragment.getActivity() == null) {
            return;
        }

        if (!(fragment.getActivity() instanceof AppCompatActivity)) {
            return;
        }

        AppCompatActivity activity = (AppCompatActivity) fragment.getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        
        if (actionBar != null) {
            actionBar.setCustomView(null);
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("");
            actionBar.setSubtitle("");
        }
    }
}
