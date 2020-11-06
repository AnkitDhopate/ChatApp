package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PhoneVerificationActivity extends AppCompatActivity {
    private String name, email, phone, verificationCode;
    private EditText otp;
    private Button verify;
    private ProgressDialog loadingBar ;
    private TextView confirmPh ;

    private DatabaseReference firebaseDatabase ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        otp = findViewById(R.id.otp);
        verify = findViewById(R.id.phone_verification_btn);
        confirmPh = findViewById(R.id.confirm_number) ;

        loadingBar = new ProgressDialog(this) ;

        name = getIntent().getStringExtra("Name");
        email = getIntent().getStringExtra("Email");
        phone = getIntent().getStringExtra("Phone");

        confirmPh.setText(phone) ;

        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users") ;
        /*loadingBar.setTitle("Verifying OTP");
        loadingBar.setMessage("Please wait ...");
        loadingBar.setCanceledOnTouchOutside(false) ;
        loadingBar.show() ;*/


        sendVerificationCodeToUser(phone);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = otp.getText().toString() ;
                if(TextUtils.isEmpty(otp.getText()) || otp.getText().toString().length()<6)
                {
                    otp.setError("Wrong OTP entered");
                    otp.requestFocus() ;
                    return ;
                }

                verifyCode(code) ;
            }
        });
    }

    private void sendVerificationCodeToUser(String phone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken)
        {
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText(PhoneVerificationActivity.this, "OTP sent !", Toast.LENGTH_SHORT).show();
            verificationCode = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e)
        {
            Toast.makeText(PhoneVerificationActivity.this, "Error :" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String code)
    {
        loadingBar.setTitle("Verifying OTP");
        loadingBar.setMessage("Please wait ...");
        loadingBar.setCanceledOnTouchOutside(false) ;
        loadingBar.show() ;
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, code) ;
        signInByCredentials(credential) ;
    }

    private void signInByCredentials(PhoneAuthCredential credential)
    {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance() ;
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    loadingBar.setMessage("Updating Account ...");
                    addUser(name, email, phone);
                }else
                {
                    loadingBar.dismiss() ;
                    Toast.makeText(PhoneVerificationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }) ;
    }

    private void addUser(String name, String email, String phone)
    {

        Map<String, String> userMap = new HashMap<>() ;
        userMap.put("Name", name) ;
        userMap.put("Email", email) ;
        userMap.put("Phone", "+91"+phone) ;

        firebaseDatabase.child("+91" + phone).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss() ;
                Toast.makeText(PhoneVerificationActivity.this, "Done !", Toast.LENGTH_SHORT).show();
                SharedPreferences preferences = getSharedPreferences("LoginStatus" , MODE_PRIVATE) ;
                SharedPreferences.Editor editor = preferences.edit() ;
                editor.putString("Remember" , "true") ;
                editor.apply();
                Intent intent = new Intent(PhoneVerificationActivity.this, HomeActivity.class) ;
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
                startActivity(intent) ;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss() ;
                Toast.makeText(PhoneVerificationActivity.this, "Error :" + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }) ;
    }
}