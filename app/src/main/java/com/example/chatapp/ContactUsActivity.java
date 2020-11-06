package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class ContactUsActivity extends AppCompatActivity
{

    private ImageButton back ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        back = findViewById(R.id.contact_us_to_settings_back) ;

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish() ;
            }
        });
    }
}