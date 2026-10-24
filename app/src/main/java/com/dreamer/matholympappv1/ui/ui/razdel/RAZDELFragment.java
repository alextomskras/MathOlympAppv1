package com.dreamer.matholympappv1.ui.ui.razdel;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamer.matholympappv1.R;
import com.dreamer.matholympappv1.utils.MyMenuInflater;
import com.dreamer.matholympappv1.utils.PrefKeys;
import com.dreamer.matholympappv1.utils.UserPrefsRepository;
import com.dreamer.matholympappv1.utils.StringIntegerConverter;
import com.dreamer.matholympappv1.utils.UserEmailLoginFirebase;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class RAZDELFragment extends Fragment {
    private static final String TAG = "TAG";
    NavController navController;
    private TextView myAppBarTitleTextView;
    private TextView myAppBarScoreTextView;
    private RecyclerView recyclerView;
    //    private UserRecyclerViewAdapter userRecyclerViewAdapter;
    private MyRAZDELRecyclerViewAdapter MyRAZDELRecyclerViewAdapter;

    private RazdelViewModel viewModel;

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;

    public String Username;
    public String Password;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsersRef;
    private UserPrefsRepository prefs;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RAZDELFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализируем единое хранилище
        prefs = new UserPrefsRepository(getContext());
        setHasOptionsMenu(true); // Важная строка для того, чтобы вызывать

        Bundle args = getArguments();
        if (args != null) {
            String username = args.getString("username");
            String uid = args.getString("uid");
            String solutionlimits = args.getString("solutionlimits", String.valueOf(PrefKeys.DEFAULT_SOLUTION_LIMITS));
            String hintlimits = args.getString("hintlimits", String.valueOf(PrefKeys.DEFAULT_HINT_LIMITS));

            int solutionlimitsnum = StringIntegerConverter.stringToInt(solutionlimits);
            int hintlimitsnum = StringIntegerConverter.stringToInt(hintlimits);

            if (username != null && !username.isEmpty()) {
                Username = username;
                Log.d(TAG, "Username получен из Bundle: " + username);
            }
            // username и uid пишутся одним edit(): пара не расходиттся по времени
            // (раньше uid появлялся только в асинхронном колбэке loadUsername)
            if (username != null && uid != null) {
                prefs.saveUsernameAndUid(username, uid);
            }
            // bundle пришёл из LoginFragment, где значения прочитаны из Firebase:
            // их кладём в локальный кэш одним edit(), обратную запись в Firebase
            // делаем только при реальном расходовании лимитов
            prefs.saveLimits(solutionlimitsnum, hintlimitsnum);
            // firebaseSaveSolutionLimits/firebaseSaveHintLimits отсюда убраны:
            // значения из bundle уже идут из базы, повторная запись создавала гонку
        }

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        viewModel = new ViewModelProvider(this).get(RazdelViewModel.class);
    }

    private void intNavcontroller() {
        Activity MainActivity = getActivity();
        assert MainActivity != null;
        navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        intNavcontroller();
        NavController navController = Navigation.findNavController(view); // правильный способ!
        // или, если используешь findNavController(view.findViewById(...)), тоже здесь
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_razdel_list, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mUsersRef = mDatabase.getReference("Users"); // Ссылка на таблицу Users

//        ActionBarHelper actionBarHelper = new ActionBarHelper(getActivity());
//        actionBarHelper.setupActionBar(getActivity(), getString(R.string.appbar_title_razdel_fragm), "1");

        recyclerView = root.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(this).get(RazdelViewModel.class);
        viewModel.getRazdelsLiveData().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> razdelList) {
                RazdelAdapter adapter = new RazdelAdapter(razdelList, new RazdelAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(String razdel) {
                        // Обработка нажатия на элемент списка
                        Bundle bundle = new Bundle();
                        bundle.putString("razdelName", razdel.toString().trim());
                        Log.d(TAG, "razdelName:" + razdel.toString().trim());
                        bundle.putString("username", Username);
                        Log.d(TAG, "username:" + bundle);
                        // Лимиты берём из единого локального кэша, а не хардкодом "3":
                        // хардкод передавался в ZadachaFragment, который записывал 3
                        // в Firebase и затирал реальные значения лимитов пользователя
                        bundle.putString("solutionlimits", String.valueOf(prefs.loadSolutionLimits()));
                        bundle.putString("hintlimits", String.valueOf(prefs.loadHintLimits()));

                        // Переход к другому фрагменту с передачей данных через навигационный граф
//                        NavHostFragment.findNavController(RAZDELFragment.this)
                        navController.navigate(R.id.action_RAZDELFragment_to_zadachaFragment, bundle);
                    }
                });
                recyclerView.setAdapter(adapter);
            }
        });

        return root;
    }

//тест
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String savedUid = prefs.loadUid();
            String savedUsername = prefs.loadUsername();

            if (!currentUser.getUid().equals(savedUid)) {
                // Данные на устройстве принадлежат другому пользователю: сначала
                // полностью очищаем хранилище, иначе счёт и лимиты прошлого
                // пользователя «мигрируют» к новому.
                prefs.clearData();
                Username = "";
                loadUsername(currentUser.getUid());
            } else if (savedUsername.isEmpty()) {
                loadUsername(currentUser.getUid());
            } else {
                Username = savedUsername;
                Log.d(TAG, "Username загружен из единого хранилища: " + Username);
            }

            // Запись локального кэша в Firebase отсюда убрана. Источник истины для
            // лимитов — Firebase RTDB; локальный кэш обновляется чтениями из базы
            // (LoginFragment, ZadachaFragment) и записи в базу делаются только при
            // реальном расходовании лимитов. push «кто последний записал» затирал
            // значения других устройств.
        } else {
            // Пользователь не авторизован - возвращаем на экран входа
            navController.clearBackStack(R.id.RAZDELFragment);
            navController.navigate(R.id.action_RAZDELFragment_to_loginFragment);
        }
    }

    private void loadUsername(String userId) {
        mUsersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    if (username != null) {
                        Username = username;
                        // Пара username+uid записывается одним edit(): они не
                        // расходятся по времени, как два раздельных editor.apply()
                        prefs.saveUsernameAndUid(username, userId);
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "Username:" + Username, Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "Username not found", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "User not found", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "Failed to load username", Snackbar.LENGTH_LONG).show();
            }
        });
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        MyMenuInflater.RazdelinflateMenu(menu, requireContext(), navController);
    }

    public void onDestroyView() {
        super.onDestroyView();
        viewModel.getRazdelsLiveData().removeObservers(getViewLifecycleOwner());
//        binding = null;
    }
//        View view = inflater.inflate(R.layout.fragment_razdel_list, container, false);
//
//        // Set the adapter
//        if (view instanceof RecyclerView) {
//            Context context = view.getContext();
//            RecyclerView recyclerView = (RecyclerView) view;
//            if (mColumnCount <= 1) {
//                recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            } else {
//                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
//            }
////            recyclerView.setAdapter(new MyRAZDELRecyclerViewAdapter(PlaceholderContent.ITEMS));
//            recyclerView.setAdapter(new RazdelAdapter(context));
//        }
//        return view;
}
