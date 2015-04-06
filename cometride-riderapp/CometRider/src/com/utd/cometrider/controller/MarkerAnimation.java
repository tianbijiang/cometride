package com.utd.cometrider.controller;

import java.util.ArrayList;
//import java.util.List;

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
	static public void animateMarkerToGB(final ArrayList<Marker> marker,
			final ArrayList<Cab> cabPositions ) {
		//final LatLng startPosition = marker.getPosition();
		final Handler handler = new Handler();
		//final long start = SystemClock.uptimeMillis();
		//final Interpolator interpolator = new AccelerateDecelerateInterpolator();
	//	final float durationInMs = 30000;

		handler.post(new Runnable() {
			//long elapsed;
			//float t;
		//	float v;
			int i = 0;

			@Override
			public void run() {
				// Calculate progress using interpolator
			//	elapsed = SystemClock.uptimeMillis() - start;
			//	t = elapsed / durationInMs;
			//	v = interpolator.getInterpolation(t);

				// marker.setPosition(latLngInterpolator.interpolate(v,
				// startPosition, finalPosition));

		
		
			   for (int i=0;i<cabPositions.size();i++){
				marker.get(i).setPosition(cabPositions.get(i).getLocation());
				
			   }

			//	if (i < 7) {
					//i++;
				//}

				//if (i == 7) {

					//i = 0;

				//}
				// Repeat till progress is complete.
				// if (t < 1) {
				// Post again 16ms later.
				handler.postDelayed(this, 2000);
				// }
			}
		});
	}

//	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//	static void animateMarkerToICS(Marker marker, LatLng finalPosition,
//			final LatLngInterpolator latLngInterpolator) {
//		TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
//			@Override
//			public LatLng evaluate(float fraction, LatLng startValue,
//					LatLng endValue) {
//				return latLngInterpolator.interpolate(fraction, startValue,
//						endValue);
//			}
//		};
//		Property<Marker, LatLng> property = Property.of(Marker.class,
//				LatLng.class, "position");
//		ObjectAnimator animator = ObjectAnimator.ofObject(marker, property,
//				typeEvaluator, finalPosition);
//		animator.setDuration(3000);
//		animator.start();
//	}
}