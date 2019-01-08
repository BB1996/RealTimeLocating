package com.tutorial.athina.pethood;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tutorial.athina.pethood.Models.AbandonedDog;
import com.tutorial.athina.pethood.Models.Canisite;
import com.tutorial.athina.pethood.Models.Dog;
import com.tutorial.athina.pethood.Models.PetShop;
import com.tutorial.athina.pethood.Models.Tracking;
import com.tutorial.athina.pethood.Models.Vet;

import java.util.ArrayList;
import java.util.List;

public class MapTracking extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    DatabaseReference locations, counterRef, canisiteRef, dogsRef, petShopRef, abadonedDogRef, vetRef;
    private ArrayList<String> userList;
    private ArrayList<String> filterUserList;
    String breed, size, mating;
    private static final String dogBreed = "dogBreed";
    private static final String dogMateFlag = "dogMateFlag";
    private static final String dogSize = "dogSize";
    private Button abandonedButoon;
    private Double latLng, lngLat;
    private List<Marker> abandonedMarker;
    private List<String> abandonedUID;

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

        userList = new ArrayList<>();
        filterUserList = new ArrayList<>();
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
                AbandonedDog abandonedDog = new AbandonedDog(latLng, lngLat);
                abadonedDogRef.child(id).setValue(abandonedDog);

            }
        });


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
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.dogpawn)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12.0f));
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        loadCanisite();
        loadPetShops();
        loadAbandonedDog();
        loadVets();


    }

    private void loadPersonalizeMap() {
        for (final String user : filterUserList) {
            if (breed != null && size != null && mating != null) {
                allFiltersMap(user);

            } else if (breed != null) {
                loadPersonalizedFilterMap(user, dogBreed, breed);
            } else if (size != null) {
                loadPersonalizedFilterMap(user, dogSize, size);
            } else if (mating != null) {
                loadPersonalizedFilterMap(user, dogMateFlag, mating);
            }


        }
        loadCanisite();
        loadPetShops();
        loadAbandonedDog();
        loadVets();
    }

    private void allFiltersMap(final String user) {
        Query allFilters = dogsRef.orderByChild(dogBreed).equalTo(breed);
        allFilters.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Dog dog = postSnapshot.getValue(Dog.class);
                    if (dog.getDogOwner().equals(user) && dog.getDogMateFlag().equals(mating) && dog.getDogSize().equals(size)) {
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
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.dogpawn)));
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

    private void loadCanisite() {
        canisiteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Canisite canisite = data.getValue(Canisite.class);

                    LatLng canisitaLocation = new LatLng(canisite.getLat(), canisite.getLng());

                    mMap.addMarker(new MarkerOptions()
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

    private void loadPersonalizedFilterMap(final String user, String column, String searchPattern) {
        Query user_breeds = dogsRef.orderByChild(column).equalTo(searchPattern);
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
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.dogpawn)));
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        if (getIntent().hasExtra("breed")) {
            filterUserList = getIntent().getStringArrayListExtra("userList");
            latLng = getIntent().getDoubleExtra("latLng", 0);
            lngLat = getIntent().getDoubleExtra("lngLat", 0);
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
        } else if (getIntent().hasExtra("userList") && getIntent().hasExtra("latLng") && getIntent().hasExtra("lngLat")) {
            userList = getIntent().getStringArrayListExtra("userList");
            latLng = getIntent().getDoubleExtra("latLng", 0);
            lngLat = getIntent().getDoubleExtra("lngLat", 0);
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
                online.putExtra("latLng", latLng);
                online.putExtra("lngLat", lngLat);
                startActivity(online);
                break;

            case R.id.action_back_online:
                startActivity(new Intent(MapTracking.this, ListOnline.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        for (final Marker marker1 : abandonedMarker) {
            if (marker.equals(marker1)) {


                AlertDialog alertDialog = new AlertDialog.Builder(MapTracking.this).create();
                alertDialog.setTitle("Abandoned dog");
                alertDialog.setMessage("Delete abandoned dog marker");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Submit",
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
            }

        }

        return true;
    }
}