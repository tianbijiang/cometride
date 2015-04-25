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
public class DriverEmulatorUV1 {

    private static final List<LatLng> locations = new ArrayList<LatLng>();

    public static void main( String[] args ) throws IOException, InterruptedException {

        locations.add( new LatLng(32.992760, -96.752034) );
        locations.add( new LatLng(32.992558, -96.752050) );
        locations.add( new LatLng(32.992342, -96.752039) );
        locations.add( new LatLng(32.992158, -96.752044) );
        locations.add( new LatLng(32.992046, -96.751942) );
        locations.add( new LatLng(32.992046, -96.751749) );
        locations.add( new LatLng(32.992046, -96.751513) );
        locations.add( new LatLng(32.992046, -96.751196) );
        locations.add( new LatLng(32.992050, -96.750960) );
        locations.add( new LatLng(32.991672, -96.750971) );
        locations.add( new LatLng(32.991101, -96.750982) );
        locations.add( new LatLng(32.990435, -96.750998) );
        locations.add( new LatLng(32.989733, -96.750982) );
        locations.add( new LatLng(32.989090, -96.750987) );
        locations.add( new LatLng(32.988375, -96.751003) );
        locations.add( new LatLng(32.987610, -96.750992) );
        locations.add( new LatLng(32.986895, -96.750976) );
        locations.add( new LatLng(32.986225, -96.751003) );
        locations.add( new LatLng(32.985532, -96.751003) );
        locations.add( new LatLng(32.984952, -96.751003) );
        locations.add( new LatLng(32.984214, -96.751030) );
        locations.add( new LatLng(32.983769, -96.751073) );
        locations.add( new LatLng(32.983211, -96.750976) );
        locations.add( new LatLng(32.982667, -96.750960) );
        locations.add( new LatLng(32.982141, -96.750987) );
        locations.add( new LatLng(32.982667, -96.750960) );
        locations.add( new LatLng(32.983211, -96.750976) );
        locations.add( new LatLng(32.983769, -96.751073) );
        locations.add( new LatLng(32.984214, -96.751030) );
        locations.add( new LatLng(32.985532, -96.751003) );
        locations.add( new LatLng(32.986225, -96.751003) );
        locations.add( new LatLng(32.986895, -96.750976) );
        locations.add( new LatLng(32.987610, -96.750992) );
        locations.add( new LatLng(32.988375, -96.751003) );
        locations.add( new LatLng(32.989090, -96.750987) );
        locations.add( new LatLng(32.990435, -96.750998) );
        locations.add( new LatLng(32.991101, -96.750982) );
        locations.add( new LatLng(32.991672, -96.750971) );
        locations.add( new LatLng(32.992050, -96.750960) );
        locations.add( new LatLng(32.992050, -96.751400) );
        locations.add( new LatLng(32.992410, -96.751475) );
        locations.add( new LatLng(32.992779, -96.751582) );
        locations.add( new LatLng(32.992752, -96.751839) );

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
        sessionData.put( "routeId", "route-282ea864-5e32-4c0b-b165-8c8432c229de" );

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
