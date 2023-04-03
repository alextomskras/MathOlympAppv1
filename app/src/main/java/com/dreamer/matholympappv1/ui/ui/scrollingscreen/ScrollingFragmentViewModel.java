package com.dreamer.matholympappv1.ui.ui.scrollingscreen;

import static com.dreamer.matholympappv1.ui.ui.scrollingscreen.ScrollingFragment.SEARCH_ANSWER_IMAGES;
import static com.dreamer.matholympappv1.ui.ui.scrollingscreen.ScrollingFragment.TAG;
import static com.dreamer.matholympappv1.utils.SharedPreffUtils.sharedPreffsLoadHintLimits;
import static com.dreamer.matholympappv1.utils.SharedPreffUtils.sharedPreffsLoadSolutionLimits;
import static com.dreamer.matholympappv1.utils.SharedPreffUtils.sharedPreffsLoadUserScore;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.dreamer.matholympappv1.utils.MyArrayList;
import com.dreamer.matholympappv1.utils.SharedPreffUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ScrollingFragmentViewModel extends ViewModel {
    public ArrayList myList;
    private Context context;
    private MutableLiveData<String> zadachaMainBody = new MutableLiveData<>();
    private MutableLiveData<String> zadachaAnswer = new MutableLiveData<>();
    private MutableLiveData<String> zadachaHint = new MutableLiveData<>();
    private MutableLiveData<String> zadachaSolution = new MutableLiveData<>();

    private MutableLiveData<Integer> neutralButtonColor = new MutableLiveData<>();
    private MutableLiveData<Integer> positiveButtonColor = new MutableLiveData<>();
    private MutableLiveData<Integer> negativeButtonColor = new MutableLiveData<>();
    private int solutionLimits;
    private int hintLimits;
    private boolean isDialogShown;

    public LiveData<Integer> getNeutralButtonColor() {
        return neutralButtonColor;
    }

    public LiveData<Integer> getPositiveButtonColor() {
        return positiveButtonColor;
    }

    public LiveData<Integer> getNegativeButtonColor() {
        return negativeButtonColor;
    }

    public ScrollingFragmentViewModel() {

    }

    public LiveData<String> getZadachaMainBody() {
        return zadachaMainBody;
    }

    public void setZadachaMainBody(String mainBody) {
        zadachaMainBody.setValue(mainBody);
    }

    public LiveData<String> getZadachaAnswer() {
        return zadachaAnswer;
    }

    public void setZadachaAnswer(String answer) {
        zadachaAnswer.setValue(answer);
    }

    public LiveData<String> getZadachaHint() {
        return zadachaHint;
    }

    public void setZadachaHint(String hint) {
        zadachaHint.setValue(hint);
    }

    public LiveData<String> getZadachaSolution() {
        return zadachaSolution;
    }

    public void setZadachaSolution(String solution) {
        zadachaSolution.setValue(solution);
    }


    public boolean checkAnswer(String answer, String zadacha_answer, String zadacha_id) {
        if (answer.isEmpty()) {
            return false;
        }

        if (answer.equals(zadacha_answer)) {
            // Add a string to the list
            MyArrayList.addString(zadacha_id);

            // Update the user score
            int userScore = sharedPreffsLoadUserScore() + 10;
            SharedPreffUtils.sharedPreffsSaveUserScore(userScore);
            FirebaseUserScoreManager.saveUserScore(userScore);

            return true;
        } else {
            return false;
        }
    }


    public void setFirebaseImage(String searchimagesPath, ImageView iv1, List listFilesFirestore, List listSolutionFilesFirestore, String zadacha_id, Context context) {
        this.context = context;
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        List locallistFiles;
        if (Objects.equals(searchimagesPath, SEARCH_ANSWER_IMAGES)) {

            locallistFiles = listFilesFirestore;
        } else {

            locallistFiles = listSolutionFilesFirestore;
        }

        StorageReference spaceRef = storageRef.child("answersimages/answer" + zadacha_id);
        spaceRef.getName();
        spaceRef.getMetadata();

        try {
            Object splitString = locallistFiles.get(Integer.parseInt(zadacha_id) - 1).toString();
            String[] parts = ((String) splitString).split(Pattern.quote("/"));
            String imageLoad = parts[4];
            String imagePatch = searchimagesPath + "/" + imageLoad;

            storageRef.child(searchimagesPath + "/" + imageLoad).getDownloadUrl().addOnSuccessListener(uri -> {
                // Download directly from StorageReference using Glide
                // (See MyAppGlideModule for Loader registration)
                Glide.with(context)
                        .load(uri)
                        .into(iv1);

                // Got the download URL for 'users/me/profile.png'
            }).addOnFailureListener(exception -> {
                Log.d(TAG, "____DATE= " + "storageRef");
                // Handle any errors
            });
        } catch (Exception e) {
            Log.e(TAG, "Error getting image URL: " + e.getMessage());
        }
    }

    public void setButtonColors(boolean isDialogShown, Context context) {
        this.solutionLimits = solutionLimits;
        this.hintLimits = hintLimits;
        this.isDialogShown = isDialogShown;
        this.context = context;
        solutionLimits = sharedPreffsLoadSolutionLimits();
        hintLimits = sharedPreffsLoadHintLimits();
        int neutralColor = ContextCompat.getColor(context, solutionLimits == 0 ? android.R.color.darker_gray : android.R.color.holo_green_light);
        int positiveColor = ContextCompat.getColor(context, hintLimits == 0 ? android.R.color.darker_gray : android.R.color.holo_green_dark);
        int negativeColor = ContextCompat.getColor(context, android.R.color.holo_red_dark);

        // Use MutableLiveData to update the button colors in the UI
        neutralButtonColor.setValue(isDialogShown ? neutralColor : neutralColor & 0x55FFFFFF);
        positiveButtonColor.setValue(isDialogShown ? positiveColor : positiveColor & 0x55FFFFFF);
        negativeButtonColor.setValue(negativeColor);
    }
}
