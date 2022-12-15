package com.dreamer.matholympappv1.ui.main.ui.scrollingscreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dreamer.matholympappv1.R;
import com.dreamer.matholympappv1.ui.main.ui.mainscreen.MainFragment;

public class ScrollingFragment extends Fragment {
    public static ScrollingFragment newInstance() {
        return new ScrollingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scrolling, container, false);
    }
}