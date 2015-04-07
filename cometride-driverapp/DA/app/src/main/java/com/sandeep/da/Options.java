package com.sandeep.da;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


public class Options extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_options, menu);
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



    public void OpenDriver(View v)
    {
        final RadioButton seven = (RadioButton) findViewById(R.id.radioButton1);
        final RadioButton nine = (RadioButton) findViewById(R.id.radioButton2);

        Button proceed = (Button) findViewById(R.id.button2);
        proceed.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(seven.isChecked()) {
                    Intent sevenIntent = new Intent(getApplicationContext(), Driver.class);
                    startActivityForResult(sevenIntent, 0);
                } else if (nine.isChecked()) {
                    Intent nineIntent = new Intent(getApplicationContext(), Driver2.class);
                    startActivityForResult(nineIntent, 0);
                }else{
                    Toast.makeText(getApplicationContext(), "Choose a Cab Type", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


}
