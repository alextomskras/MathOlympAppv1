package com.dreamer.matholympappv1.data.model.model;

public class Razdel {

    public String Razdel_id;
    //    String name="",razdel_image="";
    public String razdel_name = "", token_id = "";


    public Razdel() {
    }

    //    public Users(String name, String image) {
    public Razdel(String razdel_name, String token_id) {
        this.razdel_name = razdel_name;
        this.token_id = token_id;
//        this.razdelscore = razdelscore;
//        this.razdel_image = image;
    }

    public String getRazdelname() {
        return razdel_name;
    }

    public void setRazdelname(String razdel_name) {
        this.razdel_name = razdel_name;
    }


    public String getRazdel_token_id() {
        return token_id;
    }

    public void setRazdel_token_id(String token_id) {
        this.token_id = token_id;
    }

    //    public String getRazdel_image() {
//        return razdel_image;
//    }

//    public void setRazdel_image(String razdel_image) {
//        this.razdel_image = razdel_image;
//    }

//    public String getRazdelscore() {
//        return razdelscore;
//    }

//    public void setRazdelscore(String razdelscore) {
//        this.razdelscore = razdelscore;
//    }

    public Razdel WithId(String id) {
        this.Razdel_id = id;
        return this;
    }
}
