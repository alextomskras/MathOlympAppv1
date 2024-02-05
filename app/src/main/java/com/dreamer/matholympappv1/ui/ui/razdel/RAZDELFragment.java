package com.dreamer.matholympappv1.ui.ui.razdel;

import static com.dreamer.matholympappv1.ui.ui.scrollingscreen.FirebaseUserScoreManager.firebaseSaveHintLimits;
import static com.dreamer.matholympappv1.ui.ui.scrollingscreen.FirebaseUserScoreManager.firebaseSaveSolutionLimits;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamer.matholympappv1.R;
import com.dreamer.matholympappv1.data.model.model.Razdel;
import com.dreamer.matholympappv1.utils.StringIntegerConverter;
import com.dreamer.matholympappv1.utils.UserEmailLoginFirebase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class RAZDELFragment extends Fragment {
    private static final String TAG = "TAG";
    NavController navController;
    private FirebaseAuth mAuth;
    private TextView myAppBarTitleTextView;
    private TextView myAppBarScoreTextView;
    private RecyclerView recyclerView;
    //    private UserRecyclerViewAdapter userRecyclerViewAdapter;
    private MyRAZDELRecyclerViewAdapter MyRAZDELRecyclerViewAdapter;

    private RazdelViewModel viewModel;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    public String Username;
    public String Password;

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
        mAuth = FirebaseAuth.getInstance();


        Bundle args = getArguments();
        if (args != null) {
            String username = args.getString("username");
            String password = args.getString("password");
            String solutionlimits = args.getString("solutionlimits", "1");
            String hintlimits = args.getString("hintlimits", "3");
            Username = username;
            Password = password;
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
                        bundle.putString("username", "11@11.com");
                        Log.d(TAG, "username:" + bundle);
                        bundle.putString("password", "123456");
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
