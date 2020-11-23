package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
{
    private EditText userName, userEmail, userPhone ;
    private Button mainNextBtn ;

    private FirebaseAuth firebaseAuth ;
    private DatabaseReference firebaseDatabase ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance() ;
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users") ;

        SharedPreferences preferences = getSharedPreferences("LoginStatus" , MODE_PRIVATE) ;
        String swi = preferences.getString("Remember" , "") ;

        if(swi.equals("true"))
        {
            startActivity(new Intent(MainActivity.this , HomeActivity.class));
            finish();
        }else {

        }

        userName = findViewById(R.id.user_name) ;
        userEmail = findViewById(R.id.user_email) ;
        userPhone = findViewById(R.id.user_phone) ;
        mainNextBtn = findViewById(R.id.next_btn) ;

        mainNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(userName.getText()))
                {
                    userName.setError("Please enter your name");
                }else if(TextUtils.isEmpty(userEmail.getText()))
                {
                    userName.setError("Please enter your email");
                }else if(TextUtils.isEmpty(userPhone.getText()))
                {
                    userName.setError("Please enter your phone number");
                }else
                {
                    final String name = userName.getText().toString() ;
                    final String email = userEmail.getText().toString() ;
                    final String phone = userPhone.getText().toString() ;

                    RegisterUser(name, email, phone) ;
                }
            }
        });

    }

    private void RegisterUser(final String name, final String email, final String phone)
    {
        Intent intent = new Intent(MainActivity.this, PhoneVerificationActivity.class) ;
        intent.putExtra("Name", name) ;
        intent.putExtra("Email", email) ;
        intent.putExtra("Phone", phone) ;
        startActivity(intent) ;
    }
}