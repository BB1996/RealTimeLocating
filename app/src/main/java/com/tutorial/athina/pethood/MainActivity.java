package com.tutorial.athina.pethood;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mailUser, passwordUser;
    private Button loginButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        mailUser = (EditText) findViewById(R.id.email);
        passwordUser = (EditText) findViewById(R.id.password);

        loginButton = (Button) findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(this);

        registerButton = (Button) findViewById(R.id.btnRegister);
        registerButton.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        DbHelperLogin dbHelper = new DbHelperLogin(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] email = {mailUser.getText().toString()};
        String[] password = {passwordUser.getText().toString()};

        String queryMail = "SELECT * FROM" + " login" + " where " + " EMAIL = ?";
        String queryPassword = "SELECT * FROM " + "login" + " where " + "PASS = ?";
        Cursor cursorEmail = db.rawQuery(queryMail, email);
        Cursor cursorPassword = db.rawQuery(queryPassword, password);

        switch (v.getId()) {
            case R.id.btnLogin:
                if (cursorEmail.getCount() <= 0) {
                    cursorEmail.close();
                    Toast.makeText(MainActivity.this, "PLEASE REGISTER",
                            Toast.LENGTH_LONG).show();
                } else {
                    if (cursorPassword.getCount() <= 0) {
                        cursorPassword.close();
                        Toast.makeText(MainActivity.this, "WRONG PASSWORD", Toast.LENGTH_LONG).show();
                    }
                    else{
                        startActivity(new Intent(this, MapTracking.class));
                    }
                }
                break;
            case R.id.btnRegister:
                startActivity(new Intent(this, RegisterActivity.class));
            default:
                break;
        }

    }
}
