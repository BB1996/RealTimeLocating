package com.tutorial.athina.pethood;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tutorial.athina.pethood.DirectionsHelper.TaskLoadedCallback;
import com.tutorial.athina.pethood.Models.AbandonedDog;
import com.tutorial.athina.pethood.Models.Canisite;
import com.tutorial.athina.pethood.Models.Dog;
import com.tutorial.athina.pethood.Models.PetShop;
import com.tutorial.athina.pethood.Models.Tracking;
import com.tutorial.athina.pethood.Models.User;
import com.tutorial.athina.pethood.Models.Vet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapTracking extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, TaskLoadedCallback {

    private GoogleMap mMap;
    DatabaseReference locations, counterRef, canisiteRef, dogsRef, petShopRef, abadonedDogRef, vetRef;
    String breed, size, mating;
    private Button abandonedButoon;
    //  private Double latLng, lngLat;
    private List<Marker> abandonedMarker;
    private List<String> abandonedUID;
    public static final int MY_PERMISSON_REQUEST_CODE = 7171;
    public static final int PLAY_SERVICES_RES_REQUEST = 7172;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISTANCE = 10;
    private Location mLastLocation = new Location("dummy");
    Marker userMarker;
    HashMap<String, Marker> otherMarkers;
    Polyline currentPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tracking);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMap);
        toolbar.setTitle("Doggy Map");
        this.setSupportActionBar(toolbar);

        abandonedMarker = new ArrayList<>();
        abandonedButoon = (Button) findViewById(R.id.abadonedDogBtn);
        otherMarkers = new HashMap<>();

        abandonedUID = new ArrayList<>();

        locations = FirebaseDatabase.getInstance().getReference().child("Locations");
        counterRef = FirebaseDatabase.getInstance().getReference().child("lastOnline");
        canisiteRef = FirebaseDatabase.getInstance().getReference().child("canisite");
        dogsRef = FirebaseDatabase.getInstance().getReference().child("dog");
        petShopRef = FirebaseDatabase.getInstance().getReference().child("petShops");
        abadonedDogRef = FirebaseDatabase.getInstance().getReference().child("abandonedDog");
        vetRef = FirebaseDatabase.getInstance().getReference().child("vets");


        abandonedButoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String id = abadonedDogRef.push().getKey();
                AbandonedDog abandonedDog = new AbandonedDog(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                abadonedDogRef.child(id).setValue(abandonedDog);

            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSON_REQUEST_CODE);
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();

            }
        }

    }


    private void loadLocationsForUsers() {

        counterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User user = dataSnapshot1.getValue(User.class);
                    Query user_location = locations.orderByChild("email").equalTo(user.getEmail());
                    user_location.addValueEventListener(new ValueEventListener() {
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                Tracking tracking = postSnapshot.getValue(Tracking.class);

                                LatLng userLocation = new LatLng(Double.parseDouble(tracking.getLat()),
                                        Double.parseDouble(tracking.getLng()));

                                if (tracking.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                    if (userMarker != null) {
                                        userMarker.remove();
                                    }

                                    userMarker = mMap.addMarker(new MarkerOptions()
                                            .position(userLocation)
                                            .title(tracking.getEmail())
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mydog)));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12.0f));

                                } else {

                                    if (otherMarkers.containsKey(tracking.getEmail())) {
                                        Marker value = otherMarkers.get(tracking.getEmail());
                                        value.remove();
                                        otherMarkers.replace(tracking.getEmail(), (mMap.addMarker(new MarkerOptions()
                                                .position(userLocation)
                                                .title(tracking.getEmail())
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.dogpawn)))));

                                    } else {
                                        otherMarkers.put(tracking.getEmail(), (mMap.addMarker(new MarkerOptions()
                                                .position(userLocation)
                                                .title(tracking.getEmail())
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.dogpawn)))));
                                    }

                                }


                                loadCanisite();
                                loadPetShops();
                                loadAbandonedDog();
                                loadVets();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void loadPersonalizeMap() {

        counterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User user = dataSnapshot1.getValue(User.class);
                    allFiltersMap(user.getEmail(), breed, mating, size);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        alwaysLoadUser();
        loadCanisite();
        loadPetShops();
        loadAbandonedDog();
        loadVets();
    }

    private void allFiltersMap(final String user, final String breed, final String mating, final String size) {

        Query allFilters = dogsRef.orderByChild("dogOwner").equalTo(user);
        allFilters.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Dog dog = postSnapshot.getValue(Dog.class);
                    if (breed != "" && mating.equals("") && size.equals("")) {
                        if (dog.getDogOwner().equals(user) && dog.getDogBreed().equals(breed)) {
                            Query user_location = locations.orderByChild("email").equalTo(user);
                            user_location.addValueEventListener(new ValueEventListener() {
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                        Tracking tracking = postSnapshot.getValue(Tracking.class);

                                        LatLng userLocation = new LatLng(Double.parseDouble(tracking.getLat()),
                                                Double.parseDouble(tracking.getLng()));


                                        if (tracking.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                            if (userMarker != null) {
                                                userMarker.remove();
                                            }

                                            userMarker = mMap.addMarker(new MarkerOptions()
                                                    .position(userLocation)
                                                    .title(tracking.getEmail())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mydog)));
                                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12.0f));

                                        } else {

                                            if (otherMarkers.containsKey(tracking.getEmail())) {
                                                Marker value = otherMarkers.get(tracking.getEmail());
                                                value.remove();
                                                otherMarkers.replace(tracking.getEmail(), (mMap.addMarker(new MarkerOptions()
                                                        .position(userLocation)
                                                        .title(tracking.getEmail())
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dogpawn)))));


                                            } else {
                                                otherMarkers.put(tracking.getEmail(), (mMap.addMarker(new MarkerOptions()
                                                        .position(userLocation)
                                                        .title(tracking.getEmail())
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dogpawn)))));
                                            }

                                        }

                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    if (size != "" && breed.equals("") && mating.equals("")) {
                        if (dog.getDogOwner().equals(user) && dog.getDogSize().equals(size)) {
                            Query user_location = locations.orderByChild("email").equalTo(user);
                            user_location.addValueEventListener(new ValueEventListener() {
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                        Tracking tracking = postSnapshot.getValue(Tracking.class);

                                        LatLng userLocation = new LatLng(Double.parseDouble(tracking.getLat()),
                                                Double.parseDouble(tracking.getLng()));


                                        if (tracking.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                            if (userMarker != null) {
                                                userMarker.remove();
                                            }

                                            userMarker = mMap.addMarker(new MarkerOptions()
                                                    .position(userLocation)
                                                    .title(tracking.getEmail())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mydog)));
                                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12.0f));

                                        } else {

                                            if (otherMarkers.containsKey(tracking.getEmail())) {
                                                Marker value = otherMarkers.get(tracking.getEmail());
                                                value.remove();
                                                otherMarkers.replace(tracking.getEmail(), (mMap.addMarker(new MarkerOptions()
                                                        .position(userLocation)
                                                        .title(tracking.getEmail())
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dogpawn)))));

                                            } else {
                                                otherMarkers.put(tracking.getEmail(), (mMap.addMarker(new MarkerOptions()
                                                        .position(userLocation)
                                                        .title(tracking.getEmail())
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dogpawn)))));
                                            }

                                        }

                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    if (mating != "" && breed.equals("") && size.equals("")) {
                        if (dog.getDogOwner().equals(user) && dog.getDogMateFlag().equals(mating)) {
                            Query user_location = locations.orderByChild("email").equalTo(user);
                            user_location.addValueEventListener(new ValueEventListener() {
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                        Tracking tracking = postSnapshot.getValue(Tracking.class);

                                        LatLng userLocation = new LatLng(Double.parseDouble(tracking.getLat()),
                                                Double.parseDouble(tracking.getLng()));


                                        if (tracking.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                            if (userMarker != null) {
                                                userMarker.remove();
                                            }

                                            userMarker = mMap.addMarker(new MarkerOptions()
                                                    .position(userLocation)
                                                    .title(tracking.getEmail())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mydog)));
                                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12.0f));

                                        } else {

                                            if (otherMarkers.containsKey(tracking.getEmail())) {
                                                Marker value = otherMarkers.get(tracking.getEmail());
                                                value.remove();
                                                otherMarkers.replace(tracking.getEmail(), (mMap.addMarker(new MarkerOptions()
                                                        .position(userLocation)
                                                        .title(tracking.getEmail())
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dogpawn)))));

                                            } else {
                                                otherMarkers.put(tracking.getEmail(), (mMap.addMarker(new MarkerOptions()
                                                        .position(userLocation)
                                                        .title(tracking.getEmail())
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dogpawn)))));
                                            }

                                        }

                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    if (breed != "" && mating != "" && size.equals("")) {
                        if (dog.getDogOwner().equals(user) && dog.getDogBreed().equals(breed) && dog.getDogMateFlag().equals(mating)) {
                            Query user_location = locations.orderByChild("email").equalTo(user);
                            user_location.addValueEventListener(new ValueEventListener() {
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                        Tracking tracking = postSnapshot.getValue(Tracking.class);

                                        LatLng userLocation = new LatLng(Double.parseDouble(tracking.getLat()),
                                                Double.parseDouble(tracking.getLng()));


                                        if (tracking.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                            if (userMarker != null) {
                                                userMarker.remove();
                                            }

                                            userMarker = mMap.addMarker(new MarkerOptions()
                                                    .position(userLocation)
                                                    .title(tracking.getEmail())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mydog)));
                                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12.0f));

                                        } else {

                                            if (otherMarkers.containsKey(tracking.getEmail())) {
                                                Marker value = otherMarkers.get(tracking.getEmail());
                                                value.remove();
                                                otherMarkers.replace(tracking.getEmail(), (mMap.addMarker(new MarkerOptions()
                                                        .position(userLocation)
                                                        .title(tracking.getEmail())
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dogpawn)))));

                                            } else {
                                                otherMarkers.put(tracking.getEmail(), (mMap.addMarker(new MarkerOptions()
                                                        .position(userLocation)
                                                        .title(tracking.getEmail())
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dogpawn)))));
                                            }

                                        }

                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    if (breed != "" && size != "" && mating.equals("")) {
                        if (dog.getDogOwner().equals(user) && dog.getDogBreed().equals(breed) && dog.getDogSize().equals(size)) {
                            Query user_location = locations.orderByChild("email").equalTo(user);
                            user_location.addValueEventListener(new ValueEventListener() {
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                        Tracking tracking = postSnapshot.getValue(Tracking.class);

                                        LatLng userLocation = new LatLng(Double.parseDouble(tracking.getLat()),
                                                Double.parseDouble(tracking.getLng()));


                                        if (tracking.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                            if (userMarker != null) {
                                                userMarker.remove();
                                            }

                                            userMarker = mMap.addMarker(new MarkerOptions()
                                                    .position(userLocation)
                                                    .title(tracking.getEmail())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mydog)));
                                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12.0f));

                                        } else {

                                            if (otherMarkers.containsKey(tracking.getEmail())) {
                                                Marker value = otherMarkers.get(tracking.getEmail());
                                                value.remove();
                                                otherMarkers.replace(tracking.getEmail(), (mMap.addMarker(new MarkerOptions()
                                                        .position(userLocation)
                                                        .title(tracking.getEmail())
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dogpawn)))));

                                            } else {
                                                otherMarkers.put(tracking.getEmail(), (mMap.addMarker(new MarkerOptions()
                                                        .position(userLocation)
                                                        .title(tracking.getEmail())
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dogpawn)))));
                                            }

                                        }

                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    if (size != "" && mating != "" && breed.equals("")) {
                        if (dog.getDogOwner().equals(user) && dog.getDogSize().equals(size) && dog.getDogMateFlag().equals(mating)) {
                            Query user_location = locations.orderByChild("email").equalTo(user);
                            user_location.addValueEventListener(new ValueEventListener() {
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                        Tracking tracking = postSnapshot.getValue(Tracking.class);

                                        LatLng userLocation = new LatLng(Double.parseDouble(tracking.getLat()),
                                                Double.parseDouble(tracking.getLng()));


                                        if (tracking.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                            if (userMarker != null) {
                                                userMarker.remove();
                                            }

                                            userMarker = mMap.addMarker(new MarkerOptions()
                                                    .position(userLocation)
                                                    .title(tracking.getEmail())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mydog)));
                                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12.0f));

                                        } else {

                                            if (otherMarkers.containsKey(tracking.getEmail())) {
                                                Marker value = otherMarkers.get(tracking.getEmail());
                                                value.remove();
                                                otherMarkers.replace(tracking.getEmail(), (mMap.addMarker(new MarkerOptions()
                                                        .position(userLocation)
                                                        .title(tracking.getEmail())
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dogpawn)))));

                                            } else {
                                                otherMarkers.put(tracking.getEmail(), (mMap.addMarker(new MarkerOptions()
                                                        .position(userLocation)
                                                        .title(tracking.getEmail())
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dogpawn)))));
                                            }

                                        }

                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    if (size != "" && mating != "" && breed != "") {
                        if (dog.getDogOwner().equals(user) && dog.getDogSize().equals(size) && dog.getDogMateFlag().equals(mating)
                                && dog.getDogBreed().equals(breed)) {
                            Query user_location = locations.orderByChild("email").equalTo(user);
                            user_location.addValueEventListener(new ValueEventListener() {
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                        Tracking tracking = postSnapshot.getValue(Tracking.class);

                                        LatLng userLocation = new LatLng(Double.parseDouble(tracking.getLat()),
                                                Double.parseDouble(tracking.getLng()));


                                        if (tracking.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                            if (userMarker != null) {
                                                userMarker.remove();
                                            }

                                            userMarker = mMap.addMarker(new MarkerOptions()
                                                    .position(userLocation)
                                                    .title(tracking.getEmail())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mydog)));
                                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12.0f));

                                        } else {

                                            if (otherMarkers.containsKey(tracking.getEmail())) {
                                                Marker value = otherMarkers.get(tracking.getEmail());
                                                value.remove();
                                                otherMarkers.replace(tracking.getEmail(), (mMap.addMarker(new MarkerOptions()
                                                        .position(userLocation)
                                                        .title(tracking.getEmail())
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dogpawn)))));

                                            } else {
                                                otherMarkers.put(tracking.getEmail(), (mMap.addMarker(new MarkerOptions()
                                                        .position(userLocation)
                                                        .title(tracking.getEmail())
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dogpawn)))));
                                            }

                                        }

                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadCanisite() {
        canisiteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Canisite canisite = data.getValue(Canisite.class);

                    LatLng canisitaLocation = new LatLng(canisite.getLat(), canisite.getLng());

                    Marker canisitaMarker = mMap.addMarker(new MarkerOptions()
                            .position(canisitaLocation)
                            .title(canisite.getName())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.canisita)));


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadPetShops() {
        petShopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    PetShop petShop = data.getValue(PetShop.class);

                    LatLng petShopLocation = new LatLng(petShop.getLat(), petShop.getLng());

                    mMap.addMarker(new MarkerOptions()
                            .position(petShopLocation)
                            .title(petShop.getName())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.petshop)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void alwaysLoadUser() {

        Query actualUser = locations.orderByChild("email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        actualUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Tracking tracking = data.getValue(Tracking.class);

                    LatLng myLocation = new LatLng(Double.parseDouble(tracking.getLat()), Double.parseDouble(tracking.getLng()));

                    if (userMarker != null) {
                        userMarker.remove();
                    }

                    userMarker = mMap.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title(tracking.getEmail())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mydog)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12.0f));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadVets() {
        vetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Vet vet = data.getValue(Vet.class);

                    LatLng vetLocation = new LatLng(vet.getLat(), vet.getLng());

                    mMap.addMarker(new MarkerOptions()
                            .position(vetLocation)
                            .title(vet.getName())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.vet)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadAbandonedDog() {

        abadonedDogRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    AbandonedDog abandonedDog = data.getValue(AbandonedDog.class);

                    LatLng abandonedDogPosition = new LatLng(abandonedDog.getLat(), abandonedDog.getLng());


                    abandonedMarker.add(mMap.addMarker(new MarkerOptions()
                            .position(abandonedDogPosition)
                            .title("Abandoned Dog")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.exclamation))));
                    abandonedUID.add(data.getKey());

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
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        mMap.clear();
        if (getIntent().hasExtra("breed")) {
            breed = "";
            size = "";
            mating = "";
//            latLng = getIntent().getDoubleExtra("latLng", 0);
//            lngLat = getIntent().getDoubleExtra("lngLat", 0);
            if (getIntent().getStringExtra("breed") != null && !getIntent().getStringExtra("breed").trim().equals("")) {
                breed = getIntent().getStringExtra("breed");
            }
            if (getIntent().getStringExtra("size") != null && !getIntent().getStringExtra("size").trim().equals("")) {
                size = getIntent().getStringExtra("size");
            }
            if (getIntent().getStringExtra("mating") != null && !getIntent().getStringExtra("mating").trim().equals("")) {
                mating = getIntent().getStringExtra("mating");
            }
            loadPersonalizeMap();
        } else {
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
                online.putExtra("latLng", mLastLocation.getLatitude());
                online.putExtra("lngLat", mLastLocation.getLongitude());
                startActivity(online);
                break;

            case R.id.action_back_online:
                startActivity(new Intent(MapTracking.this, ListOnline.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {

//        LatLng myPosition = new LatLng(latLng, lngLat);
//        Location myLocation = new Location("");
//        myLocation.setLatitude(latLng);
//        myLocation.setLongitude(lngLat);


        final LatLng myPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());


        marker.showInfoWindow();

        for (final Marker marker1 : abandonedMarker) {
            if (marker.equals(marker1)) {


                AlertDialog alertDialog = new AlertDialog.Builder(MapTracking.this).create();
                alertDialog.setTitle(Html.fromHtml("<font color='#FFFFFF'>Abandoned dog</font>"));
                alertDialog.setMessage(Html.fromHtml("<font color='#FFFFFF'>Do you want to delete marker?</font>"));
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Submit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                abadonedDogRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                            AbandonedDog abandonedDog = dataSnapshot1.getValue(AbandonedDog.class);
                                            if (abandonedDog.getLat().equals(marker1.getPosition().latitude) &&
                                                    abandonedDog.getLng().equals(marker1.getPosition().longitude)) {
                                                dataSnapshot1.getRef().removeValue();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }

                                });
                                mMap.clear();
                                loadLocationsForUsers();
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Submited for deleting", Toast.LENGTH_SHORT).show();
                            }
                        });
                alertDialog.show();
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.WHITE);


            }


        }
        if (!marker.getTitle().equals("Abandoned Dog")) {
            AlertDialog directionsDialog = new AlertDialog.Builder(MapTracking.this).create();
            directionsDialog.setTitle(Html.fromHtml("<font color='#FFFFFF'>Travel</font>"));
            directionsDialog.setMessage(Html.fromHtml("<font color='#FFFFFF'>Do you want to travel to this place?</font>"));
            directionsDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Go", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + marker.getPosition().latitude + "," + marker.getPosition().longitude + "&mode=walking");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
//                    String url = getUrl(myPosition,marker.getPosition(),"driving");
//                    new FetchURL(MapTracking.this).execute(url,"driving");

                }
            });
            directionsDialog.show();
            directionsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            directionsDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        }
        return true;
    }

    private String getUrl(LatLng origin, LatLng destination, String directionMode) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_destination = "destination=" + destination.latitude + "," + destination.longitude;
        String mode = "mode=" + directionMode;
        String param = str_origin + "&" + str_destination + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key=" + "AIzaSyBKkCP8ypZC5AkgYfirRiU0qD6POrJNI64";
        return url;
    }

    private void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RES_REQUEST).show();
            } else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();

            }

            return false;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSON_REQUEST_CODE);
        } else {
            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSON_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
                break;
        }
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            locations.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(new Tracking(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            String.valueOf(mLastLocation.getLatitude()),
                            String.valueOf(mLastLocation.getLongitude())));
        } else {
            //Toast.makeText(this, "Couldn't get location", Toast.LENGTH_SHORT).show();
            Log.d("TEST", "Couldn't load location");
        }
    }

    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(DISTANCE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null) {
            currentPolyline.remove();
        }
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}