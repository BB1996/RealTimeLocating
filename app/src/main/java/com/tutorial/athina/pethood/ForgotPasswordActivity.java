package com.tutorial.athina.pethood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailText;
    private Button sendMailButton;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.forgot_password_activity);

        progressDialog = new ProgressDialog(this);

        emailText = (EditText) findViewById(R.id.forgotPasswordMail);

        sendMailButton = (Button) findViewById(R.id.sendMailButton);

        firebaseAuth = FirebaseAuth.getInstance();

        sendMailButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String userMail = emailText.getText().toString().trim();

        if (userMail.equals("")) {
            Toast.makeText(ForgotPasswordActivity.this, "Please enter your e-mail!", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.sendPasswordResetEmail(userMail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Password reset mail has been sent!", Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Error sending mail. Please refresh!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }
}
