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
import com.dreamer.matholympappv1.ui.ui.zadachascreen.ZadachaViewModel;
import com.dreamer.matholympappv1.utils.MyArrayList;
import com.dreamer.matholympappv1.utils.MyMenuInflater;
import com.dreamer.matholympappv1.utils.UserPrefsRepository;
import com.dreamer.matholympappv1.utils.InputValidator;
import com.dreamer.matholympappv1.utils.ScrollingScreenActionBarSetupHelper;
import com.dreamer.matholympappv1.utils.ScrollingScreenActionBarUpdater;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class ScrollingFragment extends Fragment implements ScrollingFragmentIntf {

    private ScrollingScreenActionBarUpdater actionBarUpdater;
    public List listFilesFirestore;
    private ScrollingScreenActionBarSetupHelper actionBarSetupHelper;
    static final String ARG_ZADACHA_ID = "MyArgZadacha_id";
    static final String ARG_ZADACHA_NAME = "MyArgZadacha_name";
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
    static final String TAG = "ScrollingFragment";
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
    private String zadacha_name;
    public ArrayList<String> myList;

    private Integer userScore;
    private String zadacha_id;
    private Integer solutionLimits;
    private Integer hintLimits;
    public AlertDialog.Builder builder;
    private String MainMessage;
    private String searchimagesPath = "answersimages";
    private String answerImageUrl;
    private String solutionImageUrl;
    private FirebaseUserScoreManager firebaseUserScoreManager;
    /** Единое хранилище: единственный путь к локальным данным этого экрана. */
    private UserPrefsRepository prefs;
    private String searchImagesPath;
    private ImageView iv1;

    private ScrollingFragmentViewModel viewModel;
    private ZadachaViewModel viewModelRazdel;
    private String razdelName;

    public ScrollingFragment() {


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBarSetupHelper = new ScrollingScreenActionBarSetupHelper((AppCompatActivity) getActivity());
        firebaseUserScoreManager = new FirebaseUserScoreManager();
        prefs = new UserPrefsRepository(requireContext());
        viewModel = new ViewModelProvider(this).get(ScrollingFragmentViewModel.class);
        viewModelRazdel = new ViewModelProvider(requireActivity()).get(ZadachaViewModel.class);

        variableSetup();

        intNavcontroller();

    }



    private void variableSetup() {
        setHasOptionsMenu(true);
        razdelName = "";
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

        getBundleArguments();
        
        String title = zadacha_id;
        if (zadacha_name != null && !zadacha_name.isEmpty()) {
            title = zadacha_name;
        }
        
        actionBarSetupHelper.setupActionBar(inflater, title, getString(R.string.appbar_score));
        binding = FragmentScrollingBinding.inflate(inflater, container, false);
        
        // Инициализируем actionBarUpdater после установки ActionBar
        actionBarUpdater = new ScrollingScreenActionBarUpdater(
            actionBarSetupHelper.getAppBarTitleTextView(),
            actionBarSetupHelper.getAppBarScoreTextView()
        );

        FirebaseUserScoreManager.setupFirebaseStorage();

        return binding.getRoot();


    }


    private void getBundleArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            razdelName = bundle.getString("MyArgRazdel_id"); //получаем раздел в котором решаем задачи
            zadacha_id = bundle.getString(ARG_ZADACHA_ID);
            zadacha_name = bundle.getString(ARG_ZADACHA_NAME);
            
            // Преобразуем техническое имя "zadacha_1" в красивое "Задача 1"
            if (zadacha_name != null && zadacha_name.startsWith("zadacha_")) {
                String numberPart = zadacha_name.substring("zadacha_".length());
                try {
                    int taskNumber = Integer.parseInt(numberPart);
                    zadacha_name = "Задача " + taskNumber;
                } catch (NumberFormatException e) {
                    // Если не удалось распарсить число, оставляем как есть
                }
            }
            
            listFilesFirestore = bundle.getStringArrayList(ARG_ZADACHA_LIST_FILES_FIRESTORE);
            listSolutionFilesFirestore = bundle.getStringArrayList(ARG_ZADACHA_LIST_SOLUTION_FILES_FIRESTORE);
            zadacha_main_body = bundle.getString(ARG_ZADACHA_MAIN_BODY);
            zadacha_answer = bundle.getString(ARG_ZADACHA_ANSWER);
            zadacha_hint = bundle.getString(ARG_ZADACHA_HINT);
            zadacha_solution = bundle.getString(ARG_ZADACHA_SOLUTION);

            Log.e(TAG, "=== getBundleArguments ===");
            Log.e(TAG, "zadacha_id: " + zadacha_id);
            Log.e(TAG, "zadacha_name: " + zadacha_name);
            Log.e(TAG, "razdelName: " + razdelName);
            Log.e(TAG, "========================");
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
        
        // Санитизация ответа перед проверкой (разрешены только цифры)
        // String sanitizedAnswer = InputValidator.validateAndSanitizeAnswer(answer);
        String sanitizedAnswer = answer.trim();
        // if (sanitizedAnswer == null) {
        //     // Если ответ не прошел валидацию, показываем ошибку
        //     alertDiaShow(getString(R.string.alertDialogShowOSHIBKASetTitle), "Неверный формат ответа. Введите только цифры.");
        //     return;
        // }
        
/// не будет реагировать на пустые строчки - без вввода
        if (sanitizedAnswer.equals(zadacha_answer)) {
            alertDiaShow(getString(R.string.alertDialogShowUSPEHTitle), getString(R.string.alertDialogShowUSPEHMessageBodySet));
            viewModelRazdel.getsubRazdel().observe(getViewLifecycleOwner(), value -> {
                if (value != null) {
                    String razdelName1 = value;
                    // Используйте значение value
                    Log.e(TAG, "Solution razdelname3: " + razdelName + razdelName1);
                }
            }); // Передача данных from вью модель через LiveData

            MyArrayList.addString(zadacha_id, razdelName);
            // Счёт читается из единого хранилища: один ключ user_score,
            // никаких параллельных "zadacha_score"
            int userScore = prefs.loadUserScore() + 10;
            prefs.saveUserScore(userScore);
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
        ImageView iv2 = team.findViewById(R.id.imageTopofDialog);

        tw1.setVisibility(View.GONE);
        builder.setView(team);

        final String USPEH_TITLE = "USPEH";
        final String HINT_TITLE = "HINT";
        final String SOLUTION_TITLE = "Solution";
        int iconResource = R.drawable.ic_baseline_bubble_chart_24;
        iv2.setBackgroundResource(iconResource);

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
                    hintLimits = prefs.loadHintLimits();
                    if (hintLimits == 0) {
                        return;
                    }
                    hintLimits = hintLimits - 1;
                    // Расходование подсказки — действие пользователя, оно авторитетно:
                    // пишем и в локальный кэш, и в Firebase (один источник истины)
                    prefs.saveHintLimits(hintLimits);
                    FirebaseUserScoreManager.firebaseSaveHintLimits(hintLimits);
                    alertDiaShow(getString(R.string.alertDialogShowTitleHINT), getString(R.string.alertDialogShowMessageBodyHINT) + zadacha_hint);
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "YESSSS",
                            Snackbar.LENGTH_LONG).show();
                });
                builder.setNeutralButton(R.string.alertDialogREZULTATNeutralButton, (dialog, id) -> {
                    solutionLimits = prefs.loadSolutionLimits();
                    if (solutionLimits == 0) {
                        return;
                    }
                    solutionLimits = solutionLimits - 1;
                    // Расходование попытки — действие пользователя: пишем и в кэш,
                    // и в Firebase, значения не расходятся
                    prefs.saveSolutionLimits(solutionLimits);
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

        viewModel.setButtonColors(true, getContext(), prefs.loadSolutionLimits(), prefs.loadHintLimits());


    }


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
}
