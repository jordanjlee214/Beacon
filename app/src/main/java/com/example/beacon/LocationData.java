package com.example.beacon;


import com.google.android.gms.maps.model.LatLng;

public class LocationData {

    public String building;
    public LatLng latLng;

    public LocationData(String b, LatLng l) {
        building = b;
        latLng = l;
    }
}
