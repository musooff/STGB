package com.mnm.georemider;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by moshe on 12/05/2017.
 */

public class TaskData {

    private String name;
    private String need;
    private boolean hasName;
    private String locationName;
    private String logicalLocation;
    private LatLng numericalLocation;
    private String time;
    private boolean friends;
    private int radius;
    private boolean entry;


    public TaskData(String name, String need,boolean hasName,String locationName, String logicalLocation, LatLng numericalLocation, String time, boolean friends, int radius, boolean entry){
        this.name = name;
        this.need = need;
        this.hasName = hasName;
        this.locationName = locationName;
        this.logicalLocation = logicalLocation;
        this.numericalLocation = numericalLocation;
        this.time = time;
        this.friends = friends;
        this.radius = radius;
        this.entry = entry;
    }

    public String getName(){return name;}
    public String getNeed(){return need;}

    public boolean isHasName() {
        return hasName;
    }

    public String getLocationName() {
        return locationName;
    }
    public String getLogicalLocation(){return logicalLocation;}
    public LatLng getNumericalLocation(){return numericalLocation;}
    public String getTime(){return time;}
    public boolean isFriends(){return friends;}
    public int getRadius(){return radius;}
    public boolean isEntry(){return entry;}

}
