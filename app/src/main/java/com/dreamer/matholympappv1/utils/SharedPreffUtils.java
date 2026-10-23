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
/**
 * Работа с обычными SharedPreferences ("MySharedPreferences").
 * Поле sharedPreferences статическое, поэтому все чтения и записи защищены
 * проверками: статические методы могут быть вызваны до создания первого
 * экземпляра SharedPreffUtils, когда поле ещё равно null. Без проверок
 * такие вызовы (например, sharedPreffsLoadSolutionLimits из ScrollingFragment
 * и его ViewModel) приводили к NullPointerException и падению приложения.
 */
public class SharedPreffUtils {
    private static SharedPreferences sharedPreferences = null;

    public SharedPreffUtils(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
        }
    }

    public static void sharedPreffsSaveUserScore(Integer zadacha_score) {
        if (sharedPreferences != null) {
            saveData("zadacha_score", zadacha_score);
        }
    }

    public static void saveData(String key, Integer value) {
        if (sharedPreferences == null || value == null) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void sharedPreffsSaveSolutionLimits(Integer solutionLimits) {
        if (solutionLimits != null && sharedPreferences != null) {
            saveData("solution_limits", solutionLimits);
        }
    }

    public static Integer sharedPreffsLoadSolutionLimits() {
//        SharedPreffUtils sharedPreferencesManager = new SharedPreffUtils(requireContext());
        return getDataFromSharedPreferences("solution_limits");
    }

    public static void sharedPreffsSaveHintLimits(Integer hintLimits) {
        if (hintLimits != null && sharedPreferences != null) {
            saveData("hint_limits", hintLimits);
        }
    }

    public static Integer sharedPreffsLoadHintLimits() {
//        SharedPreffUtils sharedPreferencesManager = new SharedPreffUtils(requireContext());
        return getDataFromSharedPreferences("hint_limits");
    }


    public static Integer sharedPreffsLoadUserScore() {
//        SharedPreffUtils sharedPreferencesManager = new SharedPreffUtils(requireContext());
        return getDataFromSharedPreferences("zadacha_score");
    }


    public static int getDataFromSharedPreferences(String key) {
        if (sharedPreferences == null) {
            return 0;
        }
        return sharedPreferences.getInt(key, 0);
    }

    public int getArrayFromSharedPreferences(String key, int defaultValue) {
        if (sharedPreferences == null) {
            return defaultValue;
        }
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

    /**
     * Полная очистка обычных SharedPreferences.
     * Вызывается при выходе из аккаунта, чтобы
     * не оставлять данные предыдущего пользователя (счёт, лимиты).
     */
    public void clearAllPreferences() {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }
    }

}

