package com.example.chatapp.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.ChatWindowActivity;
import com.example.chatapp.Model.ChatModel;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ChatRetrieveAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<ChatModel> chatModelList ;
    private String nameSender ;
    private FirebaseAuth firebaseAuth ;
    private DatabaseReference databaseReference ;

    public ChatRetrieveAdapter(List<ChatModel> chatModelList) {
        this.chatModelList = chatModelList;
        firebaseAuth = FirebaseAuth.getInstance() ;
        databaseReference = FirebaseDatabase.getInstance().getReference() ;

        String phNo = firebaseAuth.getCurrentUser().getPhoneNumber() ;
        databaseReference.child("Users").child(phNo).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nameSender = snapshot.getValue().toString() ;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if(chatModelList.get(position).getSender().equals(nameSender))
        {
            return 1 ;
        }else
        {
            return 0 ;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(viewType==1)
        {
            View sendView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_chat_layout, parent, false) ;
            return new ChatRetrieveViewHolder(sendView) ;
        }else
        {
            View receiveView = LayoutInflater.from(parent.getContext()).inflate(R.layout.receiver_chat_layout, parent, false) ;
            return new ReceivedChatRetrieveHolder(receiveView) ;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(chatModelList.get(position).getSender().equals(nameSender))
        {
            ((ChatRetrieveViewHolder)holder).chatSender.setText(chatModelList.get(position).getSender());
            ((ChatRetrieveViewHolder)holder).chatContent.setText(chatModelList.get(position).getMessage());
            ((ChatRetrieveViewHolder)holder).chatSeTime.setText(chatModelList.get(position).getTime());
        }else
        {
            ((ReceivedChatRetrieveHolder)holder).chatReceiver.setText(chatModelList.get(position).getSender());
            ((ReceivedChatRetrieveHolder)holder).chatContent.setText(chatModelList.get(position).getMessage());
            ((ReceivedChatRetrieveHolder)holder).chatReTime.setText(chatModelList.get(position).getTime());
        }
    }

    @Override
    public int getItemCount() {
        return chatModelList.size() ;
    }

    public class ChatRetrieveViewHolder extends RecyclerView.ViewHolder
    {
        TextView chatContent, chatSender, chatSeTime ;

        public ChatRetrieveViewHolder(@NonNull View itemView) {
            super(itemView);
            chatContent = itemView.findViewById(R.id.chat_msg_content) ;
            chatSender = itemView.findViewById(R.id.chat_msg_sender) ;
            chatSeTime = itemView.findViewById(R.id.se_chat_msg_time) ;
        }
    }

    public class ReceivedChatRetrieveHolder extends RecyclerView.ViewHolder
    {
        TextView chatContent, chatReceiver, chatReTime ;
        public ReceivedChatRetrieveHolder(@NonNull View itemView) {
            super(itemView);
            chatContent = itemView.findViewById(R.id.received_chat_msg_content) ;
            chatReceiver = itemView.findViewById(R.id.chat_msg_receiver) ;
            chatReTime = itemView.findViewById(R.id.re_chat_msg_time) ;
        }
    }
}
