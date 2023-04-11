package com.example.beacon;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

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

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final String Fine_location = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String Coarse_location = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private DatabaseReference mRef; //reads and writes to Firebase database
    private Ping ping;
    private HashMap<String,LatLng> pingList;
    private HashMap<String, Marker> markerList;
    private HashMap<String, String> eventList;
    private AppCompatButton pingSwitch, backButton, eventButton;
    private String selectedLoc; //send locations to event page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        com.example.beacon.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getLocationPermission();
        ping = new Ping(mRef, user, this);

        //Set buttons
        pingSwitch = findViewById(R.id.ping);
        pingSwitch.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            getDeviceLocation();
            ping.togglePing();
            //TODO link to ping form fragment
        });

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener((view -> sendToActivity(MainActivity.class)));
        pingList = new HashMap<>();
        markerList = new HashMap<>();
        eventList = new HashMap<>();
        for(Location l:MainActivity.locDat)
            eventList.put(l.building,"");

        selectedLoc = "";
        eventButton = findViewById(R.id.eventButton);
        eventButton.setOnClickListener(view -> {
            Intent intent = new Intent(MapsActivity.this, EventActivity.class);
            intent.putExtra("methodName","setFilter");
            intent.putExtra("location", selectedLoc);
            startActivity(intent);
        });
    }

    private void sendToActivity(Class<?> a) { //this method changes the activity to appropriate activity
        Intent switchToNewActivity= new Intent(MapsActivity.this, a);
        startActivity(switchToNewActivity);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //finish();
    }

    protected void getDeviceLocation() {
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                Task<android.location.Location> location = mFusedLocationProviderClient.getLastLocation();
                android.location.Location out;
                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        android.location.Location currentLocation = (android.location.Location) task.getResult();
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                        if(ping.isOn()) {
                            mRef.child("lat").setValue(currentLocation.getLatitude());
                            mRef.child("lng").setValue(currentLocation.getLongitude());
                        }
                    } else {
                        Toast.makeText(MapsActivity.this, "unable to get location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (SecurityException e) {
            System.out.println(e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MapsActivity.DEFAULT_ZOOM));
    }

    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(MapsActivity.this);
    }

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
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
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

    private void getPings() {
        DatabaseReference lRef = FirebaseDatabase.getInstance().getReference().child("Users");
        lRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()){
                    String userID = s.getKey();
                    Number lat = (Number) s.child("lat").getValue();
                    Number lng = (Number) s.child("lng").getValue();
                    if(lat instanceof Long)
                        lat = ((Long) lat).doubleValue();
                    if(lng instanceof Long)
                        lng = ((Long) lng).doubleValue();
                    if((boolean) s.child("ping").getValue())
                        pingList.put(userID, new LatLng((double)lat, (double)lng));
                    else{
                        pingList.remove(userID);
                        if(markerList.containsKey(userID)) {
                            markerList.get(userID).remove();
                            markerList.remove(userID);
                        }
                    }
                    for (String m:pingList.keySet()) {
                        if(markerList.containsKey(m)){
                            markerList.get(m).setPosition(pingList.get(m));
                        }else{ //key does not have a marker
                            MarkerOptions newMarker = new MarkerOptions();
                            newMarker.position(pingList.get(m));
                            newMarker.title((String) s.child("username").getValue());
                            newMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                            markerList.put(m, mMap.addMarker(newMarker));
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

