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

    private static final String TAG = "TAG";
    private DatabaseReference databaseReference;
    private MutableLiveData<List<Users>> usersLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Zadachi>> zadachiLiveData = new MutableLiveData<>();

    public void loadUsers() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Users> usersList = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String user_id = userSnapshot.getKey();
                    Users user = userSnapshot.getValue(Users.class).WithId(user_id);
                    usersList.add(user);
                }
                usersLiveData.setValue(usersList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error getting data", databaseError.toException());
            }
        });
    }

    public void loadZadachi() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Zadachi");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Zadachi> zadachiList = new ArrayList<>();
                for (DataSnapshot zadachiSnapshot : dataSnapshot.getChildren()) {
                    String zadachi_id = zadachiSnapshot.getKey();
                    Zadachi zadachi = zadachiSnapshot.getValue(Zadachi.class).WithId(zadachi_id);
                    zadachiList.add(zadachi);
                }
                zadachiLiveData.setValue(zadachiList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e(TAG, "Error getting data", databaseError.toException());
            }
        });
    }

    public LiveData<List<Users>> getUsersLiveData() {
        return usersLiveData;
    }

    public LiveData<List<Zadachi>> getZadachiLiveData() {
        return zadachiLiveData;
    }

}


//public class ZadachaViewModel extends ViewModel {
//
//    private final MutableLiveData<List<Users>> usersLiveData = new MutableLiveData<>();
//    private final MutableLiveData<List<Zadachi>> zadachiLiveData = new MutableLiveData<>();
//    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//
//    public ZadachaViewModel(ZadachaRepository repository) {
//    }
//
//    public LiveData<List<Users>> getUsersLiveData() {
//        return usersLiveData;
//    }
//
//    public LiveData<List<Zadachi>> getZadachiLiveData() {
//        return zadachiLiveData;
//    }
//
//    public void fetchUsers() {
//        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                List<Users> usersList = new ArrayList<>();
//                for (DataSnapshot UserList : dataSnapshot.getChildren()) {
//                    String user_id = UserList.getKey();
//                    Users users = UserList.getValue(Users.class).WithId(user_id);
//                    usersList.add(users);
//                }
//                usersLiveData.postValue(usersList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e("firebase", "Error getting data", databaseError.toException());
//            }
//        });
//    }
//
//    public void fetchZadachi() {
//        databaseReference.child("Zadachi").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                List<Zadachi> zadachiList = new ArrayList<>();
//                for (DataSnapshot ZadachiList : dataSnapshot.getChildren()) {
//                    String user_id = ZadachiList.getKey();
//                    Zadachi zadachi = ZadachiList.getValue(Zadachi.class).WithId(user_id);
//                    zadachiList.add(zadachi);
//                }
//                zadachiLiveData.postValue(zadachiList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e("firebase", "Error getting data", databaseError.toException());
//            }
//        });
//    }


//    public void loadZadachiList() {
//        ZadachaRepository repository = new ZadachaRepositoryImpl();
//        repository.getZadachiList(new ZadachaRepository.ZadachiListCallback() {
//            @Override
//            public void onSuccess(List<Zadachi> zadachiList) {
//                ZadachaViewModel.this.zadachiList.setValue(zadachiList);
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                // handle error
//            }
//        });
//    }
//}
