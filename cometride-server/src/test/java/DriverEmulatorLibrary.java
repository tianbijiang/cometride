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
public class DriverEmulatorLibrary {

    private static final List<LatLng> locations = new ArrayList<LatLng>();

    public static void main( String[] args ) throws IOException, InterruptedException {

        locations.add( new LatLng(32.987705, -96.747661) );
        locations.add( new LatLng(32.987525, -96.747017) );
        locations.add( new LatLng(32.987354, -96.746631) );
        locations.add( new LatLng(32.987471, -96.746191) );
        locations.add( new LatLng(32.987003, -96.745987) );
        locations.add( new LatLng(32.986292, -96.746008) );
        locations.add( new LatLng(32.985725, -96.746019) );
        locations.add( new LatLng(32.985248, -96.745966) );
        locations.add( new LatLng(32.984663, -96.745976) );
        locations.add( new LatLng(32.984627, -96.745365) );
        locations.add( new LatLng(32.985221, -96.745225) );
        locations.add( new LatLng(32.985428, -96.745333) );
        locations.add( new LatLng(32.985635, -96.745966) );
        locations.add( new LatLng(32.986589, -96.745987) );
        locations.add( new LatLng(32.987165, -96.746008) );
        locations.add( new LatLng(32.987786, -96.746223) );
        locations.add( new LatLng(32.987957, -96.745654) );
        locations.add( new LatLng(32.988389, -96.744893) );
        locations.add( new LatLng(32.988686, -96.745107) );
        locations.add( new LatLng(32.989001, -96.744646) );
        locations.add( new LatLng(32.989208, -96.743841) );
        locations.add( new LatLng(32.989712, -96.743788) );
        locations.add( new LatLng(32.990135, -96.743831) );
        locations.add( new LatLng(32.990108, -96.744442) );
        locations.add( new LatLng(32.989703, -96.745075) );
        locations.add( new LatLng(32.989379, -96.745601) );
        locations.add( new LatLng(32.988695, -96.745150) );
        locations.add( new LatLng(32.988506, -96.745011) );
        locations.add( new LatLng(32.988065, -96.745730) );
        locations.add( new LatLng(32.987930, -96.746438) );
        locations.add( new LatLng(32.988110, -96.747060) );



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
        sessionData.put( "routeId", "route-32ff5550-daee-444c-bf12-650f42106da6" );

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
