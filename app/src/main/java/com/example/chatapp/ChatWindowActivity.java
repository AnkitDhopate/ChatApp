package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Adapter.ChatRetrieveAdapter;
import com.example.chatapp.Classes.SendNotification;
import com.example.chatapp.Model.ChatModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatWindowActivity extends AppCompatActivity {
    private TextView friendName;
    private EditText chatContent;
    private ImageView sendMsg;
    private RecyclerView chatRecyclerView;
    private ChatRetrieveAdapter adapter;
    private String senderNo, receiverNo;
    private List<ChatModel> chatModelList;
    private String senderName, receiverName, receiverProfilePic;
    private CircleImageView userProfileImage;
    private String type, notificationKey;
    private LinearLayoutCompat chatLinearLayout ;

    private DatabaseReference firebaseDatabase;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        friendName = findViewById(R.id.chat_box_user_phone);
        chatContent = findViewById(R.id.chat_messages_input);
        sendMsg = findViewById(R.id.send_image_btn);
        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        userProfileImage = findViewById(R.id.chat_window_profile_image);
        chatLinearLayout = findViewById(R.id.linear_layout) ;

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        senderNo = firebaseAuth.getCurrentUser().getPhoneNumber();
        chatModelList = new ArrayList<>();

        type = getIntent().getStringExtra("Type");
        if (type.equals("Home")) {
            receiverName = getIntent().getStringExtra("Name");
            friendName.setText(receiverName);

            firebaseDatabase.child("Users").child(senderNo).child("Name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    senderName = snapshot.getValue().toString();

                    firebaseDatabase.child("Users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                String tempName = ds.child("Name").getValue().toString();
                                if (tempName.equals(receiverName)) {
                                    receiverNo = ds.child("Phone").getValue().toString();
                                    break;
                                }
                            }
                            fillTheChat(senderNo, receiverNo) ;

                            firebaseDatabase.child("Users").child(receiverNo).child("ProfileImage").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        receiverProfilePic = snapshot.getValue().toString() ;
                                        Picasso.get().load(snapshot.getValue().toString()).into(userProfileImage);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ChatWindowActivity.this, "Error while setting the profile pic !", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ChatWindowActivity.this, "Error while retrieving receiver number", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ChatWindowActivity.this, "Error while retrieving sender name", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (type.equals("Search")) {
            receiverNo = getIntent().getStringExtra("Phone");

            firebaseDatabase.child("Users").child(senderNo).child("Name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    senderName = snapshot.getValue().toString();

                    firebaseDatabase.child("Users").child(receiverNo).child("Name").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            receiverName = snapshot.getValue().toString();
                            friendName.setText(receiverName);

                            fillTheChat(senderNo, receiverNo) ;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ChatWindowActivity.this, "Error while retrieving the friend name", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ChatWindowActivity.this, "Error while retrieving sender name", Toast.LENGTH_SHORT).show();
                }
            });

            firebaseDatabase.child("Users").child(receiverNo).child("ProfileImage").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        receiverProfilePic = snapshot.getValue().toString() ;
                        Picasso.get().load(snapshot.getValue().toString()).into(userProfileImage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ChatWindowActivity.this, "Error while setting the profile pic !", Toast.LENGTH_SHORT).show();
                }
            });
        }

        adapter = new ChatRetrieveAdapter(chatModelList);
        chatRecyclerView.setAdapter(adapter);

        chatLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatWindowActivity.this, FriendProfileActivity.class) ;
                intent.putExtra("Name", receiverName) ;
                intent.putExtra("Phone", receiverNo) ;
                if(receiverProfilePic!=null)
                {
                    intent.putExtra("ProfilePic", receiverProfilePic) ;
                }
                startActivity(intent);
            }
        });

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(chatContent.getText())) {
                    Toast.makeText(ChatWindowActivity.this, "Cannot send empty messages !", Toast.LENGTH_SHORT).show();
                } else {
                    Calendar callForDate = Calendar.getInstance();
//                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                    SimpleDateFormat currentDate = new SimpleDateFormat("dd/MMM/yyyy");
                    final String chatDate = currentDate.format(callForDate.getTime());
                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm a");
                    final String chatTime = currentTime.format(callForDate.getTime());

                    final String msg = chatContent.getText().toString();

                    firebaseDatabase.child("Chats").child(senderNo).child(receiverNo)
                            .push().setValue(new ChatModel(senderName, msg, chatDate+" "+chatTime))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    chatContent.setText("") ;

                                    firebaseDatabase.child("Users").child(receiverNo).child("notificationKey").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            notificationKey = snapshot.getValue().toString() ;

                                            new SendNotification(msg, "Heading", notificationKey) ;
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ChatWindowActivity.this, "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }) ;

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatWindowActivity.this, "Error while sending message", Toast.LENGTH_SHORT).show();
                        }
                    }) ;

                    firebaseDatabase.child("Chats").child(receiverNo).child(senderNo)
                            .push().setValue(new ChatModel(senderName, msg, chatDate+" "+chatTime))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    chatContent.setText("") ;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatWindowActivity.this, "Error while sending message", Toast.LENGTH_SHORT).show();
                        }
                    }) ;
//                    closeKeyboard();
                }
            }
        });
    }

    private void fillTheChat(String senderName, String receiverName) {

        firebaseDatabase.child("Chats").child(senderName).child(receiverName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                chatModelList.add(snapshot.getValue(ChatModel.class)) ;
                chatRecyclerView.smoothScrollToPosition(chatModelList.size()-1);
                adapter.notifyDataSetChanged() ;
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
                Toast.makeText(ChatWindowActivity.this, "Error in loading the chat content", Toast.LENGTH_SHORT).show();
            }
        }) ;
    }

    /*private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }*/
}
