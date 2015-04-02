package com.utd.cometrider;

import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Property;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MarkerAnimation {
	static void animateMarkerToGB(final Marker marker,
			final LatLng finalPosition,
			final LatLngInterpolator latLngInterpolator) {
		//final LatLng startPosition = marker.getPosition();
		final Handler handler = new Handler();
		//final long start = SystemClock.uptimeMillis();
		//final Interpolator interpolator = new AccelerateDecelerateInterpolator();
	//	final float durationInMs = 30000;

		handler.post(new Runnable() {
			long elapsed;
			float t;
			float v;
			int i = 0;

			@Override
			public void run() {
				// Calculate progress using interpolator
			//	elapsed = SystemClock.uptimeMillis() - start;
			//	t = elapsed / durationInMs;
			//	v = interpolator.getInterpolation(t);

				// marker.setPosition(latLngInterpolator.interpolate(v,
				// startPosition, finalPosition));

				ArrayList<LatLng> cabPositions = new ArrayList<LatLng>();
				LatLng WAYPOINT1 = new LatLng(32.985642, -96.74943);

				LatLng WAYPOINT2 = new LatLng(32.985633, -96.749914);

				LatLng WAYPOINT3 = new LatLng(32.985687, -96.750976);
				LatLng WAYPOINT4 = new LatLng(32.986893, -96.750976);
				LatLng WAYPOINT5 = new LatLng(32.988369, -96.750944);
				LatLng WAYPOINT6 = new LatLng(32.989620, -96.750987);
				LatLng WAYPOINT7 = new LatLng(32.990673, -96.750998);
				LatLng WAYPOINT8 = new LatLng(32.990655, -96.752071);
			//	LatLng WAYPOINT9 = new LatLng(32.985687, -96.750976);
				//LatLng WAYPOINT10 = new LatLng(32.985687, -96.750976);
				cabPositions.add(WAYPOINT1);
				cabPositions.add(WAYPOINT2);
				cabPositions.add(WAYPOINT3);
				cabPositions.add(WAYPOINT4);
				cabPositions.add(WAYPOINT5);
				cabPositions.add(WAYPOINT6);
				cabPositions.add(WAYPOINT7);
				cabPositions.add(WAYPOINT8);
			
				marker.setPosition(cabPositions.get(i));

				if (i < 7) {
					i++;
				}

				if (i == 7) {

					i = 0;

				}
				// Repeat till progress is complete.
				// if (t < 1) {
				// Post again 16ms later.
				handler.postDelayed(this, 2000);
				// }
			}
		});
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	static void animateMarkerToICS(Marker marker, LatLng finalPosition,
			final LatLngInterpolator latLngInterpolator) {
		TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
			@Override
			public LatLng evaluate(float fraction, LatLng startValue,
					LatLng endValue) {
				return latLngInterpolator.interpolate(fraction, startValue,
						endValue);
			}
		};
		Property<Marker, LatLng> property = Property.of(Marker.class,
				LatLng.class, "position");
		ObjectAnimator animator = ObjectAnimator.ofObject(marker, property,
				typeEvaluator, finalPosition);
		animator.setDuration(3000);
		animator.start();
	}
}