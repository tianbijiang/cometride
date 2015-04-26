import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import utdallas.ridetrackers.server.datatypes.LatLng;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by matt lautz on 4/24/2015.
 */
public class DriverEmulatorCommons {

    private static final List<LatLng> locations = new ArrayList<LatLng>();

    public static void main( String[] args ) throws IOException, InterruptedException {

        locations.add( new LatLng(32.985662, -96.749399) );
        locations.add( new LatLng(32.985680, -96.750220) );
        locations.add( new LatLng(32.985689, -96.750971) );
        locations.add( new LatLng(32.986409, -96.750992) );
        locations.add( new LatLng(32.987300, -96.751003) );
        locations.add( new LatLng(32.987948, -96.750971) );
        locations.add( new LatLng(32.988623, -96.750982) );
        locations.add( new LatLng(32.989379, -96.750982) );
        locations.add( new LatLng(32.990027, -96.751003) );
        locations.add( new LatLng(32.990693, -96.750960) );
        locations.add( new LatLng(32.990720, -96.752076) );
        locations.add( new LatLng(32.991223, -96.752612) );
        locations.add( new LatLng(32.991790, -96.752634) );
        locations.add( new LatLng(32.991808, -96.753632) );
        locations.add( new LatLng(32.991268, -96.753653) );
        locations.add( new LatLng(32.990801, -96.753642) );
        locations.add( new LatLng(32.990702, -96.752602) );
        locations.add( new LatLng(32.990675, -96.751057) );
        locations.add( new LatLng(32.989730, -96.751035) );
        locations.add( new LatLng(32.988866, -96.750971) );
        locations.add( new LatLng(32.987966, -96.751057) );
        locations.add( new LatLng(32.987102, -96.751024) );
        locations.add( new LatLng(32.986409, -96.751046) );
        locations.add( new LatLng(32.985716, -96.751057) );
        locations.add( new LatLng(32.985662, -96.750273) );
        locations.add( new LatLng(32.985572, -96.749758) );


        final PassengerTracker tracker = new PassengerTracker();
        final int maxPassengers = 8;

        System.out.println( "Locations loaded!" );

        ApacheHttpClientConfig config = new DefaultApacheHttpClientConfig();
        config.getProperties().put(ApacheHttpClientConfig.PROPERTY_HANDLE_COOKIES, true);
        ApacheHttpClient client = ApacheHttpClient.create(config);
        final WebResource authResource = client.resource( "http://cometride.elasticbeanstalk.com/api/driver" );

        String authResponse = authResource.get( String.class );


        final WebResource loginResource = client.resource( "http://cometride.elasticbeanstalk.com/j_security_check" );
        Form form = new Form();
        form.putSingle("j_username", "testUser");
        form.putSingle("j_password", "testUser");
        try {
            loginResource.type("application/x-www-form-urlencoded").post(form);
        } catch ( UniformInterfaceException e ) {
            if( e.getResponse().getStatus() == 303 ) {
                System.out.println( "Redirect detected: successful authentication!" );
            } else {
                System.out.println( "Unexpected status code on login attempt. Shutting down." );
            }
        }

        TimeUnit.SECONDS.sleep( 2 );

        final WebResource sessionResource = client.resource( "http://cometride.elasticbeanstalk.com/api/driver/session" );
        Map<String, Object> sessionData = new HashMap<String, Object>();
        sessionData.put( "dutyStatus", "ON-DUTY" );
        sessionData.put( "maxCapacity", maxPassengers );
        sessionData.put( "routeId", "route-593ff0db-13c8-41df-af63-99732488925f" );

        final String sessionId = sessionResource.entity(sessionData).type(MediaType.APPLICATION_JSON).
                post(String.class);
//        System.out.println( "Session ID obtained: " + sessionId );

        final String sessionId2 = sessionResource.entity(sessionData).type(MediaType.APPLICATION_JSON).
                post(String.class);
        System.out.println( "Session ID (#2) obtained: " + sessionId2 );

        final WebResource locationResource = client.resource( "http://cometride.elasticbeanstalk.com/api/driver/cab" );

        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Map<String, Object> update = new HashMap<String, Object>();

                LatLng location = locations.remove( 0 );
                locations.add( locations.size() -1, location );

                int passengers = tracker.getPassengers();
                while( Math.random() < 0.25 && passengers < maxPassengers ) {
                    passengers++;
                }
                if( Math.random() < 0.3 && passengers > 0 ) {
                    passengers--;
                }
                int passengersAdded = passengers - tracker.getPassengers() > 0 ? passengers - tracker.getPassengers() : 0;


                update.put("cabSessionId",sessionId2);
                update.put("lat", location.getLat());
                update.put("lng", location.getLng());
                update.put("passengerCount", passengers);
                update.put("passengersAdded", passengersAdded );

                tracker.setPassengers( passengers );

                System.out.println( "Sending: " + passengers + "," + passengersAdded );

                ClientResponse response = locationResource.entity( update ).type(MediaType.APPLICATION_JSON).
                        post(ClientResponse.class);

                System.out.println( "Status: " + response.getStatus() );
                System.out.println( "Body: " + response.getEntity( String.class ) );
            }
        };

        timer.scheduleAtFixedRate( task, 0, 2500 );


        System.out.println( "Press enter to kill client cycle." );
        System.in.read();

        timer.cancel();
        timer.purge();
    }

    private static class PassengerTracker {
        private int passengers;

        private PassengerTracker() {
            passengers = 0;
        }

        private int getPassengers() {
            return passengers;
        }

        private void setPassengers(int passengers) {
            this.passengers = passengers;
        }
    }
}
