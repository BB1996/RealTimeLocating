package com.tutorial.athina.pethood.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tutorial.athina.pethood.ItemClickListener;
import com.tutorial.athina.pethood.MessageActivity;
import com.tutorial.athina.pethood.Models.Chat;
import com.tutorial.athina.pethood.Models.Owner;
import com.tutorial.athina.pethood.R;

import java.util.HashMap;
import java.util.List;

public class RecentChatsAdapter extends RecyclerView.Adapter<RecentChatsAdapter.ViewHolder> {
    private Context mContext;
    private List<Chat> mChat;
    private DatabaseReference ownerRef, chatRef;
    private HashMap<String, String> recentChat;
    private String lastMessage;


    public RecentChatsAdapter(Context mContext, List<Chat> mChat) {
        this.mContext = mContext;
        this.mChat = mChat;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_chats, parent, false);
        recentChat = new HashMap<>();
        return new RecentChatsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Chat chat = mChat.get(position);

        ownerRef = FirebaseDatabase.getInstance().getReference().child("Owners");
        ownerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Owner owner = data.getValue(Owner.class);
                    if (owner.getEmail().equals(chat.getSender())) {
                        Glide.with(mContext).load(owner.getProfileImageUrl()).into(holder.user_pic);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        chatRef = FirebaseDatabase.getInstance().getReference().child("chats");
        holder.show_userChat.setText(chat.getSender());
        lastMessage(chat.getSender(), holder.show_lastMessage);

        holder.itemClickListener = new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (!mChat.get(position).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                    Intent chatIntent = new Intent(mContext, MessageActivity.class);
                    chatIntent.putExtra("myUser", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    chatIntent.putExtra("dogOwner", mChat.get(position).getSender());
                    mContext.startActivity(chatIntent);
                }

            }
        };
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements ItemClickListener, View.OnClickListener {
        public TextView show_userChat, show_lastMessage;
        public ImageView user_pic;
        ItemClickListener itemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            user_pic = itemView.findViewById(R.id.showUserProfile);
            show_userChat = itemView.findViewById(R.id.show_userChat);
            show_lastMessage = itemView.findViewById(R.id.show_lastMessage);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view, int position) {
            itemClickListener.onClick(view, getAdapterPosition());
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition());
        }
    }

    private void lastMessage(final String userid, final TextView last_msg) {
        lastMessage = "default";
        final String firebaseUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Chat chat = data.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser) && chat.getSender().equals(userid) ||
                            chat.getSender().equals(firebaseUser) && chat.getReceiver().equals(userid)) {
                        lastMessage = chat.getMessage();
                    }
                }
                switch (lastMessage) {
                    case "default":
                        last_msg.setText("No Message");
                        break;
                    default:
                        last_msg.setText(lastMessage);
                        break;
                }
                lastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
