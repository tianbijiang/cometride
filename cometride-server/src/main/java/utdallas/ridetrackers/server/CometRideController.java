package utdallas.ridetrackers.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utdallas.ridetrackers.server.datatypes.CabStatus;
import utdallas.ridetrackers.server.datatypes.CabType;
import utdallas.ridetrackers.server.datatypes.Route;
import utdallas.ridetrackers.server.datatypes.admin.RouteDetails;
import utdallas.ridetrackers.server.datatypes.admin.UserData;
import utdallas.ridetrackers.server.datatypes.driver.CabSession;
import utdallas.ridetrackers.server.datatypes.driver.TrackingUpdate;
import utdallas.ridetrackers.server.datatypes.reports.TransDeptDailyRiders;
import utdallas.ridetrackers.server.datatypes.rider.InterestedUpdate;
import utdallas.ridetrackers.server.db.CometRideDatabaseAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public Route[] getCurrentRoutes() {
        List<Route> routes = new ArrayList<Route>();

        routes.addAll( db.getCurrentRouteDetails() );

        return routes.toArray( new Route[]{} );
    }

    public Route[] getAllRoutes() {
        List<Route> routes = new ArrayList<Route>();

        routes.addAll( db.getAllRouteDetails() );

        return routes.toArray( new Route[]{} );
    }

    public RouteDetails getRouteDetails( String id ) {
        RouteDetails details = new RouteDetails();
        details.setId( id );

        return details;
    }

    public String createRoute( RouteDetails newRouteDetails ) {

        String routeId = "route-" + UUID.randomUUID();
        newRouteDetails.setId( routeId );

        try {
            db.createRoute(newRouteDetails );
        } catch (Exception e) {
            logger.error( "Failed to create route in DB:\n" + e.getMessage() );
            // TODO: Throw error with condensed message
        }

        return routeId;
    }

    public String updateRoute( String id, RouteDetails createRoute ) {
        if( createRoute.getId().equals( id ) ) {

            try {
                db.updateRoute(createRoute);
            } catch (Exception e) {
                logger.error( "Failed to update route in DB:\n" + e.getMessage() );
                // TODO: Throw error with condensed message
            }

            return id;
        } else {
            throw new RuntimeException( "Provided route id does not match update data. Rejecting update!" );
        }
    }



    public void deleteRoute( String routeId ) {
        try {
            db.deleteRoute(routeId);
        } catch (Exception e) {
            logger.error( "Failed to delete route in DB:\n" + e.getMessage() );
            // TODO: Throw error with condensed message
        }
    }


    //
    //  Cabs
    //

    public CabStatus[] getAllCabStatuses( String queryType ) {
        List<CabStatus> cabs = new ArrayList<CabStatus>();

        try {
            cabs.addAll( db.retrieveCurrentCabStatuses() );
        } catch (Exception e) {
            logger.error( "Failed to retrieve cab statuses from DB:\n" + e.getMessage() );
            // TODO: Throw error with condensed message
        }

        return cabs.toArray( new CabStatus[]{} );
    }

    public CabStatus getCabStatus( String id ) {
        CabStatus matchingStatus = null;

        try {
            matchingStatus = db.retrieveCabStatus( id );
        } catch (Exception e) {
            logger.error( "Failed to retrieve cab status (" + id + ") from DB:\n" + e.getMessage() );
            // TODO: Throw error with condensed message
        }

        return matchingStatus;
    }

    public CabType[] getCabTypes() {
        List<CabType> cabTypes = new ArrayList<CabType>();

        try {
            cabTypes.addAll( db.retrieveCabTypes() );
        } catch (Exception e) {
            logger.error( "Failed to retrieve cab types from DB:\n" + e.getMessage() );
            // TODO: Throw error with condensed message
        }

        return cabTypes.toArray( new CabType[]{} );
    }

    public String createCabType( CabType newType ) {

        try {
            db.createCabType( newType );
        } catch (Exception e) {
            logger.error( "Failed to create cab type:\n" + e.getMessage() );
            // TODO: Throw error with condensed message
        }

        return newType.getTypeName();
    }

    public void deleteCabType( String typeName ) {
        try {
            db.deleteCabType( typeName );
        } catch (Exception e) {
            logger.error( "Failed to delete cab type from DB:\n" + e.getMessage() );
            // TODO: Throw error with condensed message
        }
    }


    //
    //  Driver
    //

    public String createCabSession( CabSession newCabSession) {
        // TODO: Maintain a list of session start / end times

        String sessionId = "cab-" + UUID.randomUUID();
        newCabSession.setCabSessionId( sessionId );

        try {
            db.createCabSession( newCabSession );
        } catch (Exception e) {
            logger.error( "Failed to create cab session in DB:\n" + e.getMessage() );
            // TODO: Throw error with condensed message
        }

        return sessionId;
    }

    public String updateDriverStatus( String sessionId, CabSession updatedCabSession) {
        if( updatedCabSession.getCabSessionId().equals( sessionId ) ) {

            try {
                db.updateCabSession( updatedCabSession );
            } catch (Exception e) {
                logger.error( "Failed to update session in DB:\n" + e.getMessage() );
                // TODO: Throw error with condensed message
            }

            return sessionId;
        } else {
            throw new RuntimeException( "Provided session id does not match update data. Rejecting update!" );
        }
    }

    public void storeTrackingUpdate( TrackingUpdate trackingUpdate) {
        // TODO: Validate input to ensure all properties are set
        db.persistTrackingUpdate(trackingUpdate);
    }


    //
    //  Rider
    //

    public void indicateRiderInterest( InterestedUpdate interestedUpdate ) {

    }


    //
    // Users
    //

    public UserData[] retrieveUsersData() {
        List<UserData> usersData = new ArrayList<UserData>();
        try {
            usersData.addAll( db.getUsersData() );
        } catch (Exception e) {
            logger.error( "Failed to retrieve users data from DB:\n" + e.getMessage() );
            // TODO: Throw error with condensed message
        }

        return usersData.toArray( new UserData[]{} );
    }

    public String createUser( UserData newData ) {
        try {
            db.createUser( newData );
        } catch (Exception e) {
            logger.error( "Failed to create user in DB:\n" + e.getMessage() );
            // TODO: Throw error with condensed message
        }

        return newData.getUserName();
    }

    public String updateUser( UserData data, String userName ) {
        if( data.getUserName().equals( userName ) ) {

            try {
                db.updateUser( data );
            } catch (Exception e) {
                logger.error( "Failed to update user in DB:\n" + e.getMessage() );
                // TODO: Throw error with condensed message
            }

            return userName;
        } else {
            throw new RuntimeException( "Provided user name does not match update data. Rejecting update!" );
        }
    }

    public void deleteUser( String userName ) {
        try {
            db.deleteUser( userName );
        } catch (Exception e) {
            logger.error( "Failed to delete user in DB:\n" + e.getMessage() );
            // TODO: Throw error with condensed message
        }
    }


    //
    // Metrics
    //

    public List<TransDeptDailyRiders> getMontlyRidersMetrics() {
        List<TransDeptDailyRiders> ridersMetrics = new ArrayList<TransDeptDailyRiders>();

        try {
            ridersMetrics.addAll( db.getMonthlyRidersMetrics() );
        } catch (Exception e) {
            logger.error( "Failed to retrieve metrics from DB:\n" + e.getMessage() );
            // TODO: Throw error with condensed message
        }

        return ridersMetrics;
    }

    public List<TransDeptDailyRiders> getMontlyRidersMetrics( String routeId ) {
        List<TransDeptDailyRiders> ridersMetrics = new ArrayList<TransDeptDailyRiders>();

        try {
            ridersMetrics.addAll( db.getMonthlyRidersMetrics() );
        } catch (Exception e) {
            logger.error( "Failed to retrieve metrics from DB:\n" + e.getMessage() );
            // TODO: Throw error with condensed message
        }

        return ridersMetrics;
    }
}