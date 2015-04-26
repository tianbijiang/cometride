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




    public String getId() {
        return id;
    }
    public void setTypeId(String id) {
        this.id = id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setCapacity(int maximumCapacity) { this.maximumCapacity=maximumCapacity;   }
    public int getCapacity() {
        return maximumCapacity;    }
    public String getName() {
        return name;
    }
    public String getTypeName() {
        return name;
    }
    public int getMaximumCapacity() {
        return maximumCapacity;
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



}