package com.sandeep.da;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


public class Driver2 extends ActionBarActivity {

    int num = 0;
    int total;
    TextView tView,tView1;
    ImageButton plus1, minus1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver2);
        tView = (TextView) findViewById(R.id.textView8);
        tView1 = (TextView) findViewById(R.id.textView7);
        plus1 = (ImageButton) findViewById(R.id.plus);
        minus1 = (ImageButton) findViewById(R.id.minus);

        plus1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (v == plus1) {
                    if(num<9)
                    {
                        num++;
                        total++;
                        tView1.setText(Integer.toString(total));
                        tView.setText(Integer.toString(num));
                    }
                }
            }
        });
        minus1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (v == minus1) {
                    if(num>0)
                    {
                        num--;

                        tView.setText(Integer.toString(num));
                    }
                }
            }
        });
        plus1.setOnLongClickListener(new View.OnLongClickListener() {

            public boolean onLongClick(View v) {

                total=total+(9-num);
                num=9;
                tView1.setText(Integer.toString(total));
                tView.setText(Integer.toString(num));
                return true;
            }
        });
        minus1.setOnLongClickListener(new View.OnLongClickListener() {

            public boolean onLongClick(View v) {

                num=0;
                tView.setText(Integer.toString(num));
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_driver2, menu);
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
}
