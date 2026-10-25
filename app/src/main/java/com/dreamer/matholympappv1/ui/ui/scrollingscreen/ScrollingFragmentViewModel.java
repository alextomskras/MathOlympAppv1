package com.dreamer.matholympappv1.ui.ui.scrollingscreen;

import static com.dreamer.matholympappv1.ui.ui.scrollingscreen.ScrollingFragment.SEARCH_ANSWER_IMAGES;
import static com.dreamer.matholympappv1.ui.ui.scrollingscreen.ScrollingFragment.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import coil.Coil;
import coil.request.ImageRequest;
import com.dreamer.matholympappv1.utils.MinioProxyImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScrollingFragmentViewModel extends ViewModel {
    public ArrayList myList;

    /**
     * Картинка из MinIO через токен-подписной прокси, по сквозному номеру задачи.
     * Приоритет: стабильное поле global в задаче (Zadachi/<раздел>/zadacha_N/global),
     * которое один раз проставляет скрипт proxy/assign_global.py и никогда не меняет.
     * Если поля нет (старые данные, скрипт ещё не запускался), фолбэк на старую
     * формулу: offset(все разделы с номером меньше текущего) + номер задачи.
     * Нумерация разделов — по цифре в конце ключа ("razdel_3" -> 3).
     * Если ключ раздела без цифры, пропуск: картинку грузит вызывающий код
     * (ScrollingFragment выбирает источник, Firebase остаётся резервным путём).
     */
    public void setRemoteImage(String sectionKey, String zadachaId, boolean isSolution,
                               ImageView iv1, Context context) {
        if (context == null || iv1 == null || sectionKey == null) {
            Log.w(TAG, "setRemoteImage: context/ImageView/sectionKey пустые, пропуск");
            return;
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(zadachaId);
        } catch (NumberFormatException e) {
            Log.e(TAG, "setRemoteImage: нечисловой zadacha_id=" + zadachaId);
            return;
        }
        int sectionNumber = trailingNumber(sectionKey);
        if (sectionNumber < 0) {
            Log.w(TAG, "setRemoteImage: ключ раздела '" + sectionKey + "' без цифры, пропуск");
            return;
        }
        DatabaseReference sections = FirebaseDatabase.getInstance().getReference("Zadachi");
        sections.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int global;
                String source;
                DataSnapshot task = snapshot.child(sectionKey).child(zadachaId).child("global");
                Long stored = task.getValue(Long.class);
                if (stored != null && stored > 0) {
                    global = stored.intValue();
                    source = "поле";
                } else {
                    int offset = 0;
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String key = child.getKey();
                        if (key == null || key.startsWith(".")) {
                            continue;
                        }
                        int n = trailingNumber(key);
                        if (n >= 0 && n < sectionNumber) {
                            offset += child.getChildrenCount();
                        }
                    }
                    global = offset + taskNumber;
                    source = "формула(нет поля global)";
                    Log.w(TAG, "setRemoteImage: поле global отсутствует в "
                            + sectionKey + "/" + zadachaId + ", нужен запуск assign_global.py");
                }
                Log.d(TAG, "Сквозной номер: global=" + global + " (" + source
                        + ", section=" + sectionNumber + ", task=" + taskNumber + ")");
                MinioProxyImageLoader.loadImage(global,
                        isSolution ? MinioProxyImageLoader.KIND_SOLUTION : MinioProxyImageLoader.KIND_ANSWER,
                        iv1, context);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "setRemoteImage: снимок Zadachi отменён: " + error.getMessage());
            }
        });
    }

    /** Цифра в конце ключа ("razdel_12" -> 12); -1, если цифр нет. */
    private static int trailingNumber(String key) {
        Matcher m = Pattern.compile(".*?(\\d+)$").matcher(key);
        if (!m.matches()) {
            return -1;
        }
        try {
            return Integer.parseInt(m.group(1));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

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
