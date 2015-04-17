package com.utd.cometrider.controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.utd.cometrider.R;


public class UpdateCabInfo {
	

	
	
	static public void update(final ArrayList<Cab> allCabs,
			final HashMap<Cab, Marker> cabMarkerMap,final LatLng myLocation) {

		final class UpdateCab extends AsyncTask<Void, Void, Void> {

			@Override
			protected Void doInBackground(Void... params) {

				JSONArray jAllCabs = null;

				try {
					jAllCabs = JsonReader
							.readJsonFromUrl("http://cometride.elasticbeanstalk.com/api/cab");

					for (int i = 0; i < jAllCabs.length(); i++) {
						// Cab cab = new Cab();
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

						Log.v("p", p.toString());
						Log.v("allCabs", allCabs.toString());
						allCabs.set(i, allCabs.get(i));
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

			
			
				
				
				
				for (int i = 0; i < allCabs.size(); i++) {
					
				int distance= CalculationByDistance(myLocation, allCabs.get(i).getLocation());
				double speed=32;
				double time = ((double)distance/speed)*60;

					// update cab location
					cabMarkerMap.get(allCabs.get(i)).setPosition(
							allCabs.get(i).getLocation());
					
					//update cab cap
					cabMarkerMap.get(allCabs.get(i)).setSnippet(" Passenger:" + allCabs.get(i).getPassengerCount() +"/" + allCabs.get(i).getMaxCapacity()+ " arriving time:" + (int)time + "m");
						
				
					// update cab color
					double cabCap = (double) allCabs.get(i).getPassengerCount()
							/ (double) allCabs.get(i).getMaxCapacity();

					
					//Check Cap green
					if (cabCap==0) {

						cabMarkerMap.get(allCabs.get(i)).setIcon(
								BitmapDescriptorFactory
										.fromResource(R.drawable.cab_green));
						// Log.v("haha", "haha");

					}
					
					if (cabCap > 0.6 && cabCap < 1.0) {

						cabMarkerMap.get(allCabs.get(i)).setIcon(
								BitmapDescriptorFactory
										.fromResource(R.drawable.cab_yellow));
						// Log.v("haha", "haha");

					}

					if (cabCap == 1.0) {

						cabMarkerMap.get(allCabs.get(i)).setIcon(
								BitmapDescriptorFactory
										.fromResource(R.drawable.cab_red));

					}

					Log.v("route id...", allCabs.get(i).getRouteId());

					Log.v("update cab locations...", allCabs.get(i)
							.getLocation().toString());
					Log.v("update cab status...", Double.toString(cabCap));
				}


			}

		}
		
		
		final Handler handler = new Handler();

		handler.post(new Thread(new Runnable() {

			@Override
			public void run() {

				new UpdateCab().execute();
				handler.postDelayed(this, 3000);

			}
		}));
	}
	
	public static int CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius=6371;//radius of earth in Km         
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
        Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult= Radius*c;
        double km=valueResult/1000;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec =  Integer.valueOf(newFormat.format(km));
        double meter=valueResult%1000;
        int  meterInDec= Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value",""+valueResult+"   KM  "+kmInDec+" Meter   "+meterInDec);

        return kmInDec;
     }
}
