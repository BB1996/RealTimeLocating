package com.tutorial.athina.pethood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tutorial.athina.pethood.Models.Breeds;
import com.tutorial.athina.pethood.Models.Dog;

import java.io.IOException;

public class RegisterDogActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static final String TAG = "Register Dog Activity";
    private static final int CHOOSE_IMAGE = 101;
    DatabaseReference databaseDog, databaseBreeds;
    private String switchResponse;
    private String checkboxResponse;
    private EditText dogName, dogOwner, dogBreed, dogColor, dogAge;
    private Button registerDogButton, addPictureButton;
    private CheckBox dogSmall, dogMedium, dogLarge;
    private Switch mateSwitch;
    private int duplicateResult = 0;
    private String name, emailOwner, breed, color, age;
    private ImageView imageViewCamera;
    private Uri uriProfileImage;
    private String profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerdog_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Dog Details</font>"));

        dogName = (EditText) findViewById(R.id.dogNameText);
        dogOwner = (EditText) findViewById(R.id.dogOwnerEmailText);
        dogBreed = (EditText) findViewById(R.id.dogBreedText);
        dogColor = (EditText) findViewById(R.id.dogColorText);
        dogAge = (EditText) findViewById(R.id.dogAgeText);

        dogSmall = (CheckBox) findViewById(R.id.dogSizeSmall);
        dogMedium = (CheckBox) findViewById(R.id.dogSizeMedium);
        dogLarge = (CheckBox) findViewById(R.id.dogSizeBig);

        mateSwitch = (Switch) findViewById(R.id.matingSwitch);
        mateSwitch.setOnCheckedChangeListener(this);

        databaseDog = FirebaseDatabase.getInstance().getReference("dog");
        databaseBreeds = FirebaseDatabase.getInstance().getReference("breeds");

        registerDogButton = (Button) findViewById(R.id.registerButton);
        registerDogButton.setOnClickListener(this);


        imageViewCamera = (ImageView) findViewById(R.id.imageViewCamera);
        imageViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();


        switch (view.getId()) {
            case R.id.dogSizeSmall:
                if (checked) {
                    checkboxResponse = "Small";
                }
                break;
            case R.id.dogSizeMedium:
                if (checked) {
                    checkboxResponse = "Medium";
                }

                break;
            case R.id.dogSizeBig:
                if (checked) {
                    checkboxResponse = "Big";
                }

                break;
            default:
                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switchResponse = "Y";
        } else {
            switchResponse = "N";
        }
    }

    public void addDog() {

        name = dogName.getText().toString().trim();
        emailOwner = dogOwner.getText().toString().trim();
        breed = dogBreed.getText().toString().trim();
        color = dogColor.getText().toString().trim();
        age = dogAge.getText().toString().trim();


        if (!TextUtils.isEmpty(name)) {

            String id = databaseDog.push().getKey();
            Dog dog = new Dog(name, emailOwner, breed, age, color, checkboxResponse, switchResponse, profileImageUrl);
            databaseDog.child(id).setValue(dog);


            databaseBreeds.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Breeds breeds = data.getValue(Breeds.class);
                        if (breeds.getName().equals(breed)) {
                            duplicateResult = 1;
                        }
                    }
                    if (duplicateResult != 1) {
                        String idBreed = databaseBreeds.push().getKey();
                        Breeds addedBreed = new Breeds(breed);
                        databaseBreeds.child(idBreed).setValue(addedBreed);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            duplicateResult = 0;
            Toast.makeText(this, "Dog Added!", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "Enter dog details!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.registerButton:
                addDog();
                duplicateResult = 0;
                startActivity(new Intent(this, LoginActivity.class));
                break;


        }

    }

    @Override
    protected void onPause() {

        super.onPause();
        name = dogName.getText().toString().trim();
        emailOwner = dogOwner.getText().toString().trim();
        breed = dogBreed.getText().toString().trim();
        color = dogColor.getText().toString().trim();
        age = dogAge.getText().toString().trim();
    }

    @Override
    protected void onResume() {

        super.onResume();
        dogName.setText(name);
        dogOwner.setText(emailOwner);
        dogBreed.setText(breed);
        dogColor.setText(color);
        dogAge.setText(age);
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Dog Profile Image"), CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imageViewCamera.setImageBitmap(bitmap);
                uploadImagetoFirebase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImagetoFirebase() {
        StorageReference profileImgRef = FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");

        if (uriProfileImage != null) {

            profileImgRef.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                             profileImageUrl = uri.toString();
                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getApplicationContext(), "Failed to upload image!", Toast.LENGTH_SHORT).show();

                        }
                    });
        }

    }
}

