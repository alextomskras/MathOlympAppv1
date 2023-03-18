package com.dreamer.matholympappv1.ui.ui.zadachascreen;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamer.matholympappv1.R;
import com.dreamer.matholympappv1.utils.MyArrayList;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 */
public class ZadachaFragment extends Fragment {
    NavController navController;
    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    private RecyclerView recyclerView;
    private UserRecyclerViewAdapter userRecyclerViewAdapter;
    private ZadachiRecyclerViewAdapter zadachiRecyclerViewAdapter;

    private ZadachaViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intNavcontroller();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        viewModel = new ViewModelProvider(this).get(ZadachaViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        userRecyclerViewAdapter = new UserRecyclerViewAdapter(new ArrayList<>(), getContext());
        zadachiRecyclerViewAdapter = new ZadachiRecyclerViewAdapter(new ArrayList<>(), getContext());

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            recyclerView.setAdapter(zadachiRecyclerViewAdapter);
        }
        // Observe user list from view model and update adapter
        viewModel.getUsersLiveData().observe(getViewLifecycleOwner(), users -> {
            userRecyclerViewAdapter.setUsersList(users);
            userRecyclerViewAdapter.notifyDataSetChanged();
        });
        // Observe zadachi list from view model and update adapter
        viewModel.getZadachiLiveData().observe(getViewLifecycleOwner(), zadachi -> {
            zadachiRecyclerViewAdapter.setZadachiList(zadachi);
            zadachiRecyclerViewAdapter.notifyDataSetChanged();
        });

        // Set adapter to recycler view
        recyclerView.setAdapter(zadachiRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        MyArrayList.readArrayListFromFirebase();
        MyArrayList.loadArrayListFromFirebase();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Fetch data from database using view model
//        viewModel.fetchUsers();
        viewModel.loadUsers();
//        viewModel.fetchZadachi();
        viewModel.loadZadachi();
    }


    private void intNavcontroller() {
        Activity MainActivity = getActivity();
        assert MainActivity != null;
        navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
    }
}