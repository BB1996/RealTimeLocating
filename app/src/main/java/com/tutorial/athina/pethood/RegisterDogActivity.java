package com.tutorial.athina.pethood;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

public class RegisterDogActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static final String TAG = "Register Dog Activity";
    private String switchResponse;
    private String checkboxResponse;
    private EditText dogName, dogOwner, dogBreed, dogColor, dogAge;
    private Button registerDogButton;
    private CheckBox dogSmall, dogMedium, dogLarge;
    private Switch mateSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_dog_layout);

        dogName = (EditText) findViewById(R.id.dogName);
        dogOwner = (EditText) findViewById(R.id.dogOwner);
        dogBreed = (EditText) findViewById(R.id.dogBreed);
        dogColor = (EditText) findViewById(R.id.dogColor);
        dogAge = (EditText) findViewById(R.id.dogAge);

        dogSmall = (CheckBox) findViewById(R.id.dogSizeSmall);
        dogMedium = (CheckBox) findViewById(R.id.dogSizeMedium);
        dogLarge = (CheckBox) findViewById(R.id.dogSizeLarge);

        mateSwitch = (Switch) findViewById(R.id.dogMating);
        mateSwitch.setOnCheckedChangeListener(this);

        registerDogButton = (Button) findViewById(R.id.registerDogBtn);
        registerDogButton.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {

        DbHelperDogs dbHelper = new DbHelperDogs(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valuesDog = new ContentValues();

        String queryDogs = "SELECT _id from " + "dogs " + " order by _id desc limit 1";
        Cursor cursorCountDogs = db.rawQuery(queryDogs,null);

        valuesDog.clear();
        if (cursorCountDogs.moveToFirst()){

            String lastID = cursorCountDogs.getString(cursorCountDogs.getColumnIndex("_id"));
            int lastIDInt = Integer.parseInt(lastID);
            valuesDog.put(DogsContract.Dogs.ID, ++lastIDInt);

        }

        else{
            valuesDog.put(DogsContract.Dogs.ID, 1);
        }

        cursorCountDogs.close();
        valuesDog.put(DogsContract.Dogs.NAME, dogName.getText().toString());
        valuesDog.put(DogsContract.Dogs.OWNER, dogOwner.getText().toString());
        valuesDog.put(DogsContract.Dogs.BREED, dogBreed.getText().toString());
        valuesDog.put(DogsContract.Dogs.SIZE, checkboxResponse);
        valuesDog.put(DogsContract.Dogs.MATE_FLAG, switchResponse);
        valuesDog.put(DogsContract.Dogs.COLOR, dogColor.getText().toString());
        valuesDog.put(DogsContract.Dogs.AGE, dogAge.getText().toString());

        Uri uriDogs = getContentResolver().insert(DogsContract.CONTENT_URI, valuesDog);

        if (uriDogs != null) {
            Log.d(TAG, String.format("%s %s %s %s %s %s %s",dogName.getText().toString(),dogOwner.getText().toString(),dogBreed.getText().toString(),checkboxResponse,
                    switchResponse,dogColor.getText().toString(),dogAge.getText().toString()));
        }
        startActivity(new Intent(this,MainActivity.class));
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();


        switch (view.getId()) {
            case R.id.dogSizeSmall:
                if (checked) {
                    checkboxResponse = "Small";                }
                break;
            case R.id.dogSizeMedium:
                if (checked) {
                    checkboxResponse = "Medium";
                }

                break;
            case R.id.dogSizeLarge:
                if (checked) {
                    checkboxResponse = "Large";
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
}
