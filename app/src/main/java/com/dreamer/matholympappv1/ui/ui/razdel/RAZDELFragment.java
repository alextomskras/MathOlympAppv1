package com.dreamer.matholympappv1.ui.ui.razdel;

import static com.dreamer.matholympappv1.ui.ui.scrollingscreen.FirebaseUserScoreManager.firebaseSaveHintLimits;
import static com.dreamer.matholympappv1.ui.ui.scrollingscreen.FirebaseUserScoreManager.firebaseSaveSolutionLimits;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamer.matholympappv1.R;
import com.dreamer.matholympappv1.data.model.model.Razdel;
import com.dreamer.matholympappv1.utils.MyMenuInflater;
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

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RAZDELFragment() {
    }


//    @SuppressWarnings("unused")
//    public static RAZDELFragment newInstance(int columnCount) {
//        RAZDELFragment fragment = new RAZDELFragment();
//        Bundle args = new Bundle();
//        String username = args.getString("username");
//        String password = args.getString("password");
//        String solutionlimits = args.getString("solutionlimits", "1");
//        String hintlimits = args.getString("hintlimits", "3");
//
//
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true); // Важная строка для того, чтобы вызывать

        Bundle args = getArguments();
        if (args != null) {
            String username = args.getString("username");
            String password = args.getString("password");
            String solutionlimits = args.getString("solutionlimits", "1");
            String hintlimits = args.getString("hintlimits", "3");
//            Username = username;
//            Password = password;
            // Do something with username and password
            UserEmailLoginFirebase.updateUiWithUser(username, password, getActivity());
//            updateUiWithUser(username, password);

            int solutionlimitsnum = StringIntegerConverter.stringToInt(solutionlimits); // num will be 123
            int hintlimitsnum = StringIntegerConverter.stringToInt(hintlimits); // num will be 123

            firebaseSaveSolutionLimits(solutionlimitsnum);
            firebaseSaveHintLimits(hintlimitsnum);

        }

        intNavcontroller();

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
        viewModel.getRazdelsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Razdel>>() {
            @Override
            public void onChanged(List<Razdel> razdelList) {
                RazdelAdapter adapter = new RazdelAdapter(razdelList, new RazdelAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Razdel razdel) {
                        // Обработка нажатия на элемент списка
                        Bundle bundle = new Bundle();
                        bundle.putString("razdelName", razdel.getRazdelname());
                        Log.d(TAG, "razdelName:" + razdel.getRazdelname());
                        bundle.putString("username", Username);
                        Log.d(TAG, "username:" + bundle);
                        bundle.putString("password", "12345");
                        Log.d(TAG, "password:" + bundle);
                        bundle.putString("solutionlimits", "3");
                        bundle.putString("hintlimits", "3");

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


    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Пользователь уже залогинен
            loadUsername(currentUser.getUid());
        } else {
            // Пользователь не залогинен
            // Реализуйте здесь логику для обработки случая, когда пользователь не залогинен
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
//                        mUsernameTextView.setText(username);
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "Username:" + Username, Snackbar.LENGTH_LONG).show();
                    } else {
//                        mUsernameTextView.setText("Username not found");
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "Username not found", Snackbar.LENGTH_LONG).show();
                    }
                } else {
//                    mUsernameTextView.setText("User not found");
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "User not found", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                mUsernameTextView.setText("Failed to load username");
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
