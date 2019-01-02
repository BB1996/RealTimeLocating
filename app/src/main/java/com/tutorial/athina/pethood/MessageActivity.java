package com.tutorial.athina.pethood;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tutorial.athina.pethood.Adapters.MessageAdapter;
import com.tutorial.athina.pethood.Models.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private String dogOwner, myUser;
    ImageButton buttonSend;
    EditText sendText;
    MessageAdapter messageAdapter;
    List<Chat> mChat;
    RecyclerView recyclerView;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        android.support.v7.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMessage);
        toolbar.setTitle("Chat");
        setSupportActionBar(toolbar);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        buttonSend = (ImageButton) findViewById(R.id.img_buttonSend);
        sendText = (EditText) findViewById(R.id.textSendChat);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_chat);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        if (getIntent() != null) {
            dogOwner = getIntent().getStringExtra("dogOwner");
            myUser = getIntent().getStringExtra("myUser");
        }

        readMessages(myUser, dogOwner);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = sendText.getText().toString();
                if (!msg.trim().equals("")) {
                    sendMessage(myUser, dogOwner, msg);
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message!", Toast.LENGTH_SHORT).show();
                }
                sendText.setText("");
            }
        });

    }

    private void sendMessage(String sender, final String receiver, String message) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        ref.child("chats").push().setValue(hashMap);


    }


    private void readMessages(final String myId, final String userId) {
        mChat = new ArrayList<>();
        DatabaseReference ref;
        ref = FirebaseDatabase.getInstance().getReference().child("chats");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Chat chat = data.getValue(Chat.class);
                    if ((chat.getReceiver().equals(myId) && chat.getSender().equals(userId)) ||
                            (chat.getReceiver().equals(userId) && chat.getSender().equals(myId))) {
                        mChat.add(chat);

                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat);
                    recyclerView.setAdapter(messageAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
