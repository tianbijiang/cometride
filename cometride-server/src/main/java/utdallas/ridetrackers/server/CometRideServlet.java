package utdallas.ridetrackers.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utdallas.ridetrackers.server.datatypes.Location;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Path("/")
public class CometRideServlet {

    private final Logger logger = LoggerFactory.getLogger( CometRideServlet.class );

    @GET
    public String sayHello(){
        logger.info( "Hello world endpoint called!" );
        StringBuilder stringBuilder = new StringBuilder("Hello generic user!" +
			" Tell me your name by adding '/{name}' to the end of this url!" );

        return stringBuilder.toString();
    }

    @GET
    @Path("{name}")
    public String sayHello(@PathParam("name") String name){
        logger.info( "Hello world endpoint called! With name: " + name );
        StringBuilder stringBuilder = new StringBuilder("Hello ");
        stringBuilder.append(name).append("!");

        return stringBuilder.toString();
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/locations" )
    public Response getLocations() {
        Location location = new Location();
        location.setId( 1 );
        location.setCabId("cab#0290920");
        location.setLat("32.0");
        location.setLon("-96.7");
        location.setTimeStamp( new Timestamp(Calendar.getInstance().getTimeInMillis() ) );


        List<Location> locations = new ArrayList<Location>();
        locations.add( location );

        return Response.ok().entity( locations ).build();
    }

    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    @Path("/locations")
    public Response createLocationUpdate( Location locationUpdate ) {
        logger.info( "createLocationUpdate called!: " + locationUpdate.toString() );
        return Response.ok().entity( locationUpdate ).build();
    }

}