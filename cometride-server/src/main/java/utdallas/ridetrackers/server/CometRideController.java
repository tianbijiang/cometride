package utdallas.ridetrackers.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utdallas.ridetrackers.server.datatypes.CabStatus;
import utdallas.ridetrackers.server.datatypes.CometRideDatabaseAccess;
import utdallas.ridetrackers.server.datatypes.LatLng;
import utdallas.ridetrackers.server.datatypes.Route;
import utdallas.ridetrackers.server.datatypes.admin.RouteDetails;
import utdallas.ridetrackers.server.datatypes.driver.DriverStatus;
import utdallas.ridetrackers.server.datatypes.driver.LocationUpdate;
import utdallas.ridetrackers.server.datatypes.driver.TallyUpdate;
import utdallas.ridetrackers.server.datatypes.rider.InterestedUpdate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mlautz
 */
public class CometRideController {

    private final Logger logger = LoggerFactory.getLogger(CometRideController.class);

    // TODO: Get rid of these!!!
    private double testLat = 32.990709;
    private double testLng = -96.752627;
    private boolean markerIncrementing = true;
    // TODO: Get rid of these!!!

    private final CometRideDatabaseAccess db = new CometRideDatabaseAccess();


    //
    //  Routes
    //

    public Route[] getAllRoutes() {
        List<Route> routes = new ArrayList<Route>();

        List<LatLng> testWaypoints = new ArrayList<LatLng>();
        testWaypoints.add( new LatLng( 32.985559, -96.749478 ) );
        testWaypoints.add( new LatLng( 32.985642, -96.749430 ) );
        testWaypoints.add( new LatLng( 32.991806, -96.753607 ) );
        testWaypoints.add( new LatLng( 32.985559, -96.749478 ) );

        Route testRoute = new Route(
                "#900dba",
                "route1",
                "Route 1",
                "ACTIVE",
                testWaypoints
        );

        routes.add( testRoute );

        List<LatLng> testWaypoints2 = new ArrayList<LatLng>();
        testWaypoints2.add( new LatLng( 32.990111, -96.743875 ) );
        testWaypoints2.add( new LatLng( 32.989424, -96.745462 ) );
        testWaypoints2.add( new LatLng( 32.991806, -96.753607 ) );
        testWaypoints2.add( new LatLng( 32.987391, -96.747009 ) );
        testWaypoints2.add( new LatLng( 32.987716, -96.746244 ) );
        testWaypoints2.add( new LatLng( 32.990111, -96.743875 ) );

        Route testRoute2 = new Route(
                "#edb712",
                "route2",
                "Route 2",
                "ACTIVE",
                testWaypoints2
        );

        routes.add( testRoute2 );

        return routes.toArray( new Route[]{} );
    }

    public RouteDetails getRouteDetails( String id ) {
        RouteDetails details = new RouteDetails( null, id, null, null, null );

        return details;
    }

    public String createRoute( RouteDetails newRouteDetails ) {

        return "1234";
    }

    public String updateRoute( String id, RouteDetails createRoute ) {

        return id;
    }


    //
    //  Cabs
    //

    public CabStatus[] getAllCabStatuses( String queryType ) {
            List<CabStatus> cabs = new ArrayList<CabStatus>();

        if( markerIncrementing ) {
            this.testLat = this.testLat + 0.00005;
        } else {
            this.testLat = this.testLat - 0.00005;
        }

        if (this.testLat > 32.991771) {
            markerIncrementing = false;
        } else if (this.testLat < 32.990709) {
            markerIncrementing = true;
        }

        cabs.add( new CabStatus( "1", new LatLng( 32.987356, -96.746551 ), 8, 2, "route2", "ON_DUTY" ));
        cabs.add( new CabStatus( "2", new LatLng( this.testLat, this.testLng ), 8, 8, "route1", "ON_DUTY" ));

        return cabs.toArray( new CabStatus[]{} );
    }

    public CabStatus getCabStatus( String id ) {
        return new CabStatus( "123", new LatLng( 32.987356, -96.746551 ), 8, 2, "route2", "ON_DUTY" );
    }


    //
    //  Driver
    //

    public String createDriverSession( DriverStatus newDriverStatus ) {

        return "1234";
    }

    public String updateDriverStatus( DriverStatus updatedDriverStatus ) {

        return "1234";
    }

    public void updateDriverLocation( LocationUpdate locationUpdate ) {
        db.persistLocationUpdate( locationUpdate );
    }

    public void updateDriverTally( TallyUpdate tallyUpdate ) {

    }


    //
    //  Rider
    //

    public void indicateRiderInterest( InterestedUpdate interestedUpdate ) {

    }
}
