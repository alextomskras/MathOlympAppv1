package com.dreamer.matholympappv1.ui.ui.zadachascreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamer.matholympappv1.R;

/**
 * A fragment representing a list of Items.
 */
public class ZadachaFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private ZadachaViewModel viewModel;
    private ZadachiRecyclerViewAdapter adapter;

    public ZadachaFragment() {
    }

    public static ZadachaFragment newInstance(int columnCount) {
        ZadachaFragment fragment = new ZadachaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        // Create ViewModel and set up adapter
        ZadachaRepository repository = new ZadachaRepositoryImpl();
        viewModel = new ViewModelProvider(this, new ZadachaViewModelFactory(repository)).get(ZadachaViewModel.class);
        adapter = new ZadachiRecyclerViewAdapter(viewModel.getZadachiList().getValue(), getContext());

        // Observe LiveData and update adapter
        viewModel.getZadachiList().observe(this, zadachiList -> {
            adapter.setZadachiList(zadachiList);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), mColumnCount));
        }
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.loadZadachiList();
    }
}