package com.example.beacon;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class Ping {

    private DatabaseReference mRef;
    private FirebaseUser user;
    private boolean pingOn;
    private String currentUserID;
    private MapsActivity maps;
    public Ping(DatabaseReference mRef, FirebaseUser user, MapsActivity maps){
        this.mRef = mRef;
        this.user = user;
        this.maps = maps;
        currentUserID = user.getUid();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pingOn = (boolean) snapshot.child("ping").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean isOn(){
        return pingOn;
    }

    public void togglePing(){


        if(!pingOn) {
            maps.getDeviceLocation();
            System.out.println("ON");
        }else{
            System.out.println("Off");
            mRef.child("lat").setValue(0);
            mRef.child("long").setValue(0);
        }
        pingOn = !pingOn;
        mRef.child("ping").setValue(pingOn);
    }
}
