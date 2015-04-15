package com.UTD.driverapp;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Options extends Activity implements View.OnClickListener {
    Button btnPost;
    Cab cab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        final RadioButton seven = (RadioButton) findViewById(R.id.radioButton1);
        final RadioButton nine = (RadioButton) findViewById(R.id.radioButton2);

        Button proceed = (Button) findViewById(R.id.next);
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
        btnPost = (Button) findViewById(R.id.next);
        btnPost.setOnClickListener(Options.this);

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



  /*  public void OpenDriver(View v)
    {
        final RadioButton seven = (RadioButton) findViewById(R.id.radioButton1);
        final RadioButton nine = (RadioButton) findViewById(R.id.radioButton2);

        Button proceed = (Button) findViewById(R.id.next);
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
    }*/
    public static String POST(String url, Cab cab){

        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("dutyStatus", cab.getStatus());
            jsonObject.accumulate("maxCapacity", cab.getmaxCapacity());
            jsonObject.accumulate("routeId", cab.getrouteId());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string using Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public void onClick(View view) {

        switch(view.getId()){
            case R.id.btnPost:
                if(!validate())
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
                // call AsynTask to perform network operation on separate thread

                new HttpAsyncTask().execute("http://cometride.elasticbeanstalk.com/api/session");
                break;
        }

    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            cab = new Cab();
            cab.setStatus("OnDuty");
            cab.setmaxCapacity(9);
            cab.setrouteId("1");


            return POST(urls[0],cab);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validate(){
       /* if(Spinner.getText().toString().trim().equals(""))
            return false;
        else if(textView8.getText().toString().trim().equals(""))
            return false;
        else if(textView7.getText().toString().trim().equals(""))
            return false;
        else*/
            return true;
    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

}
