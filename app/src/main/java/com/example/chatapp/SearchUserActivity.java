package com.example.chatapp;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapter.SearchUserAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchUserActivity extends AppCompatActivity {
    private EditText searchUserPhoneNumber;
    private ImageButton searchBtn;
    private RecyclerView userRecyclerView;
    private SearchUserAdapter adapter;


    //Phone Contacts Filter
    private List<String> searchUserModelList;
    public static final int REQUEST_READ_CONTACTS = 79;
    private ArrayList mobileArrayPhone;
    //Phone Contacts Filter

    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchUserPhoneNumber = findViewById(R.id.search_user_number);
        searchBtn = findViewById(R.id.search_user_icon);
        userRecyclerView = findViewById(R.id.search_user_recycler_view);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(searchUserPhoneNumber.getText()))
                {
                    searchUserPhoneNumber.setError("Please enter a number");
                }else
                {
                    searchUserModelList.clear() ;
                    String ph = searchUserPhoneNumber.getText().toString() ;
                    if(ph.charAt(0)!='+')
                    {
                        ph = "+91"+ph ;
                        firebaseUserSearch(ph) ;
                    }else
                    {
                        firebaseUserSearch(ph) ;
                    }
//                    firebaseUserSearch(ph) ;
                }
            }
        });


    }

    private void firebaseUserSearch(final String ph)
    {
        firebaseDatabase.orderByChild("Phone").startAt(ph).endAt(ph + "\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                boolean check = false ;
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    if(mobileArrayPhone.contains(ds.child("Phone").getValue().toString()))
                    {
                        check = true ;
                        searchUserModelList.add(ds.child("Phone").getValue().toString());
                    }else
                    {
                        if(ds.child("Phone").getValue().toString().equals(ph))
                        {
                            check = true ;
                            searchUserModelList.add(ds.child("Phone").getValue().toString());
                        }
                    }
                }
                if(check==false)
                {
                    Toast.makeText(SearchUserActivity.this, "Entered number not found !", Toast.LENGTH_SHORT).show();
                }
                adapter = new SearchUserAdapter(searchUserModelList);
                userRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }) ;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            mobileArrayPhone = getAllContacts();

            firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users");
            searchUserModelList = new ArrayList<>();

            firebaseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    for (DataSnapshot ds : snapshot.getChildren())
//                    {
//                        if(mobileArrayPhone.contains(ds.child("Phone").getValue().toString()))
//                        {
//                            searchUserModelList.add(ds.child("Phone").getValue().toString());
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
                    try{
                        for (DataSnapshot ds : snapshot.getChildren())
                        {
                            if(mobileArrayPhone.contains(ds.child("Phone").getValue().toString()))
                            {
                                searchUserModelList.add(ds.child("Phone").getValue().toString());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }catch (Exception e)
                    {
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            adapter = new SearchUserAdapter(searchUserModelList);
            userRecyclerView.setAdapter(adapter);

        } else {
            requestPermission();
        }
    }

    //The Phone Contacts Filter

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mobileArrayPhone = getAllContacts();

                    firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users");
                    searchUserModelList = new ArrayList<>();

                    firebaseDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                if(mobileArrayPhone.contains(ds.child("Phone").getValue().toString()))
                                {
                                    searchUserModelList.add(ds.child("Phone").getValue().toString());
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    adapter = new SearchUserAdapter(searchUserModelList);
                    userRecyclerView.setAdapter(adapter);
                } else {
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private ArrayList<String> getAllContacts() {
        ArrayList<String> phoneList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if(phoneNo.length()==13)
                        {
                            phoneList.add(phoneNo);
                        }else
                        {
                            String temp = "+91"+phoneNo ;
                            phoneList.add(temp);
                        }
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return phoneList;
    }

    //The Phone Contacts Filter
}