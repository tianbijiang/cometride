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
public class DriverEmulatorUV47 {

    private static final List<LatLng> locations = new ArrayList<LatLng>();

    public static void main( String[] args ) throws IOException, InterruptedException {

        locations.add( new LatLng(32.985564, -96.750113) );
        locations.add( new LatLng(32.985555, -96.749595) );
        locations.add( new LatLng(32.985667, -96.749485) );
        locations.add( new LatLng(32.985676, -96.749911) );
        locations.add( new LatLng(32.985667, -96.750574) );
        locations.add( new LatLng(32.985671, -96.750936) );
        locations.add( new LatLng(32.985671, -96.751459) );
        locations.add( new LatLng(32.985667, -96.752266) );
        locations.add( new LatLng(32.985663, -96.752891) );
        locations.add( new LatLng(32.985667, -96.753543) );
        locations.add( new LatLng(32.985824, -96.753755) );
        locations.add( new LatLng(32.986314, -96.753763) );
        locations.add( new LatLng(32.986755, -96.753760) );
        locations.add( new LatLng(32.987191, -96.753730) );
        locations.add( new LatLng(32.987672, -96.753770) );
        locations.add( new LatLng(32.988171, -96.753757) );
        locations.add( new LatLng(32.988369, -96.754033) );
        locations.add( new LatLng(32.988351, -96.754524) );
        locations.add( new LatLng(32.988369, -96.754897) );
        locations.add( new LatLng(32.988279, -96.754996) );
        locations.add( new LatLng(32.988203, -96.754655) );
        locations.add( new LatLng(32.988207, -96.754223) );
        locations.add( new LatLng(32.988171, -96.753952) );
        locations.add( new LatLng(32.987856, -96.753922) );
        locations.add( new LatLng(32.987357, -96.753925) );
        locations.add( new LatLng(32.987132, -96.753820) );
        locations.add( new LatLng(32.987020, -96.753748) );
        locations.add( new LatLng(32.986912, -96.753901) );
        locations.add( new LatLng(32.986543, -96.753963) );
        locations.add( new LatLng(32.986170, -96.753928) );
        locations.add( new LatLng(32.985864, -96.753990) );
        locations.add( new LatLng(32.985846, -96.754400) );
        locations.add( new LatLng(32.985770, -96.754612) );
        locations.add( new LatLng(32.985680, -96.754330) );
        locations.add( new LatLng(32.985680, -96.753812) );
        locations.add( new LatLng(32.985667, -96.753107) );
        locations.add( new LatLng(32.985667, -96.752402) );
        locations.add( new LatLng(32.985663, -96.751568) );
        locations.add( new LatLng(32.985676, -96.751066) );
        locations.add( new LatLng(32.985658, -96.750532) );
        locations.add( new LatLng(32.985636, -96.750218) );

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
        sessionData.put( "routeId", "route-a6e8d987-7a0c-4df9-b161-4d6329fe3ac6" );

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
