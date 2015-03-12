package utdallas.ridetrackers.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utdallas.ridetrackers.server.datatypes.LocationUpdate;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    @Path("/location")
    public Response createLocationUpdate( LocationUpdate locationUpdate ) {
        logger.info( "createLocationUpdate called!: " + locationUpdate.toString() );
        return Response.ok().entity( locationUpdate.toString() ).build();
    }

}