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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.utd.cometrider.R;


public class UpdateCabInfo {
	static public void update(final ArrayList<Cab> allCabs,
			final HashMap<Cab, Marker> cabMarkerMap) {
			
		final Handler handler = new Handler();
       
		handler.post(new Thread(new Runnable() {

			@Override
			public void run() {

				JSONArray jAllCabs = null;
			

				try {
					jAllCabs = JsonReader
							.readJsonFromUrl("http://cometride.elasticbeanstalk.com/api/cab");

					for (int i = 0; i < jAllCabs.length(); i++) {
						//Cab cab = new Cab();
						JSONObject c = jAllCabs.getJSONObject(i);
						String routeId = c.getString("routeId");
						int maxCapacity = c.getInt("maxCapacity");
						int passengerCount = c.getInt("passengerCount");
						String status = c.getString("status");
						allCabs.get(i).setRouteId(routeId);
						allCabs.get(i).setMaxCapacity(maxCapacity);
						allCabs.get(i).setPassengerCount(passengerCount);
						allCabs.get(i).setStatus(status);

						// JSONObject l = c.getJSONObject("location");
						// ArrayList<LatLng> locations = new
						// ArrayList<LatLng>();

						JSONObject position = c.getJSONObject("location");

						double lat = Double.parseDouble(position
								.getString("lat"));
						double lng = Double.parseDouble(position
								.getString("lng"));

						LatLng p = new LatLng(lat, lng);

						// locations.add(p);

						allCabs.get(i).setLocation(p);
						
						Log.v("p",p.toString());
						Log.v("allCabs", allCabs.toString());
						allCabs.set(i, allCabs.get(i));
						//allCabs.add(cab);
					}

					

				} catch (IOException e) {
					e.getMessage();
				} catch (JSONException e) {
					e.getMessage();
				}

				for (int i = 0; i < allCabs.size(); i++) {
					
					//update cab location
				    cabMarkerMap.get(allCabs.get(i)).setPosition(allCabs.get(i).getLocation());

					//update cab color
					double cabCap=(double)allCabs.get(i).getPassengerCount()/(double)allCabs.get(i).getMaxCapacity();
					
					   if (cabCap>0.6 && cabCap<1.0){
							
						   cabMarkerMap.get(allCabs.get(i)).setIcon(
									BitmapDescriptorFactory
											.fromResource(R.drawable.cab_yellow));
							Log.v("haha", "haha");
							
						}
					
					if (cabCap==1.0) {

						cabMarkerMap.get(allCabs.get(i)).setIcon(
								BitmapDescriptorFactory
										.fromResource(R.drawable.cab_red));

					}
					
				
				   
				   
				
						
					
					

					Log.v("route id...", allCabs.get(i).getRouteId());
					
					Log.v("update cab locations...", allCabs.get(i)
							.getLocation().toString());
					Log.v("update cab status...", Double.toString(cabCap));
				}

				handler.postDelayed(this, 3000);
		
			}
		}));
	}

}
