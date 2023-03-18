package com.dreamer.matholympappv1.utils;

import android.util.Log;

import java.util.ArrayList;

public class MyArrayListUtils {
    static final String TAG = "TAG";

    public static ArrayList<String> addStringToList(ArrayList<String> list, String value) {
        list.add(value);
        Log.e(TAG, "viewId1: " + list);
        return list;
    }

    public static void printArrayList(ArrayList<String> list) {
        for (String value : list) {
            System.out.println(value);
        }
    }

}
