package com.dreamer.matholympappv1.data.model.model;

public class Users {

    public String User_id;
    //    String name="",user_image="";
    public String username = "", token_id = "";


    public Users() {

    }

    //    public Users(String name, String image) {
    public Users(String username, String token_id) {
        this.username = username;
        this.token_id = token_id;
//        this.user_image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getUser_token_id() {
        return token_id;
    }

    public void setUser_token_id(String token_id) {
        this.token_id = token_id;
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
