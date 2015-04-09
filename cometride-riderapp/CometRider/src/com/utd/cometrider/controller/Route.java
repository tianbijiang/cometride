package com.utd.cometrider.controller;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

public class Route {
	 private String id;
	 private String name;
	 private String color;
	 private String status;
	 private ArrayList<LatLng> waypoints;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
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
	 
}
