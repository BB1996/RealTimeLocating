package com.tutorial.athina.pethood;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

public class RegisterDogActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static final String TAG = "Register Dog Activity";
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


    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();


        switch (view.getId()) {
            case R.id.dogSizeSmall:
                if (checked) {

                }
                break;
            case R.id.dogSizeMedium:
                if (checked) {

                }

                break;
            case R.id.dogSizeLarge:
                if (checked) {

                }

                break;
            default:
                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {

        } else {

        }
    }
}
