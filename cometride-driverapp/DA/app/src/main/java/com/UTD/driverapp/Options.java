package com.UTD.driverapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class Options extends Activity  {

    Cab cab;
    int capacity=9;
    String routeId;
    String sessionId;
    String typeId;
    JSONArray jAllRoutes = null;
    List<String> selectedRoutes = new ArrayList<String>();
    List<String> selectedCabTypes = new ArrayList<String>();
    List<String> selectedCabCapacity = new ArrayList<String>();
    List<String> cabTypeInfo = new ArrayList<String>();
    ArrayList<Route> allRoutes = new ArrayList<Route>();
    List<Route> allCabTypes = new ArrayList<Route>();
    HttpContext context = new BasicHttpContext();
    CookieStore cookieStore = new BasicCookieStore();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        Intent intent1= getIntent();
        String name = intent1.getExtras().getString("cookie-name");
        String value=intent1.getExtras().getString("cookie-value");
        String domain=intent1.getExtras().getString("cookie-domain");
        BasicClientCookie cookie = new BasicClientCookie( name, value );
        cookie.setDomain(domain);
        cookieStore.addCookie( cookie );
         new GetAllRoutes().execute();
        new GetCabTypes().execute();
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

        Button proceed = (Button) findViewById(R.id.next);
        proceed.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Spinner spinner = (Spinner) findViewById(R.id.Spinnercabroute);
                String text = spinner.getSelectedItem().toString();
                for (int i = 0; i < allRoutes.size(); i++) {
                    if (allRoutes.get(i).getName().equals(text)) {
                        routeId = allRoutes.get(i).getId();
                    }
                    Spinner cabType = (Spinner) findViewById(R.id.Spinnercabtype);
                    String text1 = cabType.getSelectedItem().toString();
                    for (int j = 0; j < cabTypeInfo.size(); j++) {
                        if (cabTypeInfo.get(j).equals(text1)) {
                            capacity = Integer.parseInt(selectedCabCapacity.get(j));
                         }
                    }
                }
                new HttpPostTask().execute("http://cometride.elasticbeanstalk.com/api/driver/session");
            }
        });
    }
    public String POST(String url, Cab cab){

        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("dutyStatus", cab.getStatus());
            jsonObject.accumulate("maxCapacity", cab.getmaxCapacity());
            jsonObject.accumulate("routeId", cab.getrouteId());
            json = jsonObject.toString();
            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpclient.execute(httpPost, context);
            inputStream = httpResponse.getEntity().getContent();
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private class HttpPostTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            cab = new Cab();
            cab.setStatus("ON-DUTY");
            cab.setmaxCapacity(capacity);
            cab.setrouteId(routeId);
            return POST(urls[0],cab);
        }
        @Override
        protected void onPostExecute(String result) {
            sessionId=result;
            Intent intent = new Intent(getApplicationContext(), Driver.class);
            intent.putExtra("capacity",capacity);
            intent.putExtra("sessionid", sessionId);
            intent.putExtra( "cookie-name", cookieStore.getCookies().get( 0 ).getName() );
            intent.putExtra( "cookie-value", cookieStore.getCookies().get( 0 ).getValue() );
            intent.putExtra( "cookie-domain", cookieStore.getCookies().get( 0 ).getDomain() );
            startActivity(intent);
        }
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
    private class GetCabTypes extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                jAllRoutes = JsonReader.readJsonFromUrl("http://cometride.elasticbeanstalk.com/api/cabtypes");
                for (int i = 0; i < jAllRoutes.length(); i++) {
                    Route route = new Route();
                    JSONObject r = jAllRoutes.getJSONObject(i);
                    String typeName = r.getString("typeName");
                    String typeId = r.getString("typeId");
                    int maximumCapacity = r.getInt("maximumCapacity");
                    route.setCapacity(maximumCapacity);
                    route.setName(typeName);
                    route.setTypeId(typeId);
                    allCabTypes.add(route);

                    cabTypeInfo.add( route.getTypeName()+ " - " + route.getMaximumCapacity() );
                    selectedCabTypes.add(route.getTypeName());
                    selectedCabCapacity.add(Integer.toString(route.getMaximumCapacity()));
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
            final Spinner sp=(Spinner) findViewById(R.id.Spinnercabtype);
            ArrayAdapter<String> adp= new ArrayAdapter<String>(getApplicationContext(),R.layout.custom_textview,cabTypeInfo);
            sp.setAdapter(adp);

        }
    }
    private class GetAllRoutes extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                jAllRoutes = JsonReader.readJsonFromUrl("http://cometride.elasticbeanstalk.com/api/route");
                for (int i = 0; i < jAllRoutes.length(); i++) {
                    Route route = new Route();
                    JSONObject r = jAllRoutes.getJSONObject(i);
                    String id = r.getString("id");
                    String name = r.getString("name");
                    route.setId(id);
                    route.setName(name);
                    allRoutes.add(route);
                    selectedRoutes.add(route.getName());
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
            final Spinner sp=(Spinner) findViewById(R.id.Spinnercabroute);
            ArrayAdapter<String> adp= new ArrayAdapter<String>(getApplicationContext(),R.layout.custom_textview,selectedRoutes);
            sp.setAdapter(adp);

        }
    }
}
