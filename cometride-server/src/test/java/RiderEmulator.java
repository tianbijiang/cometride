import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by matt lautz on 4/24/2015.
 */
public class RiderEmulator {

    public static void main( String[] args ) throws IOException {
        System.out.println( "Test Run!" );

        Client client = Client.create();
        final WebResource resource = client.resource( "http://cometride.elasticbeanstalk.com/api/cab" );

        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                ClientResponse response = resource.accept( "application/json" ).get( ClientResponse.class );

                System.out.println( "Status: " + response.getStatus() );
                System.out.println( "Body: " + response.getEntity( String.class ) );
            }
        };

        timer.scheduleAtFixedRate( task, 0, 1000 );


        System.out.println( "Press enter to kill client cycle." );
        System.in.read();

        timer.cancel();
        timer.purge();
    }
}