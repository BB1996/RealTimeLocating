package com.tutorial.athina.pethood;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

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

import java.util.ArrayList;

public class MapTracking extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String email;
    Double lat, lng;
    DatabaseReference locations, counterRef;
    private ArrayList<String> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        userList = new ArrayList<>();
        locations = FirebaseDatabase.getInstance().getReference().child("Locations");
        counterRef = FirebaseDatabase.getInstance().getReference().child("lastOnline");


    }


    private void loadLocationForThisUser() {

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
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12.0f));
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(getIntent() != null){
            userList = getIntent().getStringArrayListExtra("userList");
        }

        loadLocationForThisUser();

    }
}