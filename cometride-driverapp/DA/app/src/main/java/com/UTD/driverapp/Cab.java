package com.UTD.driverapp;


public class Cab {

    private String cabSessionId;
    private int passengerCount;
    private double lat;
    private double lng;
    private int passengerTotal;
    private String status;
    private String routeId;
    private int maxCapacity;


    public String getcabSessionId() {
        return cabSessionId;
    }

    public double getlat() {
        return lat;
    }

    public double getlng() {
        return lng;
    }

    public int getpassengerCount() {
        return passengerCount;
    }

    public int getpassengerTotal() {
        return passengerTotal;
    }

    public void setcabSessionId(String cabSessionId) {
        this.cabSessionId = cabSessionId;
    }

    public void setlat(double lat) {
        this.lat = lat;
    }

    public void setlng(double lng) {
        this.lng = lng;
    }

    public void setpassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public void setpassengerTotal(int passengerTotal) {
        this.passengerTotal = passengerTotal;
    }

    public int getPassengerTotal() {
        return passengerTotal;
    }

    public void setPassengerTotal(int passengerTotal) {
        this.passengerTotal = passengerTotal;
    }


    public String getStatus() {
        return status;
    }


    public int getmaxCapacity() {
        return maxCapacity;
    }

    public String getrouteId() {
        return routeId;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public void setmaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public void setrouteId(String routeId) {
        this.routeId = routeId;
    }
}