package com.utd.cometrider.ui;

import java.net.Authenticator;
import java.net.HttpURLConnection;

import java.net.PasswordAuthentication;
import com.utd.cometrider.R;
import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends Activity {
	// Splash screen timer
	private static int SPLASH_TIME_OUT = 3000;
	HttpURLConnection c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);

		// Authenticator.setDefault(new Authenticator(){
		// protected PasswordAuthentication getPasswordAuthentication() {
		// return new
		// PasswordAuthentication("testUser","testUser".toCharArray());
		// }});

		new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

			@Override
			public void run() {
				// This method will be executed once the timer is over
				// Start your app main activity
				Intent i = new Intent(SplashScreen.this,
						DisplayMapActivity.class);
				startActivity(i);

				// close this activity
				finish();
			}
		}, SPLASH_TIME_OUT);
	}

}
