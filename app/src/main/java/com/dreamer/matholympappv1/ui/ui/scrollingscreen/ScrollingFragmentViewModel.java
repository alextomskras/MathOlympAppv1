package com.dreamer.matholympappv1.ui.ui.scrollingscreen;

import static com.dreamer.matholympappv1.ui.ui.scrollingscreen.ScrollingFragment.SEARCH_ANSWER_IMAGES;
import static com.dreamer.matholympappv1.ui.ui.scrollingscreen.ScrollingFragment.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import coil.Coil;
import coil.request.ImageRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ScrollingFragmentViewModel extends ViewModel {
    public ArrayList myList;

    /**
     * ViewModel не хранит Context: поле `private Context context` убрано.
     * ViewModel живёт дольше Activity (переживает поворот экрана), и ссылка на
     * Context из Activity — утечка Activity на всё время жизни экрана.
     * Контекст принимается параметром метода и используется локально.
     */
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

    public void setFirebaseImage(String searchimagesPath, ImageView iv1, List listFilesFirestore, List listSolutionFilesFirestore, String zadacha_id, Context context) {
        if (context == null || iv1 == null) {
            Log.w(TAG, "setFirebaseImage: context или ImageView пустой, пропуск");
            return;
        }

        List locallistFiles;
        if (Objects.equals(searchimagesPath, SEARCH_ANSWER_IMAGES)) {
            locallistFiles = listFilesFirestore;
        } else {
            locallistFiles = listSolutionFilesFirestore;
        }

        if (locallistFiles == null || locallistFiles.isEmpty()) {
            Log.w(TAG, "setFirebaseImage: список файлов пуст, пропуск");
            return;
        }

        int index;
        try {
            index = Integer.parseInt(zadacha_id) - 1;
        } catch (NumberFormatException e) {
            Log.e(TAG, "setFirebaseImage: нечисловой zadacha_id=" + zadacha_id);
            return;
        }
        if (index < 0 || index >= locallistFiles.size()) {
            Log.w(TAG, "setFirebaseImage: индекс " + index + " вне списка (size=" + locallistFiles.size() + ")");
            return;
        }

        Object splitString = locallistFiles.get(index);
        if (splitString == null) {
            Log.w(TAG, "setFirebaseImage: элемент " + index + " пустой");
            return;
        }

        String[] parts = String.valueOf(splitString).split(Pattern.quote("/"));
        if (parts.length < 5) {
            Log.w(TAG, "setFirebaseImage: путь разбит на " + parts.length + " частей, ожидаем >= 5");
            return;
        }
        String imageLoad = parts[4];

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        storageRef.child(searchimagesPath + "/" + imageLoad).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    // Download directly from StorageReference using Coil
                    ImageRequest request = new ImageRequest.Builder(context)
                            .data(uri)
                            .target(iv1)
                            .build();

                    Coil.imageLoader(context).enqueue(request);
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "Не удалось получить download URL: "
                            + (exception != null ? exception.getMessage() : "unknown"), exception);
                });
    }

    /**
     * Лимиты приходят параметрами из единого хранилища (ScrollingFragment
     * передаёт prefs.loadSolutionLimits()/prefs.loadHintLimits()); ViewModel
     * не держит Context и не читает SharedPreferences сам.
     */
    public void setButtonColors(boolean isDialogShown, Context context, int solutionLimits, int hintLimits) {
        if (context == null) {
            Log.w(TAG, "setButtonColors: context пустой, пропуск");
            return;
        }
        this.solutionLimits = solutionLimits;
        this.hintLimits = hintLimits;
        this.isDialogShown = isDialogShown;

        int neutralColor = ContextCompat.getColor(context, solutionLimits == 0 ? android.R.color.darker_gray : android.R.color.holo_green_light);
        int positiveColor = ContextCompat.getColor(context, hintLimits == 0 ? android.R.color.darker_gray : android.R.color.holo_green_dark);
        int negativeColor = ContextCompat.getColor(context, android.R.color.holo_red_dark);

        // Use MutableLiveData to update the button colors in the UI
        neutralButtonColor.setValue(isDialogShown ? neutralColor : neutralColor & 0x55FFFFFF);
        positiveButtonColor.setValue(isDialogShown ? positiveColor : positiveColor & 0x55FFFFFF);
        negativeButtonColor.setValue(negativeColor);
    }
}
