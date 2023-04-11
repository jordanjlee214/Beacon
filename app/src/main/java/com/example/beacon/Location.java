package com.example.beacon;


import com.google.android.gms.maps.model.LatLng;

public class Location {

    public String building;
    public LatLng latLng;

    public Location(String b, LatLng l) {
        building = b;
        latLng = l;
    }

    /**
     * Is this string the same as the Location name?
     * @param s string to be checked
     * @return are they the same?
     */
    public boolean checkLoc(String s){
        return s.equalsIgnoreCase(building);
    }
}
