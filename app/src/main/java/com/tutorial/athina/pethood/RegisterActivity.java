package com.tutorial.athina.pethood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tutorial.athina.pethood.Models.Owner;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button registerUserButton;
    private EditText emailText;
    private EditText passwordText, passwordText2;
    private EditText nameText, surnameText, phoneText;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ownerRef;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        firebaseAuth = FirebaseAuth.getInstance();
        ownerRef = FirebaseDatabase.getInstance().getReference().child("Owners");
//        if (firebaseAuth.getCurrentUser() != null) {
//            finish();
//            startActivity(new Intent(getApplicationContext(), MapTracking.class));
//        }

        progressDialog = new ProgressDialog(this);
        registerUserButton = (Button) findViewById(R.id.nextButton);

        emailText = (EditText) findViewById(R.id.emailText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        passwordText2 = (EditText) findViewById(R.id.passwordText2);
        nameText = (EditText) findViewById(R.id.nameUser);
        surnameText = (EditText) findViewById(R.id.surnameUser);
        phoneText = (EditText) findViewById(R.id.phoneUser);

        registerUserButton.setOnClickListener(this);

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
        Owner owner = new Owner(name, surname, phone, email);
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
