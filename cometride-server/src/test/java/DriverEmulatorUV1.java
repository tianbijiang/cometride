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

        locations.add( new LatLng(32.985662, -96.749399) );
        locations.add( new LatLng(32.985671, -96.750558) );
        locations.add( new LatLng(32.985671, -96.751791) );
        locations.add( new LatLng(32.985662, -96.753057) );
        locations.add( new LatLng(32.985653, -96.754216) );
        locations.add( new LatLng(32.985662, -96.755257) );
        locations.add( new LatLng(32.985338, -96.755064) );
        locations.add( new LatLng(32.984645, -96.755278) );
        locations.add( new LatLng(32.983862, -96.755890) );
        locations.add( new LatLng(32.983745, -96.755160) );
        locations.add( new LatLng(32.983745, -96.754420) );
        locations.add( new LatLng(32.984582, -96.754162) );
        locations.add( new LatLng(32.985185, -96.754066) );
        locations.add( new LatLng(32.985590, -96.754270) );
        locations.add( new LatLng(32.985653, -96.753497) );
        locations.add( new LatLng(32.985653, -96.752424) );
        locations.add( new LatLng(32.985626, -96.751545) );
        locations.add( new LatLng(32.985644, -96.750772) );
        locations.add( new LatLng(32.985563, -96.750139) );
        locations.add( new LatLng(32.985662, -96.749399) );


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
        form.putSingle("j_username", "demoDriver");
        form.putSingle("j_password", "demo");
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
        sessionData.put( "routeId", "route-3837112f-78ad-4426-b706-74d9f8c77414" );

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
