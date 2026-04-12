package com.dreamer.matholympappv1.ui.ui.scrollingscreen;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.dreamer.matholympappv1.R;

public class ActionBarSetupHelper {

    private final AppCompatActivity activity;
    private final ActionBar actionBar;

    public ActionBarSetupHelper(@NonNull AppCompatActivity activity) {
        this.activity = activity;
        actionBar = activity.getSupportActionBar();
    }

    public void setupActionBar(@NonNull LayoutInflater inflater, @NonNull String title, @NonNull String score) {
        if (actionBar == null) {
            return;
        }

        // 1. Сначала полностью очищаем все флаги и скрываем всё стандартное
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(false); // Временно выключаем
        
        // 2. Очищаем стандартные текстовые поля
        actionBar.setTitle("");
        actionBar.setSubtitle("");
        
        View customView = inflater.inflate(R.layout.actionbar, null);
        
        TextView myAppBarTitleTextView = customView.findViewById(R.id.appBarTVtitle);
        TextView myAppBarScoreTextView = customView.findViewById(R.id.appBarTVscore);

        if (myAppBarTitleTextView != null) {
            myAppBarTitleTextView.setText(title);
            android.util.Log.e("TAG", "ActionBarSetupHelper: Title set to: " + title);
        } else {
            android.util.Log.e("TAG", "ActionBarSetupHelper: ERROR - appBarTVtitle is NULL");
        }
        
        if (myAppBarScoreTextView != null) {
            myAppBarScoreTextView.setText(score);
        }

        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT
        );
        layout.gravity = Gravity.CENTER_HORIZONTAL;
        
        // 3. Устанавливаем кастомный view
        actionBar.setCustomView(customView, layout);
        
        // 4. Включаем DISPLAY_SHOW_CUSTOM и DISPLAY_HOME_AS_UP (кнопка назад), используя битовые операции для уверенности
        int displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP;
        actionBar.setDisplayOptions(displayOptions);
        
        // 5. Финальная проверка - ещё раз очищаем титул
        actionBar.setTitle("");
        actionBar.setSubtitle("");
        
        android.util.Log.e("TAG", "ActionBarSetupHelper: Custom view enabled. Display options: " + actionBar.getDisplayOptions());
        android.util.Log.e("TAG", "ActionBarSetupHelper: Final title check: '" + actionBar.getTitle() + "'");
    }
}
