package com.tutorial.athina.pethood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tutorial.athina.pethood.Models.Canisite;
import com.tutorial.athina.pethood.Models.Dog;
import com.tutorial.athina.pethood.Models.Tracking;

import java.util.ArrayList;

public class MapTracking extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DatabaseReference locations, counterRef, canisiteRef, dogsRef;
    private ArrayList<String> userList;
    private ArrayList<String> filterUserList;
    String breed, size, mating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMap);
        toolbar.setTitle("Doggy Map");
        this.setSupportActionBar(toolbar);

        userList = new ArrayList<>();
        filterUserList = new ArrayList<>();

        locations = FirebaseDatabase.getInstance().getReference().child("Locations");
        counterRef = FirebaseDatabase.getInstance().getReference().child("lastOnline");
        canisiteRef = FirebaseDatabase.getInstance().getReference().child("canisite");
        dogsRef = FirebaseDatabase.getInstance().getReference().child("dog");


    }


    private void loadLocationsForUsers() {

        for (String user : userList) {
            Query user_location = locations.orderByChild("email").equalTo(user);
            user_location.addValueEventListener(new ValueEventListener() {
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        Tracking tracking = postSnapshot.getValue(Tracking.class);

                        LatLng userLocation = new LatLng(Double.parseDouble(tracking.getLat()),
                                Double.parseDouble(tracking.getLng()));


                        mMap.addMarker(new MarkerOptions()
                                .position(userLocation)
                                .title(tracking.getEmail())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12.0f));
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        canisiteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Canisite canisite = data.getValue(Canisite.class);

                    LatLng canisitaLocation = new LatLng(canisite.getLat(), canisite.getLng());

                    mMap.addMarker(new MarkerOptions()
                            .position(canisitaLocation)
                            .title(canisite.getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void loadPersonalizeMap() {
        for (final String user : filterUserList) {
            Query user_breeds = dogsRef.orderByChild("dogBreed").equalTo(breed);
            user_breeds.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Dog dog = postSnapshot.getValue(Dog.class);
                        if (dog.getDogOwner().equals(user)) {
                            Query user_location = locations.orderByChild("email").equalTo(user);
                            user_location.addValueEventListener(new ValueEventListener() {
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                        Tracking tracking = postSnapshot.getValue(Tracking.class);

                                        LatLng userLocation = new LatLng(Double.parseDouble(tracking.getLat()),
                                                Double.parseDouble(tracking.getLng()));


                                        mMap.addMarker(new MarkerOptions()
                                                .position(userLocation)
                                                .title(tracking.getEmail())
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12.0f));
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        canisiteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Canisite canisite = data.getValue(Canisite.class);

                    LatLng canisitaLocation = new LatLng(canisite.getLat(), canisite.getLng());

                    mMap.addMarker(new MarkerOptions()
                            .position(canisitaLocation)
                            .title(canisite.getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (getIntent().hasExtra("breed")) {
            filterUserList = getIntent().getStringArrayListExtra("userList");
            breed = getIntent().getStringExtra("breed");
            loadPersonalizeMap();
        }
        else if (getIntent().hasExtra("userList")) {
            userList = getIntent().getStringArrayListExtra("userList");
            loadLocationsForUsers();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_map, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                Intent online = new Intent(MapTracking.this, FiltersActivity.class);
                online.putStringArrayListExtra("userList", userList);
                startActivity(online);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}