package com.tutorial.athina.pethood;

import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tutorial.athina.pethood.Models.Owner;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CHOOSE_IMAGE = 102;
    private Button registerUserButton;
    private EditText emailText;
    private EditText passwordText, passwordText2;
    private EditText nameText, surnameText, phoneText;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ownerRef;
    private String email;
    private ImageView imageViewCamera;
    private Uri uriProfileImage;
    private String profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>User Details</font>"));

        firebaseAuth = FirebaseAuth.getInstance();
        ownerRef = FirebaseDatabase.getInstance().getReference().child("Owners");


        progressDialog = new ProgressDialog(this);
        registerUserButton = (Button) findViewById(R.id.nextButton);

        emailText = (EditText) findViewById(R.id.emailText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        passwordText2 = (EditText) findViewById(R.id.passwordText2);
        nameText = (EditText) findViewById(R.id.nameUser);
        surnameText = (EditText) findViewById(R.id.surnameUser);
        phoneText = (EditText) findViewById(R.id.phoneUser);

        registerUserButton.setOnClickListener(this);
        imageViewCamera = (ImageView) findViewById(R.id.imageViewCamera);
        imageViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
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

    @Override
    public void onClick(View view) {
        if (view == registerUserButton) {
            registerUser();
        }

    }

    private void registerUser() {
        email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        String password2 = passwordText2.getText().toString().trim();

        String name = nameText.getText().toString().trim();
        String surname = surnameText.getText().toString().trim();
        String phone = phoneText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!password.equals(password2)) {
            Toast.makeText(this, "Passwords doesn't match!", Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.setMessage("Please wait....");
        progressDialog.show();

        String id = ownerRef.push().getKey();
        Owner owner = new Owner(name, surname, phone, email,profileImageUrl);
        ownerRef.child(id).setValue(owner);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Succes!", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent registerIntent = new Intent(getApplicationContext(), RegisterDogActivity.class);
                            startActivity(registerIntent);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Account already created!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
