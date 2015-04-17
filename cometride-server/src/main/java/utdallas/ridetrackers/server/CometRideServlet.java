package utdallas.ridetrackers.server;

import com.sun.jersey.spi.resource.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utdallas.ridetrackers.server.datatypes.CabStatus;
import utdallas.ridetrackers.server.datatypes.Route;
import utdallas.ridetrackers.server.datatypes.admin.RouteDetails;
import utdallas.ridetrackers.server.datatypes.admin.UserData;
import utdallas.ridetrackers.server.datatypes.driver.CabSession;
import utdallas.ridetrackers.server.datatypes.driver.TrackingUpdate;
import utdallas.ridetrackers.server.datatypes.rider.InterestedUpdate;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Singleton
@Path("/")
public class CometRideServlet {

    private final Logger logger = LoggerFactory.getLogger( CometRideServlet.class );

    @Context
    UriInfo uriInfo;

    private final CometRideController controller;


    public CometRideServlet() {
        logger.info( "Creating servlet." );
        controller = new CometRideController();
    }

    //
    //  Routes
    //

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/route" )
    public Response getRoutes() {
        try {
            Route[] routes = controller.getAllRoutes();
            return Response.ok().entity( routes ).build();
        } catch ( Exception e ) {
            return Response.serverError().entity( "An error occurred while querying for routes." ).build();
        }
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/admin/route/{id}" )
    public Response getRouteDetails( @PathParam( "id" ) String id ) {
        try {
            RouteDetails details = controller.getRouteDetails( id );
            return Response.ok().entity( details ).build();
        } catch ( Exception e ) {
            return Response.serverError().entity( "An error occurred while querying for route " +
                    id + "." ).build();
        }
    }

    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    @Path( "/admin/route" )
    public Response createRoute( RouteDetails details ) {
        try {
            logger.info( "Creating route: " + details.getName() );
            String id = controller.createRoute( details );

            UriBuilder uib = uriInfo.getAbsolutePathBuilder();
            uib.path( id );
            return Response.created( uib.build() ).entity( id ).build();
        } catch ( Exception e ) {
            return Response.serverError().entity( "An error occurred while creating route " +
                    details.getName() + "." ).build();
        }
    }

    @PUT
    @Consumes( MediaType.APPLICATION_JSON )
    @Path( "/admin/route/{id}" )
    public Response updateRoute( @PathParam( "id" ) String id, RouteDetails details ) {
        try {
            logger.info( "Updating route: " + details.getName() );
            String updatedId = controller.updateRoute( id, details );

            UriBuilder uib = uriInfo.getAbsolutePathBuilder();
            uib.path( updatedId );
            return Response.ok().location( uib.build() ).build();
        } catch ( Exception e ) {
            return Response.serverError().entity( "An error occurred while updating route " +
                    details.getName() + "." ).build();
        }
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/admin/users" )
    public Response getAllUsers() {
        try {
            logger.info( "Retrieving users." );
            UserData[] usersList = controller.retrieveUsersData();

            return Response.ok().entity(usersList).build();
        } catch ( Exception e ) {
            return Response.serverError().entity("An error occurred while retrieving users.").build();
        }
    }

    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    @Path( "/admin/users" )
    public Response createUser( UserData newData ) {
        try {
            logger.info( "Creating user: " + newData.getUserName() );
            String id = controller.createUser( newData );

            UriBuilder uib = uriInfo.getAbsolutePathBuilder();
            uib.path( id );
            return Response.created( uib.build() ).entity( id ).build();
        } catch ( Exception e ) {
            return Response.serverError().entity( "An error occurred while creating user " +
                    newData.getUserName() + "." ).build();
        }
    }

    @PUT
    @Consumes( MediaType.APPLICATION_JSON )
    @Path( "/admin/users/{id}" )
    public Response createUser( UserData newData, @PathParam( "id" ) String userName ) {
        try {
            logger.info( "Updating user: " + userName );
            String id = controller.updateUser( newData, userName );

            UriBuilder uib = uriInfo.getAbsolutePathBuilder();
            uib.path( id );
            return Response.ok( uib.build() ).entity( id ).build();
        } catch ( Exception e ) {
            return Response.serverError().entity( "An error occurred while updating user " +
                    userName + "." ).build();
        }
    }

    @DELETE
    @Path( "/admin/users/{id}" )
    public Response createUser( @PathParam( "id" ) String userName ) {
        try {
            logger.info( "Deleting user: " + userName );
            controller.deleteUser( userName );

            return Response.ok().build();
        } catch ( Exception e ) {
            return Response.serverError().entity( "An error occurred while updating user " +
                    userName + "." ).build();
        }
    }


    //
    //  Cabs
    //

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/cab" )
    public Response getCabs( @QueryParam( "queryType" ) String queryType ) {
        try {
            // TODO: Filter cabs by last 30 seconds of updates && on-duty by default
            // TODO: Allow a query param to show off-duty cabs

            CabStatus[] cabs = controller.getAllCabStatuses( queryType );

            return Response.ok().entity( cabs ).build();
        } catch ( Exception e ) {
            return Response.serverError().entity( "An error occurred while querying for status on all cabs." ).build();
        }
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/cab/{id}" )
    public Response getCabDetails( @PathParam( "id" ) String id ) {
        try {
            CabStatus status = controller.getCabStatus( id );

            return Response.ok().entity( status ).build();
        } catch ( Exception e ) {
            return Response.serverError().entity( "An error occurred while querying for status on cab" + id + "." ).build();
        }
    }


    //
    //  Driver
    //

    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    @Path( "/driver/session" )
    public Response createDriverSession( CabSession newCabSession) {
        try {
            String id = controller.createCabSession(newCabSession);

            UriBuilder uib = uriInfo.getAbsolutePathBuilder();
            uib.path( id );
            return Response.created( uib.build() ).entity( id ).build();
        } catch ( Exception e ) {
            return Response.serverError().entity( "An error occurred while creating new driver session." ).build();
        }
    }

    @PUT
    @Consumes( MediaType.APPLICATION_JSON )
    @Path( "/driver/session/{id}" )
    public Response updateDriverStatus( CabSession statusUpdate ) {
        try {
            String id = controller.updateDriverStatus( statusUpdate );

            UriBuilder uib = uriInfo.getAbsolutePathBuilder();
            uib.path(id);
            return Response.created(uib.build()).build();
        } catch ( Exception e ) {
            return Response.serverError().entity( "An error occurred while updating driver (" +
                    statusUpdate.getCabSessionId() + ") session." ).build();
        }
    }

    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    @Path( "/driver/cab" )
    public Response updateDriverLocation( TrackingUpdate trackingUpdate) {
        try {
            trackingUpdate.setTimestamp( new Timestamp( new Date().getTime() ) );
            controller.storeTrackingUpdate(trackingUpdate);
            return Response.ok().build();
        } catch ( Exception e ) {
            return Response.serverError().entity( "An error occurred while updating driver (" +
                    trackingUpdate.getCabSessionId() + ") location." ).build();
        }
    }


    //
    //  Rider
    //

    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    @Path( "/interest" )
    public Response indicateRiderInterest( InterestedUpdate interestedUpdate ) {
        try {
            controller.indicateRiderInterest( interestedUpdate );
            return Response.ok().build();
        } catch ( Exception e ) {
            return Response.serverError().entity( "An error occurred while indicating rider interest." ).build();
        }
    }


    //
    //  Admin
    //

    // TODO: Auth support


}