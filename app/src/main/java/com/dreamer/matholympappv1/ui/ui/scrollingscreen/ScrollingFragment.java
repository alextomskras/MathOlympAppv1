package com.dreamer.matholympappv1.ui.ui.scrollingscreen;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.dreamer.matholympappv1.R;
import com.dreamer.matholympappv1.data.model.model.Zadachi;
import com.dreamer.matholympappv1.databinding.FragmentScrollingBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class ScrollingFragment extends Fragment {

    static final String TAG = "TAG";
    NavController navController;
    MenuItem menuScroll;
    private @NonNull
    FragmentScrollingBinding binding;
    private List<Zadachi> zadachiList;
    private Context context;
    //    private String mainBodyZadacha;
    private String zadacha_main_body;
    private String zadacha_answer;
    private String zadacha_hint;
    private String zadacha_solution;
    public AlertDialog.Builder builder;
    private String MainMessage;

    public ScrollingFragment() {
//        this.zadachiList = zadachiList;
//        this.context = context;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //        zadachiList = (List<Zadachi>) zadachiList.get(1);
        zadacha_main_body = "";
        zadacha_answer = "";
        zadacha_hint = "";
        zadacha_solution = "";
        MainMessage = "";
        ;
        intNavcontroller();
//        getFragmentBundles();
    }

    private void getFragmentBundles() {

    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//
//            case R.drawable.ic_baseline_alt_route_24:
//                Log.e(TAG, "iconImageViewOnClick at position9 " + item);
////                onBackPressed();
//                Snackbar.make(getActivity().findViewById(android.R.id.content),
//                        item.toString(), Snackbar.LENGTH_LONG).show();
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if(item.getItemId() == R.drawable.ic_baseline_alt_route_24) {
////            Intent intent = new Intent(this, SearchUsersActivity.class);
////            startActivity(intent);
//                            Log.e(TAG, "iconImageViewOnClick at position9 " + item);
////                onBackPressed();
//                Snackbar.make(getActivity().findViewById(android.R.id.content),
//                        item.toString(), Snackbar.LENGTH_LONG).show();
//        }
//        return true;
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {

            zadacha_main_body = bundle.getString("MyArgZadacha_main_body");
            zadacha_answer = bundle.getString("MyArgZadacha_answer");
            zadacha_hint = bundle.getString("MyArgZadacha_hint");
            zadacha_solution = bundle.getString("MyArgZadacha_solution");
            Log.e(TAG, "iconImageViewOnClick at position8 " + bundle.getString("MyArgZadacha_main_body", "_PlaceHolder_"));
        }

        binding = FragmentScrollingBinding.inflate(inflater, container, false);
        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_scrolling, container, false);


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.scroll_frag_menu, menu);
//        return true;
//    }

    public void ScrollingFragmentZdachiList(List<Zadachi> zadachiList, Context context) {
        this.zadachiList = zadachiList;
        this.context = context;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
//        answerTextView.setText(zadacha_answer);
//        hintTextView.setText("HINT:" + zadacha_hint);
//        solutionTextView.setText("SOLUTION:" + zadacha_solution);
//        edtxtAnswer.getHint();
//        edtxtAnswer.getText().toString();
        Log.e(TAG, "iconImageViewOnClick at position9 " + edtxtAnswer);
        btnVerify.setOnClickListener(view1 -> {
            String textEnter = edtxtAnswer.getText().toString();
            checkAnswer(textEnter.toString());
//            Snackbar.make(getActivity().findViewById(android.R.id.content),
//                    "task.getResult().getUser().getEmail()", Snackbar.LENGTH_LONG).show();
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
//                checkAnswer(textEnter.toString());
//                registerViewModel.loginDataChanged(usernameEditText.getText().toString(),
//                        passwordEditText.getText().toString());
            }
        };

        edtxtAnswer.addTextChangedListener(afterTextChangedListener);
        edtxtAnswer.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String textEnter = edtxtAnswer.getText().toString();
                    checkAnswer(textEnter.toString());
//                    registerViewModel.login(usernameEditText.getText().toString(),
//                            passwordEditText.getText().toString());
//                    Snackbar.make(getActivity().findViewById(android.R.id.content),
//                            "task.getResult().getUser().getEmail()", Snackbar.LENGTH_LONG).show();
                }
                return false;
            }
        });


    }

    private void checkAnswer(String answer) {


//        binding.content.saveButton.setOnClickListener(v -> {
        String etTexttitle = answer.toString();
        Log.d(TAG, "viewId: " + etTexttitle);
        if (!etTexttitle.equals("") && etTexttitle.equals(zadacha_answer)) {

//                snackbar[0] = Snackbar
//                        .make(constraintLayout, "2222.journaldev.com", Snackbar.LENGTH_LONG);
//                snackbar[0].show();
            alertDiaShow("USPEH", "TAK-TAKKKKKKKKKK");
        } else {
            alertDiaShow("OSHIBKA", "VOTT-TAK-TAKKKKKKKKKK");
//                snackbar[0] = Snackbar.make(constraintLayout, R.string.no_title, Snackbar.LENGTH_LONG);
//                snackbar[0].show();

        }
    }


//        Snackbar.make(getActivity().findViewById(android.R.id.content),
//                answer, Snackbar.LENGTH_LONG).show();


    private void alertDiaShow(String Title, String MainMessage) {
        builder = new AlertDialog.Builder(getActivity());
//        TextInputEditText etTitle = (TextInputEditText) findViewById(R.id.event_title_user_input_et);
//        TextInputEditText etTime = (TextInputEditText) findViewById(R.id.event_time_user_input_et);
//        TextInputEditText etNote = (TextInputEditText) findViewById(R.id.event_notes_user_input_et);
//        String date = getCurrentLocalDateTimeStamp();
//        Log.d(TAG,"____DATE= "+date);
        if (!Title.equals("") && Title.equals("USPEH")) {
            builder
//                .setMessage(getString(R.string.alertEvent_title)+etTitle.getText()+"\n"+getString(R.string.alertEvent_date)+date+"\n"+getString(R.string.alertEvent_time)+etTime.getText()+"\n"+getString(R.string.alertEvent_notes)+etNote.getText()+"\n")
                    .setMessage(MainMessage)
//                .setMessage(R.string.alertEvent_date)
//                .setMessage(R.string.alertEvent_time)
//                .setMessage(R.string.alertEvent_notes)
                    .setTitle("REZULTAT " + Title)
                    .setCancelable(true)
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
//                        finish();
//                        getActivity().getFragmentManager().popBackStack();
                            dialog.cancel();
                            navController.navigateUp();
//                            alertDiaShow("HINT", "zadacha_hint " + zadacha_hint);
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    "YESSSS", Snackbar.LENGTH_LONG).show();
                        }
                    })
//                    .setNeutralButton("exit", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    //  Action for 'NO' Button
//
//                                    dialog.cancel();
//                                    navController.navigateUp();
//                                    Snackbar.make(getActivity().findViewById(android.R.id.content),
//                                            "EXITTT", Snackbar.LENGTH_LONG).show();
//                                }
//                            }
//                    )

                    .setIcon(R.drawable.ic_baseline_bubble_chart_24)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
//                        navController.navigateUp();
                            dialog.cancel();
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    "NOOOOOO", Snackbar.LENGTH_LONG).show();
                        }
                    });
        } else {
            builder
//                .setMessage(getString(R.string.alertEvent_title)+etTitle.getText()+"\n"+getString(R.string.alertEvent_date)+date+"\n"+getString(R.string.alertEvent_time)+etTime.getText()+"\n"+getString(R.string.alertEvent_notes)+etNote.getText()+"\n")
                    .setMessage(MainMessage)
//                .setMessage(R.string.alertEvent_date)
//                .setMessage(R.string.alertEvent_time)
//                .setMessage(R.string.alertEvent_notes)
                    .setTitle("REZULTAT " + Title)
                    .setCancelable(true)
                    .setPositiveButton("Hint", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
//                        finish();
//                        getActivity().getFragmentManager().popBackStack();
//                        navController.navigateUp();
                            alertDiaShow("HINT", "zadacha_hint " + zadacha_hint);
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    "YESSSS", Snackbar.LENGTH_LONG).show();
                        }
                    })
                    .setNeutralButton("exit", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button

                                    dialog.cancel();
                                    navController.navigateUp();
                                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                                            "EXITTT", Snackbar.LENGTH_LONG).show();
                                }
                            }
                    )

                    .setIcon(R.drawable.ic_baseline_bubble_chart_24)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
//                        navController.navigateUp();
                            dialog.cancel();
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    "NOOOOOO", Snackbar.LENGTH_LONG).show();
                        }
                    });

        }

//        Creating dialog box
//        AlertDialog alert = builder.create();
//        //Setting the title manually
////        alert.setTitle("AlertDialogExample");
//        alert.show();
        builder.show();
//    }
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
//        menuInflater.inflate(R.menu.scroll_frag_menu, menu);
//        getFragmentBundles();
        menuScroll = menu.add("hint");
        menuScroll.setTitle("hint");
//        menuScroll.setTitle(zadacha_hint);
        menuScroll.setTitleCondensed("hint");
//        menuScroll.setIcon(R.drawable.ic_baseline_alt_route_24);
        menuScroll.setOnMenuItemClickListener(v ->
                {
//                    getFragmentBundles();
//                    alertDiaShow("HINT","zadacha_hint");
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            zadacha_hint.toString(), Snackbar.LENGTH_LONG).show();
                    return true;
                }
        );
    }
}
