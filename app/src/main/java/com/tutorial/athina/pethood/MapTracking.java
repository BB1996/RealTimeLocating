package com.tutorial.athina.pethood;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapTracking extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String email;
    Double lat, lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    private void loadLocationForThisUser(String email) {


        Location currentUser = new Location("");
        currentUser.setLatitude(lat);
        currentUser.setLongitude(lng);

        LatLng current = new LatLng(lat, lng);

        mMap.addMarker(new MarkerOptions()
                .position(current)
                .title(email)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 12.0f));

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (getIntent() != null) {

            email = getIntent().getStringExtra("email");
            lat = getIntent().getDoubleExtra("lat", 0);
            lng = getIntent().getDoubleExtra("lng", 0);
        }

        if (!TextUtils.isEmpty(email)) {
            loadLocationForThisUser(email);
        }
    }
}