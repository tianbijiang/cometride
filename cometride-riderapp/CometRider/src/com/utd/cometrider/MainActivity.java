package com.utd.cometrider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.utd.cometrider.LatLngInterpolator.LinearFixed;
import com.utd.cometrider.MultiSpinner.MultiSpinnerListener;

public class MainActivity extends FragmentActivity  {
	private static final LatLng WAYPOINT1 = new LatLng(32.985642, -96.74943);

	private static final LatLng WAYPOINT2 = new LatLng(32.991806, -96.753607);
	private static final LatLng WAYPOINT3 = new LatLng (32.981866,-96.768726);
	// private static final LatLng WALL_STREET = new LatLng(40.7064, -74.0094);
	ArrayList<LatLng>cabPositions = new ArrayList<LatLng>();


	List<String> items = Arrays.asList("Route1", "Route2", "Route3");
	MultiSpinnerListener listener;
	
	GoogleMap googleMap;
	Marker cab1;
	// final String TAG = "MainActivity";
	LatLngInterpolator  latLngInterpolator=new LinearFixed();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);

		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		googleMap = fm.getMap();
		
		// enable the My Location layer on the Map
		googleMap.setMyLocationEnabled(true);
 
		//MarkerOptions options = new MarkerOptions();

		//options.position(WAYPOINT1);
		// options.position(BROOKLYN_BRIDGE);
		// options.position(WALL_STREET);
	//	googleMap.addMarker(options);
		String url = getMapsApiDirectionsUrl();
		Log.v("Direction URL", url);
		ReadTask downloadTask = new ReadTask();
		downloadTask.execute(url);
           
		
		 
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(WAYPOINT1, 16));
		addMarkers();
		MarkerAnimation.animateMarkerToGB(cab1, WAYPOINT3, latLngInterpolator);
		//animateMarker(cab1,WAYPOINT3,false);
	
		// spinner
		MultiSpinner multiSpinner = (MultiSpinner) findViewById(R.id.multi_spinner);
		multiSpinner.setItems(items, getString(R.string.for_all), listener);

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
				String navUrl = "http://maps.google.com/maps?saddr=" + latMy
						+ "," + lngMy + "&daddr=" + "32.985642, -96.74943";

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

	}

	private String getMapsApiDirectionsUrl() {
		String waypoints = "&waypoints=optimize:true|" + WAYPOINT1.latitude
				+ "," + WAYPOINT1.longitude + "|" + "|" + WAYPOINT2.latitude
				+ "," + WAYPOINT2.longitude;

		String sensor = "sensor=false";
		String origin = "origin=32.985559,-96.749478";
		String destination = "destination=32.985559,-96.749478";
		String params = waypoints + "&" + sensor;
		String output = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + origin + "&" + destination + params;
		return url;
	}

	
	
	
	 
	private void addMarkers() {
		if (googleMap != null) {
	cab1 =	googleMap.addMarker(new MarkerOptions()
					.position(WAYPOINT1)
					.title("Cab1")
					.snippet("Active")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.cab_green)));
			// googleMap.addMarker(new
			// MarkerOptions().position(LOWER_MANHATTAN).title("Second Point"));
			// googleMap.addMarker(new
			// MarkerOptions().position(WALL_STREET).title("Third Point"));
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
					//cabPositions.add(position);
				    Log.v("lat", Double.toString(lat));
	                Log.v("lng", Double.toString(lng));
				}

				polyLineOptions.addAll(points);
				polyLineOptions.width(2);
				polyLineOptions.color(Color.GREEN);
			}

			googleMap.addPolyline(polyLineOptions);
			

		}
	}
}