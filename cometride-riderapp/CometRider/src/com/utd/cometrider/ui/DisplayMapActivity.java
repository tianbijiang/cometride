package com.utd.cometrider.ui;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.utd.cometrider.R;
import com.utd.cometrider.controller.Cab;
import com.utd.cometrider.controller.GPSLocation;
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
	ArrayList<Marker> safePoints = new ArrayList<Marker>();
	List<String> selectedRoutes = new ArrayList<String>();
	ArrayList<Polyline> routePolyLines = new ArrayList<Polyline>();
	ArrayList<Polyline> navPolyLines = new ArrayList<Polyline>();
	RouteSelectionSpinner routeSelectionSpinner;
	Context context = this;
	DisplayMapActivity displayMap = this;
	int numberOfRoutes = 0;
	GoogleMap googleMap;
	HashMap<Cab, Marker> cabMarkerMap;
	HashMap<Route, ArrayList<Marker>> safePointsRouteMap;
	HashMap<String, String> shortNameMap;
	HashMap<String, Boolean> routeSelectedMap;
	LocationManager mLocationManager;
	// LatLng myLocation;
	ImageButton navButton;
	int navFlag = 0;
	int selectedRoute = 0;
	GPSLocation myLocation;
	SupportMapFragment fm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set full screen window
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Check network connection
		if (isNetworkConnected()) {

			setContentView(R.layout.activity_displaymap);

			// get all cab information from server
		//	new GetAllCabInfo().execute();
			new GetAllRoutes().execute();
		
			// setting.setMapToolbarEnabled(false);

			// route selection drop-down
			routeSelectionSpinner = new RouteSelectionSpinner(context);
			routeSelectionSpinner = (RouteSelectionSpinner) findViewById(R.id.multi_spinner);
			routeSelectionSpinner.setVisibility(View.INVISIBLE);
			// interest button
			navButton = (ImageButton) findViewById(R.id.btn_rider);
			navButton.setVisibility(View.INVISIBLE);
			navButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					Toast.makeText(context, "Start Navigation...",
							Toast.LENGTH_SHORT).show();

					myLocation = new GPSLocation(context);

					if (myLocation.canGetLocation()) {

						navButton.setVisibility(View.INVISIBLE);
						for (int i = 0; i < navPolyLines.size(); i++) {
							navPolyLines.get(i).setVisible(false);
						}
						if (allRoutes.get(selectedRoute).getSafepoints().size() != 0) {
							new NavigationTask().execute();
						}

					} else {

						myLocation.showSettingsAlert();

					}

				}
			});

		} else {

			Toast.makeText(
					this,
					"Please activate the internet connection and location service",
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

//	private void initCabInfo() {
//		if (googleMap != null) {
//
//			cabMarkerMap = new HashMap<Cab, Marker>();
//
//			for (int i = 0; i < allCabs.size(); i++) {
//				for (int j = 0; j < allRoutes.size(); j++) {
//					
//					if(allCabs.get(i).getRouteId().equals(allRoutes.get(j).getId())){
//					cabLocationMarkers
//							.add(googleMap
//									.addMarker(new MarkerOptions()
//											.position(
//													allCabs.get(i)
//															.getLocation())
//											.title(allRoutes
//													.get(j)
//													.getShortName())
//											.snippet(
//													"From "
//															+ allRoutes
//																	.get(j)
//																	.getShortName()
//																	
//															+ "\n"
//															+ allCabs
//																	.get(i)
//																	.getPassengerCount()
//															+ "/"
//															+ allCabs
//																	.get(i)
//																	.getMaxCapacity())
//											.icon(BitmapDescriptorFactory
//													.fromResource(R.drawable.cab_green))));
//
//					cabMarkerMap.put(allCabs.get(i), cabLocationMarkers.get(i));
//					}
//				}
//			}
//		}
//	}

	private void displayAllSafePoints() {
		if (googleMap != null) {

			safePointsRouteMap = new HashMap<Route, ArrayList<Marker>>();

			for (int i = 0; i < allRoutes.size(); i++) {

				safePoints = new ArrayList<Marker>();

				if (allRoutes.get(i).getSafepoints().size() > 0) {
					for (int j = 0; j < allRoutes.get(i).getSafepoints().size(); j++) {
						safePoints
								.add(googleMap.addMarker(new MarkerOptions()
										.position(
												allRoutes.get(i)
														.getSafepoints().get(j))
										.title("Safe Waiting Point for "
												+ allRoutes.get(i)
														.getShortName())
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.safe))));

					}

					safePointsRouteMap.put(allRoutes.get(i), safePoints);
				}
			}

		}
	}

	private class NavigationTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			if (myLocation.getLocation() != null) {
				// myLocation = new LatLng(loc.getLatitude(),
				// loc.getLongitude());

				// LatLng nearestSaftPoint = new LatLng(0, 0);

				ArrayList<LatLng> safePointsPerRoute = new ArrayList<LatLng>();
				ArrayList<Double> distances = new ArrayList<Double>();

				safePointsPerRoute = allRoutes.get(selectedRoute)
						.getSafepoints();
				if (safePointsPerRoute.size() > 0 && safePointsPerRoute != null) {
					for (int i = 0; i < safePointsPerRoute.size(); i++) {

						String s = getDistance(myLocation.getLatitude(),
								myLocation.getLongitude(),
								safePointsPerRoute.get(i).latitude,
								safePointsPerRoute.get(i).longitude);

						double distance = Double.parseDouble(s.substring(0,
								s.length() - 3));
						distances.add(distance);
					}

					int minIndex = distances
							.indexOf(Collections.min(distances));

					String url = getMapsApiDirectionsUrl(
							new LatLng(myLocation.getLatitude(),
									myLocation.getLongitude()),
							safePointsPerRoute.get(minIndex));
					// Log.v("safe",
					// safePointsPerRoute.get(minIndex).toString());

					ReadGoogleMapAPIURL readURL = new ReadGoogleMapAPIURL();

					readURL.execute(url);

				}
			} else {

				// Toast.makeText(getApplicationContext(),
				// "Location service is off",
				// Toast.LENGTH_SHORT).show();

			}

			return null;
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

		}

	}

//	private class GetAllCabInfo extends AsyncTask<Void, Void, Void> {
//
//		@Override
//		protected Void doInBackground(Void... params) {
//
//			try {
//
//				jAllCabs = JsonReader
//						.readJsonFromUrl("http://cometride.elasticbeanstalk.com/api/cab");
//				for (int i = 0; i < jAllCabs.length(); i++) {
//					Cab cab = new Cab();
//					JSONObject c = jAllCabs.getJSONObject(i);
//					String routeId = c.getString("routeId");
//					int maxCapacity = c.getInt("maxCapacity");
//					int passengerCount = c.getInt("passengerCount");
//					String status = c.getString("status");
//					cab.setRouteId(routeId);
//					cab.setMaxCapacity(maxCapacity);
//					cab.setPassengerCount(passengerCount);
//					cab.setStatus(status);
//
//					// JSONObject l = c.getJSONObject("location");
//					// ArrayList<LatLng> locations = new ArrayList<LatLng>();
//
//					JSONObject position = c.getJSONObject("location");
//
//					double lat = Double.parseDouble(position.getString("lat"));
//					double lng = Double.parseDouble(position.getString("lng"));
//
//					LatLng p = new LatLng(lat, lng);
//
//					// locations.add(p);
//
//					cab.setLocation(p);
//					allCabs.add(cab);
//
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			for (int n = 0; n < allCabs.size(); n++) {
//				Log.v("Cab Locations", allCabs.get(n).getLocation().toString());
//				// Log.v("color", allRoutes.get(n).getColor());
//
//				// Log.v("WP", allRoutes.get(n).getWaypoints().toString());
//				// Log.v("color", allRoutes.get(n).getColor().toString())
//			}
//
//			return null;
//		}
//
//		protected void onPostExecute(Void result) {
//			super.onPostExecute(result);
//
//		
//			
//			
//		
//
//		}
//
//	}

	private class GetAllRoutes extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			try {

				jAllRoutes = JsonReader
						.readJsonFromUrl("http://cometride.elasticbeanstalk.com/api/route");
				shortNameMap=new HashMap<String, String>();
				routeSelectedMap= new HashMap<String, Boolean>();
				
				for (int i = 0; i < jAllRoutes.length(); i++) {
					Route route = new Route();
					JSONObject r = jAllRoutes.getJSONObject(i);
					String id = r.getString("id");
					String color = r.getString("color");
					String name = r.getString("name");
					String sName = r.getString("shortName");
					String navType = r.getString("navigationType");
					route.setId(id);
					route.setColor(color);
					route.setName(name);
					route.setShortName(sName);
					route.setNavigationType(navType);
					
					
					shortNameMap.put(id, sName);
					routeSelectedMap.put(id, true);
					
					JSONArray wp = r.getJSONArray("waypoints");
					JSONArray safe = r.getJSONArray("safepoints");
					ArrayList<LatLng> waypoints = new ArrayList<LatLng>();
					ArrayList<LatLng> safepoints = new ArrayList<LatLng>();

					for (int j = 0; j < wp.length(); j++) {

						JSONObject position = wp.getJSONObject(j);

						double lat = Double.parseDouble(position
								.getString("lat"));
						double lng = Double.parseDouble(position
								.getString("lng"));

						LatLng p = new LatLng(lat, lng);

						waypoints.add(p);

					}

					for (int j = 0; j < safe.length(); j++) {

						JSONObject position = safe.getJSONObject(j);

						double lat = Double.parseDouble(position
								.getString("lat"));
						double lng = Double.parseDouble(position
								.getString("lng"));

						LatLng p = new LatLng(lat, lng);

						safepoints.add(p);

					}

					route.setWaypoints(waypoints);

					route.setSafepoints(safepoints);

					if (waypoints.size() != 0) {
						allRoutes.add(route);
						selectedRoutes.add(route.getName());
					}

				}
			} catch (IOException e) {
				e.getMessage();
			} catch (JSONException e) {
				e.getMessage();
			}

		//	for (int n = 0; n < allRoutes.size(); n++) {
				// Log.v("id", allRoutes.get(n).getId());
				// Log.v("color", allRoutes.get(n).getColor());
				// Log.v("WP", allRoutes.get(n).getWaypoints().toString());
				// Log.v("SP", allRoutes.get(n).getSafepoints().toString());
			//}
			// Log.v("all route size", Integer.toString(allRoutes.size()));
			return null;
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			// Load map
			fm = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);

			googleMap = fm.getMap();

			// enable the My Location layer on the Map
			googleMap.setMyLocationEnabled(true);
			
		    UiSettings setting = googleMap.getUiSettings();
			setting.setZoomControlsEnabled(true);
			setting.setMapToolbarEnabled(false);
			
			
			
			
		//	if (allCabs.size() > 0 && allCabs != null) {
				//initCabInfo();
				// googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
				// allCabs.get(0).getLocation(), 16));

				LatLng UTLatLng = new LatLng(32.984563, -96.745930);
				googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
						UTLatLng, 16));
			
				
				UpdateCabInfo updater= new UpdateCabInfo();
				
				updater.update(shortNameMap,routeSelectedMap,fm);
				
			//	allCabs= updater.getAllCabs();
			
			
		//	} else {

				//LatLng UTLatLng = new LatLng(32.984563, -96.745930);
				//googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
						//UTLatLng, 16));

		//	}

			
			
			displayAllSafePoints();
			hideAllSafePoints();

			// Log.v("allCabs", allCabs.toString());
			// Log.v("cabMarkerMap", cabMarkerMap.toString());

			// if (myLocation!=null){

			// }

			// Init route selection drop down
			routeSelectionSpinner.setItems(selectedRoutes,
					getString(R.string.all_routes), displayMap);
			routeSelectionSpinner.setVisibility(View.VISIBLE);
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

	// Google Map Direction API for Drawing Routes
	private String getMapsApiDirectionsUrl(int routeId) {
		String waypoints = "";
		String url = "";
		String navType="";
		navFlag = 0;
		
		if (allRoutes.get(routeId).getWaypoints().size() > 0) {
			
			waypoints = "&waypoints=optimize:true";

			for (int i = 1; i < allRoutes.get(routeId).getWaypoints().size() - 1; i++) {
				waypoints += "|"
						+ allRoutes.get(routeId).getWaypoints().get(i).latitude
						+ ","
						+ allRoutes.get(routeId).getWaypoints().get(i).longitude;

			}

			navType=allRoutes.get(routeId).getNavigationType();
			if(navType.equals("WALKING")){	
				navType="walking";
			}else{
				
				navType="driving";
			}
			
			Log.v("navType", navType);
			String sensor = "sensor=false";
			String mode = "mode="+ navType;
			String origin = "origin="
					+ allRoutes.get(routeId).getWaypoints().get(0).latitude
					+ ","
					+ allRoutes.get(routeId).getWaypoints().get(0).longitude;
			String destination = "destination="
					+ allRoutes.get(routeId).getWaypoints().get(0).latitude
					+ ","
					+ allRoutes.get(routeId).getWaypoints().get(0).longitude;
			String params = waypoints + "&" + sensor +"&" + mode ;
			String output = "json";
			url += "https://maps.googleapis.com/maps/api/directions/" + output
					+ "?" + origin + "&" + destination + params;

			Log.v("Route Start", allRoutes.get(routeId).getWaypoints().get(0)
					.toString());

			Log.v("URL", url);
			return url;

		} else {
			return null;
		}

	}
    //For Navigation only
	private String getMapsApiDirectionsUrl(LatLng startPoint, LatLng endPoint) {

		navFlag = 1;
		String url = "";
		// String sensor = "sensor=false";
		String origin = "origin=" + startPoint.latitude + ","
				+ startPoint.longitude;
		String destination = "destination=" + endPoint.latitude + ","
				+ endPoint.longitude;
		String mode = "&mode=walking";
		String output = "json";
		url += "https://maps.googleapis.com/maps/api/directions/" + output
				+ "?" + origin + "&" + destination + mode;

		// Log.v("Route Start",
		// allRoutes.get(routeId).getWaypoints().get(0).toString());
		return url;

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
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions polyLineOptions = null;
			Polyline polyLine = null;

			// traversing through routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				polyLineOptions = new PolylineOptions();
				List<HashMap<String, String>> path = result.get(i);

				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					// if(j==0){ // Get distance from the list
					// distance = (String)point.get("distance");
					// continue;
					// }else if(j==1){ // Get duration from the list
					// duration = (String)point.get("duration");
					// continue;
					// }

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);

				}

				polyLineOptions.addAll(points);
				polyLineOptions.width(5);

				if (navFlag == 0) {

					polyLineOptions.color(Color.parseColor(allRoutes.get(
							numberOfRoutes).getColor()));
				} else {

					polyLineOptions.color(Color.parseColor("#000000"));

				}
				// Log.v("color", Integer.toString(routes.size()));

			}

			// Log.v("color", allRoutes.get(numberOfRoutes).getColor());

			polyLine = googleMap.addPolyline(polyLineOptions);

			if (navFlag == 0) {

				routePolyLines.add(polyLine);

				numberOfRoutes++;
			} else {

				navPolyLines.add(polyLine);
			}

		}
	}

	@Override
	public void displaySelectedRoute(int id) {

		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(allRoutes
				.get(id).getWaypoints().get(0), 16));

		selectedRoute = id;

		if (routePolyLines != null && routePolyLines.size() > 0) {
			routePolyLines.get(id).setVisible(true);
			
			
		}

		routeSelectedMap.put(allRoutes.get(id).getId(), true );
		
//		if (allCabs.size() > 0) {
//
//			for (int i = 0; i < allCabs.size(); i++) {
//				// cabMarkerMap.get(allCabs.get(i)).setVisible(true);
//
//				if (allRoutes.get(id).getId()
//						.equals(allCabs.get(i).getRouteId())) {
//
//					Log.v("routeId", allRoutes.get(id).getId());
//					Log.v("cabrouteId", allCabs.get(i).getRouteId());
//
//					cabMarkerMap.get(allCabs.get(i)).setVisible(true);
//
//					// cabLocationMarkers.get(i).setVisible(true);
//
//				}
//			}
//		}

	}

	public void hideNavRoute() {

		if (navPolyLines.size() > 0 && navPolyLines != null) {
			
			for(int i=0;i<navPolyLines.size();i++){
			navPolyLines.get(i).setVisible(false);
			}
		}
	}

	public void hideNavButton() {
		navButton.setVisibility(View.INVISIBLE);

	}

	public void displayNavButton() {

		navButton.setVisibility(View.VISIBLE);

	}

	// public void hideSpinner() {
	//
	// routeSelectionSpinner.setVisibility(View.INVISIBLE);
	//
	// }
	//
	// public void displaySpinner() {
	//
	// routeSelectionSpinner.setVisibility(View.VISIBLE);
	//
	// }

	public void hideAllSafePoints() {
		// TODO Auto-generated method stub
		for (int i = 0; i < allRoutes.size(); i++) {

			if (allRoutes.get(i).getSafepoints().size() > 0
					&& allRoutes.get(i).getSafepoints() != null) {
				for (int j = 0; j < allRoutes.get(i).getSafepoints().size(); j++) {

					safePointsRouteMap.get(allRoutes.get(i)).get(j)
							.setVisible(false);

				}
			}
		}

	}

	public void diplaySafePointsPerRoute(int id) {

		if (allRoutes != null && allRoutes.size() > 0) {

			for (int i = 0; i < allRoutes.get(id).getSafepoints().size(); i++) {
				safePointsRouteMap.get(allRoutes.get(id)).get(i)
						.setVisible(true);

			}
		}

	}

	public void setRouteVisibleFalse() {
		// TODO Auto-generated method stub

		if (routePolyLines != null && routePolyLines.size() > 0) {
			for (int i = 0; i < numberOfRoutes; i++) {
				routePolyLines.get(i).setVisible(false);
				routeSelectedMap.put(allRoutes.get(i).getId(), false );

			}

		}
		// Log.i("route number", Integer.toString(numberOfRoutes));

//		if (allCabs != null && allCabs.size() > 0) {
//
//			for (int j = 0; j < allCabs.size(); j++) {
//
//				cabLocationMarkers.get(j).setVisible(false);
//				
//			}
//		}
	}

	// public static double CalculationByDistance(LatLng StartP, LatLng EndP) {
	// int Radius = 6371;// radius of earth in Km
	// double lat1 = StartP.latitude;
	// double lat2 = EndP.latitude;
	// double lon1 = StartP.longitude;
	// double lon2 = EndP.longitude;
	// double dLat = Math.toRadians(lat2 - lat1);
	// double dLon = Math.toRadians(lon2 - lon1);
	// double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
	// + Math.cos(Math.toRadians(lat1))
	// * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
	// * Math.sin(dLon / 2);
	// double c = 2 * Math.asin(Math.sqrt(a));
	// double valueResult = Radius * c;
	// double km = valueResult / 1000;
	// DecimalFormat newFormat = new DecimalFormat("####");
	// int kmInDec = Integer.valueOf(newFormat.format(km));
	// double meter = valueResult % 1000;
	// int meterInDec = Integer.valueOf(newFormat.format(meter));
	// Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
	// + " Meter   " + meterInDec);
	//
	// return valueResult;
	// }

	public String getDistance(double lat1, double lon1, double lat2, double lon2) {
		String result_in_kms = "";
		String url = "http://maps.google.com/maps/api/directions/xml?origin="
				+ lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2
				+ "&sensor=false&units=metric&mode=walking";
		String tag[] = { "text" };
		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost httpPost = new HttpPost(url);
			response = httpClient.execute(httpPost, localContext);
			InputStream is = response.getEntity().getContent();
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = builder.parse(is);
			if (doc != null) {
				NodeList nl;
				ArrayList args = new ArrayList();
				for (String s : tag) {
					nl = doc.getElementsByTagName(s);
					if (nl.getLength() > 0) {
						Node node = nl.item(nl.getLength() - 1);
						args.add(node.getTextContent());
					} else {
						args.add(" - ");
					}
				}
				result_in_kms = String.format("%s", args.get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result_in_kms;
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