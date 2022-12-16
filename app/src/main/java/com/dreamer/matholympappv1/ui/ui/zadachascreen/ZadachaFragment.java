package com.dreamer.matholympappv1.ui.ui.zadachascreen;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamer.matholympappv1.R;
import com.dreamer.matholympappv1.data.placeholder.placeholder.PlaceholderContent;

/**
 * A fragment representing a list of Items.
 */
public class ZadachaFragment extends Fragment {

    static final String TAG = "TAG";
    NavController navController;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ZadachaFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyZadachaRecyclerViewAdapter(PlaceholderContent.ITEMS, new MyZadachaRecyclerViewAdapter.MyAdapterListener() {
                @Override
                public void iconTextViewOnClick(View v, int position) {
                    Log.e(TAG, "iconTextViewOnClick at position " + position);
                }

                @Override
                public void iconImageViewOnClick(View v, int position) {
                    Log.e(TAG, "iconImageViewOnClick at position " + position);
                    Activity MainActivity = getActivity();
                    assert MainActivity != null;
                    navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
                    navController.navigate(R.id.action_zadachaFragment_to_scrollingFragment2);
                }
            }));
        }
        return view;
    }
}