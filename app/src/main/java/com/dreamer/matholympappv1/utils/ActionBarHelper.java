package com.dreamer.matholympappv1.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.dreamer.matholympappv1.R;

public class ActionBarHelper {

    private TextView myAppBarTitleTextView;
    private TextView myAppBarScoreTextView;
    private AppCompatActivity activity;

    public ActionBarHelper(FragmentActivity activity) {
        this.activity = (AppCompatActivity) activity;
    }

    public void setupActionBar(FragmentActivity activity, String title, String subTitle) {
        ActionBar actionBar = this.activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);

            View customView = LayoutInflater.from(this.activity).inflate(R.layout.actionbar, null);
            myAppBarTitleTextView = customView.findViewById(R.id.appBarTVtitle);
            myAppBarScoreTextView = customView.findViewById(R.id.appBarTVscore);
            updateActionBarTextViewTitle(title);
            updateActionBarTextViewScore(subTitle);

            actionBar.setCustomView(customView);
        }
    }

    private void updateActionBarTextViewTitle(String title) {
        myAppBarTitleTextView.setText(title);
    }

    private void updateActionBarTextViewScore(String subTitle) {
        Integer userScore = sharedPreffsLoadUserScore();
        if (userScore != null && userScore != 0) {
            String score = activity.getString(R.string.appbar_score) + userScore;
            myAppBarScoreTextView.setText(score);
        } else {
            myAppBarScoreTextView.setVisibility(View.GONE);
        }
    }

    private Integer sharedPreffsLoadUserScore() {
        SharedPreffUtils sharedPreferencesManager = new SharedPreffUtils(activity);
        Integer zadacha_score = sharedPreferencesManager.getDataFromSharedPreferences("zadacha_score");
        return zadacha_score;
    }
}