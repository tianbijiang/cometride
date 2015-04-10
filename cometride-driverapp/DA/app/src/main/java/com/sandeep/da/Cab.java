package com.sandeep.da;


public class Cab {
//    private LatLng location;
    private int maxCapacity;
    private int passengerCount;
    private String status;
    private String routeId;
    private String cabId;
    private int capacity;

    public String getCabSessionId() {
        return cabId;
    }
    public void setCabId(String cabId) {
        this.cabId = cabId;
    }
    public String getRouteId() {
        return routeId;
    }
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
   // public LatLng getLocation() {
    //    return location;
    //}
  //  public void setLocation(LatLng location) {
   //     this.location = location;
    //}
    public int getMaxCapacity() {
        return maxCapacity;
    }
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
    public int getPassengerCount() {
        return passengerCount;
    }
    public void setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public Object PassengerCount() {return null;
    }

    public Object PassengerTotal() {return null;
    }
    public Object getStatus() {
        return status;
    }

    public Object getCapacity() {
        return capacity;
    }

    public Object getrouteId() {
        return routeId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setrouteId(String routeId) {
        this.routeId = routeId;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}