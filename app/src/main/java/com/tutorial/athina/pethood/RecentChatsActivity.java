package com.tutorial.athina.pethood;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tutorial.athina.pethood.Adapters.RecentChatsAdapter;
import com.tutorial.athina.pethood.Models.Chat;
import com.tutorial.athina.pethood.Models.Owner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RecentChatsActivity extends AppCompatActivity {
    RecentChatsAdapter recentChatsAdapter;
    RecyclerView recyclerView;
    private List<String> ownerList;
    private List<Chat> mChats;
    FirebaseUser firebaseUser;
    DatabaseReference ref;
    List<String> ownerList2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent_chats);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Recent chats</font>"));


        recyclerView = (RecyclerView) findViewById(R.id.listRecentChats);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ownerList = new ArrayList<>();
        ref = FirebaseDatabase.getInstance().getReference().child("chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ownerList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Chat chat = data.getValue(Chat.class);
                    if (chat.getSender().equals(firebaseUser.getEmail())) {
                        ownerList.add(chat.getReceiver());
                    }
                    if (chat.getReceiver().equals(firebaseUser.getEmail())) {
                        ownerList.add(chat.getSender());
                    }
                }
                Set<String> set = new HashSet<String>();
                set.addAll(ownerList);
                ownerList.clear();
                ownerList.addAll(set);
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void readChats() {
        mChats = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference().child("Owners");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Owner owner = data.getValue(Owner.class);
                    for (String email : ownerList) {
                        if (owner.getEmail().equals(email)) {
                            if (mChats.size() != 0) {
                                for (int i = 0; i < mChats.size(); i++) {
                                    Chat chat = mChats.get(i);
                                    if (!owner.getEmail().equals(chat.getSender())) {
                                        mChats.add(new Chat(owner.getEmail(), firebaseUser.getEmail(), ""));
                                        break;

                                    }
                                }
                            } else {
                                mChats.add(new Chat(owner.getEmail(), firebaseUser.getEmail(), ""));

                            }
                        }
                    }
                }
                recentChatsAdapter = new RecentChatsAdapter(getApplicationContext(), mChats);
                recyclerView.setAdapter(recentChatsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}



