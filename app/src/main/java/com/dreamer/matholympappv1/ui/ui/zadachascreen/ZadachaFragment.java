package com.dreamer.matholympappv1.ui.ui.zadachascreen;

import static com.dreamer.matholympappv1.ui.ui.scrollingscreen.FirebaseUserScoreManager.firebaseSaveHintLimits;
import static com.dreamer.matholympappv1.ui.ui.scrollingscreen.FirebaseUserScoreManager.firebaseSaveSolutionLimits;
import static com.dreamer.matholympappv1.utils.MyArrayList.firebaseGetSolutionLimits;

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
import com.dreamer.matholympappv1.utils.StringIntegerConverter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 */
public class ZadachaFragment extends Fragment {
    NavController navController;
    private FirebaseAuth mAuth;
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
        mAuth = FirebaseAuth.getInstance();
// Get arguments passed from previous fragment
        Bundle args = getArguments();
        if (args != null) {
            String username = args.getString("username");
            String password = args.getString("password");
            String solutionlimits = args.getString("solutionlimits", "1");
            String hintlimits = args.getString("hintlimits", "3");

            // Do something with username and password
            updateUiWithUser(username, password);

            int solutionlimitsnum = StringIntegerConverter.stringToInt(solutionlimits); // num will be 123
            int hintlimitsnum = StringIntegerConverter.stringToInt(hintlimits); // num will be 123

            firebaseSaveSolutionLimits(solutionlimitsnum);
            firebaseSaveHintLimits(hintlimitsnum);

        }

        intNavcontroller();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        solutionslimits = 0;
        hintslimits = 1;
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

        //other metod for catch data from firebaseDB
        getSolutionLimitsFromFirebase(new MyArrayList.SolutionLimitsCallback() {
            @Override
            public int onSuccess(Integer solutionlimits) {
                if (solutionlimits != null) {
                    // Do something with the solution limits value here
                    solutionslimits = solutionlimits;
                    Log.d(TAG, "Solution limits9: " + solutionlimits);
                } else {
                    // Handle the null value here
                    return
                            Log.e(TAG, "Solution limits value is null.");
                }
                return solutionslimits;
            }

            @Override
            public void onFailure(Exception e) {
                // Handle the error here
                Log.e(TAG, "Error getting solution limits from Firebase: " + e.getMessage());
            }
        });


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
        if (solutionLimits == null) {
            solutionLimits = 0;
            return;
        }
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
        firebaseGetSolutionLimits(solutionlimits -> {
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

    public void getSolutionLimitsFromFirebase(MyArrayList.SolutionLimitsCallback callback) {
        MyArrayList.firebaseGetSolutionLimits(callback);
    }

    public void getHintLimitsFromFirebase() {
        MyArrayList.firebaseGetHintLimits(hintlimits -> {
            if (hintlimits != null) {
                // Do something with the solution limits value here
                hintslimits = hintlimits;
                Log.d(TAG, "Solution limits3: " + hintlimits);
                Log.d(TAG, "Solution limits31: " + hintslimits);
                sharedPreffsSaveHintLimits(hintslimits);
            } else {
                // Handle the null value here
                Log.e(TAG, "Hint limits value is null");
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle the error here
                Log.e(TAG, "Error getting solution limits from Firebase: " + e.getMessage());
            }
        });
    }

    private void updateUiWithUser(String username, String password) {
        String username1 = username;
        String password1 = password;
//        String welcome = getString(R.string.welcome) + model.getDisplayName() + model.getPassword();
//        Log.d(TAG, "User ID1welcome: " + welcome);
//        mAuth.signInWithEmailAndPassword(model.getDisplayName(), model.getPassword()).addOnCompleteListener((Activity) getContext(),
        mAuth.signInWithEmailAndPassword(username1, password1).addOnCompleteListener((Activity) getContext(),
                task -> {
                    if (task.isSuccessful()) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                task.getResult().getUser().getEmail(), Snackbar.LENGTH_LONG).show();
//                        FragmentTransaction ft = getFragmentManager().beginTransaction();
//                        Fragment mFrag = new ZadachaFragment();
//                        ft.replace(R.id.zadachaFragment, mFrag);
//                        ft.commit();
//                        Bundle args = new Bundle();
//                        args.putString("username", username);
//                        args.putString("password", password);
//                        navController.clearBackStack(R.id.loginFragment);
//                        navController.navigate(R.id.action_loginFragment_to_zadachaFragment, args);
                    } else {
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                task.getException().getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                    }

                });
        // TODO : initiate successful logged in experience
        if (getContext() != null && getContext().getApplicationContext() != null) {
//            Toast.makeText(getContext().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();


        }
    }
}