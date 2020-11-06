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
import com.example.chatapp.Model.SearchUserModel;
import com.example.chatapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.SearchUserViewHolder>
{
    private List<String> searchUserModelList ;

    public SearchUserAdapter(List<String> searchUserModelList) {
        this.searchUserModelList = searchUserModelList;
    }

    @NonNull
    @Override
    public SearchUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_user_layout, parent, false) ;
        return new SearchUserViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchUserViewHolder holder, final int position)
    {
//        holder.userPh.setText(searchUserModelList.get(position));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users") ;
        databaseReference.child(searchUserModelList.get(position)).child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String noToName = snapshot.getValue().toString() ;
                holder.userPh.setText(noToName) ;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(holder.itemView.getContext(), "failed to load names", Toast.LENGTH_SHORT).show();
            }
        }) ;

        databaseReference.child(searchUserModelList.get(position)).child("ProfileImage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Picasso.get().load(snapshot.getValue().toString()).into(holder.contactImg) ;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(holder.itemView.getContext(), "failed to load profile image", Toast.LENGTH_SHORT).show();
            }
        }) ;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(holder.itemView.getContext(), ChatWindowActivity.class) ;
                intent.putExtra("Phone", searchUserModelList.get(position).toString()) ;
                intent.putExtra("Type", "Search") ;
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchUserModelList.size() ;
    }

    public class SearchUserViewHolder extends RecyclerView.ViewHolder
    {
        TextView userPh ;
        CircleImageView contactImg ;
        public SearchUserViewHolder(@NonNull View itemView) {
            super(itemView);
            userPh = itemView.findViewById(R.id.found_user_phone_number) ;
            contactImg = itemView.findViewById(R.id.search_contact_profile_image) ;
        }
    }
}







/*
public class SearchUserAdapter extends FirebaseRecyclerAdapter<SearchUserModel, SearchUserAdapter.SearchViewHolder>
{
    public SearchUserAdapter(@NonNull FirebaseRecyclerOptions<SearchUserModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final SearchViewHolder holder, int position, @NonNull SearchUserModel model) {
        holder.userPh.setText(model.getPhone());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(holder.itemView.getContext(), ChatWindowActivity.class) ;
                Toast.makeText(holder.itemView.getContext(), "Sending no " + holder.userPh.getText().toString(), Toast.LENGTH_SHORT).show();
                intent.putExtra("Phone", holder.userPh.getText().toString()) ;
                intent.putExtra("Type", "Search") ;
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_user_layout, parent, false) ;
        return new SearchViewHolder(view) ;
    }

    class SearchViewHolder extends RecyclerView.ViewHolder
    {
        TextView userPh ;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            userPh = itemView.findViewById(R.id.found_user_phone_number) ;
        }
    }
}*/
