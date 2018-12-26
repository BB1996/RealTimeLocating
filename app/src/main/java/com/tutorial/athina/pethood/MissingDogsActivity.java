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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tutorial.athina.pethood.Adapters.MessageAdapter;
import com.tutorial.athina.pethood.Adapters.ReportAdapter;
import com.tutorial.athina.pethood.Models.MissingDog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MissingDogsActivity extends AppCompatActivity {
    ImageButton buttonSend;
    EditText sendText;
    RecyclerView recyclerView;
    List<MissingDog> mMissingDog;
    ReportAdapter reportAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.missing_dogs_layout);

        android.support.v7.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolBarMissingDog);
        toolbar.setTitle("Missing Dogs Reports");
        setSupportActionBar(toolbar);

        buttonSend = (ImageButton) findViewById(R.id.img_buttonSendMissing);
        sendText = (EditText) findViewById(R.id.textSendMissingDog);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_missingDog);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        readMessages();

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = sendText.getText().toString();
                if (!msg.trim().equals("")) {
                    sendMessage(FirebaseAuth.getInstance().getCurrentUser().getEmail(), msg);
                } else {
                    Toast.makeText(MissingDogsActivity.this, "You can't send empty message!", Toast.LENGTH_SHORT).show();
                }
                sendText.setText("");
            }
        });
    }

    private void sendMessage(String sender, String message) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("sender", sender);
        hashMap.put("message", message);

        ref.child("reports").push().setValue(hashMap);


    }

    private void readMessages() {

        mMissingDog = new ArrayList<>();
        DatabaseReference ref;
        ref = FirebaseDatabase.getInstance().getReference().child("reports");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMissingDog.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    MissingDog missingDog = data.getValue(MissingDog.class);
                    mMissingDog.add(missingDog);



                reportAdapter = new ReportAdapter(MissingDogsActivity.this, mMissingDog);
                recyclerView.setAdapter(reportAdapter);

            }
        }

        @Override
        public void onCancelled (@NonNull DatabaseError databaseError){

        }
    });

}
}
