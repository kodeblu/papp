package com.bencorp.papp;

/**
 * Created by hp-pc on 1/8/2019.
 */

public class User {
    public String id;
    public String name;
    public String phone_1;
    public String phone_2;
    public String services;
    public String address;
    public String country;
    public String state;
    public String date;
    public User(String name,String id, String phone_1, String phone_2, String address){
        this.name = name;
        this.phone_1 = phone_1;
        this.phone_2 = phone_2;
        this.id = id;
        this.address = address;
    }



    public User(String id, String[] userInfo,String[] locationInfo,String date) {
        this.name = userInfo[0];
        this.phone_1 = userInfo[1];
        this.phone_2 = userInfo[2];
        this.services = userInfo[3];
        this.address = userInfo[4];
        this.country = locationInfo[0];
        this.state = locationInfo[1];
        this.id = id;
        this.date = date;
    }

    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getPhone_1(){
        return phone_1;
    }
    public String getPhone_2(){
        return phone_2;
    }
    public String getAddress(){
        return address;
    }
}
