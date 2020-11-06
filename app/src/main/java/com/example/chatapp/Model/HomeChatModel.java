package com.example.chatapp.Model;

public class HomeChatModel
{
    private String ProfileImage, Name ;

    public HomeChatModel() {
    }

    public HomeChatModel(String profileImage, String name) {
        ProfileImage = profileImage;
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }
}
