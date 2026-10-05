package com.dreamer.matholympappv1.ui.ui.scrollingscreen;

import static com.dreamer.matholympappv1.ui.ui.scrollingscreen.ScrollingFragment.SEARCH_ANSWER_IMAGES;
import static com.dreamer.matholympappv1.ui.ui.scrollingscreen.ScrollingFragment.SEARCH_SOLUTION_IMAGES;
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

import coil.Coil;
import coil.request.ImageRequest;
import com.dreamer.matholympappv1.utils.MyArrayList;
import com.dreamer.matholympappv1.utils.SharedPreffUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
            MyArrayList.addString(zadacha_id, "tttt");

            // Update the user score
            int userScore = sharedPreffsLoadUserScore() + 10;
            SharedPreffUtils.sharedPreffsSaveUserScore(userScore);
            FirebaseUserScoreManager.saveUserScore(userScore);

            return true;
        } else {
            return false;
        }
    }


    /**
     * Загружает картинку ответа/решения задачи из Firebase Storage
     * (bucket gs://matholymp1.appspot.com, папки answersimages/ и solutionimages/).
     *
     * Имя файла определяется ПРЯМЫМ запросом getDownloadUrl("answer<id>" / "solution<id>")
     * — так же, как это делалось изначально в приложении. Это единственный надёжный способ:
     * список файлов через listAll() приходит из ZadachiRecyclerViewAdapter асинхронно
     * и к моменту открытия диалога может быть ещё пуст (или вовсе null после ротации экрана),
     * поэтому на него больше нельзя полагаться.
     *
     * Если файла с таким именем в Storage нет, Firebase вернёт ошибку "not found" (404) —
     * в этом случае ImageView скрывается, чтобы в диалоге не было пустого места.
     * Расширение файла указывать не нужно: имена вида answer7.png / answer7.jpg находятся
     * перебором стандартных расширений.
     */
    public void setFirebaseImage(String searchimagesPath, ImageView iv1, List listFilesFirestore, List listSolutionFilesFirestore, String zadacha_id, Context context) {
        this.context = context;
        if (iv1 == null || context == null) {
            return;
        }
        final int id;
        try {
            id = Integer.parseInt(zadacha_id.trim());
        } catch (NumberFormatException | NullPointerException e) {
            Log.e(TAG, "Invalid zadacha_id for image lookup: '" + zadacha_id + "'");
            iv1.setVisibility(android.view.View.GONE);
            return;
        }

        // Имена файлов без расширения: для ответов — "answer<id>", для решений — "solution<id>".
        String stem;
        if (Objects.equals(searchimagesPath, SEARCH_ANSWER_IMAGES)) {
            stem = "answer" + id;
        } else if (Objects.equals(searchimagesPath, SEARCH_SOLUTION_IMAGES)) {
            stem = "solution" + id;
        } else {
            stem = searchimagesPath + id;
        }

        tryDirectLookup(searchimagesPath, stem, iv1);
    }

    /**
     * Прямой поиск картинки в Storage по каноническому имени "answer<id>"/"solution<id>".
     * Сначала пробуем имя без расширения (исторически файлы в бакете лежат именно так),
     * затем — со стандартными расширениями. Первый успешно разрешившийся URL грузится в ImageView.
     */
    private void tryDirectLookup(final String folder, final String stem, final ImageView iv1) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        tryNextName(storageRef, folder, iv1, 0, new String[]{stem, stem + ".png", stem + ".jpg", stem + ".jpeg", stem + ".webp"});
    }

    private void tryNextName(final StorageReference storageRef, final String folder,
                             final ImageView iv1, final int index, final String[] names) {
        if (index >= names.length) {
            // Картинки с таким номером в Storage нет — прячем место под неё в диалоге
            Log.d(TAG, "No image found in '" + folder + "' for name(s): " + Arrays.toString(names));
            iv1.setVisibility(android.view.View.GONE);
            return;
        }
        final String name = names[index];
        storageRef.child(folder + "/" + name).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    ImageRequest request = new ImageRequest.Builder(context)
                            .data(uri)
                            .target(iv1)
                            .build();
                    Coil.imageLoader(context).enqueue(request);
                    iv1.setVisibility(android.view.View.VISIBLE);
                })
                .addOnFailureListener(exception -> {
                    // Файла нет (404/not_found) или сеть недоступна — пробуем следующее имя
                    tryNextName(storageRef, folder, iv1, index + 1, names);
                });
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
