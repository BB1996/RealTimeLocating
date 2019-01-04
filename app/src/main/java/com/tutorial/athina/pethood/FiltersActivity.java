package com.tutorial.athina.pethood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tutorial.athina.pethood.Models.Breeds;

import java.util.ArrayList;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class FiltersActivity extends AppCompatActivity implements View.OnClickListener {

    private String checkboxSize, checkboxMating, checkBreed;
    private DatabaseReference breedsRef;
    private Button applyFilters;
    private ArrayList<String> userList;
    private ArrayList<String> breedList;
    private SpinnerDialog spinnerDialog;
    private Button buttonSearchBreed;

    private CheckBox dogSmall, dogMedium, dogLarge, matingYes, matingNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filters_layout);

        checkboxSize = "";
        checkboxMating = "";
        checkBreed = "";
        userList = new ArrayList<>();
        breedList = new ArrayList<>();

        breedsRef = FirebaseDatabase.getInstance().getReference().child("breeds");

        initializeBreeds();
        dogSmall = (CheckBox) findViewById(R.id.dogSizeSmallSearch);
        dogMedium = (CheckBox) findViewById(R.id.dogSizeMediumSearch);
        dogLarge = (CheckBox) findViewById(R.id.dogSizeBigSearch);

        matingYes = (CheckBox) findViewById(R.id.yesMating);
        matingNo = (CheckBox) findViewById(R.id.noMating);

        applyFilters = (Button) findViewById(R.id.buttonApplyFilters);
        buttonSearchBreed = (Button) findViewById(R.id.searchBreedButton);

        spinnerDialog = new SpinnerDialog(FiltersActivity.this, breedList, "Select Breed");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String s, int i) {
                checkBreed = s;
                Toast.makeText(FiltersActivity.this, "Selected: " + s, Toast.LENGTH_SHORT).show();
            }
        });

        if (getIntent().hasExtra("userList")) {
            userList = getIntent().getStringArrayListExtra("userList");
        }


        applyFilters.setOnClickListener(this);
        buttonSearchBreed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog.showSpinerDialog();
            }
        });

    }

    private void initializeBreeds() {

        breedsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Breeds breeds = data.getValue(Breeds.class);
                    breedList.add(breeds.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();


        switch (view.getId()) {
            case R.id.dogSizeSmallSearch:
                if (checked) {
                    checkboxSize = "Small";
                }
                break;
            case R.id.dogSizeMediumSearch:
                if (checked) {
                    checkboxSize = "Medium";
                }

                break;
            case R.id.dogSizeBigSearch:
                if (checked) {
                    checkboxSize = "Big";
                }

                break;
            case R.id.yesMating:
                if (checked) {
                    checkboxMating = "Y";
                }

                break;
            case R.id.noMating:
                if (checked) {
                    checkboxMating = "N";
                }

                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonApplyFilters) {
            Intent filtersIntent = new Intent(FiltersActivity.this, MapTracking.class);
            filtersIntent.putExtra("size", checkboxSize);
            filtersIntent.putExtra("breed", checkBreed);
            filtersIntent.putExtra("mating", checkboxMating);
            filtersIntent.putStringArrayListExtra("userList", (ArrayList<String>) userList);
            startActivity(filtersIntent);
        }
    }


}
