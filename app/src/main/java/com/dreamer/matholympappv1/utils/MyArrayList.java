package com.dreamer.matholympappv1.utils;

import java.util.ArrayList;

public class MyArrayList {
    private static ArrayList<String> list;

    public MyArrayList() {
        this.list = new ArrayList<String>();
    }

    public static void addString(String value) {
        list.add(value);
    }

    public static ArrayList<String> getList() {
        return list;
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }
}
