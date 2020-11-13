package com.example.chatapp.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.ChatWindowActivity;
import com.example.chatapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class HomeAllChatsAdapter extends RecyclerView.Adapter<HomeAllChatsAdapter.HomeAllChatViewHolder> {
    private List<String> allChatList;

    public HomeAllChatsAdapter(List<String> allChatList) {
        this.allChatList = allChatList;
    }

    @NonNull
    @Override
    public HomeAllChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_friend_layout, parent, false);
        return new HomeAllChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeAllChatViewHolder holder, final int position) {

        holder.chatDisplayText.setText(allChatList.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), ChatWindowActivity.class);
                intent.putExtra("Name", allChatList.get(position));
                intent.putExtra("Type", "Home");
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allChatList.size();
    }

    public class HomeAllChatViewHolder extends RecyclerView.ViewHolder {
        TextView chatDisplayText;

        public HomeAllChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatDisplayText = itemView.findViewById(R.id.friend_id);
        }
    }
}
