package com.dreamer.matholympappv1.ui.ui.scrollingscreen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dreamer.matholympappv1.data.data.model.Zadachi;
import com.dreamer.matholympappv1.databinding.FragmentScrollingBinding;

import java.util.ArrayList;
import java.util.List;

public class ScrollingFragment extends Fragment {

    static final String TAG = "TAG";
    private @NonNull
    FragmentScrollingBinding binding;
    private List<Zadachi> zadachiList;

    public static ScrollingFragment newInstance() {
        return new ScrollingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        zadachiList = new ArrayList<>();
        ;
        Log.e(TAG, "iconImageViewOnClick at position8 " + zadachiList);
        binding = FragmentScrollingBinding.inflate(inflater, container, false);
        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_scrolling, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView zadachaTextView = binding.tvScrollMainText;
        final TextView answerTextView = binding.tvScrollAnswerText;
        zadachaTextView.setAllCaps(true);
//        zadachaTextView.setText((CharSequence) zadachiList.get(1));
    }

}