package com.utd.cometrider.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.utd.cometrider.R;
import com.utd.cometrider.controller.Cab;
import com.utd.cometrider.controller.HttpConnection;
import com.utd.cometrider.controller.JsonReader;
import com.utd.cometrider.controller.PathJSONParser;
import com.utd.cometrider.controller.Route;
import com.utd.cometrider.controller.UpdateCabInfo;
import com.utd.cometrider.ui.RouteSelectionSpinner.MultiSpinnerListener;

public class DisplayMapActivity extends FragmentActivity implements
		MultiSpinnerListener {

	// Init all variables
	JSONArray jAllRoutes = null;
	JSONArray jAllCabs = null;
	ArrayList<Route> allRoutes = new ArrayList<Route>();
	ArrayList<Cab> allCabs = new ArrayList<Cab>();
	ArrayList<Marker> cabLocationMarkers = new ArrayList<Marker>();
	List<String> selectedRoutes = new ArrayList<String>();
	ArrayList<Polyline> routePolyLines = new ArrayList<Polyline>();
	RouteSelectionSpinner routeSelectionSpinner;
	Context context = this;
	DisplayMapActivity displayMap = this;
	int numberOfRoutes = 0;
	GoogleMap googleMap;
	HashMap<Cab, Marker> cabMarkerMap;
	LocationManager mLocationManager;
	LatLng myLocation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set full screen window
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Check network connection
		if (isNetworkConnected()) {

			setContentView(R.layout.activity_displaymap);
			
	
			// get all cab information from server
			new GetAllCabInfo().execute();
			
            //get current gps location
			mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				            0, mLocationListener);
				
			myLocation=new LatLng(0,0);
			
			

			// Load map
			SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);

			googleMap = fm.getMap();

			// enable the My Location layer on the Map
			googleMap.setMyLocationEnabled(true);

			// route selection drop-down
			routeSelectionSpinner = new RouteSelectionSpinner(context);
			routeSelectionSpinner = (RouteSelectionSpinner) findViewById(R.id.multi_spinner);

			// interest button
			final Button button = (Button) findViewById(R.id.btn_rider);
			button.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
					Criteria cri = new Criteria();

					Location loc = locManager.getLastKnownLocation(locManager
							.getBestProvider(cri, true));
					String latMy = String.valueOf(loc.getLatitude());
					String lngMy = String.valueOf(loc.getLongitude());

					Toast.makeText(getApplicationContext(), "Navigation",
							Toast.LENGTH_SHORT).show();
					String navUrl = "http://maps.google.com/maps?saddr="
							+ latMy + "," + lngMy + "&daddr="
							+ "32.985642, -96.74943";

					Intent navigation = new Intent(Intent.ACTION_VIEW);
					navigation.setData(Uri.parse(navUrl));

					startActivity(navigation);

				}
			});

		} else {

			Toast.makeText(this, "Please activate the internet connection",
					Toast.LENGTH_SHORT).show();
			// finish();

		}

	}

	// Network Connection
	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}

	
	private final LocationListener mLocationListener = new LocationListener() {
	    @Override
	    public void onLocationChanged(final Location location) {
	    	myLocation =new LatLng( location.getLatitude(),location.getLongitude());
	    	 String Text = "My current location is: " +
	    		        "Latitud = " + location.getLatitude() +
	    		        "Longitud = " + location.getLongitude();

	    		        Toast.makeText( getApplicationContext(), Text, Toast.LENGTH_SHORT).show();
	    }

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private void initCabInfo() {
		if (googleMap != null) {

			cabMarkerMap = new HashMap<Cab, Marker>();

			for (int i = 0; i < allCabs.size(); i++) {

				cabLocationMarkers.add(googleMap.addMarker(new MarkerOptions()
						.position(allCabs.get(i).getLocation())
						.title(allCabs.get(i).getRouteId())
						.snippet("From " + allCabs.get(i).getRouteId()+ "\n" + allCabs.get(i).getPassengerCount() +"/" + allCabs.get(i).getMaxCapacity())
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.cab_green))));

				cabMarkerMap.put(allCabs.get(i), cabLocationMarkers.get(i));
			}
		}
	}

	private class GetAllCabInfo extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			
			 
			
			
			try {

				jAllCabs = JsonReader
						.readJsonFromUrl("http://cometride.elasticbeanstalk.com/api/cab");
				for (int i = 0; i < jAllCabs.length(); i++) {
					Cab cab = new Cab();
					JSONObject c = jAllCabs.getJSONObject(i);
					String routeId = c.getString("routeId");
					int maxCapacity = c.getInt("maxCapacity");
					int passengerCount = c.getInt("passengerCount");
					String status = c.getString("status");
					cab.setRouteId(routeId);
					cab.setMaxCapacity(maxCapacity);
					cab.setPassengerCount(passengerCount);
					cab.setStatus(status);

					// JSONObject l = c.getJSONObject("location");
					// ArrayList<LatLng> locations = new ArrayList<LatLng>();

					JSONObject position = c.getJSONObject("location");

					double lat = Double.parseDouble(position.getString("lat"));
					double lng = Double.parseDouble(position.getString("lng"));

					LatLng p = new LatLng(lat, lng);

					// locations.add(p);

					cab.setLocation(p);
					allCabs.add(cab);
				
					
					//

				
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (int n = 0; n < allCabs.size(); n++) {
				Log.v("Cab Locations", allCabs.get(n).getLocation().toString());
				// Log.v("color", allRoutes.get(n).getColor());

				// Log.v("WP", allRoutes.get(n).getWaypoints().toString());
				// Log.v("color", allRoutes.get(n).getColor().toString())
			}

			return null;
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

		

		
            
            new GetAllRoutes().execute();

		}

	}

	
	private class GetAllRoutes extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			try {

				jAllRoutes = JsonReader
						.readJsonFromUrl("http://cometride.elasticbeanstalk.com/api/route");
				for (int i = 0; i < jAllRoutes.length(); i++) {
					Route route = new Route();
					JSONObject r = jAllRoutes.getJSONObject(i);
					String id = r.getString("id");
					String color = r.getString("color");
					String name = r.getString("name");
					route.setId(id);
					route.setColor(color);
					route.setName(name);
					JSONArray wp = r.getJSONArray("waypoints");
					ArrayList<LatLng> waypoints = new ArrayList<LatLng>();

					for (int j = 0; j < wp.length(); j++) {

						JSONObject position = wp.getJSONObject(j);

						double lat = Double.parseDouble(position
								.getString("lat"));
						double lng = Double.parseDouble(position
								.getString("lng"));

						LatLng p = new LatLng(lat, lng);

						waypoints.add(p);

					}

					route.setWaypoints(waypoints);
					allRoutes.add(route);
					selectedRoutes.add(route.getName());

				}
			} catch (IOException e) {
				e.getMessage();
			} catch (JSONException e) {
				e.getMessage();
			}

			for (int n = 0; n < allRoutes.size(); n++) {
				Log.v("id", allRoutes.get(n).getId());
				Log.v("color", allRoutes.get(n).getColor());
				Log.v("WP", allRoutes.get(n).getWaypoints().toString());

			}

			return null;
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			initCabInfo();

			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
					allCabs.get(0).getLocation(), 16));

			Log.v("allCabs", allCabs.toString());
			Log.v("cabMarkerMap", cabMarkerMap.toString());
			
			
			 
			if (myLocation!=null){
				UpdateCabInfo.update(allCabs, cabMarkerMap, myLocation);
	            
			}
			
			// Init route selection drop down
			routeSelectionSpinner.setItems(selectedRoutes,
					getString(R.string.all_routes), displayMap);

			// Get all Route info from server url
			for (int n = 0; n < allRoutes.size(); n++) {

				String url = getMapsApiDirectionsUrl(n);

				if (url != null) {

					ReadGoogleMapAPIURL readURL = new ReadGoogleMapAPIURL();

					readURL.execute(url);
				}

			}

		}

	}

	// Google Map Direction API
	private String getMapsApiDirectionsUrl(int routeId) {
		String waypoints = "";
		String url = "";
		if (allRoutes.get(routeId).getWaypoints().size() > 0) {
			waypoints = "&waypoints=optimize:true";

			for (int i = 1; i < allRoutes.get(routeId).getWaypoints().size() - 1; i++) {
				waypoints += "|"
						+ allRoutes.get(routeId).getWaypoints().get(i).latitude
						+ ","
						+ allRoutes.get(routeId).getWaypoints().get(i).longitude;

			}

			String sensor = "sensor=false";
			String origin = "origin="
					+ allRoutes.get(routeId).getWaypoints().get(0).latitude
					+ ","
					+ allRoutes.get(routeId).getWaypoints().get(0).longitude;
			String destination = "destination="
					+ allRoutes.get(routeId).getWaypoints().get(0).latitude
					+ ","
					+ allRoutes.get(routeId).getWaypoints().get(0).longitude;
			String params = waypoints + "&" + sensor;
			String output = "json";
			url += "https://maps.googleapis.com/maps/api/directions/" + output
					+ "?" + origin + "&" + destination + params;

			Log.v("Route Start", allRoutes.get(routeId).getWaypoints().get(0)
					.toString());
			return url;
		} else {
			return null;
		}

	}

	private class ReadGoogleMapAPIURL extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... url) {
			String data = "";

			try {
				HttpConnection http = new HttpConnection();
				data = http.readUrl(url[0]);

			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			new ParserGoogleMapAPIURLToJSON().execute(result);
		}
	}

	private class ParserGoogleMapAPIURLToJSON extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				PathJSONParser parser = new PathJSONParser();
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
			ArrayList<LatLng> points = null;
			PolylineOptions polyLineOptions = null;
			Polyline polyLine = null;

			// traversing through routes
			for (int i = 0; i < routes.size(); i++) {
				points = new ArrayList<LatLng>();
				polyLineOptions = new PolylineOptions();
				List<HashMap<String, String>> path = routes.get(i);

				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);
					points.add(position);

				}

				polyLineOptions.addAll(points);
				polyLineOptions.width(5);

				polyLineOptions.color(Color.parseColor(allRoutes.get(
						numberOfRoutes).getColor()));

				// Log.v("color", Integer.toString(routes.size()));

			}

			Log.v("color", allRoutes.get(numberOfRoutes).getColor());

			polyLine = googleMap.addPolyline(polyLineOptions);
			routePolyLines.add(polyLine);
			numberOfRoutes++;

		}
	}

	@Override
	public void displaySelectedRoute(int id) {

		routePolyLines.get(id).setVisible(true);

		for (int i = 0; i < allCabs.size(); i++) {
			// cabMarkerMap.get(allCabs.get(i)).setVisible(true);

			if (allRoutes.get(id).getId().equals(allCabs.get(i).getRouteId())) {

				Log.v("routeId", allRoutes.get(id).getId());
				Log.v("cabrouteId", allCabs.get(i).getRouteId());

				cabMarkerMap.get(allCabs.get(i)).setVisible(true);

				// cabLocationMarkers.get(i).setVisible(true);

			}
		}

	}

	public void setRouteVisibleFalse() {
		// TODO Auto-generated method stub
		for (int i = 0; i < numberOfRoutes; i++) {
			routePolyLines.get(i).setVisible(false);

		}

		for (int j = 0; j < allCabs.size(); j++) {

			cabLocationMarkers.get(j).setVisible(false);
		}

	}

	// exit application, clear all running threads
	private Boolean exit = false;

	@Override
	public void onBackPressed() {
		if (exit) {
			System.exit(0); // finish activity
		} else {
			Toast.makeText(this, "Press Back again to Exit.",
					Toast.LENGTH_SHORT).show();
			exit = true;
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					exit = false;
				}
			}, 3 * 1000);

		}

	}

}