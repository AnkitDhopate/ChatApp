package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Adapter.HomeAllChatsAdapter;
import com.example.chatapp.Model.HomeChatModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
{
    private FloatingActionButton newChatBtn ;
    private ImageView tempLogout ;
    private TextView appName, appText ;
    private HomeAllChatsAdapter adapter ;
    private RecyclerView allChatsRecyclerView ;
    private List<String> allChatList ;
    private String userName, userPh;

    private DatabaseReference firebaseDatabase ;
    private FirebaseAuth firebaseAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        newChatBtn = findViewById(R.id.add_new_chat_btn) ;
        tempLogout = findViewById(R.id.imageView2) ;
        appName = findViewById(R.id.textView5) ;
        appText = findViewById(R.id.textView6) ;
        allChatsRecyclerView = findViewById(R.id.chats_recycler_view) ;

        allChatsRecyclerView.setLayoutManager(new LinearLayoutManager(this)) ;
        allChatList = new ArrayList<>() ;

        firebaseDatabase = FirebaseDatabase.getInstance().getReference() ;
        firebaseAuth = FirebaseAuth.getInstance() ;

        userPh = firebaseAuth.getCurrentUser().getPhoneNumber() ;

        firebaseDatabase.child("Users").child(userPh).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.getValue().toString() ;
                firebaseDatabase.child("Chats").child(userName).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if(snapshot.exists())
                        {
                            appName.setVisibility(View.GONE) ;
                            appText.setVisibility(View.GONE) ;
                            tempLogout.setVisibility(View.GONE) ;
                            allChatsRecyclerView.setVisibility(View.VISIBLE) ;
                            allChatList.add(snapshot.getKey()) ;

                            adapter = new HomeAllChatsAdapter(allChatList) ;
                            allChatsRecyclerView.setAdapter(adapter) ;
                        }else
                        {
                            Toast.makeText(HomeActivity.this, "No data available", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }) ;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        newChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SearchUserActivity.class) ;
                startActivity(intent) ;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu) ;
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId() ;
        if(id==R.id.settings)
        {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
        }else if(id==R.id.logout)
        {
            SharedPreferences preferences = getSharedPreferences("LoginStatus" , MODE_PRIVATE) ;
            SharedPreferences.Editor editor = preferences.edit() ;
            editor.putString("Remember" , "false") ;
            editor.apply();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class) ;
            startActivity(intent) ;
            finish() ;
        }
        return super.onOptionsItemSelected(item);
    }
}