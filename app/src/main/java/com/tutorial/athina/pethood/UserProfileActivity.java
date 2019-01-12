package com.tutorial.athina.pethood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tutorial.athina.pethood.Adapters.OwnerList;
import com.tutorial.athina.pethood.Models.Owner;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private String dogOwner;
    private Button backButton;
    private ImageView logoView;
    private String ownerID;
    private String ownerName, ownerSurname, ownerPhone, ownerPhoto;
    private TextView textview;


    ListView listViewUser;
    List<Owner> ownerList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_details_view_personal);

        backButton = (Button) findViewById(R.id.backToOnline);
        listViewUser = (ListView) findViewById(R.id.listViewUser);
        logoView = (ImageView) findViewById(R.id.imageViewLogo);
        textview = (TextView) findViewById(R.id.textViewUser);

        ownerList = new ArrayList<>();

        backButton.setOnClickListener(this);

        listViewUser.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Owner owner = ownerList.get(position);
                showUpdateDialog();
                return false;
            }
        });
    }

    private void showUpdateDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater layoutInflater = getLayoutInflater();

        final View dialogView = layoutInflater.inflate(R.layout.update_owner_value, null);
        dialogBuilder.setView(dialogView);

        final TextView textView = (TextView) dialogView.findViewById(R.id.textViewEdit);
        final EditText editText = (EditText) dialogView.findViewById(R.id.newValue);
        editText.setText(ownerName);
        final TextView textView2 = (TextView) dialogView.findViewById(R.id.textViewEdit2);
        final EditText editText2 = (EditText) dialogView.findViewById(R.id.newValue2);
        editText2.setText(ownerSurname);
        final TextView textView3 = (TextView) dialogView.findViewById(R.id.textViewEdit3);
        final EditText editText3 = (EditText) dialogView.findViewById(R.id.newValue3);
        editText3.setText(ownerPhone);
        final Button button = (Button) dialogView.findViewById(R.id.buttonUpdate);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString();
                String surname = editText2.getText().toString();
                String phone = editText3.getText().toString();


                if (TextUtils.isEmpty(name) && TextUtils.isEmpty(surname) && TextUtils.isEmpty(phone)) {
                    Toast.makeText(UserProfileActivity.this, "Field empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    name = ownerName;
                }
                if (TextUtils.isEmpty(surname)) {
                    surname = ownerSurname;
                }
                if (TextUtils.isEmpty(phone)) {
                    phone = ownerPhone;
                }

                updateOwner(name, surname, phone, dogOwner, ownerPhoto);
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private boolean updateOwner(String name, String surname, String phone, String email, String photo) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Owners").child(ownerID);
        Owner owner = new Owner(name, surname, phone, email, photo);
        reference.setValue(owner);
        Toast.makeText(this, "Profile Updated! Please refresh to see changes", Toast.LENGTH_SHORT).show();
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backToOnline:
                startActivity(new Intent(this, ListOnline.class));
                break;

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent() != null) {
            dogOwner = getIntent().getStringExtra("dogOwner");
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

                    OwnerList adapter = new OwnerList(UserProfileActivity.this, ownerList);
                    listViewUser.setAdapter(adapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}
