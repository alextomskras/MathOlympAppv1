package com.dreamer.matholympappv1.ui.ui.zadachascreen;

import androidx.lifecycle.MutableLiveData;

import com.dreamer.matholympappv1.data.model.model.Users;
import com.dreamer.matholympappv1.data.model.model.Zadachi;

import java.util.List;

public interface ZadachaRepository {

    void fetchUsers();

    void fetchZadachi();

    void getUsersList(MutableLiveData<List<Users>> usersLiveData);

    void getZadachiList(MutableLiveData<List<Zadachi>> zadachiLiveData);

    void removeListeners();
}
