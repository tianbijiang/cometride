package com.utd.cometrider.controller;

import com.google.android.gms.maps.model.LatLng;

public class Cab {
      private LatLng location;
      private int maxCapacity;
      private int passengerCount;
      private String status;
      private String routeId;
      private String cabId;

	public String getCabId() {
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
	public LatLng getLocation() {
		return location;
	}
	public void setLocation(LatLng location) {
		this.location = location;
	}
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
      
}
