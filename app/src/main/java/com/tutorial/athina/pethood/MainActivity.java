package com.tutorial.athina.pethood;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static String emailUser;

    private EditText mailUser, passwordUser;
    private Button loginButton, registerButton;

    public static final int MY_PERMISSON_REQUEST_CODE = 7171;
    public static final int PLAY_SERVICES_RES_REQUEST = 7172;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation = new Location("dummy");

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISTANCE = 10;

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


    @Override
    public void onClick(View v) {

        DbHelperLogin dbHelper = new DbHelperLogin(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valuesLogin = new ContentValues();

        String id = "";

        String[] email = {mailUser.getText().toString()};
        String[] password = {passwordUser.getText().toString()};

        String queryMail = "SELECT * FROM" + " login" + " where " + " EMAIL = ?";

        String queryPassword = "SELECT * FROM " + "login" + " where " + "PASS = ?";
        Cursor cursorEmail = db.rawQuery(queryMail, email);
        Cursor cursorPassword = db.rawQuery(queryPassword, password);

        String[] args = new String[]{mailUser.getText().toString()};

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

                        valuesLogin.put(LoginContract.Login.ONLINE, "Yes");
                        valuesLogin.put(LoginContract.Login.LATITUDE,  mLastLocation.getLatitude());
                        valuesLogin.put(LoginContract.Login.LONGITUDE,  mLastLocation.getLongitude());

                        int count = db.update(LoginContract.TABLE, valuesLogin, "EMAIL=? ", args);

                        if(count != -1){

                            Intent map = new Intent(MainActivity.this, MapTracking.class);
                            map.putExtra("email", mailUser.getText().toString());
                            map.putExtra("lat", mLastLocation.getLatitude());
                            map.putExtra("lng", mLastLocation.getLongitude());
                            cursorEmail.close();
                            startActivity(map);

                        }

                    }
                }
                break;
            case R.id.btnRegister:
                startActivity(new Intent(this, RegisterActivity.class));
            default:
                break;
        }

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
    @SuppressLint("RestrictedApi")
    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(DISTANCE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
                    Log.d("TEST", String.valueOf(mLastLocation.getLatitude()));
                    Log.d("TEST", String.valueOf(mLastLocation.getLongitude()));

        } else {
            //Toast.makeText(this, "Couldn't get location", Toast.LENGTH_SHORT).show();
            Log.d("TEST", "Couldn't load location");
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        displayLocation();

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
        }
        else{
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

    private void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();
    }

}