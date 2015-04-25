package com.UTD.driverapp;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

public class Route {
    private String id;
    private String name;
    private String typeName;
    private int maximumCapacity;
    private String color;
    private String status;
    private ArrayList<LatLng> waypoints;
    private ArrayList<LatLng> safepoints;

    public ArrayList<LatLng> getSafepoints() {
        return safepoints;
    }

    public void setSafepoints(ArrayList<LatLng> safepoints) {
        this.safepoints = safepoints;
    }

    public String getId() {
        return id;
    }
    public void setTypeId(String id) {
        this.id = id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public int getCapacity(int maximumCapacity) {
        return maximumCapacity;
    }
    public String getName() {
        return name;
    }
    public String getTypeName() {
        return typeName;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<LatLng> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(ArrayList<LatLng> waypoints) {
        this.waypoints = waypoints;
    }

    public void setCapacity(int maximumCapacity) {
    }



}