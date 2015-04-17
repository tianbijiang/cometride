package utdallas.ridetrackers.server.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utdallas.ridetrackers.server.datatypes.CabStatus;
import utdallas.ridetrackers.server.datatypes.LatLng;
import utdallas.ridetrackers.server.datatypes.Route;
import utdallas.ridetrackers.server.datatypes.admin.RouteDetails;
import utdallas.ridetrackers.server.datatypes.admin.UserData;
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
        logger.info( "RDS_DB_NAME: " + dbName );
        userName = System.getProperty("RDS_USERNAME");
        logger.info( "RDS_USERNAME: " + userName );
        password = System.getProperty("RDS_PASSWORD");
        logger.info( "RDS_PASSWORD: " + password );
        hostname = System.getProperty("RDS_HOSTNAME");
        logger.info( "RDS_HOSTNAME: " + hostname );
        port = System.getProperty("RDS_PORT");
        logger.info( "RDS_PORT: " + port );

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

        ensureUserTablesExist();
        createTables();
    }

    //
    // Driver/Cab Updates
    //

    public List<CabStatus> retrieveCurrentCabStatuses() throws SQLException {
        List<CabStatus> statusList = new ArrayList<CabStatus>();

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
        String queryStatement = "SELECT cab_session_id, cabStatus.lat, cabStatus.lng, max_capacity, cabStatus.passenger_count, route_id, duty_status \n" +
                "FROM ebdb.CabSession cabSession JOIN \n" +
                "\t( SELECT cab_session_id cabId, lat, lng, passenger_count, MAX( submission_time ) submission_time " +
                "FROM ebdb.CabStatus WHERE cab_session_id = '" + cabId + "' ) cabStatus \n" +
                "ON cabStatus.cabId =  cabSession.cab_session_id;";

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



    //
    // Routes
    //

    public List<Route> getRouteDetails() {
        // TODO: Enhance select statement with time info
        String queryStatement = "SELECT route_id, name, color, status FROM ebdb.RouteInfo;";

        List<Route> routes = new ArrayList<Route>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection( jdbcUrl );
            Statement setupStatement = connection.createStatement();
            ResultSet results = setupStatement.executeQuery( queryStatement );

            while( results.next() ) {
                Route newDetails = new Route();
                List<LatLng> routeWaypoints = new ArrayList<LatLng>();

                try {
                    String waypointQueryStatement = "SELECT route_id, lat, lng, sequence_num FROM ebdb.RouteWaypoints " +
                            "WHERE route_id = '" + results.getString( "route_id" ) + "' ORDER BY sequence_num ASC;";

                    Statement waypointSetupStatement = connection.createStatement();
                    ResultSet waypointResults = waypointSetupStatement.executeQuery( waypointQueryStatement );

                    while( waypointResults.next() ) {
                        LatLng newWaypoint = new LatLng();

                        newWaypoint.setLat( waypointResults.getDouble( "lat" ) );
                        newWaypoint.setLng(waypointResults.getDouble("lng" ) );

                        routeWaypoints.add( newWaypoint );
                    }

                    waypointSetupStatement.close();
                } catch (SQLException ex) {
                    logger.error("SQLException: " + ex.getMessage());
                    logger.error("SQLState: " + ex.getSQLState());
                    logger.error("VendorError: " + ex.getErrorCode());
                }

                List<LatLng> routeSafepoints = new ArrayList<LatLng>();

                try {
                    String safepointQueryStatement = "SELECT route_id, lat, lng, sequence_num FROM ebdb.RouteSafepoints " +
                            "WHERE route_id = '" + results.getString( "route_id" ) + "' ORDER BY sequence_num ASC;";

                    Statement safepointSetupStatement = connection.createStatement();
                    ResultSet safepointResults = safepointSetupStatement.executeQuery( safepointQueryStatement );

                    while( safepointResults.next() ) {
                        LatLng newSafepoint = new LatLng();

                        newSafepoint.setLat( safepointResults.getDouble( "lat" ) );
                        newSafepoint.setLng(safepointResults.getDouble("lng"));

                        routeSafepoints.add( newSafepoint );
                    }

                    safepointResults.close();
                } catch (SQLException ex) {
                    logger.error("SQLException: " + ex.getMessage());
                    logger.error("SQLState: " + ex.getSQLState());
                    logger.error("VendorError: " + ex.getErrorCode());
                }


                newDetails.setId( results.getString("route_id") );
                newDetails.setName( results.getString("name") );
                newDetails.setColor( results.getString("color") );
                newDetails.setStatus( results.getString("status") );
                newDetails.setWaypoints( routeWaypoints );
                newDetails.setSafepoints( routeSafepoints );

                routes.add( newDetails );
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

        return routes;
    }

    public void createRoute( RouteDetails newRoute ) {

        List<LatLng> waypoints = newRoute.getWaypoints();
        List<LatLng> safepoints = newRoute.getSafepoints();

        String routeInfoStatement = "INSERT INTO ebdb.RouteInfo (route_id, name, color, status) VALUES ( ?, ?, ?, ? );";
        db.executeUpdate( routeInfoStatement, newRoute.getId(), newRoute.getName(), newRoute.getColor(),
                newRoute.getStatus() );

        // TODO: Add Date / Time Handling

        for( int i=0; i < waypoints.size(); i++ ) {
            LatLng waypoint = waypoints.get(i);
            String wayPointStatement = "\n" +
                    "INSERT INTO ebdb.RouteWaypoints (route_id, lat, lng, sequence_num) VALUES ( ?, ?, ?, ? );";
            db.executeUpdate(wayPointStatement, newRoute.getId(), waypoint.getLat(), waypoint.getLng(), i);
        }

        for( int i=0; i < safepoints.size(); i++ ) {
            LatLng safepoint = waypoints.get(i);
            String wayPointStatement = "\n" +
                    "INSERT INTO ebdb.RouteSafepoints (route_id, lat, lng, sequence_num) VALUES ( ?, ?, ?, ? );";
            db.executeUpdate(wayPointStatement, newRoute.getId(), safepoint.getLat(), safepoint.getLng(), i);
        }
    }

    public void updateRoute( RouteDetails routeDetails ) {

        String deleteStatement = "DELETE FROM ebdb.RouteInfo WHERE route_id = '" + routeDetails.getId() + "';";
        db.executeStatement( deleteStatement );

        List<LatLng> waypoints = routeDetails.getWaypoints();
        List<LatLng> safepoints = routeDetails.getSafepoints();

        String routeInfoStatement = "INSERT INTO ebdb.RouteInfo (route_id, name, color, status) VALUES ( ?, ?, ?, ? );";
        db.executeUpdate( routeInfoStatement, routeDetails.getId(), routeDetails.getName(), routeDetails.getColor(),
                routeDetails.getStatus() );

        // TODO: Add Date / Time Handling

        for( int i=0; i < waypoints.size(); i++ ) {
            LatLng waypoint = waypoints.get(i);
            String wayPointStatement = "\n" +
                    "INSERT INTO ebdb.RouteWaypoints (route_id, lat, lng, sequence_num) VALUES ( ?, ?, ?, ? );";
            db.executeUpdate(wayPointStatement, routeDetails.getId(), waypoint.getLat(), waypoint.getLng(), i);
        }

        for( int i=0; i < safepoints.size(); i++ ) {
            LatLng safepoint = waypoints.get(i);
            String wayPointStatement = "\n" +
                    "INSERT INTO ebdb.RouteSafepoints (route_id, lat, lng, sequence_num) VALUES ( ?, ?, ?, ? );";
            db.executeUpdate(wayPointStatement, routeDetails.getId(), safepoint.getLat(), safepoint.getLng(), i);
        }
    }


    public void deleteRoute( String routeId ) {
        String deleteStatement = "DELETE FROM ebdb.RouteInfo WHERE route_id = '" + routeId + "';";
        db.executeStatement( deleteStatement );
    }



    //
    // Users
    //

    public List<UserData> getUsersData() {
        String queryStatement = "SELECT user_name FROM ebdb.users;";

        List<UserData> usersData = new ArrayList<UserData>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection( jdbcUrl );
            Statement setupStatement = connection.createStatement();
            ResultSet results = setupStatement.executeQuery( queryStatement );

            while( results.next() ) {
                UserData userData = new UserData();
                List<String> userRoles = new ArrayList<String>();

                try {
                    String rolesQueryStatement = "SELECT role_name FROM ebdb.user_roles WHERE user_name = '" + 
                            results.getString( "user_name" ) + "';";

                    Statement rolesSetupStatement = connection.createStatement();
                    ResultSet rolesResults = rolesSetupStatement.executeQuery( rolesQueryStatement );

                    while( rolesResults.next() ) {
                        userRoles.add( rolesResults.getString( "role_name" ) );
                    }

                    rolesSetupStatement.close();
                } catch (SQLException ex) {
                    logger.error("SQLException: " + ex.getMessage());
                    logger.error("SQLState: " + ex.getSQLState());
                    logger.error("VendorError: " + ex.getErrorCode());
                }

                userData.setUserName( results.getString( "user_name" ) );
                userData.setUserRoles( userRoles );

                usersData.add( userData );
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

        return usersData;
    }

    public void createUser( UserData newData ) {
        String createStatement = "INSERT INTO ebdb.users ( user_name, user_pass ) VALUES ( ?, ? );";
        db.executeUpdate( createStatement, newData.getUserName(), newData.getUserPassword() );

        for( String role : newData.getUserRoles() ) {
            String createRoleStatement = "INSERT INTO ebdb.user_roles ( user_name, role_name ) VALUES ( ?, ? );";
            db.executeUpdate( createRoleStatement, newData.getUserName(), role );
        }
    }

    public void updateUser( UserData updateData ) {
        String deleteStatement = "DELETE FROM ebdb.users WHERE user_name = '" + userName + "';";
        db.executeStatement( deleteStatement );

        String createStatement = "INSERT INTO ebdb.users ( user_name, user_pass ) VALUES ( ?, ? );";
        db.executeUpdate( createStatement, updateData.getUserName(), updateData.getUserPassword() );

        for( String role : updateData.getUserRoles() ) {
            String createRoleStatement = "INSERT INTO ebdb.user_roles ( user_name, role_name ) VALUES ( ?, ? );";
            db.executeUpdate( createRoleStatement, updateData.getUserName(), role );
        }
    }

    public void deleteUser( String userName ) {
        String deleteStatement = "DELETE FROM ebdb.users WHERE user_name = '" + userName + "';";
        db.executeStatement( deleteStatement );
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
                "  `waypoint_id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `route_id` varchar(45) NOT NULL,\n" +
                "  `lat` double NOT NULL,\n" +
                "  `lng` varchar(45) NOT NULL,\n" +
                "  `sequence_num` int(11) NOT NULL,\n" +
                "  PRIMARY KEY (`waypoint_id`),\n" +
                "  UNIQUE KEY `waypoint_id_UNIQUE` (`waypoint_id`),\n" +
                "  KEY `route_id_idx` (`route_id`),\n" +
                "  CONSTRAINT `waypoint_route_id` FOREIGN KEY (`route_id`) REFERENCES `ebdb`.`RouteInfo` (`route_id`) ON DELETE CASCADE ON UPDATE NO ACTION\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n";
        db.executeStatement( routeWaypointsTableSatement );

        // Create Route Safepoints Table
        String routeSafepointsTableSatement = "CREATE TABLE IF NOT EXISTS `ebdb`.`RouteSafepoints` (\n" +
                "  `safepoint_id` INT NOT NULL AUTO_INCREMENT,\n" +
                "  `route_id` VARCHAR(45) NOT NULL,\n" +
                "  `lat` DOUBLE NOT NULL,\n" +
                "  `lng` VARCHAR(45) NOT NULL,\n" +
                "  `sequence_num` INT NOT NULL,\n" +
                "  PRIMARY KEY (`safepoint_id`),\n" +
                "  UNIQUE INDEX `safepoint_id_UNIQUE` (`safepoint_id` ASC),\n" +
                "  KEY `route_id_idx` (`route_id`),\n" +
                "  CONSTRAINT `safepoint_route_id` FOREIGN KEY (`route_id`) REFERENCES `ebdb`.`RouteInfo` (`route_id`) ON DELETE CASCADE ON UPDATE NO ACTION\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n;";
        db.executeStatement( routeSafepointsTableSatement );

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


    public void ensureUserTablesExist() {
        String userTableCreate = "CREATE TABLE IF NOT EXISTS `ebdb`.`users` (\n" +
                "  user_name         varchar(15) not null primary key,\n" +
                "  user_pass         varchar(15) not null\n" +
                ");";

        String rolesTableCreate = "CREATE TABLE IF NOT EXISTS `ebdb`.`user_roles` (\n" +
                "  `user_name` varchar(15) NOT NULL,\n" +
                "  `role_name` varchar(15) NOT NULL,\n" +
                "  PRIMARY KEY (`user_name`,`role_name`),\n" +
                "  CONSTRAINT `user_name` FOREIGN KEY (`user_name`) " +
                "  REFERENCES `ebdb`.`users` (`user_name`) ON DELETE CASCADE ON UPDATE NO ACTION\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n";

        db.executeStatement( userTableCreate );
        db.executeStatement( rolesTableCreate );
    }

}
