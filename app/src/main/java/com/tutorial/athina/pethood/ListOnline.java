package com.tutorial.athina.pethood;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tutorial.athina.pethood.Models.Dog;
import com.tutorial.athina.pethood.Models.Owner;
import com.tutorial.athina.pethood.Models.Tracking;
import com.tutorial.athina.pethood.Models.User;
import com.tutorial.athina.pethood.Notifications.BootReceiver;

import java.util.ArrayList;
import java.util.List;

public class ListOnline extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, NavigationView.OnNavigationItemSelectedListener {

    DatabaseReference onlineRef, currentUserRef, counterRef, locations, offlineRef, ownersRef, chatsRef, dogsRef;
    FirebaseRecyclerAdapter<User, ListOnlineViewHolder> adapter;
    RecyclerView listOnline;
    NavigationView mNavigationView;
    RecyclerView.LayoutManager layoutManager;
    private List<String> userList, offlineList;
    BroadcastReceiver broadcastReceiver;
    public static final int MY_PERMISSON_REQUEST_CODE = 7171;
    public static final int PLAY_SERVICES_RES_REQUEST = 7172;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation = new Location("dummy");
    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISTANCE = 10;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private TextView drawerUserText, drawerUserDog;
    private ImageView drawerImageUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_online);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Online</font>"));

        mDrawer = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.open_drawer, R.string.close_drawer);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        mNavigationView.bringToFront();
        mNavigationView.setNavigationItemSelectedListener(this);

        View headerLayout = mNavigationView.getHeaderView(0);

        drawerImageUser = (ImageView) headerLayout.findViewById(R.id.drawerImageUser);
        drawerUserText = (TextView) headerLayout.findViewById(R.id.drawerUserName);
        drawerUserDog = (TextView) headerLayout.findViewById(R.id.drawerUserDog);

        listOnline = (RecyclerView) findViewById(R.id.listOnline);
        listOnline.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listOnline.setLayoutManager(layoutManager);


        userList = new ArrayList<String>();
        offlineList = new ArrayList<String>();

        broadcastReceiver = new BootReceiver();
        IntentFilter intentFilter = new IntentFilter("com.tutorial.athina.pethood.action.REFRESH_INTERVAL");
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(broadcastReceiver, intentFilter);

        startService(new Intent(this, NotificationService.class));


        locations = FirebaseDatabase.getInstance().getReference("Locations");
        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        counterRef = FirebaseDatabase.getInstance().getReference("lastOnline");
        ownersRef = FirebaseDatabase.getInstance().getReference("Owners");
        currentUserRef = FirebaseDatabase.getInstance().getReference("lastOnline")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        offlineRef = FirebaseDatabase.getInstance().getReference("offlineUsers");
        chatsRef = FirebaseDatabase.getInstance().getReference().child("chats");
        dogsRef = FirebaseDatabase.getInstance().getReference().child("dog");

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
        setupSystem();
        updateList();
    }

    private void setupSystem() {
        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class)) {
                    currentUserRef.onDisconnect().removeValue();
                    counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(), "Online"));
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        counterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    Log.d("LOG", "" + user.getEmail() + "" + user.getStatus());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ownersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Owner owner = dataSnapshot1.getValue(Owner.class);
                    if (owner.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        drawerUserText.setText(owner.getName() + " " + owner.getSurname());
                        Glide.with(getApplicationContext()).load(owner.getProfileImageUrl()).into(drawerImageUser);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dogsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Dog dog = data.getValue(Dog.class);
                    if (dog.getDogOwner().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        drawerUserDog.setText("Owner of " + dog.getDogName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void updateList() {

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(counterRef, User.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<User, ListOnlineViewHolder>(options) {

            @Override
            protected void onBindViewHolder(ListOnlineViewHolder viewHolder, int position, final User model) {
                userList.add(model.getEmail());
                viewHolder.txtEmail.setText(model.getEmail());
                if (model.getStatus().equals("Online")) {
                    viewHolder.statusImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_name));
                }

                viewHolder.itemClickListener = new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        if (!model.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                            Intent dogDetails = new Intent(ListOnline.this, DogDetailsAndChatActivity.class);
                            dogDetails.putExtra("dogOwner", model.getEmail());
                            dogDetails.putExtra("myUser", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            startActivity(dogDetails);
                        }
                    }
                };

            }

            @Override
            public ListOnlineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(getBaseContext()).inflate(R.layout.online_users, parent, false);

                return new ListOnlineViewHolder(itemView);
            }


        };
        adapter.startListening();
        listOnline.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendBroadcast() {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setAction("com.tutorial.athina.pethood.action.REFRESH_INTERVAL");
        sendBroadcast(intent);
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
        sendBroadcast();
        startService(new Intent(this, NotificationService.class));
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        if (adapter != null) {
            adapter.stopListening();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        updateList();
        notifyChat();

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

    private void notifyChat() {
        Query query = chatsRef.child("receiver").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                startService(new Intent(getApplicationContext(), NotificationService.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        notifyChat();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);

        int id = item.getItemId();
        switch (id) {

            case R.id.itemServiceStart:
                Toast.makeText(this, "REFRESH", Toast.LENGTH_SHORT).show();
                sendBroadcast();
                startService(new Intent(ListOnline.this, NotificationService.class));
                break;
            case R.id.action_map:
                Intent map = new Intent(ListOnline.this, MapTracking.class);
                map.putStringArrayListExtra("userList", (ArrayList<String>) userList);
                map.putExtra("latLng", mLastLocation.getLatitude());
                map.putExtra("lngLat", mLastLocation.getLongitude());
                startActivity(map);
                break;
            case R.id.userDetails:
                Intent myUserProfile = new Intent(ListOnline.this, UserProfileActivity.class);
                myUserProfile.putExtra("dogOwner", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(myUserProfile);
                break;
            case R.id.dogDetails:
                Intent myDogDetails = new Intent(ListOnline.this, DogDetailsActivity.class);
                myDogDetails.putExtra("dogOwner", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(myDogDetails);
                break;
            case R.id.reportMissingDog:
                startActivity(new Intent(ListOnline.this, MissingDogsActivity.class));
                break;
            case R.id.editInterval:
                startActivity(new Intent(ListOnline.this, SettingsActivity.class));
                break;
            case R.id.action_logout:
                currentUserRef.removeValue();
                startActivity(new Intent(ListOnline.this, LoginActivity.class));
                break;
        }

        return true;
    }
}