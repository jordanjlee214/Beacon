package com.example.beacon;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.beacon.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private String currentUserID;

    private static final String Fine_location = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String Coarse_location = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private DatabaseReference mRef; //reads and writes to Firebase database
    private Ping ping; //this user's ping object
    private boolean locationSuccessful;
    private HashMap<String,LatLng> pingList; //All pings to be displayed
    private HashMap<String, Marker> markerList; //All markers to be displayed
    private HashMap<String, String> eventList;
    private AppCompatButton pingSwitch, backButton, eventButton;
    private String selectedLoc; //send locations to event page

    private DatabaseReference pingInfoRef;

    /**
     * Sets up the Maps page
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserID = user.getUid().toString();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        pingInfoRef = FirebaseDatabase.getInstance().getReference().child("Pings");
        com.example.beacon.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getLocationPermission();

        //set up this user's ping
        ping = new Ping(mRef, user, this);

        locationSuccessful = true; //this boolean says if location finding was successful or not

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!((boolean)snapshot.child("ping").getValue())){
                    mRef.child("lat").setValue(0);
                    mRef.child("lng").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Set buttons
        pingSwitch = findViewById(R.id.ping);
        pingSwitch.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            openPingForm();
            updateUserLocation();
        });

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener((view -> sendToActivity(MainActivity.class)));
        pingList = new HashMap<>();
        markerList = new HashMap<>();
        /*eventList = new HashMap<>();
        for(Location l:MainActivity.locDat)
            eventList.put(l.building,"");*/

        selectedLoc = "";
        eventButton = findViewById(R.id.eventButton);
        eventButton.setOnClickListener(view -> {
            Intent intent = new Intent(MapsActivity.this, EventActivity.class);
            intent.putExtra("methodName","setFilter");
            intent.putExtra("location", selectedLoc);
            startActivity(intent);
        });
    }

    /**
     * Switch to another activity
     * @param a activity to go to
     */
    private void sendToActivity(Class<?> a) { //this method changes the activity to appropriate activity
        Intent switchToNewActivity= new Intent(MapsActivity.this, a);
        startActivity(switchToNewActivity);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //finish();
    }

    /**
     * Use the Google maps API to find user's location
     * Works with Ping class to put location in database
     */
    protected void getDeviceLocation() {
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                Task<android.location.Location> location = mFusedLocationProviderClient.getLastLocation();
                android.location.Location out;
                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        android.location.Location currentLocation = (android.location.Location) task.getResult();
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                        if(ping.isOn()) { //references the Ping object
                            mRef.child("lat").setValue(currentLocation.getLatitude());
                            mRef.child("lng").setValue(currentLocation.getLongitude());
                        }
                        locationSuccessful = true;

                    } else {
                        Toast.makeText(MapsActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                        locationSuccessful = false;

                        LocationManager locationManager = (LocationManager)  this.getSystemService(Context.LOCATION_SERVICE);
                        Criteria criteria = new Criteria();
                        String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
                        locationManager.requestLocationUpdates(bestProvider, (long) 1000, (float) 0, new LocationListener() {
                            @Override
                            public void onLocationChanged(@NonNull android.location.Location location) {

                            }
                        });
                    }
                });
            }
        } catch (SecurityException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Move the camera to a specified location
     * @param latLng where to move to
     */
    private void moveCamera(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MapsActivity.DEFAULT_ZOOM));
    }

    /**
     * Create the map
     */
    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(MapsActivity.this);
    }

    /**
     * Asks user for location Permissions
     */
    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Fine_location) ==
                PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Coarse_location) ==
                    PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length < 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = false;
                        return;
                    }
                }
                mLocationPermissionGranted = true;
                initMap();
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            //getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
           // mMap.setMyLocationEnabled(true);
            mMap.setLatLngBoundsForCameraTarget(new LatLngBounds(new LatLng(41.864406, -88.103689),new LatLng(41.874021, -88.087758)));
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
            mMap.setMinZoomPreference(15.5f);

        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(41.869092, -88.099211)));

        for(Location e:MainActivity.locDat){ //place location pins on map
            MarkerOptions locPin = new MarkerOptions();
            locPin.position(e.latLng);
            locPin.title(e.building);

            Marker m = mMap.addMarker(locPin);
        }

        getPings();

        mMap.setOnMarkerClickListener(marker -> {
            selectedLoc = marker.getTitle();
            marker.showInfoWindow();
            return true;
        });
    }

    /**
     * Accesses the database to display other user's pings
     */
    private void getPings() {
        DatabaseReference lRef = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference fRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUserID);
        ArrayList<String> friends = new ArrayList<>();
        friends.add(currentUserID);
        fRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(String i:friends){
                    if(i!=currentUserID)
                        friends.remove(i);
                }
                for (DataSnapshot s: snapshot.getChildren()){
                    friends.add(s.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        lRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("Pings Set");
                for (DataSnapshot s : snapshot.getChildren()){ //Loop through the users
                    String userID = s.getKey();
                    if(friends.contains(userID)) {
                        Number lat = (Number) s.child("lat").getValue();
                        Number lng = (Number) s.child("lng").getValue();

                        //Make sure types are all correct from database
                        if (lat instanceof Long)
                            lat = ((Long) lat).doubleValue();
                        if (lng instanceof Long)
                            lng = ((Long) lng).doubleValue();

                        //Checks user ping
                        if ((boolean) s.child("ping").getValue())
                            pingList.put(userID, new LatLng((double) lat, (double) lng));
                        else {
                            pingList.remove(userID);
                            if (markerList.containsKey(userID)) {
                                markerList.get(userID).remove();
                                markerList.remove(userID);
                            }
                        }

                        //puts the pings on the map
                        for (String m : pingList.keySet()) {//Ping is already on the map
                            if (markerList.containsKey(m)) {
                                markerList.get(m).setPosition(pingList.get(m));
                            } else { //key does not have a marker
                                MarkerOptions newMarker = new MarkerOptions();
                                newMarker.position(pingList.get(m));
                                newMarker.title((String) s.child("username").getValue());
                                newMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                if (userID.equals(currentUserID)) //if its the current user's pin, make it purple
                                    newMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                                markerList.put(m, mMap.addMarker(newMarker));
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * toggles the ping and tracks device location
     */
    public void updateUserLocation(){
        getDeviceLocation();
        if(locationSuccessful)
        {
            ping.togglePing();
        }
        Log.i("jordanjlee", ""+ ping.isOn());
        Log.i("jordanjlee", "locationSuccessful: "+ locationSuccessful);
    }

    /**
     * Connects to the ping form
     */
    private void openPingForm(){
        //if you are turning a ping on, open the form
        mRef.child("ping").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((boolean) snapshot.getValue()){ //its pinged already, so just remove the ping info from database
                    removePingInfo();
                }
                else{ //its not pinged, so open the ping form
                    PingInfoFragment pingInfoFragment = PingInfoFragment.newInstance(currentUserID);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                    transaction.addToBackStack(null);
                    transaction.add(R.id.map, pingInfoFragment, "BLANK_FRAGMENT").commit();
                    makeButtonsVisible(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void makeButtonsVisible(boolean visible){ //makes the buttons visible or invisible for when the ping form displays
        if(visible){
            pingSwitch.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
            eventButton.setVisibility(View.VISIBLE);
        }
        else{
            pingSwitch.setVisibility(View.GONE);
            backButton.setVisibility(View.GONE);
            eventButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() { //closes the ping form fragment
        super.onBackPressed();

        makeButtonsVisible(true);

        /*in the case that you press the back button
            and close the ping form fragment before filling it out,
            this will check and see if ping info has been put on
            the database. if it hasn't, that means nothing was
            sent. therefore, we turn off the location ping if it is on
            and take it off the map.
        */
        pingInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.child(currentUserID).exists()){
                    if(ping.isOn()) //if you had been pinged, update it again
                        updateUserLocation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removePingInfo(){ //removes the ping info from the database
        pingInfoRef.child(currentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(MapsActivity.this, "You are no longer pinged.", Toast.LENGTH_SHORT).show();
                    //TODO: Send notification?
                }
            }
        });
    }

}

