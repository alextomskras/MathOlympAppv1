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

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(false);

        View customView = inflater.inflate(R.layout.actionbar, null);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT
        );
        layout.gravity = Gravity.END;
        actionBar.setCustomView(customView, layout);

        TextView myAppBarTitleTextView = customView.findViewById(R.id.appBarTVtitle);
        TextView myAppBarScoreTextView = customView.findViewById(R.id.appBarTVscore);

        myAppBarTitleTextView.setText(title);
        myAppBarScoreTextView.setText(score);
    }
}
