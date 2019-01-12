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
import com.tutorial.athina.pethood.Adapters.DogList;
import com.tutorial.athina.pethood.Models.Dog;

import java.util.ArrayList;
import java.util.List;

public class DogDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private String dogOwner;
    private Button backButton;
    private ImageView logoView;
    private String dogID;
    private String dogName, dogBreed, dogAge, dogSize, dogColor, dogMating, dogPhoto;
    private TextView textView;


    ListView listViewDogs;
    List<Dog> dogList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dog_details_view);


        backButton = (Button) findViewById(R.id.backToOnline);
        listViewDogs = (ListView) findViewById(R.id.listViewDogs);
        logoView = (ImageView) findViewById(R.id.imageViewLogo);
        textView = (TextView) findViewById(R.id.textViewDog);
        dogList = new ArrayList<>();

        backButton.setOnClickListener(this);

        listViewDogs.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Dog dog = dogList.get(position);
                showUpdateDialog();
                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent() != null) {
            dogOwner = getIntent().getStringExtra("dogOwner");
        }
        if (!TextUtils.isEmpty(dogOwner)) {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("dog");
            ref.orderByChild("dogOwner").equalTo(dogOwner).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Dog dog = data.getValue(Dog.class);
                        Glide.with(getApplicationContext()).load(dog.getDogPhoto()).into(logoView);
                        if (dog.getDogMateFlag() != null && dog.getDogMateFlag().equals("Y")) {
                            dog.setDogMateFlag("to mate");
                            dogMating = "Y";
                        } else {
                            dog.setDogMateFlag("not to mate");
                            dogMating = "N";
                        }
                        dogList.add(dog);
                        dogID = data.getKey();
                        dogName = dog.getDogName();
                        dogBreed = dog.getDogBreed();
                        dogAge = dog.getDogAge();
                        dogColor = dog.getDogColor();
                        dogSize = dog.getDogSize();
                        dogPhoto = dog.getDogPhoto();
                    }

                    DogList adapter = new DogList(DogDetailsActivity.this, dogList);
                    listViewDogs.setAdapter(adapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void showUpdateDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater layoutInflater = getLayoutInflater();

        final View dialogView = layoutInflater.inflate(R.layout.update_value, null);
        dialogBuilder.setView(dialogView);

        final TextView textView = (TextView) dialogView.findViewById(R.id.textViewEdit);
        final EditText editText = (EditText) dialogView.findViewById(R.id.newValue);
        editText.setText(dogName);
        final TextView textView2 = (TextView) dialogView.findViewById(R.id.textViewEdit2);
        final EditText editText2 = (EditText) dialogView.findViewById(R.id.newValue2);
        editText2.setText(dogBreed);
        final TextView textView3 = (TextView) dialogView.findViewById(R.id.textViewEdit3);
        final EditText editText3 = (EditText) dialogView.findViewById(R.id.newValue3);
        editText3.setText(dogAge);
        final TextView textView4 = (TextView) dialogView.findViewById(R.id.textViewEdit4);
        final EditText editText4 = (EditText) dialogView.findViewById(R.id.newValue4);
        editText4.setText(dogColor);
        final TextView textView5 = (TextView) dialogView.findViewById(R.id.textViewEdit5);
        final EditText editText5 = (EditText) dialogView.findViewById(R.id.newValue5);
        editText5.setText(dogSize);
        final TextView textView6 = (TextView) dialogView.findViewById(R.id.textViewEdit6);
        final EditText editText6 = (EditText) dialogView.findViewById(R.id.newValue6);
        editText6.setText(dogMating);
        final Button button = (Button) dialogView.findViewById(R.id.buttonUpdate);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString();
                String breed = editText2.getText().toString();
                String age = editText3.getText().toString();
                String color = editText4.getText().toString();
                String size = editText5.getText().toString();
                String mating = editText6.getText().toString();

                if (TextUtils.isEmpty(name) && TextUtils.isEmpty(breed) && TextUtils.isEmpty(age)
                        && TextUtils.isEmpty(color) && TextUtils.isEmpty(size) && TextUtils.isEmpty(mating)) {
                    Toast.makeText(DogDetailsActivity.this, "Field empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    name = dogName;
                }
                if (TextUtils.isEmpty(breed)) {
                    breed = dogBreed;
                }
                if (TextUtils.isEmpty(age)) {
                    age = dogAge;
                }
                if (TextUtils.isEmpty(color)) {
                    color = dogColor;
                }
                if (TextUtils.isEmpty(size)) {
                    size = dogSize;
                }
                if (TextUtils.isEmpty(mating)) {
                    mating = dogMating;
                }
                updateDog(name, dogOwner, breed, age, color, size, mating, dogPhoto);
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private boolean updateDog(String name, String owner, String breed, String age, String color, String size, String mating, String photo) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("dog").child(dogID);
        Dog dog = new Dog(name, owner, breed, age, color, size, mating, photo);
        reference.setValue(dog);
        Toast.makeText(this, "Dog Updated! Please refresh to see changes", Toast.LENGTH_SHORT).show();
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
}
