package com.dreamer.matholympappv1.data.data.model;

public class Zadachi {

    public String Zadachi_id;
    //    String name="",user_image="";
    public String answer = "", list_name = "", main_body = "";


    public Zadachi() {

    }

    //    public Users(String name, String image) {
    public Zadachi(String username) {
        this.answer = answer;
        this.list_name = list_name;
        this.main_body = main_body;
    }

    public String getZadachi_Answer() {
        return answer;
    }

    public void setZadachi_Answer(String answer) {
        this.answer = answer;
    }


    public String getZadachi_list_name() {
        return list_name;
    }

    public void setZadachi_list_name(String list_name) {
        this.list_name = list_name;
    }

    public String getZadachi_main_body() {
        return main_body;
    }

    public void setZadachi_main_body(String main_body) {
        this.main_body = main_body;
    }


    public Zadachi WithId(String id) {
        this.Zadachi_id = id;
        return this;
    }

}
