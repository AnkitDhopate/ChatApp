package com.example.chatapp.Model;

public class HomeChatModel {
    private String ProfileImage, Name, Phone;

    public HomeChatModel() {
    }

    public HomeChatModel(String profileImage, String name, String phone) {
        ProfileImage = profileImage;
        Name = name;
        Phone = phone ;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }
}
