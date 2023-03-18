package com.dreamer.matholympappv1.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyArrayList {
    private static final String TAG = "TAG";
    public static ArrayList<String> list;

    public MyArrayList() {
        this.list = new ArrayList<String>();
    }

    public static void addString(String value) {
        list.add(value);
        saveArrayToFirebase(list);
    }

    public static ArrayList<String> getList() {
        return list;
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }


    private static void saveArrayToFirebase(ArrayList<String> list) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Error: current user is null");
            return;
        }
        String uid = user.getUid();
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference complitedtasksRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("completed_tasks");

// Convert the list to a HashMap
            HashMap<String, String> hashMap = new HashMap<>();
            for (int i = 0; i < MyArrayList.list.size(); i++) {
                hashMap.put(String.valueOf(i), MyArrayList.list.get(i));


// Save the HashMap in Firebase
                complitedtasksRef.setValue(hashMap);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving user score to Firebase: " + e.getMessage());
        }
    }

    public static void readArrayListFromFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Error: current user is null");
            return;
        }
        String uid = user.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference complitedtasksRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("completed_tasks");

        complitedtasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        HashMap<String, String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();

                        // Iterate over the HashMap to create the ArrayList
                        List<String> myList = new ArrayList<>();
                        for (String key : hashMap.keySet()) {
                            String item = hashMap.get(key);
                            myList.add(item);
                        }

                        // Use the ArrayList as needed
                        Log.e(TAG, "My list: " + myList);

                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error reading ArrayList from Firebase: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    public static void loadArrayListFromFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Error: current user is null");
            return;
        }
        String uid = user.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference complitedtasksRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("completed_tasks");

        complitedtasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<String> myList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String item = snapshot.getValue(String.class);
                        myList.add(item);
                    }

                    // Use the ArrayList as needed
                    Log.d(TAG, "My list: " + myList);
                    getList();
                    list = myList;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

}

