package com.tutorial.athina.realtimelocating;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    public static final int LOGIN_PERMISSON = 1000;
    public static final int MY_PERMISSON_REQUEST_CODE = 7171;
    Button buttonLogIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonLogIn = (Button) findViewById(R.id.buttonSignIn);
        buttonLogIn = (Button) findViewById(R.id.buttonSignIn);
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent loginIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                        .setAvailableProviders(Collections.singletonList(new AuthUI.IdpConfig.EmailBuilder().setAllowNewAccounts(true).build()))
                        .build();
                startActivityForResult(loginIntent, LOGIN_PERMISSON);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == LOGIN_PERMISSON) {
            startNewActivity(resultCode, data);
        }
    }

    private void startNewActivity(int resultCode, Intent data) {

        if(resultCode ==RESULT_OK){
            Intent intent = new Intent(MainActivity.this,ListOnline.class);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(this,"Login Failed!",Toast.LENGTH_SHORT).show();

        }
    }
}
