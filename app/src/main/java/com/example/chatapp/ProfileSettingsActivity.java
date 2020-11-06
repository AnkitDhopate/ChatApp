package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSettingsActivity extends AppCompatActivity {

    private CircleImageView userProfileImage ;
    private EditText userName ;
    private TextView changeProfileImageBtn, userPhoneNumber ;
    private Button resetChangesBtn ;
    private String profileUrl ;

    private DatabaseReference databaseReference ;
    private FirebaseAuth firebaseAuth ;
    private StorageReference mReference ;

    private Uri uri ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        userProfileImage = findViewById(R.id.user_profile_image) ;
        userName = findViewById(R.id.user_name) ;
        changeProfileImageBtn = findViewById(R.id.change_image_btn) ;
        resetChangesBtn = findViewById(R.id.reset_changes) ;
        userPhoneNumber = findViewById(R.id.profile_user_phone) ;

        databaseReference = FirebaseDatabase.getInstance().getReference("Users") ;
        firebaseAuth = FirebaseAuth.getInstance() ;
        mReference = FirebaseStorage.getInstance().getReference().child("Profile Images") ;

        resetChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDetails(userName.getText()) ;
            }
        });

        changeProfileImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.startPickImageActivity(ProfileSettingsActivity.this) ;
            }
        });

        getUserInfo() ;
    }

    private void updateDetails(final Editable text)
    {
        final ProgressDialog progressDialog = new ProgressDialog(this) ;
        progressDialog.setTitle("Set your profile") ;
        progressDialog.setMessage("Please wait .. ") ;
        progressDialog.show() ;

        if(uri!=null)
        {
            mReference.child(firebaseAuth.getCurrentUser().getPhoneNumber()+".jpg").putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            mReference.child(firebaseAuth.getCurrentUser().getPhoneNumber()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    HashMap<String, Object> updateMap = new HashMap<>() ;
                                    updateMap.put("Name", text.toString()) ;
                                    updateMap.put("ProfileImage", uri.toString()) ;
                                    databaseReference.child(firebaseAuth.getCurrentUser().getPhoneNumber()).updateChildren(updateMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(ProfileSettingsActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(ProfileSettingsActivity.this, HomeActivity.class)) ;
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss() ;
                                            Toast.makeText(ProfileSettingsActivity.this, "Error while updating the database", Toast.LENGTH_SHORT).show();
                                        }
                                    }) ;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(ProfileSettingsActivity.this, "Error!!!", Toast.LENGTH_SHORT).show();
                                }
                            }) ;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileSettingsActivity.this, "Error : " + exception.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    long percentage = (100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount() ;
                    progressDialog.setMessage("Uploading " + percentage + "%");
                }
            }) ;
        }else
        {
            databaseReference.child(firebaseAuth.getCurrentUser().getPhoneNumber()).child("Name").setValue(text.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid)
                {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileSettingsActivity.this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ProfileSettingsActivity.this, HomeActivity.class)) ;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileSettingsActivity.this, "Error while updating name", Toast.LENGTH_SHORT).show();
                }
            }) ;
        }
    }

    private void getUserInfo()
    {
        databaseReference.child(firebaseAuth.getCurrentUser().getPhoneNumber()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount()>0)
                {
                    if(snapshot.hasChild("ProfileImage"))
                    {
                        String proImg = snapshot.child("ProfileImage").getValue().toString() ;
                        Picasso.get().load(proImg).into(userProfileImage) ;
                    }
                    if(snapshot.hasChild("Name"))
                    {
                        userName.setText(snapshot.child("Name").getValue().toString()) ;
                    }
                    if(snapshot.hasChild("Phone"))
                    {
                        userPhoneNumber.setText(snapshot.child("Phone").getValue().toString()) ;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }) ;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            Uri imageUri = CropImage.getPickImageResultUri(this, data) ;
            uri = imageUri ;
            if(CropImage.isReadExternalStoragePermissionsRequired(this, imageUri))
            {
                Toast.makeText(this, "URI is " + imageUri.toString(), Toast.LENGTH_SHORT).show();
                uri = imageUri ;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        0) ;
            }else
            {
                startCrop(imageUri) ;
            }
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data) ;
            if(resultCode==RESULT_OK)
            {
                userProfileImage.setImageURI(result.getUri());
                profileUrl = result.getUri().toString() ;
            }
        }
    }

    private void startCrop(Uri imageUri)
    {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true).start(this) ;
    }
}