package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendProfileActivity extends AppCompatActivity
{
    private CircleImageView profilePic ;
    private TextView name, phone ;
    private String intName, intPhone, intUrl ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        profilePic = findViewById(R.id.friend_profile_image) ;
        name = findViewById(R.id.friend_profile_name) ;
        phone = findViewById(R.id.friend_profile_phone) ;

        intName = getIntent().getStringExtra("Name") ;
        intPhone = getIntent().getStringExtra("Phone") ;
        try {
            intUrl = getIntent().getStringExtra("ProfilePic") ;
            Picasso.get().load(intUrl).into(profilePic);
        }catch (Exception e)
        {
            Picasso.get().load(R.drawable.user_temp_profile).into(profilePic) ;
        }

        name.setText(intName);
        phone.setText(intPhone);
    }
}