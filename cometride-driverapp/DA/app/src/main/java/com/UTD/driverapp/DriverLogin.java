package com.UTD.driverapp;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;



public class DriverLogin extends Activity {

    TextView username;
    EditText pass;
    int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_login);
        username = (TextView) findViewById(R.id.email);
        pass=(EditText) findViewById(R.id.password);
        Button btn = (Button) findViewById(R.id.email_sign_in_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new HttpPostTask().execute("http://cometride.elasticbeanstalk.com/api/driver/session");

            }
        });
    }
    public class HttpPostTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            String cookieUrl = "http://cometride.elasticbeanstalk.com/api/driver/session";
            String authenticateUrl = "http://cometride.elasticbeanstalk.com/j_security_check";
            String dataUrl = "http://cometride.elasticbeanstalk.com/api/admin/users";


            final String userNameKey = "j_username";
            final String userPassKey = "j_password";
            final String userName = username.getText().toString();
            final String userPass = pass.getText().toString();

            HttpClient client = new DefaultHttpClient();
            CookieStore cookieStore = new BasicCookieStore();
            HttpContext context = new BasicHttpContext();
            context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

            String getUrl = cookieUrl;
            HttpGet get = new HttpGet(getUrl);
            HttpResponse getResponse = null;
            try {
                getResponse = client.execute(get, context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("ConnectionTest", "GET @ " + getUrl);
            Log.d("ConnectionTest", getResponse.getStatusLine().toString());

            List<NameValuePair> authDataList = new ArrayList<NameValuePair>();
            authDataList.add(new NameValuePair() {
                @Override
                public String getName() {
                    return userNameKey;
                }

                @Override
                public String getValue() {
                    return userName;
                }
            });
            authDataList.add(new NameValuePair() {
                @Override
                public String getName() {
                    return userPassKey;
                }

                @Override
                public String getValue() {
                    return userPass;
                }
            });
            HttpEntity authEntity = null;
            try {
                authEntity = new UrlEncodedFormEntity(authDataList);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String authPostUrl = authenticateUrl;
            HttpPost authPost = new HttpPost(authPostUrl);
            authPost.setEntity(authEntity);
            HttpResponse authPostResponse = null;
            try {
                authPostResponse = client.execute(authPost, context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("ConnectionTest", "POST @ " + authPostUrl);
            Log.d("ConnectionTest", authPostResponse.getStatusLine().getStatusCode() +"");
            Log.d("ConnectionTest", authPostResponse.getStatusLine().toString());

            status=authPostResponse.getStatusLine().getStatusCode();
            Log.d("ConnectionTest", status +"");

            if(status==405)
            {
                Intent next = new Intent(getApplicationContext(), Options.class);
                next.putExtra( "cookie-name", cookieStore.getCookies().get( 0 ).getName() );
                next.putExtra( "cookie-value", cookieStore.getCookies().get( 0 ).getValue() );
                next.putExtra( "cookie-domain", cookieStore.getCookies().get( 0 ).getDomain() );
                startActivity(next);
                Log.v( "cookie-name", cookieStore.getCookies().get( 0 ).getName());
                Log.v("cookie-value", cookieStore.getCookies().get( 0 ).getValue());

            }
            else
            {
                Toast.makeText(getBaseContext(), "Login Failed!", Toast.LENGTH_LONG).show();
            }
            return null;
        }



        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {



        }
    }

}
