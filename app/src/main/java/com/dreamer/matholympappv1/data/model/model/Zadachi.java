package com.dreamer.matholympappv1.data.model.model;

public class Zadachi {

    public String Zadachi_id;
    //    String name="",user_image="";
    public String answer = "", list_name = "", main_body = "", hint = "", solution = "";


    public Zadachi() {

    }

    //    public Users(String name, String image) {
    public Zadachi(String answer, String list_name, String main_body, String hint, String solution) {
        this.answer = answer;
        this.list_name = list_name;
        this.main_body = main_body;
        this.hint = hint;
        this.solution = solution;
    }

//    public String getZadachi_id(){
//        return Zadachi_id;
//    }
//    public void setZadachi_id(String Zadachi_id) {
//        this.answer = Zadachi_id;
//    }

    public String getZadachi_Answer() {
        return answer;
    }

    public void setZadachi_Answer(String answer) {
        this.answer = answer;
    }

    public String getZadachi_Hint() {
        return hint;
    }

    public void setZadachi_Hint(String hint) {
        this.hint = hint;
    }

    public String getZadachi_solution() {
        return solution;
    }

    public void setZadachi_solution(String solution) {
        this.solution = solution;
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
