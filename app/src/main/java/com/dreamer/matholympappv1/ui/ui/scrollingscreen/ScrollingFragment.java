package com.dreamer.matholympappv1.ui.ui.scrollingscreen;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.dreamer.matholympappv1.R;
import com.dreamer.matholympappv1.data.model.model.Zadachi;
import com.dreamer.matholympappv1.databinding.FragmentScrollingBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ScrollingFragment extends Fragment {

    static final String TAG = "TAG";
    NavController navController;
    MenuItem menuScroll;
    private @NonNull
    FragmentScrollingBinding binding;
    private List<Zadachi> zadachiList;
    FirebaseStorage storageReference = FirebaseStorage.getInstance();
    private List listFilesFirestore;
    private Context context;
    //    private String mainBodyZadacha;
    private String zadacha_main_body;
    private String zadacha_answer;
    private String zadacha_hint;
    private List listSolutionFilesFirestore;
    private String zadacha_solution;
    private String zadacha_id;
    public AlertDialog.Builder builder;
    private String MainMessage;
    private String searchimagesPath;
    private String answerImageUrl;
    private String solutionImageUrl;

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
        zadacha_id = "";
        zadacha_solution = "";
        MainMessage = "";
        searchimagesPath = "";
        answerImageUrl = "gs://matholymp1.appspot.com/answersimages/";
        solutionImageUrl = "gs://matholymp1.appspot.com/solutionimages/";
        listFilesFirestore = new ArrayList<>();
        listSolutionFilesFirestore = new ArrayList<>();

        intNavcontroller();
//        listAllFilesDirestore();

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
            zadacha_id = bundle.getString("MyArgZadacha_id");
            listFilesFirestore = bundle.getStringArrayList("MyArgZadacha_listFilesFirestore");
            listSolutionFilesFirestore = bundle.getStringArrayList("MyArgZadacha_listSolutionFilesFirestore");
            zadacha_main_body = bundle.getString("MyArgZadacha_main_body");
            zadacha_answer = bundle.getString("MyArgZadacha_answer");
            zadacha_hint = bundle.getString("MyArgZadacha_hint");
            zadacha_solution = bundle.getString("MyArgZadacha_solution");
            Log.e(TAG, "iconImageViewOnClick at position8 " + bundle.getString("MyArgZadacha_main_body", "_PlaceHolder_"));
            Log.e(TAG, "iconImageViewOnClick at position18 " + bundle.getString("MyArgZadacha_id", "_PlaceHolder_"));
            Log.e(TAG, "iconImageViewOnClick at position19 " + listFilesFirestore);
            Log.e(TAG, "iconImageViewOnClick at position29 " + listSolutionFilesFirestore);
        }
//        final firebase_storage.FirebaseStorage storage =
//                firebase_storage.FirebaseStorage.instance;
//        String url = await storage
//                .ref('YourImagePath')
//                .getDownloadURL();
        binding = FragmentScrollingBinding.inflate(inflater, container, false);
        storageReference = FirebaseStorage.getInstance("gs://matholymp1.appspot.com");
        StorageReference storageRef = storageReference.getReference();
        // Create a child reference
// imagesRef now points to "images"
//        StorageReference imagesRef = storageRef.child("answersimages");
        // Child references can also take paths
// spaceRef now points to "images/space.jpg
// imagesRef still points to "images"
//        StorageReference spaceRef = storageRef.child("answersimages/answer1.jpg");
//        answerImageUrl = String.valueOf(spaceRef);
//        Log.d(TAG,"____DATE= "+answerImageUrl);
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
        answerTextView.setVisibility(View.GONE);
        hintTextView.setVisibility(View.GONE);
        solutionTextView.setVisibility(View.GONE);
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
            alertDiaShow(getString(R.string.alertDialogShowUSPEHTitle), getString(R.string.alertDialogShowUSPEHMessageBodySet));
        } else {
            alertDiaShow(getString(R.string.alertDialogShowOSHIBKASetTitle), getString(R.string.alertDialogShowOSHIBKAMessageBodySet));
//                snackbar[0] = Snackbar.make(constraintLayout, R.string.no_title, Snackbar.LENGTH_LONG);
//                snackbar[0].show();

        }
    }


//        Snackbar.make(getActivity().findViewById(android.R.id.content),
//                answer, Snackbar.LENGTH_LONG).show();


    private void alertDiaShow(String Title, String MainMessage) {
        builder = new MaterialAlertDialogBuilder(getActivity(), R.style.MyTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        View team = inflater.inflate(R.layout.allertdialog_layout, null);
        TextView tw = (TextView) team.findViewById(R.id.allertMesage1);
        TextView tw1 = (TextView) team.findViewById(R.id.allertMesage2);// id of your imageView element
        ImageView iv1 = (ImageView) team.findViewById(R.id.imageOtvet1);
        tw1.setVisibility(View.GONE);
        builder.setView(team);
//        TextInputEditText etTitle = (TextInputEditText) findViewById(R.id.event_title_user_input_et);
//        TextInputEditText etTime = (TextInputEditText) findViewById(R.id.event_time_user_input_et);
//        TextInputEditText etNote = (TextInputEditText) findViewById(R.id.event_notes_user_input_et);
//        String date = getCurrentLocalDateTimeStamp();
//        Log.d(TAG,"____DATE= "+date);
//        builder
        if (!Title.equals("") && Title.equals(getString(R.string.alertDialogUSPEHTitleCheck))) {
            builder.setView(team);
            tw.setText(MainMessage);
//            iv1.setImageResource(R.drawable.dudlejump);
            searchimagesPath = "answersimages";
//            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//            Log.d(TAG, "____DATE=1 " + storageRef);
//            Log.d(TAG, "____DATE=11 " + searchimagesPath);
//            // Reference's name is the last segment of the full path: "space.jpg"
//// This is analogous to the file name
////            storageRef.getName();
//
//            Log.d(TAG, "____DATE4= " + storageRef);
//            StorageReference spaceRef = storageRef.child("answersimages/answer" + zadacha_id);
//            spaceRef.getName();
//            Log.d(TAG, "____DATE3= " + zadacha_id);
//            spaceRef.getMetadata();
//            Log.d(TAG, "____DATE=2 " + spaceRef);
//            Object splitString = listFilesFirestore.get(Integer.parseInt(zadacha_id) - 1).toString();
//            Log.d(TAG, "____DATE=21 " + splitString);
//            String[] parts = ((String) splitString).split(Pattern.quote("/"));
//            Log.d(TAG, "____DATE=5 " + parts[4]);
//            String imageLoad = parts[4];
//            String imagePatch = searchimagesPath + "/" + imageLoad;
//            Log.d(TAG, "____DATE=51 " + imagePatch);
////        storageRef.child(searchimagesPath + "/" + imageLoad).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            storageRef.child("answersimages/" + imageLoad).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                //            spaceRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    Log.d(TAG, "____DATE=12 " + uri);
//                    // Download directly from StorageReference using Glide
//// (See MyAppGlideModule for Loader registration)
//                    Glide.with(getContext())
//                            .load(uri)
//                            .into(iv1);
//
//                    // Got the download URL for 'users/me/profile.png'
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    Log.d(TAG, "____DATE= " + "storageRef");
//                    // Handle any errors
//                }
//            });
            setFirebaseImage(searchimagesPath, iv1);

//            Glide.with(this)
//                    .load("https://firebasestorage.googleapis.com/v0/b/matholymp1.appspot.com/o/answersimages%2Fanswer1.jpg?alt=media&token=1b22c5ef-6469-4d2a-aa6c-59d13c9c87bd")
//                    .into(iv1);
// File path is "images/space.jpg"
//            String path = spaceRef.getPath();
//            Snackbar.make(getActivity().findViewById(android.R.id.content),
//                    answerImageUrl, Snackbar.LENGTH_LONG).show();
//            Picasso.get().load(answerImageUrl).into(iv1);
            builder

//                .setMessage(getString(R.string.alertEvent_title)+etTitle.getText()+"\n"+getString(R.string.alertEvent_date)+date+"\n"+getString(R.string.alertEvent_time)+etTime.getText()+"\n"+getString(R.string.alertEvent_notes)+etNote.getText()+"\n")
//                    .setMessage(MainMessage)
//                .setMessage(R.string.alertEvent_date)
//                .setMessage(R.string.alertEvent_time)
//                .setMessage(R.string.alertEvent_notes)
//                    .setTitle(getString(R.string.alertDialogUSPEHSetTitle) + Title)
                    .setCancelable(true)


                    .setPositiveButton(R.string.alertDialogUSPEHPositiveButton, new DialogInterface.OnClickListener() {
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
                    .setNegativeButton(R.string.alertDialogUSPEHNegativeButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
//                        navController.navigateUp();
                            dialog.cancel();
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    "NOOOOOO", Snackbar.LENGTH_LONG).show();
                        }
                    });
        } else if (!Title.equals("") && Title.equals(getString(R.string.alertDialogTitleHintCheck))) {
            builder.setView(team);
            tw.setText(MainMessage);
//            iv1.setImageResource(R.drawable.forti1800f);
            builder
//                .setMessage(getString(R.string.alertEvent_title)+etTitle.getText()+"\n"+getString(R.string.alertEvent_date)+date+"\n"+getString(R.string.alertEvent_time)+etTime.getText()+"\n"+getString(R.string.alertEvent_notes)+etNote.getText()+"\n")
//                    .setMessage(MainMessage)
//                .setMessage(R.string.alertEvent_date)
//                .setMessage(R.string.alertEvent_time)
//                .setMessage(R.string.alertEvent_notes)

//                    .setTitle(getString(R.string.alertDialogHintSetTitle) + Title)
                    .setCancelable(true)
                    .setPositiveButton(R.string.alertDialogHintPositiveButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
//                        finish();
//                        getActivity().getFragmentManager().popBackStack();
//                        navController.navigateUp();
                            dialog.cancel();
//                            alertDiaShow("HINT", "zadacha_hint " + zadacha_hint);
//                            Snackbar.make(getActivity().findViewById(android.R.id.content),
//                                    "YESSSS", Snackbar.LENGTH_LONG).show();
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
                    .setNegativeButton(R.string.allertDialogNegativeButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
//                        navController.navigateUp();
                            dialog.cancel();
                            navController.navigateUp();
//                            Snackbar.make(getActivity().findViewById(android.R.id.content),
//                                    "NOOOOOO", Snackbar.LENGTH_LONG).show();
                        }
                    });

        } else if (!Title.equals("") && Title.equals(getString(R.string.alertDialogTitleSolution))) {
            builder.setView(team);
            tw.setText(MainMessage);

            searchimagesPath = "solutionimages";
            setFirebaseImage(searchimagesPath, iv1);
            builder
//                .setMessage(getString(R.string.alertEvent_title)+etTitle.getText()+"\n"+getString(R.string.alertEvent_date)+date+"\n"+getString(R.string.alertEvent_time)+etTime.getText()+"\n"+getString(R.string.alertEvent_notes)+etNote.getText()+"\n")
//                    .setMessage(MainMessage)
//                .setMessage(R.string.alertEvent_date)
//                .setMessage(R.string.alertEvent_time)
//                .setMessage(R.string.alertEvent_notes)
//                    .setTitle("Title " + Title)

                    .setCancelable(true)
                    .setPositiveButton(R.string.alertDialogPositiveButtonSolution, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
//                        finish();
//                        getActivity().getFragmentManager().popBackStack();
//                        navController.navigateUp();
                            dialog.cancel();
//                            alertDiaShow("HINT", "zadacha_hint " + zadacha_hint);
//                            Snackbar.make(getActivity().findViewById(android.R.id.content),
//                                    "YESSSS", Snackbar.LENGTH_LONG).show();
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
                    .setNegativeButton(R.string.allertDialogNegativeButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
//                        navController.navigateUp();
                            dialog.cancel();
                            navController.navigateUp();
//                            Snackbar.make(getActivity().findViewById(android.R.id.content),
//                                    "NOOOOOO", Snackbar.LENGTH_LONG).show();
                        }
                    });

        } else {
            builder.setView(team);
            tw.setText(MainMessage);
            builder
//                .setMessage(getString(R.string.alertEvent_title)+etTitle.getText()+"\n"+getString(R.string.alertEvent_date)+date+"\n"+getString(R.string.alertEvent_time)+etTime.getText()+"\n"+getString(R.string.alertEvent_notes)+etNote.getText()+"\n")
//                    .setMessage(MainMessage)
//                .setMessage(R.string.alertEvent_date)
//                .setMessage(R.string.alertEvent_time)
//                .setMessage(R.string.alertEvent_notes)
//                    .setTitle(getString(R.string.alertDialogTitleForREZULTAT) + Title)
                    .setCancelable(true)
                    .setPositiveButton(R.string.alertDialogHintButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
//                        finish();
//                        getActivity().getFragmentManager().popBackStack();
//                        navController.navigateUp();
                            alertDiaShow(getString(R.string.alertDialogShowTitleHINT), getString(R.string.alertDialogShowMessageBodyHINT) + zadacha_hint);
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    "YESSSS", Snackbar.LENGTH_LONG).show();
                        }
                    })
                    .setNeutralButton(R.string.alertDialogREZULTATNeutralButton, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    alertDiaShow(getString(R.string.alertDialogRezultatForSolutionTitle), getString(R.string.alertDialogRezultatForSolutionMessageBody) + zadacha_solution);
//                                    dialog.cancel();
//                                    navController.navigateUp();
                                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                                            "EXITTT", Snackbar.LENGTH_LONG).show();
                                }
                            }
                    )

                    .setIcon(R.drawable.ic_baseline_bubble_chart_24)
                    .setNegativeButton(R.string.alertDialogRezultatNegativeButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
//                        navController.navigateUp();
                            dialog.cancel();
                            navController.navigateUp();
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
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setInverseBackgroundForced(true);

        dialog.show();

        Button buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonPositive.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
        Button buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonNegative.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
//    }
    }

    private void setFirebaseImage(String searchimagesPath, ImageView iv1) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        List locallistFiles;
        if (searchimagesPath == "answersimages") {
            locallistFiles = listFilesFirestore;
        } else {
            locallistFiles = listSolutionFilesFirestore;
        }
        Log.d(TAG, "____DATE=1 " + storageRef);
        Log.d(TAG, "____DATE=11 " + searchimagesPath);
        // Reference's name is the last segment of the full path: "space.jpg"
// This is analogous to the file name
//            storageRef.getName();

        Log.d(TAG, "____DATE4= " + storageRef);
        StorageReference spaceRef = storageRef.child("answersimages/answer" + zadacha_id);
        spaceRef.getName();
        Log.d(TAG, "____DATE3= " + zadacha_id);
        spaceRef.getMetadata();
        Log.d(TAG, "____DATE=2 " + spaceRef);


        Object splitString = locallistFiles.get(Integer.parseInt(zadacha_id) - 1).toString();
        Log.d(TAG, "____DATE=21 " + splitString);
        String[] parts = ((String) splitString).split(Pattern.quote("/"));
        Log.d(TAG, "____DATE=5 " + parts[4]);
        String imageLoad = parts[4];
        String imagePatch = searchimagesPath + "/" + imageLoad;
        Log.d(TAG, "____DATE=51 " + imagePatch);

        storageRef.child(searchimagesPath + "/" + imageLoad).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            //        storageRef.child("answerimages/" + imageLoad).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            //            spaceRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "____DATE=12 " + uri);
                // Download directly from StorageReference using Glide
// (See MyAppGlideModule for Loader registration)
                Glide.with(getContext())
                        .load(uri)
                        .into(iv1);

                // Got the download URL for 'users/me/profile.png'
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "____DATE= " + "storageRef");
                // Handle any errors
            }
        });
    }

    private void intNavcontroller() {
        Activity MainActivity = getActivity();
        assert MainActivity != null;
        navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
    }

//    private void listAllFilesDirestore(){
//        storageReference = FirebaseStorage.getInstance("gs://matholymp1.appspot.com");
//        StorageReference listRef = storageReference.getReference().child("answersimages");
//
//        listRef.listAll()
//                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
//                    @Override
//                    public void onSuccess(ListResult listResult) {
//                        for (StorageReference prefix : listResult.getPrefixes()) {
//                            // All the prefixes under listRef.
//                            // You may call listAll() recursively on them.
//
//                            Log.d(TAG,"____DATEprefix= "+prefix);
//                        }
//
//                        for (StorageReference item : listResult.getItems()) {
//                            // All the items under listRef.
//                            Log.e(TAG,"____DATEitem= "+item);
//                            listFilesFirestore.add(item);
//                            Log.e(TAG,"____DATEitem= "+listFilesFirestore);
//                            int listSize = listFilesFirestore.size();
//                            Log.e(TAG,"____DATEitem2= "+listSize);
//
//
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Uh-oh, an error occurred!
//                    }
//                });
//        if (listFilesFirestore.size()!=0)  {
//            Object stringFileName = listFilesFirestore.get(1);
//            Log.e(TAG,"____DATEitem1= "+stringFileName);
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
//        menuInflater.inflate(R.menu.scroll_frag_menu, menu);
//        getFragmentBundles();
        menuScroll = menu.add(R.string.scrollFrgMenuAddHintTitle);
        menuScroll.setTitle(R.string.scrollFrgMenuTitle);
//        menuScroll.setTitle(zadacha_hint);
        menuScroll.setTitleCondensed(getString(R.string.scrollFrgMenuCondesedTitle));
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
