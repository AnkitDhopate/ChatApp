package com.example.chatapp.Model;

public class SearchUserModel
{
    private String /*Name*/Phone ;

    public SearchUserModel() {
    }

    public SearchUserModel(String /*name*/phone) {
//        Name = name;
        Phone = phone ;
    }

    /*public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }*/

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
