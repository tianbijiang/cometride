package com.utd.cometrider.controller;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Handler;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.utd.cometrider.R;

public class UpdateCabInfo {

	static ArrayList<Marker> allCabMarkers = new ArrayList<Marker>();

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

			@Override
			protected Void doInBackground(Void... params) {

				allCabs = new ArrayList<Cab>();

				JSONArray jAllCabs = null;

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
;
				// remove all markers
				for (int i = 0; i < allCabMarkers.size(); i++) {

					allCabMarkers.get(i).remove();

				}

				//Update Cabs

				if (allCabs.size() > 0) {

					for (int i = 0; i < allCabs.size(); i++) {

						if (routeSelectedMap != null
								&& routeSelectedMap.size() > 0
								&& allCabs != null && allCabs.size() > 0) {


							if (routeSelectedMap.get(allCabs.get(i)
									.getRouteId()) == true) {

								if (shortNameMap.get(allCabs.get(i)
										.getRouteId()) != ""
										&& shortNameMap.get(allCabs.get(i)
												.getRouteId()) != null) {

									if (allCabs != null && allCabs.size() > 0) {
										Marker newMarker = fm
												.getMap()
												.addMarker(
														new MarkerOptions()
																.position(
																		allCabs.get(
																				i)
																				.getLocation())
																.title(shortNameMap
																		.get(allCabs
																				.get(i)
																				.getRouteId())));

										if (allCabs != null
												&& allCabs.size() > 0) {

											// update cab cap
											newMarker
													.setSnippet("Current Passengers:"
															+ allCabs
																	.get(i)
																	.getPassengerCount()
															+ "/"
															+ allCabs
																	.get(i)
																	.getMaxCapacity());

											// update cab color
											double cabCap = (double) allCabs
													.get(i).getPassengerCount()
													/ (double) allCabs.get(i)
															.getMaxCapacity();

											// Check Cap green
											if (cabCap < 0.6) {

												newMarker
														.setIcon(BitmapDescriptorFactory
																.fromResource(R.drawable.cab_green));
												// Log.v("haha", "haha");

											}

											if (cabCap > 0.6 && cabCap < 1.0) {

												newMarker
														.setIcon(BitmapDescriptorFactory
																.fromResource(R.drawable.cab_yellow));
												// Log.v("haha", "haha");

											}

											if (cabCap >= 1.0) {

												newMarker
														.setIcon(BitmapDescriptorFactory
																.fromResource(R.drawable.cab_red));

											}

											allCabMarkers.add(newMarker);
										}

									}
								}

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
