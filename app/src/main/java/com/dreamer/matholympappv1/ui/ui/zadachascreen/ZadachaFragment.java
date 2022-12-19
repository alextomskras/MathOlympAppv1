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
import com.dreamer.matholympappv1.data.data.model.Users;
import com.dreamer.matholympappv1.data.data.model.Zadachi;
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
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    RecyclerView recyclerView;
    MyZadachaRecyclerViewAdapter myZadachaRecyclerViewAdapter;
    UserRecyclerViewAdapter userRecyclerViewAdapter;
    ZadachiRecyclerViewAdapter zadachiRecyclerViewAdapter;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
    private List<Users> usersList;
    private List<Zadachi> zadachiList;
//    public UsersFragment() {
//
//    }

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
        intNavcontroller();
        if (getArguments() != null) {


            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
//            myZadachaRecyclerViewAdapter = new MyZadachaRecyclerViewAdapter(PlaceholderContent.ITEMS,);

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
//        recyclerView.setHasFixedSize(true);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
//            recyclerView.setAdapter(new MyZadachaRecyclerViewAdapter(PlaceholderContent.ITEMS, new MyZadachaRecyclerViewAdapter.MyAdapterListener() {
//                @Override
//                public void iconTextViewOnClick(View v, int position) {
//                    Log.e(TAG, "iconTextViewOnClick at position " + position);
//                }
//
//                @Override
//                public void iconImageViewOnClick(View v, int position) {
//                    Log.e(TAG, "iconImageViewOnClick at position " + position);
//                    Activity MainActivity = getActivity();
//                    assert MainActivity != null;
//                    navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
//                    navController.navigate(R.id.action_zadachaFragment_to_scrollingFragment2);
//                }
//            }));

            recyclerView.setAdapter(zadachiRecyclerViewAdapter);
//            recyclerView.setAdapter(userRecyclerViewAdapter);
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
//                     HashMap user_name = (HashMap) UserList.getValue();
                    Log.e(TAG, "iconImageViewOnClick at position2 " + user_id);
//                    Log.e(TAG, "iconImageViewOnClick at position2user_name " + user_name);
//                    Users users = UserList.getValue(Users.class).WithId(user_id);
                    Users users = UserList.getValue(Users.class).WithId(user_id);
                    usersList.add(users);
                    Log.e(TAG, "iconImageViewOnClick at position3 " + usersList);
//                    userRecyclerViewAdapter.notifyDataSetChanged();
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
                    String user_id = ZadachiList.getKey();
//                     HashMap user_name = (HashMap) UserList.getValue();
                    Log.e(TAG, "iconImageViewOnClick at position6 " + user_id);
//                    Log.e(TAG, "iconImageViewOnClick at position2user_name " + user_name);
//                    Users users = UserList.getValue(Users.class).WithId(user_id);
                    Zadachi users = ZadachiList.getValue(Zadachi.class).WithId(user_id);
                    zadachiList.add(users);
                    Log.e(TAG, "iconImageViewOnClick at position7 " + zadachiList);
//                    userRecyclerViewAdapter.notifyDataSetChanged();
                    zadachiRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "iconImageViewOnClick at position8 Error getting data", databaseError.toException());
            }

        });

//        databaseReference.child("Zadachi").child("1").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//
//
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e("firebase_zadachi", "Error getting data", task.getException());
//                }
//                else {
//                    Log.d("firebase_zadachi", String.valueOf(task.getResult().getValue()));
//                }
//            }
//        });


    }

    private void intNavcontroller() {
        Activity MainActivity = getActivity();
        assert MainActivity != null;
        navController = Navigation.findNavController(MainActivity, R.id.nav_host_fragment);
    }
}