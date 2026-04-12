package com.dreamer.matholympappv1.utils;

import android.widget.TextView;

public class ScrollingScreenActionBarUpdater {
    private final TextView myAppBarTitleTextView;
    private final TextView myAppBarScoreTextView;

    public ScrollingScreenActionBarUpdater(TextView myAppBarTitleTextView, TextView myAppBarScoreTextView) {
        this.myAppBarTitleTextView = myAppBarTitleTextView;
        this.myAppBarScoreTextView = myAppBarScoreTextView;
    }

    public void updateTitle(String title) {
        if (myAppBarTitleTextView != null) {
            myAppBarTitleTextView.setText(title);
        }
    }

    public void updateScore(String score) {
        if (myAppBarScoreTextView != null) {
            myAppBarScoreTextView.setText(score);
        }
    }
}
