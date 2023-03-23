package com.dreamer.matholympappv1.ui.ui.zadachascreen;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamer.matholympappv1.R;
import com.dreamer.matholympappv1.utils.MyArrayList;
import com.dreamer.matholympappv1.utils.SharedPreffUtils;
import com.google.android.gms.tasks.OnFailureListener;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 */
public class ZadachaFragment extends Fragment {
    NavController navController;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = "TAG";
    private TextView myAppBarTitleTextView;
    private TextView myAppBarScoreTextView;

    //        getSolutionLimitsFromFirebase();
//        solutionlimits = MyArrayList.firebaseLoadSolutionLimits();
    private Integer solutionslimits;
    private Integer hintslimits;
    private int mColumnCount = 1;
    private RecyclerView recyclerView;
    private UserRecyclerViewAdapter userRecyclerViewAdapter;
    private ZadachiRecyclerViewAdapter zadachiRecyclerViewAdapter;

    private ZadachaViewModel viewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        intNavcontroller();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        solutionslimits = 0;
        hintslimits = 0;
        viewModel = new ViewModelProvider(this).get(ZadachaViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
// Inflate the custom view
        View customView = inflater.inflate(R.layout.actionbar, null);
        // Set the text on the ActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            // Add the custom layout to the ActionBar
            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.MATCH_PARENT);
            layout.gravity = Gravity.END;
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);

            myAppBarTitleTextView = customView.findViewById(R.id.appBarTVtitle);
            myAppBarScoreTextView = customView.findViewById(R.id.appBarTVscore);
            updateActionBarTextViewTitle(getString(R.string.appbar_title_zadacha_fragm));
            updateActionBarTextViewScore(getString(R.string.appbar_score));


            actionBar.setCustomView(customView);
        }


        userRecyclerViewAdapter = new UserRecyclerViewAdapter(new ArrayList<>(), getContext());
        zadachiRecyclerViewAdapter = new ZadachiRecyclerViewAdapter(new ArrayList<>(), getContext());

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            recyclerView.setAdapter(zadachiRecyclerViewAdapter);
        }
        // Observe user list from view model and update adapter
        viewModel.getUsersLiveData().observe(getViewLifecycleOwner(), users -> {
            userRecyclerViewAdapter.setUsersList(users);
            userRecyclerViewAdapter.notifyDataSetChanged();
        });
        // Observe zadachi list from view model and update adapter
        viewModel.getZadachiLiveData().observe(getViewLifecycleOwner(), zadachi -> {
            zadachiRecyclerViewAdapter.setZadachiList(zadachi);
            zadachiRecyclerViewAdapter.notifyDataSetChanged();
        });

        // Set adapter to recycler view
        recyclerView.setAdapter(zadachiRecyclerViewAdapter);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Obtain a reference to the ActionBar object in the parent activity

//        MyArrayList.readArrayListFromFirebase();
        MyArrayList.loadArrayListFromFirebase();
        //get values of Solutions and Hints Limits from firebaseDB and save to sharedPreffs
        getSolutionLimitsFromFirebase();
        getHintLimitsFromFirebase();
//        Log.e(TAG, "MyArrayList.firebaseLoadSolutionLimits(): " + solutionslimits);
//        Log.e(TAG, "MyArrayList.firebaseLoadSolutionLimits2(): " + hintslimits);


    }

    @Override
    public void onStart() {
        super.onStart();
        // Fetch data from database using view model
//        viewModel.fetchUsers();
        viewModel.loadUsers();
//        viewModel.fetchZadachi();
        viewModel.loadZadachi();
    }

    // Update the text of the TextView in the ActionBar
    private void updateActionBarTextViewTitle(String title) {
        myAppBarTitleTextView.setText(title);

    }

    private void sharedPreffsSaveHintLimits(Integer hintLimits) {
        SharedPreffUtils sharedPreferencesManager = new SharedPreffUtils(requireContext());
        sharedPreferencesManager.saveData("hint_limits", hintLimits);
    }

    private void sharedPreffsSaveSolutionLimits(Integer solutionLimits) {
        SharedPreffUtils sharedPreferencesManager = new SharedPreffUtils(requireContext());
        sharedPreferencesManager.saveData("solution_limits", solutionLimits);
    }


    private Integer sharedPreffsLoadSolutionLimits() {
        SharedPreffUtils sharedPreferencesManager = new SharedPreffUtils(requireContext());
        Integer solutionLimits = sharedPreferencesManager.getDataFromSharedPreferences("solution_limits");
        return solutionLimits;
    }

    private void updateActionBarTextViewScore(String score) {
        Integer userScore = sharedPreffsLoadUserScore();
        if (userScore != 0) {
            myAppBarScoreTextView.setText(score + userScore);

        }

    }

    private Integer sharedPreffsLoadUserScore() {
        SharedPreffUtils sharedPreferencesManager = new SharedPreffUtils(requireContext());
        Integer zadacha_score = sharedPreferencesManager.getDataFromSharedPreferences("zadacha_score");
        return zadacha_score;
    }

    private void intNavcontroller() {
        Activity MainActivity = getActivity();
        assert MainActivity != null;
        navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
    }


    public void getSolutionLimitsFromFirebase() {
        MyArrayList.firebaseGetSolutionLimits(solutionlimits -> {
            // Do something with the solution limits value here
            solutionslimits = solutionlimits;
            Log.d(TAG, "Solution limits2: " + solutionlimits);
            Log.d(TAG, "Solution limits21: " + solutionslimits);
            sharedPreffsSaveSolutionLimits(solutionslimits);

        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle the error here
                Log.e(TAG, "Error getting solution limits from Firebase: " + e.getMessage());
            }
        });
        Log.d(TAG, "Solution limits22: " + solutionslimits);

    }


    public void getHintLimitsFromFirebase() {
        MyArrayList.firebaseGetHintLimits(hintlimits -> {
            // Do something with the solution limits value here
            hintslimits = hintlimits;
            Log.d(TAG, "Solution limits3: " + hintlimits);
            Log.d(TAG, "Solution limits31: " + hintslimits);
            sharedPreffsSaveHintLimits(hintslimits);
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle the error here
                Log.e(TAG, "Error getting solution limits from Firebase: " + e.getMessage());
            }
        });
    }
}