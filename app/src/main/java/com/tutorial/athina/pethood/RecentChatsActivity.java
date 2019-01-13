package com.tutorial.athina.pethood;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tutorial.athina.pethood.Adapters.RecentChatsAdapter;
import com.tutorial.athina.pethood.Adapters.ReportAdapter;
import com.tutorial.athina.pethood.Models.Chat;
import com.tutorial.athina.pethood.Models.MissingDog;

import java.util.ArrayList;
import java.util.List;

public class RecentChatsActivity extends AppCompatActivity {

    List<Chat> mChat;
    RecentChatsAdapter recentChatsAdapter;
    RecyclerView recyclerView;
    private static List<String > userChat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent_chats);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Recent chats</font>"));

        userChat = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.listRecentChats);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        showMessages();
    }

    private void showMessages() {

        mChat = new ArrayList<>();
        DatabaseReference ref;
        ref = FirebaseDatabase.getInstance().getReference().child("chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Chat chat = data.getValue(Chat.class);
                    if(chat.getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()) && !userChat.contains(chat.getSender())){
                        userChat.add(chat.getSender());
                        mChat.add(chat);
                    }
                    recentChatsAdapter = new RecentChatsAdapter(RecentChatsActivity.this, mChat);
                    recyclerView.setAdapter(recentChatsAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
