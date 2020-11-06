package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class SettingsActivity extends AppCompatActivity
{
    private LinearLayout settingsProfile, settingsContactUs ;
    private ImageButton settingsBackBtn ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);

        settingsProfile = findViewById(R.id.settings_profile) ;
        settingsContactUs = findViewById(R.id.settings_contact) ;
        settingsBackBtn = findViewById(R.id.setting_to_home_back_btn) ;

        settingsProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, ProfileSettingsActivity.class));
            }
        });

        settingsContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, ContactUsActivity.class));
            }
        });

        settingsBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish() ;
            }
        });

    }
}