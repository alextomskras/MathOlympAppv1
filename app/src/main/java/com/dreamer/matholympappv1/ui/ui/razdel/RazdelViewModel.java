package com.dreamer.matholympappv1.ui.ui.razdel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dreamer.matholympappv1.data.model.model.Razdel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RazdelViewModel extends ViewModel {
    private static final String TAG = "TAG";
    private MutableLiveData<List<Razdel>> razdelsLiveData;
    private DatabaseReference razdelsRef;
    private ValueEventListener valueEventListener;

    public RazdelViewModel() {
        razdelsLiveData = new MutableLiveData<>();
        razdelsRef = FirebaseDatabase.getInstance().getReference().child("Razdel");
        loadRazdels();
    }

    public LiveData<List<Razdel>> getRazdelsLiveData() {
        return razdelsLiveData;
    }

    private void loadRazdels() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Razdel> razdels = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Razdel razdel = dataSnapshot.getValue(Razdel.class);
                    if (razdel != null) {
                        razdels.add(razdel);
                    }
                }
                razdelsLiveData.setValue(razdels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибок загрузки данных
                Log.e(TAG, "Error getting data", error.toException());
            }
        };
        razdelsRef.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (valueEventListener != null) {
            razdelsRef.removeEventListener(valueEventListener);
        }
    }


//
//    private static final String TAG = "TAG";
//    private DatabaseReference databaseReference;
//    private MutableLiveData<List<Razdel>> razdelLiveData = new MutableLiveData<>();
//
//
//
//    public void loadRazdel() {
//        databaseReference = FirebaseDatabase.getInstance().getReference().child("Razdel");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<Razdel> razdelList = new ArrayList<>();
//                for (DataSnapshot razdelSnapshot : dataSnapshot.getChildren()) {
//                    String razdel_id = razdelSnapshot.getKey();
//                    Razdel razdel = razdelSnapshot.getValue(Razdel.class).WithId(razdel_id);
//                    razdelList.add(razdel);
//                }
//                razdelLiveData.setValue(razdelList);
////                return null;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                Log.e(TAG, "Error getting data", databaseError.toException());
//            }
//        });
//    }
//    public LiveData<List<Razdel>> getRazdelLiveData() {
//        return razdelLiveData;
//    }

}
