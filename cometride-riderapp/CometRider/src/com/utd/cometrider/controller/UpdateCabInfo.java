package com.utd.cometrider.controller;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;


import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.utd.cometrider.R;

public class UpdateCabInfo {
	
	static ArrayList<Marker> allCabMarkers= new ArrayList<Marker>();
	
	private ArrayList<Cab> allCabs;
		
	public ArrayList<Cab> getAllCabs() {
		return allCabs;
	}

	public void setAllCabs(ArrayList<Cab> allCabs) {
		this.allCabs = allCabs;
	}

	public void update(final HashMap<String, String> shortNameMap,
			final HashMap<String, Boolean> routeSelectedMap,
			final SupportMapFragment fm) {

		final class UpdateCab extends AsyncTask<Void, Void, Void> {
		
			// HashMap<Cab, Marker> cabMarkerMap= new HashMap<Cab, Marker>();
			

			@Override
			protected Void doInBackground(Void... params) {

			//	jAllCabs 
				///cabMarkerMap = new HashMap<Cab, Marker>();
				//allCabs = new ArrayList<Cab>();
			   // allCabMarkers = new ArrayList<Marker>();
				
			    JSONArray jAllCabs= null;
			    allCabs= new ArrayList<Cab>(); 
				// JSONArray jAllCabs = null;
				try {
					jAllCabs = JsonReader
							.readJsonFromUrl("http://cometride.elasticbeanstalk.com/api/cab");

					for (int i = 0; i < jAllCabs.length(); i++) {
						// Cab cab = new Cab();
						JSONObject c = jAllCabs.getJSONObject(i);
						String cabId = c.getString("cabId");
						String routeId = c.getString("routeId");
						int maxCapacity = c.getInt("maxCapacity");
						int passengerCount = c.getInt("passengerCount");
						String status = c.getString("status");

						// if(i>allCabs.size()-1){
						Cab cab = new Cab();
						cab.setCabId(cabId);
						cab.setRouteId(routeId);
						cab.setMaxCapacity(maxCapacity);
						cab.setPassengerCount(passengerCount);
						cab.setStatus(status);
						JSONObject position = c.getJSONObject("location");

						double lat = Double.parseDouble(position
								.getString("lat"));
						double lng = Double.parseDouble(position
								.getString("lng"));

						LatLng p = new LatLng(lat, lng);

						cab.setLocation(p);
						allCabs.add(cab);

						// }else{
						//
						// allCabs.get(i).setRouteId(routeId);
						// allCabs.get(i).setMaxCapacity(maxCapacity);
						// allCabs.get(i).setPassengerCount(passengerCount);
						// allCabs.get(i).setStatus(status);
						//
						// // JSONObject l = c.getJSONObject("location");
						// // ArrayList<LatLng> locations = new
						// // ArrayList<LatLng>();
						//
						// JSONObject position = c.getJSONObject("location");
						//
						// double lat = Double.parseDouble(position
						// .getString("lat"));
						// double lng = Double.parseDouble(position
						// .getString("lng"));
						//
						// LatLng p = new LatLng(lat, lng);
						//
						// // locations.add(p);
						//
						// allCabs.get(i).setLocation(p);
						//
						// }
						// Log.v("p", p.toString());
						// Log.v("allCabs", allCabs.toString());
						// allCabs.set(i, allCabs.get(i));
						// allCabs.add(cab);

					}

				} catch (IOException e) {
					e.getMessage();
				} catch (JSONException e) {
					e.getMessage();
				}

				return null;
			}

			protected void onPostExecute(Void result) {
				super.onPostExecute(result);

				// cabMarkerMapNew = new HashMap<Cab, Marker>();
			//	Log.v("makers", Integer.toString(allCabMarkers.size()));
				// remove all markers
				for(int i=0;i<allCabMarkers.size();i++){
					
					allCabMarkers.get(i).remove();
					
				}
				
				
			//	allCabMarkers= new ArrayList<Marker>();

				//Log.v("cab number", Integer.toString(allCabs.size()));

				if (allCabs.size() > 0) {

					for (int i = 0; i < allCabs.size(); i++) {

						// for (int j = 0; j < allRoutes.size(); j++) {
						Log.v("cab", allCabs.get(i).toString());
						Log.v("routeId", allCabs.get(i).getRouteId());
						
					    Log.v("short name", shortNameMap.keySet().toString());
					    
					//    Log.v("route selected", routeSelectedMap.get( allCabs.get(i).getRouteId()).toString());
					    Log.v("route selected key", routeSelectedMap.keySet().toString());
					 
					 
					    if( routeSelectedMap.get(allCabs.get(i).getRouteId()) == true ) {
						 
							// if(allCabs.get(i).getRouteId().equals()){
				        if (shortNameMap.get(allCabs.get(i).getRouteId()) != ""
									&& shortNameMap
											.get(allCabs.get(i).getRouteId()) != null) {
							
				        	
				        	Marker newMarker = fm.getMap().addMarker(
										new MarkerOptions().position(
										allCabs.get(i).getLocation())
										.title(shortNameMap.get(allCabs
												.get(i).getRouteId())));
			
				
	
								// update cab cap
								newMarker.setSnippet(
												"Current Passengers:"
														+ allCabs.get(i)
																.getPassengerCount()
														+ "/"
														+ allCabs.get(i)
																.getMaxCapacity());
	
								// update cab color
								double cabCap = (double) allCabs.get(i)
										.getPassengerCount()
										/ (double) allCabs.get(i).getMaxCapacity();
	
								// Check Cap green
								if (cabCap <0.6) {
	
									newMarker.setIcon(
													BitmapDescriptorFactory
															.fromResource(R.drawable.cab_green));
									// Log.v("haha", "haha");
	
								}
	
								if (cabCap > 0.6 && cabCap < 1.0) {
	
									newMarker.setIcon(
													BitmapDescriptorFactory
															.fromResource(R.drawable.cab_yellow));
									// Log.v("haha", "haha");
	
								}
	
								if (cabCap >= 1.0) {
	
									newMarker.setIcon(
											BitmapDescriptorFactory
													.fromResource(R.drawable.cab_red));
	
								}
								
								
								allCabMarkers.add( newMarker );
							}

				
					    }
						
						
					}
					
					
					
					

				}

		
				
			
			}

		}

		final Handler handler = new Handler();

		handler.post(new Thread(new Runnable() {

			@Override
			public void run() {

				new UpdateCab().execute();
				handler.postDelayed(this, 2000);

			}
		}));
	}

}
