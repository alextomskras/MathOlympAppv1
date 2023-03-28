package com.dreamer.matholympappv1.ui.ui.scrollingscreen;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.dreamer.matholympappv1.R;
import com.dreamer.matholympappv1.data.model.model.Zadachi;
import com.dreamer.matholympappv1.databinding.FragmentScrollingBinding;
import com.dreamer.matholympappv1.utils.MyArrayList;
import com.dreamer.matholympappv1.utils.SharedPreffUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ScrollingFragment extends Fragment {

    private ActionBarUpdater actionBarUpdater;
    static final String ARG_ZADACHA_ID = "MyArgZadacha_id";
    static final String ARG_ZADACHA_LIST_FILES_FIRESTORE = "MyArgZadacha_listFilesFirestore";
    static final String ARG_ZADACHA_LIST_SOLUTION_FILES_FIRESTORE = "MyArgZadacha_listSolutionFilesFirestore";
    static final String ARG_ZADACHA_MAIN_BODY = "MyArgZadacha_main_body";
    static final String ARG_ZADACHA_ANSWER = "MyArgZadacha_answer";
    static final String ARG_ZADACHA_HINT = "MyArgZadacha_hint";
    static final String ARG_ZADACHA_SOLUTION = "MyArgZadacha_solution";
    static final String TAG = "TAG";
    NavController navController;
    MenuItem menuScroll;
    private @NonNull
    FragmentScrollingBinding binding;
    private List<Zadachi> zadachiList;
    FirebaseStorage storageReference = FirebaseStorage.getInstance();
    private List listFilesFirestore;
    private Context context;

    private String zadacha_main_body;
    private String zadacha_answer;
    private String zadacha_hint;
    private List listSolutionFilesFirestore;
    private String zadacha_solution;
    public ArrayList<String> myList;
    public SharedPreffUtils sharedPreferencesManager;

    private Integer userScore;
    private String zadacha_id;
    private Integer solutionLimits;
    private Integer hintLimits;
    public AlertDialog.Builder builder;
    private String MainMessage;
    private TextView myAppBarTitleTextView;
    private TextView myAppBarScoreTextView;
    private String searchimagesPath;
    private String answerImageUrl;
    private String solutionImageUrl;

    public ScrollingFragment() {


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        SharedPreffUtils sharedPreferencesManager = new SharedPreffUtils(requireContext());
        zadacha_main_body = "";
//        solutionLimits="";
        zadacha_answer = "";
        zadacha_hint = "";
        zadacha_id = "";
        zadacha_solution = "";
        MainMessage = "";
        searchimagesPath = "";
        userScore = 0;
        answerImageUrl = "gs://matholymp1.appspot.com/answersimages/";
        solutionImageUrl = "gs://matholymp1.appspot.com/solutionimages/";
        listFilesFirestore = new ArrayList<>();
        listSolutionFilesFirestore = new ArrayList<>();

        intNavcontroller();

    }

    private void getFragmentBundles() {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getBundleArguments();

        ActionBarSetup(inflater);
        binding = FragmentScrollingBinding.inflate(inflater, container, false);
        setupFirebaseStorage();


        return binding.getRoot();


    }

    private void ActionBarSetup(@NonNull LayoutInflater inflater) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
// Inflate the custom view
        View customView = inflater.inflate(R.layout.actionbar, null);
        // Set the text on the ActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowCustomEnabled(false);
            // Add the custom layout to the ActionBar
            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.MATCH_PARENT);
            layout.gravity = Gravity.END;
//            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);

            myAppBarTitleTextView = customView.findViewById(R.id.appBarTVtitle);
            myAppBarScoreTextView = customView.findViewById(R.id.appBarTVscore);
            // Create an instance of ActionBarUpdater
            actionBarUpdater = new ActionBarUpdater(myAppBarTitleTextView, myAppBarScoreTextView);
            actionBarUpdater.updateTitle(getString(R.string.appbar_title_scroll_fragm));
            actionBarUpdater.updateScore(getString(R.string.appbar_score));


            actionBar.setCustomView(customView);
        }
    }


    private void getBundleArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            zadacha_id = bundle.getString(ARG_ZADACHA_ID);
            listFilesFirestore = bundle.getStringArrayList(ARG_ZADACHA_LIST_FILES_FIRESTORE);
            listSolutionFilesFirestore = bundle.getStringArrayList(ARG_ZADACHA_LIST_SOLUTION_FILES_FIRESTORE);
            zadacha_main_body = bundle.getString(ARG_ZADACHA_MAIN_BODY);
            zadacha_answer = bundle.getString(ARG_ZADACHA_ANSWER);
            zadacha_hint = bundle.getString(ARG_ZADACHA_HINT);
            zadacha_solution = bundle.getString(ARG_ZADACHA_SOLUTION);

        }
    }

    private void setupFirebaseStorage() {
        storageReference = FirebaseStorage.getInstance("gs://matholymp1.appspot.com");
        StorageReference storageRef = storageReference.getReference();
    }

    public void ScrollingFragmentZdachiList(List<Zadachi> zadachiList, Context context) {
        this.zadachiList = zadachiList;
        this.context = context;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> myList = new ArrayList<String>();

//        MyArrayList myArrayList = new MyArrayList();
        //Ищем все что есть на макете
        NavController navController;
        final TextView zadachaTextView = binding.tvScrollMainText;
        final TextView answerTextView = binding.tvScrollAnswerText;
        final TextView hintTextView = binding.tvScrollHintText;
        final TextView solutionTextView = binding.tvScrollSolutionText;
        final TextInputEditText edtxtAnswer = binding.edtxtAnswer;
        final Button btnVerify = binding.btnVerify;

        //Set all to CAPS
        zadachaTextView.setAllCaps(true);

        zadachaTextView.setText(zadacha_main_body);

        answerTextView.setVisibility(View.GONE);
        hintTextView.setVisibility(View.GONE);
        solutionTextView.setVisibility(View.GONE);

        btnVerify.setOnClickListener(view1 -> {
            String textEnter = edtxtAnswer.getText().toString();
            checkAnswer(textEnter, myList);

        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                String textEnter = edtxtAnswer.getText().toString();

            }
        };

        edtxtAnswer.addTextChangedListener(afterTextChangedListener);
        edtxtAnswer.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String textEnter = edtxtAnswer.getText().toString();
                checkAnswer(textEnter, myList);

            }
            return false;
        });


    }

    private void checkAnswer(String answer, ArrayList myArrayList) {


        String etTexttitle = answer;
//        Log.d(TAG, "viewId: " + etTexttitle);
        if (!etTexttitle.equals("") && etTexttitle.equals(zadacha_answer)) {


            alertDiaShow(getString(R.string.alertDialogShowUSPEHTitle), getString(R.string.alertDialogShowUSPEHMessageBodySet));
            // Add a string to the list

            MyArrayList.addString(zadacha_id);

            //делаем операции со счетчиком юзера
            Integer userScore = sharedPreffsLoadUserScore();
            Log.e(TAG, "userScore: " + userScore);
            userScore = userScore + 10;
            Log.e(TAG, "userScore: " + userScore);
            sharedPreffsSaveUserScore(userScore);
            firebaseSaveUserScore(userScore);


        } else {
            alertDiaShow(getString(R.string.alertDialogShowOSHIBKASetTitle), getString(R.string.alertDialogShowOSHIBKAMessageBodySet));


        }
    }

    // Update the text of the TextView in the ActionBar
    private void updateActionBarTextViewTitle(String title) {
        myAppBarTitleTextView.setText(title);

    }


    private void updateActionBarTextViewScore(String score) {
        myAppBarScoreTextView.setText("");
    }

    private void alertDiaShow(String Title, String MainMessage) {

        builder = new MaterialAlertDialogBuilder(getActivity(), R.style.MyTheme);

        LayoutInflater inflater = this.getLayoutInflater();
        View team = inflater.inflate(R.layout.allertdialog_layout, null);

        TextView tw = team.findViewById(R.id.allertMesage1);
        TextView tw1 = team.findViewById(R.id.allertMesage2);// id of your imageView element
        ImageView iv1 = team.findViewById(R.id.imageOtvet1);
        tw1.setVisibility(View.GONE);
        builder.setView(team);

        final String USPEH_TITLE = getString(R.string.alertDialogUSPEHTitleCheck);
        final String HINT_TITLE = getString(R.string.alertDialogTitleHintCheck);
        final String SOLUTION_TITLE = getString(R.string.alertDialogTitleSolution);

        if (!Title.equals("") && Title.equals(USPEH_TITLE)) {
            builder.setView(team);
            tw.setText(MainMessage);
            searchimagesPath = "answersimages";

            setFirebaseImage(searchimagesPath, iv1);


            builder


                    .setCancelable(true)


                    .setPositiveButton(R.string.alertDialogUSPEHPositiveButton, (dialog, id) -> {

                        dialog.cancel();
                        navController.navigateUp();
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "YESSSS", Snackbar.LENGTH_LONG).show();
                    })


                    .setIcon(R.drawable.ic_baseline_bubble_chart_24)
                    .setNegativeButton(R.string.alertDialogUSPEHNegativeButton, (dialog, id) -> {
                        //  Action for 'NO' Button

                        dialog.cancel();
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "NOOOOOO", Snackbar.LENGTH_LONG).show();
                    });
        } else if (!Title.equals("") && Title.equals(HINT_TITLE)) {
            builder.setView(team);
            tw.setText(MainMessage);

            builder

                    .setCancelable(true)
                    .setPositiveButton(R.string.alertDialogHintPositiveButton, (dialog, id) -> dialog.cancel())


                    .setIcon(R.drawable.ic_baseline_bubble_chart_24)
                    .setNegativeButton(R.string.allertDialogNegativeButton, (dialog, id) -> {
                        //  Action for 'NO' Button

                        dialog.cancel();
                        navController.navigateUp();

                    });

        } else if (!Title.equals("") && Title.equals(SOLUTION_TITLE)) {
            builder.setView(team);
            tw.setText(MainMessage);

            searchimagesPath = "solutionimages";
            setFirebaseImage(searchimagesPath, iv1);
            builder


                    .setCancelable(true)
                    .setPositiveButton(R.string.alertDialogPositiveButtonSolution, (dialog, id) -> dialog.cancel())


                    .setIcon(R.drawable.ic_baseline_bubble_chart_24)
                    .setNegativeButton(R.string.allertDialogNegativeButton, (dialog, id) -> {
                        //  Action for 'NO' Button

                        dialog.cancel();
                        navController.navigateUp();

                    });


        } else {
            builder.setView(team);
            tw.setText(MainMessage);
            builder

                    .setCancelable(true)
                    .setPositiveButton(R.string.alertDialogHintButton, (dialog, id) -> {
                        hintLimits = sharedPreffsLoadHintLimits();
                        if (hintLimits == 0) {

                            return;
                        }
                        hintLimits = hintLimits - 1;
                        sharedPreffsSaveHintLimits(hintLimits);
                        firebaseSaveHintLimits(hintLimits);
                        alertDiaShow(getString(R.string.alertDialogShowTitleHINT), getString(R.string.alertDialogShowMessageBodyHINT) + zadacha_hint);
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "YESSSS",
                                Snackbar.LENGTH_LONG).show();
                    })
                    .setNeutralButton(R.string.alertDialogREZULTATNeutralButton, (dialog, id) -> {
                                //  Action for 'NO' Button
                                solutionLimits = sharedPreffsLoadSolutionLimits();
                                if (solutionLimits == 0) {

                                    return;
                                }
                                solutionLimits = solutionLimits - 1;
                                sharedPreffsSaveSolutionLimits(solutionLimits);
                                firebaseSaveSolutionLimits(solutionLimits);
                                alertDiaShow(getString(R.string.alertDialogRezultatForSolutionTitle), getString(R.string.alertDialogRezultatForSolutionMessageBody) + zadacha_solution);

                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                        "EXITTT", Snackbar.LENGTH_LONG).show();
                            }
                    )

                    .setIcon(R.drawable.ic_baseline_bubble_chart_24)
                    .setNegativeButton(R.string.alertDialogRezultatNegativeButton, (dialog, id) -> {
                        //  Action for 'NO' Button

                        dialog.cancel();
                        navController.navigateUp();
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "NOOOOOO", Snackbar.LENGTH_LONG).show();
                    });

        }


        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setInverseBackgroundForced(true);

        dialog.show();
        solutionLimits = sharedPreffsLoadSolutionLimits();
        if (solutionLimits == 0) {
            Button buttonNeutral = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
            buttonNeutral.setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
//            return;
        }
        hintLimits = sharedPreffsLoadHintLimits();
        if (hintLimits == 0) {
            Button buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            buttonPositive.setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
//            return;
        } else {
            Button buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            buttonPositive.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
        }

        Button buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonNegative.setTextColor(ContextCompat.getColor(getContext(), R.color.red));

    }

    private void setFirebaseImage(String searchimagesPath, ImageView iv1) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        List locallistFiles;
        if (searchimagesPath == "answersimages") {
            locallistFiles = listFilesFirestore;
        } else {
            locallistFiles = listSolutionFilesFirestore;
        }


        StorageReference spaceRef = storageRef.child("answersimages/answer" + zadacha_id);
        spaceRef.getName();
        spaceRef.getMetadata();


        Object splitString = locallistFiles.get(Integer.parseInt(zadacha_id) - 1).toString();
        String[] parts = ((String) splitString).split(Pattern.quote("/"));
        String imageLoad = parts[4];
        String imagePatch = searchimagesPath + "/" + imageLoad;


        storageRef.child(searchimagesPath + "/" + imageLoad).getDownloadUrl().addOnSuccessListener(uri -> {
            // Download directly from StorageReference using Glide
// (See MyAppGlideModule for Loader registration)
            Glide.with(getContext())
                    .load(uri)
                    .into(iv1);

            // Got the download URL for 'users/me/profile.png'
        }).addOnFailureListener(exception -> {
            Log.d(TAG, "____DATE= " + "storageRef");
            // Handle any errors
        });
    }

    private void intNavcontroller() {
        Activity MainActivity = getActivity();
        assert MainActivity != null;
        navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
    }

    private void sharedPreffsSaveUserScore(Integer zadacha_score) {
        SharedPreffUtils sharedPreferencesManager = new SharedPreffUtils(requireContext());
        sharedPreferencesManager.saveData("zadacha_score", zadacha_score);
    }

    private void sharedPreffsSaveSolutionLimits(Integer solutionLimits) {
        SharedPreffUtils sharedPreferencesManager = new SharedPreffUtils(requireContext());
        sharedPreferencesManager.saveData("solution_limits", solutionLimits);
    }

    private Integer sharedPreffsLoadSolutionLimits() {
        SharedPreffUtils sharedPreferencesManager = new SharedPreffUtils(requireContext());
        return sharedPreferencesManager.getDataFromSharedPreferences("solution_limits");
    }

    private void sharedPreffsSaveHintLimits(Integer hintLimits) {
        SharedPreffUtils sharedPreferencesManager = new SharedPreffUtils(requireContext());
        sharedPreferencesManager.saveData("hint_limits", hintLimits);
    }

    private Integer sharedPreffsLoadHintLimits() {
        SharedPreffUtils sharedPreferencesManager = new SharedPreffUtils(requireContext());
        return sharedPreferencesManager.getDataFromSharedPreferences("hint_limits");
    }

    private void firebaseSaveUserScore(Integer zadacha_score) {
        String str2 = Integer.toString(zadacha_score);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Error: current user is null");
            return;
        }
        String uid = user.getUid();
        try {
            DatabaseReference scoreRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("userscore");
            scoreRef.setValue(str2);
        } catch (Exception e) {
            Log.e(TAG, "Error saving user score to Firebase: " + e.getMessage());
        }
    }

    private void firebaseSaveSolutionLimits(Integer solutionlimits) {
        String str2 = Integer.toString(solutionlimits);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Error: current user is null");
            return;
        }
        String uid = user.getUid();
        try {
            DatabaseReference solutionRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("solutionlimits");
            solutionRef.setValue(str2);
        } catch (Exception e) {
            Log.e(TAG, "Error saving user score to Firebase: " + e.getMessage());
        }
    }

    private void firebaseSaveHintLimits(Integer hintlimits) {
        String str2 = Integer.toString(hintlimits);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Error: current user is null");
            return;
        }
        String uid = user.getUid();
        try {
            DatabaseReference hintRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("hintlimits");
            hintRef.setValue(str2);
        } catch (Exception e) {
            Log.e(TAG, "Error saving user score to Firebase: " + e.getMessage());
        }
    }

    private Integer sharedPreffsLoadUserScore() {
        SharedPreffUtils sharedPreferencesManager = new SharedPreffUtils(requireContext());
        return sharedPreferencesManager.getDataFromSharedPreferences("zadacha_score");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        menuScroll = menu.add(R.string.scrollFrgMenuAddHintTitle);
        menuScroll.setTitle(R.string.scrollFrgMenuTitle);
        menuScroll.setTitleCondensed(getString(R.string.scrollFrgMenuCondesedTitle));
        menuScroll.setOnMenuItemClickListener(v ->
                {
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            zadacha_hint, Snackbar.LENGTH_LONG).show();
                    return true;
                }
        );
    }
}
