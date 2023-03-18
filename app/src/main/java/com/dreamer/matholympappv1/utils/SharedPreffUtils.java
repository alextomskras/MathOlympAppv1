package com.dreamer.matholympappv1.utils;

import android.content.Context;
import android.content.SharedPreferences;

//public class SharedPreffUtils {
//
//
//    public void saveDataToSharedPreferences(String key, String value) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(key, value);
//        editor.apply();
//    }
//
//    public String getDataFromSharedPreferences(String key) {
//        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
//        return sharedPreferences.getString(key, "");
//    }
//}
public class SharedPreffUtils {
    private final SharedPreferences sharedPreferences;

    public SharedPreffUtils(Context context) {
        sharedPreferences = context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
    }

    public void saveData(String key, Integer value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getDataFromSharedPreferences(String key, int defaultValue) {
//        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public int getArrayFromSharedPreferences(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void putArrayFromSharedPreferences(String key, Integer values) {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(values);
//        Log.e("MyApp", "Value: " + json);
//        editor.putInt(key, json);
//        editor.apply();
    }

//    public Integer[] putArrayFromSharedPreferences(String key,String value){
////        ArrayList<Integer> myArrayList = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5));
//        ArrayList<String> myArrayList = new ArrayList<String>();
////        Arrays myArray;
////        myArray = new Array();
////
////        myArray.add(value);
////        Integer[] myArray = {1, 2, 3, 4};
//        Gson gson = new Gson();
//        String json = gson.toJson(myArrayList);
//        Set<String> mySet = new HashSet<String>();
//        mySet.add(json);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        editor.putStringSet(key, mySet);
//        editor.apply();
//
//
//
//        return new Integer[0];
//    }

}

