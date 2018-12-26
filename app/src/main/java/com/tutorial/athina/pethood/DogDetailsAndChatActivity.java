package com.tutorial.athina.pethood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tutorial.athina.pethood.Adapters.DogList;
import com.tutorial.athina.pethood.Models.Dog;

import java.util.ArrayList;
import java.util.List;

public class DogDetailsAndChatActivity extends AppCompatActivity implements View.OnClickListener {
    private String dogOwner, myUser;
    private Button chatButton;


    ListView listViewDogs;
    List<Dog> dogList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dog_details_others);



        chatButton = (Button) findViewById(R.id.chatButton);
        listViewDogs = (ListView) findViewById(R.id.listViewDogs);
        dogList = new ArrayList<>();


        chatButton.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent() != null) {
            dogOwner = getIntent().getStringExtra("dogOwner");
            myUser = getIntent().getStringExtra("myUser");
        }
        if (!TextUtils.isEmpty(dogOwner)) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("dog");
            ref.orderByChild("dogOwner").equalTo(dogOwner).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Dog dog = data.getValue(Dog.class);
                        dogList.add(dog);
                    }

                    DogList adapter = new DogList(DogDetailsAndChatActivity.this, dogList);
                    listViewDogs.setAdapter(adapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.chatButton:
                Intent chatIntent = new Intent(this, MessageActivity.class);
                chatIntent.putExtra("dogOwner", dogOwner);
                chatIntent.putExtra("myUser", myUser);
                startActivity(chatIntent);

        }

    }
}
