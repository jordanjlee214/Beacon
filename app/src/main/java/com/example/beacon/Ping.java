package com.example.beacon;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import android.location.Location;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class Ping {

    private DatabaseReference mRef;
    private FirebaseUser user;
    private boolean pingOn;
    private String currentUserID;
    private MapsActivity maps;

    /**
     * Constructor
     * Allows access to Database
     * @param mRef reference to the Database
     * @param user the current user
     * @param maps the map object
     */
    public Ping(DatabaseReference mRef, FirebaseUser user, MapsActivity maps){
        this.mRef = mRef;
        this.user = user;
        this.maps = maps;
        currentUserID = user.getUid();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pingOn = (boolean) snapshot.child("ping").getValue();
                MapsActivity.setUpdateVisibility(pingOn);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Getter for ping variable
     * @return state of pingOn
     */
    public boolean isOn(){
        return pingOn;
    }

    /**
     * Setter for ping variable
     * @param on what state should be set to
     */
    public void onOrOff(boolean on){
        pingOn = on;
    }

    /**
     * Switch the state of the ping
     * Puts state and location in database
     * Uses map reference to find location
     */
    public void togglePing(){


        if(!pingOn) {
            //MapsActivity.setUpdateVisibility(true);
            maps.getDeviceLocation();
        }else{
            //MapsActivity.setUpdateVisibility(false);
            mRef.child("lat").setValue(0);
            mRef.child("lng").setValue(0);
        }
        pingOn = !pingOn;
        MapsActivity.setUpdateVisibility(pingOn);
        mRef.child("ping").setValue(pingOn);
    }
}
