package com.dreamer.matholympappv1.data.data.model;

public class Users {

    public String User_id;
    //    String name="",user_image="";
    String name = "";


    public Users() {

    }

    //    public Users(String name, String image) {
    public Users(String name) {
        this.name = name;
//        this.user_image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


//    public String getUser_image() {
//        return user_image;
//    }

//    public void setUser_image(String user_image) {
//        this.user_image = user_image;
//    }


    public Users WithId(String id) {
        this.User_id = id;
        return this;
    }
}
