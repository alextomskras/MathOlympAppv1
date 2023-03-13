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
import com.dreamer.matholympappv1.data.model.model.Users;
import com.dreamer.matholympappv1.data.model.model.Zadachi;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class ZadachaFragment extends Fragment {

    static final String TAG = "TAG";
    NavController navController;

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;

    RecyclerView recyclerView;
    MyZadachaRecyclerViewAdapter myZadachaRecyclerViewAdapter;
    UserRecyclerViewAdapter userRecyclerViewAdapter;
    ZadachiRecyclerViewAdapter zadachiRecyclerViewAdapter;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
    private List<Users> usersList;
    private List<Zadachi> zadachiList;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ZadachaFragment() {
    }


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
        intNavcontroller();
        if (getArguments() != null) {


            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        usersList = new ArrayList<>();
        zadachiList = new ArrayList<>();
        userRecyclerViewAdapter = new UserRecyclerViewAdapter(usersList, getContext());
        zadachiRecyclerViewAdapter = new ZadachiRecyclerViewAdapter(zadachiList, getContext());

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }


            recyclerView.setAdapter(zadachiRecyclerViewAdapter);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        usersList.clear();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot UserList : dataSnapshot.getChildren()) {
                    Log.e(TAG, "iconImageViewOnClick at position1 " + UserList);
                    String user_id = UserList.getKey();
                    Log.e(TAG, "iconImageViewOnClick at position2 " + user_id);
                    Users users = UserList.getValue(Users.class).WithId(user_id);
                    usersList.add(users);
                    Log.e(TAG, "iconImageViewOnClick at position3 " + usersList);
                    userRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("firebase", "Error getting data", databaseError.toException());
            }

        });
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Zadachi");
        zadachiList.clear();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ZadachiList : dataSnapshot.getChildren()) {
                    Log.e(TAG, "iconImageViewOnClick at position5 " + ZadachiList);
                    //Get refference to Database
                    String user_id = ZadachiList.getKey();

                    Log.e(TAG, "iconImageViewOnClick at position6 " + user_id);

                    Zadachi users = ZadachiList.getValue(Zadachi.class).WithId(user_id);

                    zadachiList.add(users);
                    Log.e(TAG, "iconImageViewOnClick at position7 " + zadachiList);

                    zadachiRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "iconImageViewOnClick at position8 Error getting data", databaseError.toException());
            }

        });


    }

    private void intNavcontroller() {
        Activity MainActivity = getActivity();
        assert MainActivity != null;
        navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
    }
}