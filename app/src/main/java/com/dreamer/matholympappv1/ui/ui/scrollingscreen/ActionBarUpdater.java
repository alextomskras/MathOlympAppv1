package com.dreamer.matholympappv1.ui.ui.scrollingscreen;

import android.widget.TextView;

public class ActionBarUpdater {
    private final TextView myAppBarTitleTextView;
    private final TextView myAppBarScoreTextView;

    public ActionBarUpdater(TextView myAppBarTitleTextView, TextView myAppBarScoreTextView) {
        this.myAppBarTitleTextView = myAppBarTitleTextView;
        this.myAppBarScoreTextView = myAppBarScoreTextView;
    }

    public void updateTitle(String title) {
        myAppBarTitleTextView.setText(title);
    }

    public void updateScore(String score) {
        myAppBarScoreTextView.setText("");
    }
}
