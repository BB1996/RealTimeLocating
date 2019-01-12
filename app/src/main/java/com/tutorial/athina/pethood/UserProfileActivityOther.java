package com.tutorial.athina.pethood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tutorial.athina.pethood.Adapters.OwnerList;
import com.tutorial.athina.pethood.Models.Owner;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivityOther extends AppCompatActivity implements View.OnClickListener {

    private String dogOwner,myUser;
    private Button backButton;
    private ImageView logoView;
    private String ownerID;
    private String ownerName, ownerSurname, ownerPhone, ownerPhoto;


    ListView listViewUser;
    List<Owner> ownerList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_details_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Profile</font>"));

        backButton = (Button) findViewById(R.id.backToOnline);
        listViewUser = (ListView) findViewById(R.id.listViewUser);
        logoView = (ImageView) findViewById(R.id.imageViewLogo);
        ownerList = new ArrayList<>();

        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backToOnline:
                Intent intent = new Intent(this,DogDetailsAndChatActivity.class);
                intent.putExtra("dogOwner",dogOwner);
                intent.putExtra("myUser",myUser);
                startActivity(intent);
                break;

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent() != null) {
            dogOwner = getIntent().getStringExtra("dogOwner");
            myUser = getIntent().getStringExtra("myUser");
        }
        if (!TextUtils.isEmpty(dogOwner)) {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Owners");
            ref.orderByChild("email").equalTo(dogOwner).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Owner owner = data.getValue(Owner.class);
                        Glide.with(getApplicationContext()).load(owner.getProfileImageUrl()).into(logoView);
                        ownerList.add(owner);
                        ownerID = data.getKey();
                        ownerName = owner.getName();
                        ownerSurname = owner.getSurname();
                        ownerPhone = owner.getPhone();
                        ownerPhoto = owner.getProfileImageUrl();
                    }

                    OwnerList adapter = new OwnerList(UserProfileActivityOther.this, ownerList);
                    listViewUser.setAdapter(adapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}
