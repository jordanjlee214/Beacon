package com.example.beacon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class CampusLocations {
    public static final String[] LOCATION_LIST = {"All", "Anderson Commons", "Meyer Science Center", "Smith-Traber", "Chrouser Sports", "Fischer",
            "North Harrison Hall", "McManis-Evans", "Campus Store", "College Ave Apartments", "Terrace Apartments",
            "Saint & Elliot Apartments", "Michigan-Crescent Apartments", "Bean Stadium", "Billy Graham Hall", "Blanchard Hall",
            "Adams Hall", "Williston Hall", "Memorial Student Center", "Armerding Center", "Wyngarden & Schell", "Buswell Library",
            "Edman Chapel", "Wade Center", "Public Library", "Phelps Room", "Lower Beamer",
            "Fireside Room", "The Stupe", "Center of Vocation & Career", "Welcome Center", "McCully Stadium", "Tennis Courts",
            "Jenks Hall"
    };

   public static String[] sorted(){
       ArrayList<String> locationArrayList = new ArrayList<>(Arrays.asList(LOCATION_LIST));
       locationArrayList.sort(Comparator.naturalOrder());
       locationArrayList.add("Other");
       String[] arr = new String[locationArrayList.size()];
       arr = locationArrayList.toArray(arr);
       return arr;
   }

}
