package utdallas.ridetrackers.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utdallas.ridetrackers.server.datatypes.CabStatus;
import utdallas.ridetrackers.server.datatypes.LatLng;
import utdallas.ridetrackers.server.datatypes.Route;
import utdallas.ridetrackers.server.datatypes.admin.RouteDetails;
import utdallas.ridetrackers.server.datatypes.driver.DriverStatus;
import utdallas.ridetrackers.server.datatypes.driver.LocationUpdate;
import utdallas.ridetrackers.server.datatypes.rider.InterestedUpdate;

import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.ws.soap.MTOM;
import java.util.ArrayList;
import java.util.List;

@Path("/")
public class CometRideServlet {

    private final Logger logger = LoggerFactory.getLogger( CometRideServlet.class );

    @Context
    UriInfo uriInfo;

    private final CometRideController controller;


    public CometRideServlet() {
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
    @Path( "/route/{id}" )
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
    @Path( "/route" )
    public Response createRoute( RouteDetails details ) {
        try {
            logger.info( "Creating route: " + details.getName() );
            String id = controller.createRoute( details );

            UriBuilder uib = uriInfo.getAbsolutePathBuilder();
            uib.path( id );
            return Response.created( uib.build() ).build();
        } catch ( Exception e ) {
            return Response.serverError().entity( "An error occurred while creating route " +
                    details.getName() + "." ).build();
        }
    }

    @PUT
    @Consumes( MediaType.APPLICATION_JSON )
    @Path( "/route/{id}" )
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
    @Path( "/driver" )
    public Response createDriverSession( DriverStatus newDriverStatus ) {
        try {
            String id = controller.createDriverSession( newDriverStatus );

            UriBuilder uib = uriInfo.getAbsolutePathBuilder();
            uib.path( id );
            return Response.created( uib.build() ).build();
        } catch ( Exception e ) {
            return Response.serverError().entity( "An error occurred while creating new driver session." ).build();
        }
    }

    @PUT
    @Consumes( MediaType.APPLICATION_JSON )
    @Path( "/driver/{id}" )
    public Response updateDriverStatus( DriverStatus statusUpdate ) {
        try {
            String id = controller.updateDriverStatus( statusUpdate );

            UriBuilder uib = uriInfo.getAbsolutePathBuilder();
            uib.path( id );
            return Response.created( uib.build() ).build();
        } catch ( Exception e ) {
            return Response.serverError().entity( "An error occurred while updating driver (" +
                    statusUpdate.getDriverId() + ") session." ).build();
        }
    }

    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    @Path( "/location" )
    public Response updateDriverLocation( LocationUpdate locationUpdate ) {
        try {
            controller.updateDriverLocation( locationUpdate );
            return Response.ok().build();
        } catch ( Exception e ) {
            return Response.serverError().entity( "An error occurred while updating driver (" +
                    locationUpdate.getDriverId() + ") location." ).build();
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