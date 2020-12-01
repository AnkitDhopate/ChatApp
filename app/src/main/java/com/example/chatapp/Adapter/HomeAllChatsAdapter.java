package com.example.chatapp.Adapter;

import android.content.Intent;
import android.graphics.Color;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.ChatWindowActivity;
import com.example.chatapp.FriendProfileActivity;
import com.example.chatapp.Model.HomeChatModel;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAllChatsAdapter extends RecyclerView.Adapter<HomeAllChatsAdapter.HomeAllChatViewHolder> {
    private List<HomeChatModel> homeChatModelList ;
    private FirebaseAuth firebaseAuth ;
    private DatabaseReference databaseReference ;
    private String currUser ;

    public HomeAllChatsAdapter(List<HomeChatModel> homeChatModelList) {
        this.homeChatModelList = homeChatModelList;

        firebaseAuth = FirebaseAuth.getInstance() ;
        databaseReference = FirebaseDatabase.getInstance().getReference() ;
        currUser = firebaseAuth.getCurrentUser().getPhoneNumber() ;
    }

    @NonNull
    @Override
    public HomeAllChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_friend_layout, parent, false);
        return new HomeAllChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeAllChatViewHolder holder, final int position)
    {

        setLastMsg(currUser, homeChatModelList.get(position).getPhone(), holder.lastMsgTxt) ;

        holder.chatDisplayText.setText(homeChatModelList.get(position).getName());
        Picasso.get().load(homeChatModelList.get(position).getProfileImage()).into(holder.profileImg) ;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), ChatWindowActivity.class);
                intent.putExtra("Name", homeChatModelList.get(position).getName());
                intent.putExtra("Type", "Home");
                holder.itemView.getContext().startActivity(intent);
            }
        });

        holder.profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), FriendProfileActivity.class) ;
                intent.putExtra("Name", homeChatModelList.get(position).getName()) ;
                intent.putExtra("Phone", homeChatModelList.get(position).getPhone()) ;
                intent.putExtra("ProfilePic", homeChatModelList.get(position).getProfileImage()) ;
                holder.itemView.getContext().startActivity(intent) ;
            }
        });
    }

    @Override
    public int getItemCount() {
        return homeChatModelList.size() ;
    }

    public class HomeAllChatViewHolder extends RecyclerView.ViewHolder {
        TextView chatDisplayText, lastMsgTxt ;
        CircleImageView profileImg ;
        ImageView online, offline ;
        public HomeAllChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatDisplayText = itemView.findViewById(R.id.friend_id);
            profileImg = itemView.findViewById(R.id.friend_home_profile_image) ;
            lastMsgTxt = itemView.findViewById(R.id.friend_latest_message) ;
        }
    }

    public void setLastMsg(String user, String friend, final TextView lstMsg)
    {
        databaseReference.child("Chats").child(user).child(friend).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    lstMsg.setText(ds.child("message").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }) ;
    }

}
