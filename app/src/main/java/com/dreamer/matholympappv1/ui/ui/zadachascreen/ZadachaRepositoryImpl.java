package com.dreamer.matholympappv1.ui.ui.zadachascreen;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.dreamer.matholympappv1.data.model.model.Users;
import com.dreamer.matholympappv1.data.model.model.Zadachi;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ZadachaRepositoryImpl implements ZadachaRepository {

    private DatabaseReference usersRef;
    private DatabaseReference zadachiRef;
    private ValueEventListener usersValueEventListener;
    private ValueEventListener zadachiValueEventListener;

    public ZadachaRepositoryImpl() {
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        zadachiRef = FirebaseDatabase.getInstance().getReference().child("Zadachi");
    }

    @Override
    public void fetchUsers() {

    }

    @Override
    public void fetchZadachi() {

    }

    @Override
    public void getUsersList(MutableLiveData<List<Users>> usersLiveData) {
        usersValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Users> usersList = new ArrayList<>();
                for (DataSnapshot UserList : dataSnapshot.getChildren()) {
                    String user_id = UserList.getKey();
                    Users users = UserList.getValue(Users.class).WithId(user_id);
                    usersList.add(users);
                }
                usersLiveData.postValue(usersList);
//                return null;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("firebase", "Error getting data", databaseError.toException());
            }
        };
        usersRef.addValueEventListener(usersValueEventListener);
    }

    @Override
    public void getZadachiList(MutableLiveData<List<Zadachi>> zadachiLiveData) {
        zadachiValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Zadachi> zadachiList = new ArrayList<>();
                for (DataSnapshot ZadachiList : dataSnapshot.getChildren()) {
                    String user_id = ZadachiList.getKey();
                    Zadachi zadachi = ZadachiList.getValue(Zadachi.class).WithId(user_id);
                    zadachiList.add(zadachi);
                }
                zadachiLiveData.postValue(zadachiList);
//                return null;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("firebase", "Error getting data", databaseError.toException());
            }
        };
        zadachiRef.addValueEventListener(zadachiValueEventListener);
    }

    @Override
    public void removeListeners() {
        if (usersValueEventListener != null) {
            usersRef.removeEventListener(usersValueEventListener);
        }
        if (zadachiValueEventListener != null) {
            zadachiRef.removeEventListener(zadachiValueEventListener);
        }
    }
}
