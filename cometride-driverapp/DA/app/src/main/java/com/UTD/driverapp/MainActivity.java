package com.UTD.driverapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton imgbtn = (ImageButton)findViewById(R.id.logo);
        Animation ranim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        imgbtn.setAnimation(ranim);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {

                Intent nextIntent = new Intent(getApplicationContext(), Options.class);
                startActivityForResult(nextIntent, 0);

            }

        }, 5000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void OpenOptions(View view){
        Intent intent  =new Intent(this, Options.class);
        startActivity(intent);
    }
}
