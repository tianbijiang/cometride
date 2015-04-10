package com.sandeep.da;
import com.sandeep.da.Cab;
import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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


public class Driver2 extends Activity implements View.OnClickListener {

    int num = 0;
    int total;
    TextView tView,tView1;
    ImageButton plus1, minus1;

    TextView textView7,textView8,textView10;
    Button btnPost;
    Cab cab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver2);
        tView = (TextView) findViewById(R.id.textView8);
        tView1 = (TextView) findViewById(R.id.textView7);
        plus1 = (ImageButton) findViewById(R.id.plus);
        minus1 = (ImageButton) findViewById(R.id.minus);

        textView10 = (TextView) findViewById(R.id.textView10);
        textView7= (TextView) findViewById(R.id.textView7);
        textView8= (TextView) findViewById(R.id.textView8);
        btnPost = (Button) findViewById(R.id.btnPost);

        btnPost.setOnClickListener(Driver2.this);

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
    public boolean dispatchKeyEvent(KeyEvent event) {

        tView = (TextView) findViewById(R.id.textView8);
        tView1 = (TextView) findViewById(R.id.textView7);
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(num<9)
                            {
                                num++;
                                total++;
                                tView1.setText(Integer.toString(total));
                                tView.setText(Integer.toString(num));
                            }
                        }
                    });
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(num>0)
                            {
                                num--;

                                tView.setText(Integer.toString(num));
                            }
                        }
                    });
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
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
            jsonObject.accumulate("maxCapacity", cab.getCapacity());
            jsonObject.accumulate("routeId", cab.getrouteId());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
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
            cab.setStatus(textView10.getText().toString());
            cab.setCapacity(Integer.parseInt(textView8.getText().toString()));
            cab.setrouteId(textView7.getText().toString());

            return POST(urls[0],cab);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validate(){
        if(textView10.getText().toString().trim().equals(""))
            return false;
        else if(textView8.getText().toString().trim().equals(""))
            return false;
        else if(textView7.getText().toString().trim().equals(""))
            return false;
        else
            return true;
    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
