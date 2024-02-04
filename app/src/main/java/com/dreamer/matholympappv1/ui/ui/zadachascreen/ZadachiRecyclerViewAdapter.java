//package com.dreamer.matholympappv1.ui.ui.zadachascreen;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.dreamer.matholympappv1.MainActivity;
//import com.dreamer.matholympappv1.R;
//import com.dreamer.matholympappv1.data.model.model.Zadachi;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.ListResult;
//import com.google.firebase.storage.StorageReference;
//
//import java.util.ArrayList;
//import java.util.List;
//
//// Адаптер для отображения списка задач в RecyclerView
//public class ZadachiRecyclerViewAdapter extends RecyclerView.Adapter<ZadachiRecyclerViewAdapter.ViewHolder> {
//
//    // Тег для логирования
//    static final String TAG = "ZadachiRecyclerView";
//
//    // Навигационный контроллер и активность для навигации
//    NavController navController;
//    MainActivity mainActivity;
//
//    // Ссылка на Firebase Storage для работы с файлами
//    FirebaseStorage storageReference = FirebaseStorage.getInstance();
//
//    // Список задач для отображения
//    private List<Zadachi> zadachiList;
//
//    // Размер списка задач для скрытия
//    public int testHideZadachiSize;
//
//    // Список файлов из Firebase Storage
//    private List<String> listFilesFirestore = new ArrayList<>();
//
//    // Список файлов решений из Firebase Storage
//    private List<String> listSolutionFilesFirestore = new ArrayList<>();
//
//    // Контекст приложения
//    private Context context;
//
//    // Место поиска изображений (ответов или решений)
//    private String spotOfSearchImages;
//
//    // Конструктор адаптера
//    public ZadachiRecyclerViewAdapter(List<Zadachi> zadachiList, Context context) {
//        this.zadachiList = zadachiList;
//        this.context = context;
//        spotOfSearchImages = "answersimages";
//        listAllFilesDirestore(spotOfSearchImages);
//        spotOfSearchImages = "solutionimages";
//        listAllFilesDirestore(spotOfSearchImages);
//        intNavcontroller();
//    }
//
//    // Метод для получения списка файлов из Firebase Storage
//    private void listAllFilesDirestore(String spotOfSearchImages) {
//        if (storageReference == null || spotOfSearchImages == null) {
//            return; // Проверяем на null, прежде чем продолжить
//        }
//
//        storageReference = FirebaseStorage.getInstance("gs://matholymp1.appspot.com");
//        StorageReference listRef = storageReference.getReference().child(spotOfSearchImages);
//
//        listRef.listAll()
//                .addOnSuccessListener(listResult -> {
//                    for (StorageReference item : listResult.getItems()) {
//                        // Добавляем имена файлов в соответствующий список
//                        if (spotOfSearchImages.equals("answersimages")) {
//                            listFilesFirestore.add(item.getName());
//                        } else {
//                            listSolutionFilesFirestore.add(item.getName());
//                        }
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    // Обработка ошибки
//                    Log.e(TAG, "Error listing files from Firebase Storage: " + e.getMessage());
//                });
//    }
//
//    // Создание нового ViewHolder для элемента RecyclerView
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item, parent, false);
//        return new ViewHolder(view);
//    }
//
//    // Привязка данных к элементу ViewHolder
//    @Override
//    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
//        if (zadachiList == null || position < 0 || position >= zadachiList.size()) {
//            return; // Выходим из метода, если список задач пустой или позиция некорректна
//        }
//
//        // Устанавливаем текст названия задачи
//        holder.name.setText(zadachiList.get(position).getZadachi_list_name());
//
//        // Получаем ID задачи и другие данные для передачи через навигацию
//        final String user_id = zadachiList.get(position).Zadachi_id;
//        final String zadacha_main_body = zadachiList.get(position).getZadachi_main_body();
//        final String zadacha_answer = zadachiList.get(position).getZadachi_Answer();
//        final String zadacha_hint = zadachiList.get(position).getZadachi_Hint();
//        final String zadacha_solution = zadachiList.get(position).getZadachi_solution();
//
//        // Обработчик нажатия на элемент RecyclerView
//        holder.view.setOnClickListener(v -> {
//            // Проверяем, зачеркнут ли текст задачи
//            if (holder.name.getCurrentTextColor() == Color.GRAY) {
//                // Если текст зачеркнут, то ничего не делаем
//                return;
//            } else {
//                // Иначе передаем данные через Bundle и переходим к следующему фрагменту
//                Bundle bundle = new Bundle();
//                bundle.putString("MyArgZadacha_id", user_id);
//                bundle.putStringArrayList("MyArgZadacha_listFilesFirestore", (ArrayList<String>) listFilesFirestore);
//                bundle.putStringArrayList("MyArgZadacha_listSolutionFilesFirestore", (ArrayList<String>) listSolutionFilesFirestore);
//                bundle.putString("MyArgZadacha_main_body", zadacha_main_body);
//                bundle.putString("MyArgZadacha_answer", zadacha_answer);
//                bundle.putString("MyArgZadacha_hint", zadacha_hint);
//                bundle.putString("MyArgZadacha_solution", zadacha_solution);
//
//                Activity MainActivity = (Activity) context;
//                navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
//                navController.navigate(R.id.action_zadachaFragment_to_scrollingFragment2, bundle);
//            }
//        });
//    }
//
//    public void setZadachiList(List<Zadachi> zadachiList) {
//        this.zadachiList = zadachiList;
//        notifyDataSetChanged();
//    }
//
//    // Получение количества элементов в RecyclerView
//    @Override
//    public int getItemCount() {
//        return zadachiList != null ? zadachiList.size() : 0;
//    }
//
//    // Инициализация NavController
//    private void intNavcontroller() {
//        Activity MainActivity = (Activity) context;
//        navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
//    }
//
//    // ViewHolder для элементов RecyclerView
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        private View view;
//        private TextView name;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            view = itemView;
//            name = view.findViewById(R.id.content);
//        }
//    }
//}
//
//


//package com.dreamer.matholympappv1.ui.ui.zadachascreen;
//
//import static com.google.android.material.internal.ContextUtils.getActivity;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.dreamer.matholympappv1.MainActivity;
//import com.dreamer.matholympappv1.R;
//import com.dreamer.matholympappv1.data.model.model.Zadachi;
//import com.dreamer.matholympappv1.utils.MyArrayList;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.ListResult;
//import com.google.firebase.storage.StorageReference;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//// Адаптер для RecyclerView, отображающий список задач
//public class ZadachiRecyclerViewAdapter extends RecyclerView.Adapter<ZadachiRecyclerViewAdapter.ViewHolder> {
//
//    // Тег для логирования
//    static final String TAG = "TAG";
//
//    // NavController и MainActivity для навигации
//    NavController navController;
//    MainActivity mainActivity;
//
//    // Ссылка на Firebase Storage для загрузки файлов
//    FirebaseStorage storageReference = FirebaseStorage.getInstance();
//
//    // Список задач для отображения в RecyclerView
//    private List<Zadachi> zadachiList;
//
//    // Размер списка задач для скрытия
//    public int testHideZadachiSize;
//
//    // Список файлов из Firebase Storage
//    private List listFilesFirestore;
//
//    // Список файлов решений из Firebase Storage
//    private List listSolutionFilesFirestore;
//
//    // Контекст приложения
//    private Context context;
//
//    // Место поиска изображений (ответов или решений)
//    private String spotOfSearchImages;
//
//    // Конструктор адаптера
//    public ZadachiRecyclerViewAdapter(List<Zadachi> zadachiList, Context context) {
//        listFilesFirestore = new ArrayList<>();
//        listSolutionFilesFirestore = new ArrayList<>();
//
//        createArray();
//
//        this.zadachiList = zadachiList;
//        this.context = context;
//        spotOfSearchImages = "answersimages";
//        listAllFilesDirestore(spotOfSearchImages);
//        spotOfSearchImages = "solutionimages";
//        listAllFilesDirestore(spotOfSearchImages);
//        intNavcontroller();
//    }
//
//    // Метод для создания массива
//    private void createArray() {
//        ArrayList<Integer> testHideZadachi;
//        testHideZadachi = new ArrayList<>();
//        testHideZadachi.add(1);
//        testHideZadachi.add(2);
//        testHideZadachi.add(4);
//        testHideZadachiSize = testHideZadachi.size();
//        Log.e(TAG, "____testHideZadachiSize= " + testHideZadachiSize);
//    }
//
//    // Метод для получения списка всех файлов в Firebase Storage
//    private void listAllFilesDirestore(String spotOfSearchImages) {
//        if (storageReference == null || spotOfSearchImages == null) {
//            return; // Проверяем на null, прежде чем продолжить
//        }
//
//        storageReference = FirebaseStorage.getInstance("gs://matholymp1.appspot.com");
//        StorageReference listRef = storageReference.getReference().child(this.spotOfSearchImages);
//
//        listRef.listAll()
//                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
//                    @Override
//                    public void onSuccess(ListResult listResult) {
//                        for (StorageReference prefix : listResult.getPrefixes()) {
//                            // Все префиксы внутри listRef.
//                            // Можно вызывать listAll() рекурсивно для них.
//
//                            Log.d(TAG, "____DATEprefix= " + prefix);
//                        }
//
//                        for (StorageReference item : listResult.getItems()) {
//                            // Все элементы внутри listRef.
//                            Log.e(TAG, "____DATEitem= " + item);
//                            if (Objects.equals(spotOfSearchImages, "answersimages")) {
//                                listFilesFirestore.add(item);
//                                Log.e(TAG, "____DATEitem= " + listFilesFirestore);
//                                int listSize = listFilesFirestore.size();
//                                Log.e(TAG, "____DATEitem2= " + listSize);
//                            } else {
//                                listSolutionFilesFirestore.add(item);
//                                Log.e(TAG, "____DATEitem= " + listSolutionFilesFirestore);
//                                int listSize = listSolutionFilesFirestore.size();
//                                Log.e(TAG, "____DATEitem2= " + listSize);
//                            }
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Произошла ошибка
//                    }
//                });
//        if (listFilesFirestore.size() != 0) {
//            Object stringFileName = listFilesFirestore.get(1);
//            Log.e(TAG, "____DATEitem1= " + stringFileName);
//        }
//
//    }
//
//    // Создание нового ViewHolder для элемента RecyclerView
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item, parent, false);
//        return new ViewHolder(view);
//    }
//
//    // Привязка данных к элементу ViewHolder
//    @Override
//    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
//        if (zadachiList == null || position < 0 || position >= zadachiList.size()) {
//            return; // Выходим из метода, если список задач пустой или позиция некорректна
//        }
//
//        holder.name.setText(zadachiList.get(position).getZadachi_list_name());
//
//        final String user_id = zadachiList.get(position).Zadachi_id;
//        final String zadacha_main_body = zadachiList.get(position).getZadachi_main_body();
//        final String zadacha_answer = zadachiList.get(position).getZadachi_Answer();
//        final String zadacha_hint = zadachiList.get(position).getZadachi_Hint();
//        final String zadacha_solution = zadachiList.get(position).getZadachi_solution();
//        holder.view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (holder.name.getCurrentTextColor() == Color.GRAY) {
//                    // Флаг зачеркнутого текста установлен
//                    // Делаем что-то...
//                    Log.e(TAG, "____testHideZadachi5= " + "Color.GRAY");
//                    return;
//                } else {
//                    // Флаг зачеркнутого текста не установлен
//                    // Делаем что-то ещё...
//                    Log.e(TAG, "____testHideZadachi6= " + "No-Color.GRAY");
//                }
//                Bundle bundle = new Bundle();
//                bundle.putString("MyArgZadacha_id", user_id);
//                bundle.putStringArrayList("MyArgZadacha_listFilesFirestore", (ArrayList<String>) listFilesFirestore);
//                bundle.putStringArrayList("MyArgZadacha_listSolutionFilesFirestore", (ArrayList<String>) listSolutionFilesFirestore);
//                bundle.putString("MyArgZadacha_main_body", zadacha_main_body);
//                bundle.putString("MyArgZadacha_answer", zadacha_answer);
//                bundle.putString("MyArgZadacha_hint", zadacha_hint);
//                bundle.putString("MyArgZadacha_solution", zadacha_solution);
//
//                @SuppressLint("RestrictedApi") Activity MainActivity = getActivity(context);
//                assert MainActivity != null;
//                navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
//                navController.navigate(R.id.action_zadachaFragment_to_scrollingFragment2, bundle);
//            }
//        });
//    }
//
//    // Получение количества элементов в RecyclerView
//    @Override
//    public int getItemCount() {
//        if (zadachiList != null) {
//            return zadachiList.size();
//        }
//        return 0;
//    }
//
//    // Инициализация NavController
//    private void intNavcontroller() {
//        @SuppressLint("RestrictedApi") Activity MainActivity = getActivity(context);
//        assert MainActivity != null;
//        navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
//    }
//
//    // Установка нового списка задач
//    public void setZadachiList(List<Zadachi> zadachiList) {
//        this.zadachiList = zadachiList;
//        notifyDataSetChanged();
//    }
//
//    // ViewHolder для элементов RecyclerView
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        public boolean isStruckThrough;
//        private View view;
//        private TextView name;
//        private ProgressBar progressBar;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            view = itemView;
//            name = (TextView) view.findViewById(R.id.content);
//            progressBar = (ProgressBar) view.findViewById(androidx.appcompat.R.id.progress_circular);
//        }
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
package com.dreamer.matholympappv1.ui.ui.zadachascreen;

import static com.google.android.material.internal.ContextUtils.getActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamer.matholympappv1.MainActivity;
import com.dreamer.matholympappv1.R;
import com.dreamer.matholympappv1.data.model.model.Zadachi;
import com.dreamer.matholympappv1.utils.MyArrayList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//public class ZadachiRecyclerViewAdapter extends RecyclerView.Adapter<ZadachiRecyclerViewAdapter.ViewHolder> {
//
//    static final String TAG = "TAG";
//    NavController navController;
//    FirebaseStorage storageReference = FirebaseStorage.getInstance();
//    private List<Zadachi> zadachiList;
//    private List<String> listFilesFirestore;
//    private List<String> listSolutionFilesFirestore;
//    private Context context;
//
//    public ZadachiRecyclerViewAdapter(List<Zadachi> zadachiList, Context context) {
//        this.zadachiList = zadachiList;
//        this.context = context;
//        listFilesFirestore = new ArrayList<>();
//        listSolutionFilesFirestore = new ArrayList<>();
//        listAllFilesFirestore("answersimages");
//        listAllFilesFirestore("solutionimages");
//        initNavController();
//    }
//
//    private void listAllFilesFirestore(String spotOfSearchImages) {
//        if (storageReference == null || spotOfSearchImages == null) {
//            return;
//        }
//
//        storageReference = FirebaseStorage.getInstance("gs://matholymp1.appspot.com");
//        StorageReference listRef = storageReference.getReference().child(spotOfSearchImages);
//
//        listRef.listAll()
//                .addOnSuccessListener(listResult -> {
//                    for (StorageReference item : listResult.getItems()) {
//                        if (Objects.equals(spotOfSearchImages, "answersimages")) {
//                            listFilesFirestore.add(item.getName());
//                        } else {
//                            listSolutionFilesFirestore.add(item.getName());
//                        }
//                    }
//                    notifyDataSetChanged();
//                })
//                .addOnFailureListener(e -> {
//                    Log.e(TAG, "Error fetching files from Firestore: " + e.getMessage());
//                });
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
//        if (zadachiList == null || position < 0 || position >= zadachiList.size()) {
//            return;
//        }
//
//        if (listFilesFirestore.isEmpty()) {
//            holder.progressBar.setVisibility(View.VISIBLE);
//        } else {
//            holder.progressBar.setVisibility(View.GONE);
//        }
//
//        holder.name.setText(zadachiList.get(position).getZadachi_list_name());
//
//        ArrayList<String> list = MyArrayList.getList();
//        for (String s : list) {
//            if (s != null) {
//                int num = Integer.parseInt(s);
//                if (list.contains(String.valueOf(position + 1))) {
//                    holder.isStruckThrough = true;
//                } else {
//                    holder.isStruckThrough = false;
//                }
//                if (holder.isStruckThrough) {
//                    holder.name.setPaintFlags(holder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                    holder.name.setTextColor(Color.GRAY);
//                } else {
//                    holder.name.setPaintFlags(holder.name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
//                    holder.name.setTextColor(Color.BLACK);
//                }
//            }
//        }
//
//        final String user_id = zadachiList.get(position).Zadachi_id;
//        final String zadacha_main_body = zadachiList.get(position).getZadachi_main_body();
//        final String zadacha_answer = zadachiList.get(position).getZadachi_Answer();
//        final String zadacha_hint = zadachiList.get(position).getZadachi_Hint();
//        final String zadacha_solution = zadachiList.get(position).getZadachi_solution();
//        holder.view.setOnClickListener(v -> {
//            if (holder.name.getCurrentTextColor() == Color.GRAY) {
//                return;
//            } else {
//                Bundle bundle = new Bundle();
//                bundle.putString("MyArgZadacha_id", user_id);
//                bundle.putStringArrayList("MyArgZadacha_listFilesFirestore", new ArrayList<>(listFilesFirestore));
//                bundle.putStringArrayList("MyArgZadacha_listSolutionFilesFirestore", new ArrayList<>(listSolutionFilesFirestore));
//                bundle.putString("MyArgZadacha_main_body", zadacha_main_body);
//                bundle.putString("MyArgZadacha_answer", zadacha_answer);
//                bundle.putString("MyArgZadacha_hint", zadacha_hint);
//                bundle.putString("MyArgZadacha_solution", zadacha_solution);
//
//                Activity MainActivity = (Activity) context;
//                navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
//                navController.navigate(R.id.action_zadachaFragment_to_scrollingFragment2, bundle);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return zadachiList != null ? zadachiList.size() : 0;
//    }
//
//    private void initNavController() {
//        Activity MainActivity = (Activity) context;
//        navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
//    }
//
//    public void setZadachiList(List<Zadachi> zadachiList) {
//        this.zadachiList = zadachiList;
//        notifyDataSetChanged();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        public boolean isStruckThrough;
//        private View view;
//        private TextView name;
//        private ProgressBar progressBar;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            view = itemView;
//            name = itemView.findViewById(R.id.content);
//            progressBar = itemView.findViewById(androidx.appcompat.R.id.progress_circular);
//        }
//    }
//}

public class ZadachiRecyclerViewAdapter extends RecyclerView.Adapter<ZadachiRecyclerViewAdapter.ViewHolder> {

    static final String TAG = "TAG";
    //    private List<Users> usersList;
    NavController navController;
    MainActivity mainActivity;
    FirebaseStorage storageReference = FirebaseStorage.getInstance();
    private List<Zadachi> zadachiList;

    public int testHideZadachiSize;
    private List<String> listFilesFirestore;
    public ArrayList testHideZadachi;
    private List<String> listSolutionFilesFirestore;
    private Context context;
    private String spotOfSearchImages;


    public ZadachiRecyclerViewAdapter(List<Zadachi> zadachiList, Context context) {
//        listFilesFirestore = new ArrayList<>();
//        listSolutionFilesFirestore = new ArrayList<>();

//        createArray();

//        testHideZadachi = Collections.singletonList(new int[]{1, 3, 4});
        this.zadachiList = zadachiList;
        this.context = context;
        listFilesFirestore = new ArrayList<>();
        listSolutionFilesFirestore = new ArrayList<>();
        spotOfSearchImages = "answersimages";
//        listAllFilesDirestore(spotOfSearchImages);
        listAllFilesDirestore("answersimages");
        spotOfSearchImages = "solutionimages";
//        listAllFilesDirestore(spotOfSearchImages);
        listAllFilesDirestore("solutionimages");
//        listFilesFirestore = new ArrayList<>();
//        listSolutionFilesFirestore = new ArrayList<>();
//        listAllFilesFirestore("answersimages");
//        listAllFilesFirestore("solutionimages");
        intNavcontroller();
    }

//    private void createArray() {
//
//        ArrayList<Integer> testHideZadachi;
//        testHideZadachi = new ArrayList<>();
//        testHideZadachi.add(1);
//        testHideZadachi.add(2);
//        testHideZadachi.add(4);
//        testHideZadachiSize = testHideZadachi.size();
//        Log.e(TAG, "____testHideZadachiSize= " + testHideZadachiSize);
//    }

    private void listAllFilesDirestore(String spotOfSearchImages) {
        if (storageReference == null || spotOfSearchImages == null) {
            return; // Проверяем на null, прежде чем продолжить
        }

        storageReference = FirebaseStorage.getInstance("gs://matholymp1.appspot.com");
//        StorageReference listRef = storageReference.getReference().child("answersimages");
        StorageReference listRef = storageReference.getReference().child(this.spotOfSearchImages);

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.

                            Log.d(TAG, "____DATEprefix= " + prefix);
                        }

                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            Log.e(TAG, "____DATEitem= " + item);
                            if (Objects.equals(spotOfSearchImages, "answersimages")) {

                                listFilesFirestore.add(String.valueOf(item));
                                Log.e(TAG, "____DATEitem= " + listFilesFirestore);
                                int listSize = listFilesFirestore.size();
                                Log.e(TAG, "____DATEitem2= " + listSize);
                            } else {
                                listSolutionFilesFirestore.add(String.valueOf(item));
                                Log.e(TAG, "____DATEitem= " + listSolutionFilesFirestore);
                                int listSize = listSolutionFilesFirestore.size();
                                Log.e(TAG, "____DATEitem2= " + listSize);
                            }


                        }
                        notifyDataSetChanged();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });
        if (listFilesFirestore.size() != 0) {
            Object stringFileName = listFilesFirestore.get(1);
            Log.e(TAG, "____DATEitem1= " + stringFileName);
        }


    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        boolean isStruckThrough;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (zadachiList == null || position < 0 || position >= zadachiList.size()) {
            return; // Выходим из метода, если список задач пустой или позиция некорректна
        }
        if (listSolutionFilesFirestore.isEmpty()) {
            holder.progressBar.setVisibility(View.VISIBLE);
        } else {
            holder.progressBar.setVisibility(View.GONE);
        }
//        ArrayList<Integer> testHideZadachi;
//        testHideZadachi = new ArrayList<>();
//        testHideZadachi.add(0);
//        testHideZadachi.add(2);
//        testHideZadachi.add(3);
//        testHideZadachiSize = testHideZadachi.size();
//        Log.e(TAG, "____testHideZadachiSize= " + testHideZadachiSize);


        holder.name.setText(zadachiList.get(position).getZadachi_list_name());


//        SharedPreffUtils sharedPreferencesManager = new SharedPreffUtils(context);
//        Integer number =  sharedPreferencesManager.getDataFromSharedPreferences("zadacha", 0);
//        List<Integer> myArrayList= Collections.singletonList(sharedPreferencesManager.getArrayFromSharedPreferences("zadacha", 1));
//        for (Integer integer : myArrayList) {
//            Log.e("MyApp", "Value: " + myArrayList);
//        }


//        for (int i = 0; i < testHideZadachiSize; i++) {
//        ArrayList<String> myList = null;

        // Get the list and print its contents
        ArrayList<String> list = MyArrayList.getList();
        for (String s : list) {
            Log.e(TAG, "____testHideZadachi= " + list);
            System.out.println(s);
            String number = s;
            Log.e(TAG, "____testHideZadachi2= " + number);
            if (number != null) {
                int num = Integer.parseInt(number);
                String str = String.valueOf(position + 1); // using String.valueOf()
                Log.e(TAG, "____testHideZadachi4= " + str);
                String str2 = Integer.toString(position + 1); // using Integer.toString()
                Log.e(TAG, "____testHideZadachi3= " + str2);


                if (list.contains(String.valueOf(position + 1))) {
                    holder.isStruckThrough = true;
                } else {
                    holder.isStruckThrough = false;
                }
//                holder.isStruckThrough = num >= (position + 1);
//                if (str2.equals(number)) {
                if (holder.isStruckThrough) {
//                    holder.name.setBackgroundColor(Color.RED);
                    //делаем зачеркнутый текст у пройденных задач
                    holder.name.setPaintFlags(holder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.name.setTextColor(Color.GRAY);

//                    Log.e(TAG, "____testHideZadachi5= " + holder.name.getPaintFlags());
                } else {
                    holder.name.setPaintFlags(holder.name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.name.setTextColor(Color.BLACK);
//                if (holder.isStruckThrough) {
//                    holder.name.setPaintFlags(holder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                    holder.name.setTextColor(Color.GRAY);
//                } else {
//                    holder.name.setPaintFlags(holder.name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
//                    holder.name.setTextColor(/* Set your desired non-strikethrough text color here */);
//                }
                }
            }
        }


//        assert false;
//        String number = MyArrayList.();
//            Log.e(TAG, "____testHideZadachi= " + number);
//            // Perform your check on each number here


//        if (number!=0) {
//            if (position == number-1) {
//                holder.name.setBackgroundColor(Color.RED);
//            }
//        }
//        }


//        for (Integer number : testHideZadachi) {
//
//            Log.e(TAG, "____testHideZadachi= " + number);
//            // Perform your check on each number here
//            if (number > 3) {
//                holder.name.setBackgroundColor(Color.RED);
//            }
//        }

//        holder.name.setText(zadachiList.get(position).setVisibility(View.GONE));

//         CircleImageView circleImageView = holder.circleImageView;
//        Glide.with(context).load(usersList.get(position).getUser_image()).listener(new RequestListener<Drawable>() {
//            @Override
//            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//
//                holder.progressBar.setVisibility(View.INVISIBLE);
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                holder.progressBar.setVisibility(View.INVISIBLE);
//                return false;
//            }
//        }).into(circleImageView);

        final String user_id = zadachiList.get(position).Zadachi_id;
        Log.e(TAG, "iconImageViewOnClick at position10 " + zadachiList.size());

//        final String user_name = usersList.get(position).getUsername();
//        final String user_name = zadachiList.get(position).getZadachi_list_name();
        final String zadacha_main_body = zadachiList.get(position).getZadachi_main_body();
        final String zadacha_answer = zadachiList.get(position).getZadachi_Answer();
        final String zadacha_hint = zadachiList.get(position).getZadachi_Hint();
        final String zadacha_solution = zadachiList.get(position).getZadachi_solution();
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //проверяем что цвет серый и не пускаем в уже пройденные задачи - ибо не надо накручивать счетчик очков
                if (holder.name.getCurrentTextColor() == Color.GRAY) {
                    // Strike-through flag is set
                    // Do something...
                    Log.e(TAG, "____testHideZadachi5= " + "Color.GRAY");
                    return;
                } else {
                    // Strike-through flag is not set
                    // Do something else...
                    Log.e(TAG, "____testHideZadachi6= " + "No-Color.GRAY");
                }
                Bundle bundle = new Bundle();
                Log.e(TAG, "_Dateitem=10 " + listFilesFirestore.size());
                bundle.putString("MyArgZadacha_id", user_id);
                bundle.putStringArrayList("MyArgZadacha_listFilesFirestore", (ArrayList<String>) listFilesFirestore);
                bundle.putStringArrayList("MyArgZadacha_listSolutionFilesFirestore", (ArrayList<String>) listSolutionFilesFirestore);
                bundle.putString("MyArgZadacha_main_body", zadacha_main_body);
                bundle.putString("MyArgZadacha_answer", zadacha_answer);
                bundle.putString("MyArgZadacha_hint", zadacha_hint);
                bundle.putString("MyArgZadacha_solution", zadacha_solution);

                @SuppressLint("RestrictedApi") Activity MainActivity = getActivity(context);
                assert MainActivity != null;
                navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
                navController.navigate(R.id.action_zadachaFragment_to_scrollingFragment2, bundle);

//               Intent intent = new Intent(context, SendActivity.class);
//               intent.putExtra("user_id",user_id);
//               intent.putExtra("user_name",user_name);
//               context.startActivity(intent);
            }
        });

        ProgressBar progressBar = holder.progressBar;
        if (listFilesFirestore.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (zadachiList != null) {
            Log.e(TAG, "iconImageViewOnClick at position8 " + zadachiList.size());
            return zadachiList.size();
        }
        return 0;
//        Log.e(TAG, "iconImageViewOnClick at position8 " + zadachiList.size());
//        return zadachiList.size();
    }

    private void intNavcontroller() {
        @SuppressLint("RestrictedApi") Activity MainActivity = getActivity(context);
        assert MainActivity != null;
        navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
    }

    public void setZadachiList(List<Zadachi> zadachiList) {

        this.zadachiList = zadachiList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public boolean isStruckThrough;
        private View view;
        //        private CircleImageView circleImageView;
        private TextView name;
        private TextView id;

        private ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
//            circleImageView = (CircleImageView)view.findViewById(R.id.listview_image);
            id = (TextView) view.findViewById(R.id.item_number);
            name = (TextView) view.findViewById(R.id.content);
            progressBar = (ProgressBar) view.findViewById(androidx.appcompat.R.id.progress_circular);

        }
    }


}
