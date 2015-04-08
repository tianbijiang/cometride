package utdallas.ridetrackers.server.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utdallas.ridetrackers.server.datatypes.CabStatus;
import utdallas.ridetrackers.server.datatypes.LatLng;
import utdallas.ridetrackers.server.datatypes.driver.CabSession;
import utdallas.ridetrackers.server.datatypes.driver.TrackingUpdate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mlautz
 */
public class CometRideDatabaseAccess {

    private final Logger logger = LoggerFactory.getLogger(CometRideDatabaseAccess.class);

    private final String dbName;
    private final String userName;
    private final String password;
    private final String hostname;
    private final String port;
    private final String jdbcUrl;

    private final DatabaseExecutor db;

    public CometRideDatabaseAccess() {
        dbName = System.getProperty("RDS_DB_NAME");
        userName = System.getProperty("RDS_USERNAME");
        password = System.getProperty("RDS_PASSWORD");
        hostname = System.getProperty("RDS_HOSTNAME");
        port = System.getProperty("RDS_PORT");
        jdbcUrl = "jdbc:mysql://" + hostname + ":" +
                port + "/" + dbName + "?user=" + userName + "&password=" + password;

        try {
            System.out.println("Loading driver...");
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded!");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find the driver in the classpath!", e);
        }

        db = new DatabaseExecutor( jdbcUrl );

        createTables();
    }

    //
    // Driver/Cab Updates
    //

    public List<CabStatus> retrieveCurrentCabStatuses() throws SQLException {
        List<CabStatus> statusList = new ArrayList<CabStatus>();

//        String queryStatement = "SELECT cab_session_id, lat, lng, passenger_count FROM ebdb.CabStatus t1 " +
//                "JOIN ( SELECT cab_session_id id, MAX( submission_time ) subtime FROM ebdb.CabStatus GROUP BY cab_session_id ) t2 " +
//                "ON t1.cab_session_id = t2.id AND t1.submission_time = t2.subtime;";

        String queryStatement = "SELECT cab_session_id, cabStatus.lat, cabStatus.lng, max_capacity, cabStatus.passenger_count, route_id, duty_status \n" +
                "FROM ebdb.CabSession cabSession JOIN \n" +
                "\t( SELECT cab_session_id cabId, lat, lng, passenger_count FROM ebdb.CabStatus t1 \n" +
                "\tJOIN ( SELECT cab_session_id id, MAX( submission_time ) subtime FROM ebdb.CabStatus GROUP BY cab_session_id ) t2 \n" +
                "\tON t1.cab_session_id = t2.id AND t1.submission_time = t2.subtime ) cabStatus \n" +
                "ON cabStatus.cabId =  cabSession.cab_session_id;";
        // TODO: Add the ability to include / exclude inactive cabs

        logger.info( "Running query: " + queryStatement );
        Connection connection = null;

        try {
            connection = DriverManager.getConnection( jdbcUrl );
            Statement setupStatement = connection.createStatement();
            ResultSet results = setupStatement.executeQuery( queryStatement );

            while( results.next() ) {
                CabStatus newStatus = new CabStatus();

                newStatus.setCabId( results.getString( "cab_session_id" ) );

                LatLng location = new LatLng( results.getDouble( "lat" ), results.getDouble( "lng" ) );
                newStatus.setLocation( location );

                newStatus.setMaxCapacity( results.getInt( "max_capacity" ) );
                newStatus.setPassengerCount( results.getInt( "passenger_count" ) );
                newStatus.setRouteId( results.getString( "route_id" ) );
                newStatus.setStatus( results.getString( "duty_status" ) );

                statusList.add( newStatus );
            }

            setupStatement.close();
        } catch (SQLException ex) {
            logger.error("SQLException: " + ex.getMessage());
            logger.error("SQLState: " + ex.getSQLState());
            logger.error("VendorError: " + ex.getErrorCode());
        } finally {
            System.out.println("Closing the connection.");
            if (connection != null) try { connection.close(); } catch (SQLException ignore) {}
        }

        return statusList;
    }

    public CabStatus retrieveCabStatus( String cabId ) throws SQLException {
        String queryStatement = "SELECT cab_session_id, lat, lng, passenger_count, MAX( submission_time ) submission_time " +
                "FROM ebdb.CabStatus WHERE cab_session_id = '" + cabId + "';";
        // TODO: Protect this from injection             ^^^^^
        // TODO: Add the ability to include / exclude inactive cabs

        logger.info( "Running query: " + queryStatement );
        Connection connection = null;
        CabStatus resultStatus = null;

        try {
            connection = DriverManager.getConnection( jdbcUrl );
            Statement setupStatement = connection.createStatement();
            ResultSet results = setupStatement.executeQuery( queryStatement );
            results.first();

            resultStatus = new CabStatus();

            resultStatus.setCabId(results.getString("cab_session_id"));

            LatLng location = new LatLng( results.getDouble( "lat" ), results.getDouble( "lng" ) );
            resultStatus.setLocation( location );

            resultStatus.setPassengerCount( results.getInt( "passenger_count" ) );

            setupStatement.close();
        } catch (SQLException ex) {
            logger.error("SQLException: " + ex.getMessage());
            logger.error("SQLState: " + ex.getSQLState());
            logger.error("VendorError: " + ex.getErrorCode());
        } finally {
            System.out.println("Closing the connection.");
            if (connection != null) try { connection.close(); } catch (SQLException ignore) {}
        }

        return resultStatus;
    }

    public void createCabSession( CabSession cabSession ) {
        String insertStatement = "INSERT INTO CabSession (cab_session_id, duty_status, route_id, max_capacity) " +
                "VALUES (?, ?, ?, ?)";

        db.executeUpdate( insertStatement, cabSession.getCabSessionId(), cabSession.getDutyStatus(),
                cabSession.getRouteId(), cabSession.getMaxCapacity() );
    }

    public void persistTrackingUpdate( TrackingUpdate trackingUpdate) {

        String insertStatement = "INSERT INTO CabStatus (cab_session_id, lat, lng, passenger_count, " +
                "passenger_total, submission_time) VALUES (?, ?, ?, ?, ?, ?)";

        db.executeUpdate(insertStatement, trackingUpdate.getCabSessionId(), trackingUpdate.getLat(),
                trackingUpdate.getLng(), trackingUpdate.getPassengerCount(), trackingUpdate.getPassengerTotal(),
                trackingUpdate.getTimestamp());
    }


    // Tables

    private void createTables() {

        // Create Route Info Table
        String routeInfoTableSatement = "CREATE TABLE IF NOT EXISTS `ebdb`.`RouteInfo` (\n" +
                "  `route_id` VARCHAR(45) NOT NULL,\n" +
                "  `name` VARCHAR(45) NOT NULL,\n" +
                "  `color` VARCHAR(45) NOT NULL,\n" +
                "  `status` VARCHAR(45) NOT NULL,\n" +
                "  PRIMARY KEY (`route_id`),\n" +
                "  UNIQUE INDEX `route_id_UNIQUE` (`route_id` ASC));";
        db.executeStatement( routeInfoTableSatement );


        // Create Route Waypoints Table
        String routeWaypointsTableSatement = "CREATE TABLE IF NOT EXISTS `ebdb`.`RouteWaypoints` (\n" +
                "  `waypoint_id` VARCHAR(45) NOT NULL,\n" +
                "  `route_id` VARCHAR(45) NOT NULL,\n" +
                "  `lat` DOUBLE NOT NULL,\n" +
                "  `lng` VARCHAR(45) NOT NULL,\n" +
                "  `sequence_num` INT NOT NULL,\n" +
                "  PRIMARY KEY (`waypoint_id`),\n" +
                "  UNIQUE INDEX `waypoint_id_UNIQUE` (`waypoint_id` ASC));";
        db.executeStatement( routeWaypointsTableSatement );

        // Create Route Time Table
        String routeTimeTableStatement = "CREATE TABLE IF NOT EXISTS `ebdb`.`RouteTimes` (\n" +
                "  `route_times_id` INT NOT NULL AUTO_INCREMENT,\n" +
                "  `route_id` VARCHAR(45) NOT NULL,\n" +
                "  `start_time` TIME NOT NULL,\n" +
                "  `end_time` TIME NOT NULL,\n" +
                "  PRIMARY KEY (`route_times_id`),\n" +
                "  UNIQUE INDEX `route_times_id_UNIQUE` (`route_times_id` ASC));";
        db.executeStatement( routeTimeTableStatement );

        // Create Route Dates Table
        String routeDateTableStatement = "CREATE TABLE IF NOT EXISTS `ebdb`.`RouteDates` (\n" +
                "  `route_dates_id` INT NOT NULL AUTO_INCREMENT,\n" +
                "  `route_id` VARCHAR(45) NOT NULL,\n" +
                "  `start_date` DATE NOT NULL,\n" +
                "  `end_date` DATE NOT NULL,\n" +
                "  PRIMARY KEY (`route_dates_id`),\n" +
                "  UNIQUE INDEX `route_times_id_UNIQUE` (`route_dates_id` ASC));";
        db.executeStatement( routeDateTableStatement );

        //Create Cab Session Table
        String cabSessionTableStatement = "CREATE TABLE IF NOT EXISTS `ebdb`.`CabSession` (\n" +
                "  `cab_session_id` VARCHAR(45) NOT NULL,\n" +
                "  `duty_status` VARCHAR(45) NOT NULL,\n" +
                "  `route_id` VARCHAR(45) NOT NULL,\n" +
                "  `max_capacity` INT NOT NULL,\n" +
                "  PRIMARY KEY (`cab_session_id`),\n" +
                "  UNIQUE INDEX `cab_session_id_UNIQUE` (`cab_session_id` ASC));";
        db.executeStatement( cabSessionTableStatement );

        // Create CabStatus Table
        String cabStatusTableStatement = "CREATE TABLE IF NOT EXISTS `ebdb`.`CabStatus` (\n" +
                "  `cab_status_id` INT NOT NULL AUTO_INCREMENT,\n" +
                "  `cab_session_id` VARCHAR(45) NOT NULL,\n" +
                "  `lat` DOUBLE NOT NULL,\n" +
                "  `lng` DOUBLE NOT NULL,\n" +
                "  `passenger_count` INT NOT NULL,\n" +
                "  `passenger_total` INT NOT NULL,\n" +
                "  `submission_time` TIMESTAMP NOT NULL,\n" +
                "  PRIMARY KEY (`cab_status_id`),\n" +
                "  UNIQUE INDEX `cabStatusId_UNIQUE` (`cab_status_id` ASC)); ";
        db.executeStatement( cabStatusTableStatement );
    }

}