package com.dreamer.matholympappv1.ui.ui.scrollingscreen;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dreamer.matholympappv1.data.model.model.Zadachi;
import com.dreamer.matholympappv1.databinding.FragmentScrollingBinding;

import java.util.List;

public class ScrollingFragment extends Fragment {

    static final String TAG = "TAG";
    private @NonNull
    FragmentScrollingBinding binding;
    private List<Zadachi> zadachiList;
    private Context context;
    //    private String mainBodyZadacha;
    private String zadacha_main_body;
    private String zadacha_answer;

    public ScrollingFragment() {
//        this.zadachiList = zadachiList;
//        this.context = context;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
//        zadachiList = (List<Zadachi>) zadachiList.get(1);
        zadacha_main_body = "";
        zadacha_answer = "";
        ;
        Bundle bundle = getArguments();
        if (bundle != null) {

            zadacha_main_body = bundle.getString("MyArgZadacha_main_body");
            zadacha_answer = bundle.getString("MyArgZadacha_answer");
        }
        Log.e(TAG, "iconImageViewOnClick at position8 " + bundle.getString("MyArgZadacha_main_body", "_PlaceHolder_"));
        binding = FragmentScrollingBinding.inflate(inflater, container, false);
        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_scrolling, container, false);


    }

    public void ScrollingFragmentZdachiList(List<Zadachi> zadachiList, Context context) {
        this.zadachiList = zadachiList;
        this.context = context;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView zadachaTextView = binding.tvScrollMainText;
        final TextView answerTextView = binding.tvScrollAnswerText;

        //Set all to CAPS
        zadachaTextView.setAllCaps(true);

        zadachaTextView.setText(zadacha_main_body);
        answerTextView.setText(zadacha_answer);
    }

}