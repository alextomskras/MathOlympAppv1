package com.dreamer.matholympappv1.data.model.model;

public class Razdel {
    //    private String id; // ID раздела
//    private String subrazdelId; // ID подраздела
    public String Razdel_id;
    public String answer = "", list_name = "", main_body = "", hint = "", solution = "";

    public Razdel() {
        // Пустой конструктор нужен для Firebase, чтобы преобразовать данные из снимка в объект класса Razdel
    }

    public Razdel(String answer, String list_name, String main_body, String hint, String solution) {
//        this.id = id;
//        this.subrazdelId = subrazdelId;
        this.answer = answer;
        this.list_name = list_name;
        this.main_body = main_body;
        this.hint = hint;
        this.solution = solution;
    }


    public String getList_name() {
        return list_name;
    }

    public void setList_name(String list_name) {
        this.list_name = list_name;
    }


    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getMain_body() {
        return main_body;
    }

    public void setMain_body(String main_body) {
        this.main_body = main_body;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public Razdel WithId(String id) {
        this.Razdel_id = id;
        return this;
    }
}


//public class Razdel {
//
//    public String Razdel_id;
//    //    String name="",razdel_image="";
////    public String razdel_name = "", token_id = "";
//
//
//    public Razdel() {
//    }
//
//    //    public Users(String name, String image) {
//    public Razdel(String razdel_name, String token_id) {
//        this.razdel_name = razdel_name;
//        this.token_id = token_id;
////        this.razdelscore = razdelscore;
////        this.razdel_image = image;
//    }
//
//    public String getRazdelname() {
//        return razdel_name;
//    }
//
//    public void setRazdelname(String razdel_name) {
//        this.razdel_name = razdel_name;
//    }
//
//
//    public String getRazdel_token_id() {
//        return token_id;
//    }
//
//    public void setRazdel_token_id(String token_id) {
//        this.token_id = token_id;
//    }
//
//    //    public String getRazdel_image() {
////        return razdel_image;
////    }
//
////    public void setRazdel_image(String razdel_image) {
////        this.razdel_image = razdel_image;
////    }
//
////    public String getRazdelscore() {
////        return razdelscore;
////    }
//
////    public void setRazdelscore(String razdelscore) {
////        this.razdelscore = razdelscore;
////    }
//
//    public Razdel WithId(String id) {
//        this.Razdel_id = id;
//        return this;
//    }
//}
