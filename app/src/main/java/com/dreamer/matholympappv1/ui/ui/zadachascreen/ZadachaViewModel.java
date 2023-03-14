package com.dreamer.matholympappv1.ui.ui.zadachascreen;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dreamer.matholympappv1.data.model.model.Users;
import com.dreamer.matholympappv1.data.model.model.Zadachi;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ZadachaViewModel extends ViewModel {

    private final MutableLiveData<List<Users>> usersLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Zadachi>> zadachiLiveData = new MutableLiveData<>();
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public LiveData<List<Users>> getUsersLiveData() {
        return usersLiveData;
    }

    public LiveData<List<Zadachi>> getZadachiLiveData() {
        return zadachiLiveData;
    }

    public void fetchUsers() {
        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Users> usersList = new ArrayList<>();
                for (DataSnapshot UserList : dataSnapshot.getChildren()) {
                    String user_id = UserList.getKey();
                    Users users = UserList.getValue(Users.class).WithId(user_id);
                    usersList.add(users);
                }
                usersLiveData.postValue(usersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("firebase", "Error getting data", databaseError.toException());
            }
        });
    }

    public void fetchZadachi() {
        databaseReference.child("Zadachi").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Zadachi> zadachiList = new ArrayList<>();
                for (DataSnapshot ZadachiList : dataSnapshot.getChildren()) {
                    String user_id = ZadachiList.getKey();
                    Zadachi zadachi = ZadachiList.getValue(Zadachi.class).WithId(user_id);
                    zadachiList.add(zadachi);
                }
                zadachiLiveData.postValue(zadachiList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("firebase", "Error getting data", databaseError.toException());
            }
        });
    }
}
