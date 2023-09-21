package com.dreamer.matholympappv1.ui.ui.scrollingscreen;


import static com.dreamer.matholympappv1.utils.SharedPreffUtils.sharedPreffsLoadHintLimits;
import static com.dreamer.matholympappv1.utils.SharedPreffUtils.sharedPreffsLoadSolutionLimits;
import static com.dreamer.matholympappv1.utils.SharedPreffUtils.sharedPreffsLoadUserScore;
import static com.dreamer.matholympappv1.utils.SharedPreffUtils.sharedPreffsSaveHintLimits;
import static com.dreamer.matholympappv1.utils.SharedPreffUtils.sharedPreffsSaveSolutionLimits;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.dreamer.matholympappv1.R;
import com.dreamer.matholympappv1.data.model.model.Zadachi;
import com.dreamer.matholympappv1.databinding.FragmentScrollingBinding;
import com.dreamer.matholympappv1.utils.MyArrayList;
import com.dreamer.matholympappv1.utils.MyMenuInflater;
import com.dreamer.matholympappv1.utils.SharedPreffUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class ScrollingFragment extends Fragment implements ScrollingFragmentIntf {

    private ActionBarUpdater actionBarUpdater;
    public List listFilesFirestore;
    private ActionBarSetupHelper actionBarSetupHelper;
    static final String ARG_ZADACHA_ID = "MyArgZadacha_id";
    static final String ARG_ZADACHA_LIST_FILES_FIRESTORE = "MyArgZadacha_listFilesFirestore";
    static final String ARG_ZADACHA_LIST_SOLUTION_FILES_FIRESTORE = "MyArgZadacha_listSolutionFilesFirestore";
    static final String ARG_ZADACHA_MAIN_BODY = "MyArgZadacha_main_body";
    static final String ARG_ZADACHA_ANSWER = "MyArgZadacha_answer";
    static final String ARG_ZADACHA_HINT = "MyArgZadacha_hint";
    static final String ARG_ZADACHA_SOLUTION = "MyArgZadacha_solution";
    static final String BASE_IMAGE_URL = "gs://matholymp1.appspot.com";
    static final String BASE_IMAGE_ANSWERIMAGES = "gs://matholymp1.appspot.com/answersimages/";
    static final String BASE_IMAGE_SOLUTIONIMAGES = "gs://matholymp1.appspot.com/solutionimages/";
    static final String SEARCH_ANSWER_IMAGES = "answersimages";
    static final String SEARCH_SOLUTION_IMAGES = "solutionimages";
    static final String TAG = "TAG";
    NavController navController;
    MenuItem menuScroll;
    private @NonNull
    FragmentScrollingBinding binding;

    private List<Zadachi> zadachiList;
    FirebaseStorage storageReference = FirebaseStorage.getInstance();
    public List listSolutionFilesFirestore;
    private Context context;

    private String zadacha_main_body;
    private String zadacha_answer;
    private String zadacha_hint;
    public FirebaseImageLoader firebaseImageLoader;
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
    private String searchimagesPath = "answersimages";
    private String answerImageUrl;
    private String solutionImageUrl;
    private FirebaseUserScoreManager firebaseUserScoreManager;
    private SharedPreffUtils sharedPreffUtils;
    private String searchImagesPath;
    private ImageView iv1;

    private ScrollingFragmentViewModel viewModel;

    public ScrollingFragment() {


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBarSetupHelper = new ActionBarSetupHelper((AppCompatActivity) getActivity());
        firebaseUserScoreManager = new FirebaseUserScoreManager();
        sharedPreffUtils = new SharedPreffUtils(requireContext());
        viewModel = new ViewModelProvider(this).get(ScrollingFragmentViewModel.class);

        variableSetup();

        intNavcontroller();

    }



    private void variableSetup() {
        setHasOptionsMenu(true);
        zadacha_main_body = "";
        zadacha_answer = "";
        zadacha_hint = "";
        zadacha_id = "";
        zadacha_solution = "";
        MainMessage = "";
        searchimagesPath = "";
        userScore = 0;
        answerImageUrl = BASE_IMAGE_ANSWERIMAGES;
        solutionImageUrl = BASE_IMAGE_SOLUTIONIMAGES;
        listFilesFirestore = new ArrayList<>();
        listSolutionFilesFirestore = new ArrayList<>();
    }

    private void getFragmentBundles() {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        actionBarSetupHelper.setupActionBar(inflater, getString(R.string.appbar_title_scroll_fragm), getString(R.string.appbar_score));
        binding = FragmentScrollingBinding.inflate(inflater, container, false);

        FirebaseUserScoreManager.setupFirebaseStorage();
        getBundleArguments();


        return binding.getRoot();


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




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> myList = new ArrayList<String>();

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
        if (answer.trim().isEmpty()) {
            return;
        }
/// не будет реагировать на пустые строчки - без вввода
        if (answer.equals(zadacha_answer)) {
            alertDiaShow(getString(R.string.alertDialogShowUSPEHTitle), getString(R.string.alertDialogShowUSPEHMessageBodySet));
            MyArrayList.addString(zadacha_id);
            //берет данные из шаредпреффс что может быть не совсем хорошо и выдавать не актуальные данные после зачистки программы на девайсе
            int userScore = sharedPreffsLoadUserScore() + 10;
            SharedPreffUtils.sharedPreffsSaveUserScore(userScore);
            FirebaseUserScoreManager.saveUserScore(userScore);
        } else {
            alertDiaShow(getString(R.string.alertDialogShowOSHIBKASetTitle), getString(R.string.alertDialogShowOSHIBKAMessageBodySet));
        }

    }


    private void alertDiaShow(String Title, String MainMessage) {
        if (builder == null) {
            builder = new MaterialAlertDialogBuilder(getActivity(), R.style.MyTheme);
        }

        builder = new MaterialAlertDialogBuilder(getActivity(), R.style.MyTheme);

        LayoutInflater inflater = this.getLayoutInflater();
        View team = inflater.inflate(R.layout.allertdialog_layout, null);

        TextView tw = team.findViewById(R.id.allertMesage1);
        TextView tw1 = team.findViewById(R.id.allertMesage2);// id of your imageView element
        ImageView iv1 = team.findViewById(R.id.imageOtvet1);

        tw1.setVisibility(View.GONE);
        builder.setView(team);

        final String USPEH_TITLE = "USPEH";
        final String HINT_TITLE = "HINT";
        final String SOLUTION_TITLE = "Solution";
        int iconResource = R.drawable.ic_baseline_bubble_chart_24;

        switch (Title) {

            case USPEH_TITLE:
                builder.setView(team);
                tw.setText(MainMessage);
                viewModel.setFirebaseImage(SEARCH_ANSWER_IMAGES, iv1, listFilesFirestore, listSolutionFilesFirestore, zadacha_id, getContext());
                builder
                        .setCancelable(true)
                        .setPositiveButton(R.string.alertDialogUSPEHPositiveButton, (dialog, id) -> {
                            dialog.cancel();
                            navController.navigateUp();
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    "YESSSS", Snackbar.LENGTH_LONG).show();
                        })
//                        .setIcon(R.drawable.ic_baseline_bubble_chart_24)
                        .setIcon(iconResource)
                        .setNegativeButton(R.string.alertDialogUSPEHNegativeButton, (dialog, id) -> {
                            dialog.cancel();
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    "NOOOOOO", Snackbar.LENGTH_LONG).show();
                        });
                break;
            case HINT_TITLE:
                builder.setView(team);
                tw.setText(MainMessage);
                builder
                        .setCancelable(true)
                        .setPositiveButton(R.string.alertDialogHintPositiveButton, (dialog, id) -> dialog.cancel())
                        .setIcon(iconResource)
                        .setNegativeButton(R.string.allertDialogNegativeButton, (dialog, id) -> {
                            dialog.cancel();
                            navController.navigateUp();
                        });
                break;
            case SOLUTION_TITLE:
                builder.setView(team);
                tw.setText(MainMessage);
                viewModel.setFirebaseImage(SEARCH_SOLUTION_IMAGES, iv1, listFilesFirestore, listSolutionFilesFirestore, zadacha_id, getContext());
                builder
                        .setCancelable(true)
                        .setPositiveButton(R.string.alertDialogPositiveButtonSolution, (dialog, id) -> dialog.cancel())
                        .setIcon(iconResource)
                        .setNegativeButton(R.string.allertDialogNegativeButton, (dialog, id) -> {
                            dialog.cancel();
                            navController.navigateUp();
                        });
                break;
            default:
                builder.setView(team);
                tw.setText(MainMessage);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.alertDialogHintButton, (dialog, id) -> {
                    hintLimits = sharedPreffsLoadHintLimits();
                    if (hintLimits == 0) {
                        return;
                    }
                    hintLimits = hintLimits - 1;
                    sharedPreffsSaveHintLimits(hintLimits);
                    FirebaseUserScoreManager.firebaseSaveHintLimits(hintLimits);
                    alertDiaShow(getString(R.string.alertDialogShowTitleHINT), getString(R.string.alertDialogShowMessageBodyHINT) + zadacha_hint);
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "YESSSS",
                            Snackbar.LENGTH_LONG).show();
                });
                builder.setNeutralButton(R.string.alertDialogREZULTATNeutralButton, (dialog, id) -> {
                    solutionLimits = sharedPreffsLoadSolutionLimits();
                    if (solutionLimits == 0) {
                        return;
                    }
                    solutionLimits = solutionLimits - 1;
                    sharedPreffsSaveSolutionLimits(solutionLimits);
                    FirebaseUserScoreManager.firebaseSaveSolutionLimits(solutionLimits);
                    alertDiaShow(getString(R.string.alertDialogRezultatForSolutionTitle), getString(R.string.alertDialogRezultatForSolutionMessageBody) + zadacha_solution);
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "EXITTT", Snackbar.LENGTH_LONG).show();
                });
                builder.setIcon(iconResource);
                builder.setNegativeButton(R.string.alertDialogRezultatNegativeButton, (dialog, id) -> {
                    dialog.cancel();
                    navController.navigateUp();
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "NOOOOOO", Snackbar.LENGTH_LONG).show();
                });
                break;
        }


        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setInverseBackgroundForced(true);

        dialog.show();
//       viewModel.setButtonColors(dialog);

        viewModel.getNeutralButtonColor().observe(getViewLifecycleOwner(), color -> {
            dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(color);
        });

        viewModel.getPositiveButtonColor().observe(getViewLifecycleOwner(), color -> {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(color);
        });

        viewModel.getNegativeButtonColor().observe(getViewLifecycleOwner(), color -> {
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(color);
        });

        viewModel.setButtonColors(true, getContext());


    }


//    private void setButtonColors(AlertDialog dialog) {
//        solutionLimits = sharedPreffsLoadSolutionLimits();
//        hintLimits = sharedPreffsLoadHintLimits();
//
//        Button buttonNeutral = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
//        Button buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
//        Button buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
//
//        buttonNeutral.setTextColor(ContextCompat.getColor(getContext(), solutionLimits == 0 ? android.R.color.darker_gray : android.R.color.holo_green_light));
//        buttonPositive.setTextColor(ContextCompat.getColor(getContext(), hintLimits == 0 ? android.R.color.darker_gray : R.color.green));
//        buttonNegative.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
//    }


//    private void setFirebaseImage(String searchimagesPath, ImageView iv1) {
//        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//        List locallistFiles;
//        if (searchimagesPath == SEARCH_ANSWER_IMAGES) {
//            locallistFiles = listFilesFirestore;
//        } else {
//            locallistFiles = listSolutionFilesFirestore;
//        }
//
//        StorageReference spaceRef = storageRef.child("answersimages/answer" + zadacha_id);
//        spaceRef.getName();
//        spaceRef.getMetadata();
//
//        try {
//            Object splitString = locallistFiles.get(Integer.parseInt(zadacha_id) - 1).toString();
//            String[] parts = ((String) splitString).split(Pattern.quote("/"));
//            String imageLoad = parts[4];
//            String imagePatch = searchimagesPath + "/" + imageLoad;
//
//            storageRef.child(searchimagesPath + "/" + imageLoad).getDownloadUrl().addOnSuccessListener(uri -> {
//                // Download directly from StorageReference using Glide
//                // (See MyAppGlideModule for Loader registration)
//                Glide.with(getContext())
//                        .load(uri)
//                        .into(iv1);
//
//                // Got the download URL for 'users/me/profile.png'
//            }).addOnFailureListener(exception -> {
//                Log.d(TAG, "____DATE= " + "storageRef");
//                // Handle any errors
//            });
//        } catch (Exception e) {
//            Log.e(TAG, "Error getting image URL: " + e.getMessage());
//        }
//    }


    private void intNavcontroller() {
        Activity MainActivity = getActivity();
        assert MainActivity != null;
        navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MyMenuInflater.inflateMenu(menu, requireContext(), navController, zadacha_hint);
    }
//    public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater menuInflater) {
//
//        menuScroll = menu.add(R.string.scrollFrgMenuAddHintTitle);
//        menuScroll.setTitle(R.string.scrollFrgMenuTitle);
//        menuScroll.setTitleCondensed(getString(R.string.scrollFrgMenuCondesedTitle));
//        menuScroll.setOnMenuItemClickListener(v ->
//                {
//                    Snackbar.make(getActivity().findViewById(android.R.id.content),
//                            zadacha_hint, Snackbar.LENGTH_LONG).show();
//                    return true;
//                }
//        );
//        menuScroll = menu.add("exit");
//        menuScroll.setTitle("exit");
//        menuScroll.setTitleCondensed("exit");
//        menuScroll.setOnMenuItemClickListener(v ->
//                {
//                    FirebaseAuth.getInstance().signOut();
//                    navController.clearBackStack(R.id.scrollingfragment);
//                    navController.navigate(R.id.action_scrollingFragment2_to_loginFragment);
////                    onDestroyView();
////                    Snackbar.make(getActivity().findViewById(android.R.id.content),
////                            zadacha_hint, Snackbar.LENGTH_LONG).show();
////                    return true;
//                    return true;
//                }
//        );
//    }

//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//
//        // Add the first menu item
//        MenuItem item1 = menu.add(0, R.id.menu_item_1, 0, "Menu item 1");
//        item1.setIcon(com.google.android.material.R.drawable.abc_ic_star_black_16dp);
//        item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//
//        // Add the second menu item
//        MenuItem item2 = menu.add(0, R.id.menu_item_2, 0, "Menu item 2");
//        item2.setIcon(com.google.android.material.R.drawable.abc_ic_star_black_36dp);
//        item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//    }


}
