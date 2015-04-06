package com.utd.cometrider.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.utd.cometrider.R;
import com.utd.cometrider.R.drawable;
import com.utd.cometrider.R.id;
import com.utd.cometrider.R.layout;
import com.utd.cometrider.R.string;
import com.utd.cometrider.controller.Cab;
import com.utd.cometrider.controller.HttpConnection;
import com.utd.cometrider.controller.JsonReader;
import com.utd.cometrider.controller.LatLngInterpolator;
import com.utd.cometrider.controller.MarkerAnimation;
import com.utd.cometrider.controller.PathJSONParser;
import com.utd.cometrider.controller.Route;
import com.utd.cometrider.ui.MultiSpinner.MultiSpinnerListener;

public class MainActivity extends FragmentActivity implements MultiSpinnerListener{

	// private static final LatLng WAYPOINT1 = new LatLng(32.985642, -96.74943);

	// private static final LatLng WAYPOINT2 = new LatLng(32.991806,
	// -96.753607);
	// private static final LatLng WAYPOINT3 = new LatLng(32.981866,
	// -96.768726);
	// private static final LatLng WALL_STREET = new LatLng(40.7064, -74.0094);
	// ArrayList<LatLng> cabPositions = new ArrayList<LatLng>();

	JSONArray jAllRoutes = null;
	JSONArray jAllCabs = null;
	ArrayList<Route> allRoutes = new ArrayList<Route>();
	ArrayList<Cab> allCabs = new ArrayList<Cab>();
	ArrayList<Marker> cabLocations = new ArrayList<Marker>();
	List<String> selectedRoutes = new ArrayList<String>();
	ArrayList<Polyline> polyLines=new ArrayList<Polyline>();
	//Context context=getApplicationContext();
	MultiSpinner multiSpinner;
	Context context;
	MainActivity main=this;
	//Activity activity;
    //MultiSpinnerListener listener=null;
//	MultiSpinner multiSpinner;
	//MultiSpinnerListener listener;

	// String routeColor="";
	int numberOfRoutes = 0;
	GoogleMap googleMap;

	// final String TAG = "MainActivity";
	// LatLngInterpolator latLngInterpolator=new LinearFixed();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isNetworkConnected()) {
			
			setContentView(R.layout.activity_main);
			
			main=this;
			context=this;

			
			new getAllCabPositions().execute();

			SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);
		
			  
			googleMap = fm.getMap();

			// enable the My Location layer on the Map
			googleMap.setMyLocationEnabled(true);

			// MarkerOptions options = new MarkerOptions();

			// options.position(WAYPOINT1);
			// options.position(BROOKLYN_BRIDGE);
			// options.position(WALL_STREET);
			// googleMap.addMarker(options);

			// GsonBuilder builder = new GsonBuilder();
			// Gson gson = builder.create();

			// Route route = gson.fromJson(routedata, Route.class);

			// / Log.v("Waypoints", routedata);

			// JSONArray json = jParser.getJSONFromUrl(url);

			// Log.v("Direction URL", url);

			// RouteTask getRoute = new RouteTask();
			// getRoute.execute();

		   
			// animateMarker(cab1,WAYPOINT3,false);

			// spinner
			multiSpinner=new MultiSpinner(context);
			multiSpinner = (MultiSpinner) findViewById(R.id.multi_spinner);
		
		//	 multiSpinner= new MultiSpinner(getContext());
			// multiSpinner.setListener(listener);
			// button
			final Button button = (Button) findViewById(R.id.button1);
			button.setOnClickListener(new View.OnClickListener() {
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

					// Uri gmmIntentUri =
					// Uri.parse("google.navigation:q=Taronga+Zoo,+Sydney+Australia");
					// Intent mapIntent = new Intent(Intent.ACTION_VIEW,
					// gmmIntentUri);
					// mapIntent.setPackage("com.google.android.apps.maps");
					// startActivity(mapIntent);
				}
			});

		} else {

			Toast.makeText(this, "Please activate the internet connection",
					Toast.LENGTH_SHORT).show();
			// finish();

		}

	}



	
	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}

	private String getMapsApiDirectionsUrl(int routeId) {
		String waypoints = "&waypoints=optimize:true";
		// String waypoints="&waypoints=";
		for (int i = 1; i < allRoutes.get(routeId).getWaypoints().size() - 1; i++) {
			waypoints += "|"
					+ allRoutes.get(routeId).getWaypoints().get(i).latitude
					+ ","
					+ allRoutes.get(routeId).getWaypoints().get(i).longitude;

		}
		String sensor = "sensor=false";
		String origin = "origin="
				+ allRoutes.get(routeId).getWaypoints().get(0).latitude + ","
				+ allRoutes.get(routeId).getWaypoints().get(0).longitude;
		String destination = "destination="
				+ allRoutes.get(routeId).getWaypoints().get(0).latitude + ","
				+ allRoutes.get(routeId).getWaypoints().get(0).longitude;
		String params = waypoints + "&" + sensor;
		String output = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + origin + "&" + destination + params;

		Log.v("Route Start", allRoutes.get(routeId).getWaypoints().get(0)
				.toString());
		return url;

	}

	private void addMarkers() {
		if (googleMap != null) {

			for (int i = 0; i < allCabs.size(); i++) {

				cabLocations.add(googleMap.addMarker(new MarkerOptions()
						.position(allCabs.get(i).getLocation())
						.title("Cab" + i)
						.snippet("Active")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.cab_green))));
				// googleMap.addMarker(new
				// MarkerOptions().position(LOWER_MANHATTAN).title("Second Point"));
				// googleMap.addMarker(new
				// MarkerOptions().position(WALL_STREET).title("Third Point"));
			}
		}
	}

	private class getAllCabPositions extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			try {

				jAllCabs = JsonReader
						.readJsonFromUrl("http://cometride.elasticbeanstalk.com/api/cab");
				for (int i = 0; i < jAllCabs.length(); i++) {
					Cab cab = new Cab();
					JSONObject c = jAllCabs.getJSONObject(i);
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
				// Log.v("color", allRoutes.get(n).getColor().toString());

			}

			return null;
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			addMarkers();
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
					allCabs.get(0).getLocation(), 16));
            
			  MarkerAnimation.animateMarkerToGB(cabLocations,allCabs);
			
			new getAllRoutes().execute();

		}

	}

	private class getAllRoutes extends AsyncTask<Void, Void, Void> {

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
					//

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (int n = 0; n < allRoutes.size(); n++) {
				Log.v("id", allRoutes.get(n).getId());
				Log.v("color", allRoutes.get(n).getColor());
				Log.v("WP", allRoutes.get(n).getWaypoints().toString());
				// Log.v("color", allRoutes.get(n).getColor().toString());

			}

			return null;
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);


			 multiSpinner.setItems(selectedRoutes,getString(R.string.for_all),main);
			
			 for (int n = 0; n < allRoutes.size(); n++) {

				String url = getMapsApiDirectionsUrl(n);

				ReadTask downloadTask = new ReadTask();

				downloadTask.execute(url);
				// Log.v("color", routeColor);

			}

			 //Toast.makeText(getApplicationContext(),
		//	 multiSpinner.getSelectedStrings().get(0),
			//Toast.LENGTH_SHORT).show();
		}

	}

	private class ReadTask extends AsyncTask<String, Void, String> {
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

			new ParserTask().execute(result);
		}
	}

	private class ParserTask extends
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
			Polyline polyLine= null;

			
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
					// cabPositions.add(position);
					// Log.v("lat", Double.toString(lat));
					// Log.v("lng", Double.toString(lng));

					// Log.v("color", Integer.toString(path.size()));
				}

				polyLineOptions.addAll(points);
				polyLineOptions.width(5);
				
				if (allRoutes.get(numberOfRoutes).getColor().equals("purple")) {
					polyLineOptions.color(Color.parseColor("#800080"));
				}
				if (allRoutes.get(numberOfRoutes).getColor().equals("green")) {
					polyLineOptions.color(Color.GREEN);
				}
				// Log.v("color", Integer.toString(routes.size()));

			}

			// for(int i=0;i<numberOfRoutes;i++){

			// }
			Log.v("color", allRoutes.get(numberOfRoutes).getColor());
			
			polyLine=googleMap.addPolyline(polyLineOptions);
		    polyLines.add(polyLine);
		   // polyLine.setVisible(false);
		  //  polyLines.get(1).setVisible(false);
		    
		  
		    
			numberOfRoutes++;

		}
	}

	@Override
	public void displaySelectedRoute(int id) {
		polyLines.get(id).setVisible(true);
		cabLocations.get(id).setVisible(true);
		
	}



	public void SetRouteVisibleFalse() {
		// TODO Auto-generated method stub
		for(int i=0;i<numberOfRoutes;i++){
		polyLines.get(i).setVisible(false);
		cabLocations.get(i).setVisible(false);
		}
	}


	
}