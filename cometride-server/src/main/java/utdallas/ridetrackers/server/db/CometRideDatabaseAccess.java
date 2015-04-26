package utdallas.ridetrackers.server.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utdallas.ridetrackers.server.datatypes.*;
import utdallas.ridetrackers.server.datatypes.admin.RouteDetails;
import utdallas.ridetrackers.server.datatypes.admin.UserData;
import utdallas.ridetrackers.server.datatypes.driver.CabSession;
import utdallas.ridetrackers.server.datatypes.driver.TrackingUpdate;
import utdallas.ridetrackers.server.datatypes.reports.TransDeptDailyRiders;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//        logger.info( "RDS_DB_NAME: " + dbName );
        userName = System.getProperty("RDS_USERNAME");
//        logger.info( "RDS_USERNAME: " + userName );
        password = System.getProperty("RDS_PASSWORD");
//        logger.info( "RDS_PASSWORD: " + password );
        hostname = System.getProperty("RDS_HOSTNAME");
//        logger.info( "RDS_HOSTNAME: " + hostname );
        port = System.getProperty("RDS_PORT");
//        logger.info( "RDS_PORT: " + port );

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

    public List<CabStatus> retrieveCurrentCabStatuses() {
        List<CabStatus> statusList = new ArrayList<CabStatus>();

        String queryStatement = "SELECT cab_session_id, cabStatus.lat, cabStatus.lng, max_capacity, cabStatus.passenger_count, route_id, duty_status \n" +
                "FROM ebdb.CabSession cabSession JOIN \n" +
                "\t( SELECT cab_session_id cabId, lat, lng, passenger_count FROM ebdb.CabStatus t1 \n" +
                "\tJOIN ( SELECT cab_session_id id, MAX( submission_time ) subtime FROM ebdb.CabStatus " +
                "\tWHERE submission_time BETWEEN DATE_SUB( NOW(), INTERVAL 30 SECOND ) AND NOW()" +
                "\tGROUP BY cab_session_id ) t2 \n" +
                "\tON t1.cab_session_id = t2.id AND t1.submission_time = t2.subtime ) cabStatus \n" +
                "ON cabStatus.cabId =  cabSession.cab_session_id AND duty_status = 'ON-DUTY';";

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

    public CabStatus retrieveCabStatus( String cabId ) {
        String queryStatement = "SELECT cab_session_id, cabStatus.lat, cabStatus.lng, max_capacity, cabStatus.passenger_count, route_id, duty_status \n" +
                "FROM ebdb.CabSession cabSession JOIN \n" +
                "\t( SELECT cab_session_id cabId, lat, lng, passenger_count, MAX( submission_time ) submission_time " +
                "FROM ebdb.CabStatus WHERE cab_session_id = '" + cabId + "' ) cabStatus \n" +
                "ON cabStatus.cabId =  cabSession.cab_session_id;";

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

    public List<CabType> retrieveCabTypes() {
        List<CabType> cabTypes = new ArrayList<CabType>();
        String cabQuery = "SELECT * FROM ebdb.CabTypes;";

        logger.info( "Running query: " + cabQuery );
        Connection connection = null;

        try {
            connection = DriverManager.getConnection( jdbcUrl );
            Statement setupStatement = connection.createStatement();
            ResultSet results = setupStatement.executeQuery( cabQuery );
            results.first();

            while ( results.next() ) {
                CabType cabType = new CabType();
                cabType.setTypeId( results.getString( "type_id" ) );
                cabType.setTypeName( results.getString( "type_name" ) );
                cabType.setMaximumCapacity( results.getInt( "max_capacity" ) );

                cabTypes.add( cabType );
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


        return cabTypes;
    }

    public void createCabType( CabType newCabType ) {
        String insertStatement = "INSERT INTO ebdb.CabTypes ( type_id, type_name, max_capacity ) " +
                "VALUES ( ?, ?, ? );";

        db.executeUpdate( insertStatement, newCabType.getTypeId(), newCabType.getTypeName(),
                newCabType.getMaximumCapacity() );
    }

    public void deleteCabType( String typeName ) {
        String deleteStatement = "DELETE FROM ebdb.CabTypes WHERE type_id = '" + typeName + "';";
        db.executeStatement( deleteStatement );
    }


    public void createCabSession( CabSession cabSession ) {
        String insertStatement = "INSERT INTO CabSession (cab_session_id, duty_status, route_id, max_capacity) " +
                "VALUES (?, ?, ?, ?)";

        db.executeUpdate( insertStatement, cabSession.getCabSessionId(), cabSession.getDutyStatus(),
                cabSession.getRouteId(), cabSession.getMaxCapacity() );
    }

    public void updateCabSession( CabSession cabSession ) {
        String insertStatement = "UPDATE `ebdb`.`CabSession` SET `duty_status`=? WHERE `cab_session_id`=?;";
        db.executeUpdate( insertStatement, cabSession.getDutyStatus(), cabSession.getCabSessionId() );
    }

    public void persistTrackingUpdate( TrackingUpdate trackingUpdate) {

        String insertStatement = "INSERT INTO CabStatus (cab_session_id, lat, lng, passenger_count, " +
                "passengers_added, submission_time) VALUES (?, ?, ?, ?, ?, ?)";

        db.executeUpdate(insertStatement, trackingUpdate.getCabSessionId(), trackingUpdate.getLat(),
                trackingUpdate.getLng(), trackingUpdate.getPassengerCount(), trackingUpdate.getPassengersAdded(),
                trackingUpdate.getTimestamp());
    }



    //
    // Routes
    //

    public List<Route> getCurrentRouteDetails() {
        String queryStatement = "SELECT route_id, name, short_name, color, status, navigation_type FROM ebdb.RouteInfo info JOIN\n" +
                "\t( ( SELECT route_id id FROM ebdb.RouteTimes WHERE\n" +
                "\t( start_time < DATE_ADD( NOW(), INTERVAL 10 MINUTE ) AND end_time > NOW() )\n" +
                "\tOR ( start_time IS NULL AND end_time IS NULL ) ) routeTime JOIN \n" +
                "    ( SELECT route_id id FROM ebdb.RouteDates WHERE\n" +
                "\t( start_date < NOW() AND end_date > NOW() )\n" +
                "\tOR ( start_date IS NULL AND end_date IS NULL ) ) routeDate\n" +
                "    ON routeDate.id = routeTime.id ) ON info.route_id = routeTime.id GROUP BY route_id;";

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
                newDetails.setName(results.getString("name"));
                newDetails.setShortName(results.getString("short_name"));
                newDetails.setColor( results.getString("color") );
                newDetails.setStatus( results.getString("status") );
                newDetails.setNavigationType( results.getString("navigation_type") );
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

    public List<Route> getAllRouteDetails() {
        String queryStatement = "SELECT route_id, name, short_name, color, status, navigation_type FROM ebdb.RouteInfo info;";

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
                newDetails.setName(results.getString("name"));
                newDetails.setShortName(results.getString("short_name"));
                newDetails.setColor( results.getString("color") );
                newDetails.setStatus( results.getString("status") );
                newDetails.setNavigationType( results.getString("navigation_type" ) );
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

    public void createRoute( RouteDetails newRoute ) throws ParseException {

        if( newRoute.getNavigationType() == null ) {
            newRoute.setNavigationType( "DRIVING" );
        }

        List<LatLng> waypoints = newRoute.getWaypoints();
        List<LatLng> safepoints = newRoute.getSafepoints();

        String routeInfoStatement = "INSERT INTO ebdb.RouteInfo (route_id, name, short_name, color, status, navigation_type) VALUES ( ?, ?, ?, ?, ?, ? );";
        db.executeUpdate( routeInfoStatement, newRoute.getId(), newRoute.getName(), newRoute.getShortName(),
                newRoute.getColor(), newRoute.getStatus(), newRoute.getNavigationType() );

        for( int i=0; i < waypoints.size(); i++ ) {
            LatLng waypoint = waypoints.get(i);
            String wayPointStatement = "\n" +
                    "INSERT INTO ebdb.RouteWaypoints (route_id, lat, lng, sequence_num) VALUES ( ?, ?, ?, ? );";
            db.executeUpdate(wayPointStatement, newRoute.getId(), waypoint.getLat(), waypoint.getLng(), i);
        }

        for( int i=0; i < safepoints.size(); i++ ) {
            LatLng safepoint = safepoints.get(i);
            String safePointStatement = "\n" +
                    "INSERT INTO ebdb.RouteSafepoints (route_id, lat, lng, sequence_num) VALUES ( ?, ?, ?, ? );";
            db.executeUpdate(safePointStatement, newRoute.getId(), safepoint.getLat(), safepoint.getLng(), i);
        }

        java.sql.Date startSqlDate = null;
        java.sql.Date endSqlDate = null;
        if( newRoute.getStartDate() != null && newRoute.getEndDate() != null ) {
            try {
                startSqlDate = new java.sql.Date(newRoute.getStartDate().getTime());
                endSqlDate = new java.sql.Date(newRoute.getEndDate().getTime());
            } catch ( Exception e ) {
                logger.error( "Failed to parse new date!" );
                startSqlDate = null;
                endSqlDate = null;
            }
        }
        String dateCreationStatement = "INSERT INTO ebdb.RouteDates ( route_id, start_date, end_date ) " +
                "VALUES ( ?, ?, ? );";
        db.executeUpdate( dateCreationStatement, newRoute.getId(), startSqlDate, endSqlDate );

        String timeCreationStatement = "INSERT INTO ebdb.RouteTimes ( route_id, start_time, end_time ) " +
                "VALUES ( ?, ?, ? );";

        if( newRoute.getTimes().size() == 0 ) {
            db.executeUpdate(timeCreationStatement, newRoute.getId(), null, null);
        } else {
            for (TimeRange range : newRoute.getTimes()) {
                java.sql.Time startSqlTime = null;
                java.sql.Time endSqlTime = null;

                if (range.getStart() != null && range.getEnd() != null) {
                    DateFormat formatter = new SimpleDateFormat("hh:mma");
                    java.util.Date startTime = formatter.parse(range.getStart());
                    java.util.Date endTime = formatter.parse(range.getEnd());

                    startSqlTime = new java.sql.Time(startTime.getTime());
                    endSqlTime = new java.sql.Time(endTime.getTime());
                }
                db.executeUpdate(timeCreationStatement, newRoute.getId(), startSqlTime, endSqlTime);
            }
        }

        // TODO: Add Day of Week
    }

    public void updateRoute( RouteDetails routeDetails ) throws ParseException {
        if( routeDetails.getNavigationType() == null ) {
            routeDetails.setNavigationType( "DRIVING" );
        }

        String deleteStatement = "DELETE FROM ebdb.RouteInfo WHERE route_id = '" + routeDetails.getId() + "';";
        db.executeStatement( deleteStatement );

        List<LatLng> waypoints = routeDetails.getWaypoints();
        List<LatLng> safepoints = routeDetails.getSafepoints();

        String routeInfoStatement = "INSERT INTO ebdb.RouteInfo (route_id, name, short_name, color, status, navigation_type) VALUES ( ?, ?, ?, ?, ?, ? );";
        db.executeUpdate( routeInfoStatement, routeDetails.getId(), routeDetails.getName(), routeDetails.getShortName(),
                routeDetails.getColor(), routeDetails.getStatus(), routeDetails.getNavigationType() );

        // TODO: Add Date / Time Handling

        for( int i=0; i < waypoints.size(); i++ ) {
            LatLng waypoint = waypoints.get(i);
            String wayPointStatement = "\n" +
                    "INSERT INTO ebdb.RouteWaypoints (route_id, lat, lng, sequence_num) VALUES ( ?, ?, ?, ? );";
            db.executeUpdate(wayPointStatement, routeDetails.getId(), waypoint.getLat(), waypoint.getLng(), i);
        }

        for( int i=0; i < safepoints.size(); i++ ) {
            LatLng safepoint = safepoints.get(i);
            String safePointStatement = "\n" +
                    "INSERT INTO ebdb.RouteSafepoints (route_id, lat, lng, sequence_num) VALUES ( ?, ?, ?, ? );";
            db.executeUpdate(safePointStatement, routeDetails.getId(), safepoint.getLat(), safepoint.getLng(), i);
        }

        java.sql.Date startSqlDate = null;
        java.sql.Date endSqlDate = null;
        if( routeDetails.getStartDate() != null && routeDetails.getEndDate() != null ) {
            startSqlDate = new java.sql.Date(routeDetails.getStartDate().getTime());
            endSqlDate = new java.sql.Date(routeDetails.getEndDate().getTime());
        }
        String dateCreationStatement = "INSERT INTO ebdb.RouteDates ( route_id, start_date, end_date ) " +
                "VALUES ( ?, ?, ? );";
        db.executeUpdate( dateCreationStatement, routeDetails.getId(), startSqlDate, endSqlDate );

        String timeCreationStatement = "INSERT INTO ebdb.RouteTimes ( route_id, start_date, end_date ) " +
                "VALUES ( ?, ?, ? );";
        for( TimeRange range : routeDetails.getTimes() ) {
            java.sql.Time startSqlTime = null;
            java.sql.Time endSqlTime = null;

            if( range.getStart() != null && range.getEnd()!= null ) {
                DateFormat formatter = new SimpleDateFormat("hh:mma");
                java.util.Date startTime = formatter.parse( range.getStart() );
                java.util.Date endTime = formatter.parse( range.getEnd() );

                startSqlTime = new java.sql.Time( startTime.getTime() );
                endSqlTime = new java.sql.Time( endTime.getTime() );
            }
            db.executeUpdate( timeCreationStatement, routeDetails.getId(), startSqlTime, endSqlTime );
        }

        // TODO: Add Day of Week
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
        String routeInfoTableSatement = "CREATE TABLE IF NOT EXISTS `RouteInfo` (\n" +
                "  `route_id` varchar(45) NOT NULL,\n" +
                "  `name` varchar(45) NOT NULL,\n" +
                "  `color` varchar(45) NOT NULL,\n" +
                "  `status` varchar(45) NOT NULL,\n" +
                "  `navigation_type` varchar(45) NOT NULL DEFAULT 'DRIVING',\n" +
                "  `short_name` varchar(45) NOT NULL,\n" +
                "  PRIMARY KEY (`route_id`),\n" +
                "  UNIQUE KEY `route_id_UNIQUE` (`route_id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n";
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
        String routeTimeTableStatement = "CREATE TABLE IF NOT EXISTS `RouteTimes` (\n" +
                "  `route_times_id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `route_id` varchar(45) NOT NULL,\n" +
                "  `start_time` time DEFAULT NULL,\n" +
                "  `end_time` time DEFAULT NULL,\n" +
                "  PRIMARY KEY (`route_times_id`),\n" +
                "  UNIQUE KEY `route_times_id_UNIQUE` (`route_times_id`),\n" +
                "  KEY `time_route_id_idx` (`route_id`),\n" +
                "  CONSTRAINT `time_route_id` FOREIGN KEY (`route_id`) REFERENCES `ebdb`.`RouteInfo` (`route_id`) ON DELETE CASCADE ON UPDATE NO ACTION\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;\n";
        db.executeStatement( routeTimeTableStatement );

        // Create Route Dates Table
        String routeDateTableStatement = "CREATE TABLE IF NOT EXISTS `RouteDates` (\n" +
                "  `route_dates_id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `route_id` varchar(45) NOT NULL,\n" +
                "  `start_date` date DEFAULT NULL,\n" +
                "  `end_date` date DEFAULT NULL,\n" +
                "  PRIMARY KEY (`route_dates_id`),\n" +
                "  UNIQUE KEY `route_times_id_UNIQUE` (`route_dates_id`),\n" +
                "  KEY `dates_route_id_idx` (`route_id`),\n" +
                "  CONSTRAINT `dates_route_id` FOREIGN KEY (`route_id`) REFERENCES `ebdb`.`RouteInfo` (`route_id`) ON DELETE CASCADE ON UPDATE NO ACTION\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;\n";
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
                "  `passengers_added` INT NOT NULL,\n" +
                "  `submission_time` TIMESTAMP NOT NULL,\n" +
                "  PRIMARY KEY (`cab_status_id`),\n" +
                "  UNIQUE INDEX `cabStatusId_UNIQUE` (`cab_status_id` ASC)); ";
        db.executeStatement( cabStatusTableStatement );

        String cabTypesTableStatement = "CREATE TABLE IF NOT EXISTS `CabTypes` (\n" +
                "  `type_name` varchar(45) NOT NULL,\n" +
                "  `max_capacity` int(11) NOT NULL,\n" +
                "  `type_id` varchar(45) NOT NULL,\n" +
                "  PRIMARY KEY (`type_id`),\n" +
                "  UNIQUE KEY `type_id_UNIQUE` (`type_id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n";
        db.executeStatement( cabTypesTableStatement );
    }


    public void ensureUserTablesExist() {
        String userTableCreate = "CREATE TABLE IF NOT EXISTS `ebdb`.`users` (\n" +
                "  user_name         varchar(30) not null primary key,\n" +
                "  user_pass         varchar(30) not null\n" +
                ");";

        String rolesTableCreate = "CREATE TABLE IF NOT EXISTS `ebdb`.`user_roles` (\n" +
                "  `user_name` varchar(30) NOT NULL,\n" +
                "  `role_name` varchar(30) NOT NULL,\n" +
                "  PRIMARY KEY (`user_name`,`role_name`),\n" +
                "  CONSTRAINT `user_name` FOREIGN KEY (`user_name`) " +
                "  REFERENCES `ebdb`.`users` (`user_name`) ON DELETE CASCADE ON UPDATE NO ACTION\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n";

        db.executeStatement( userTableCreate );
        db.executeStatement( rolesTableCreate );
    }


    public List<TransDeptDailyRiders> getMonthlyRidersMetrics() {
        List<TransDeptDailyRiders> metricsList = new ArrayList<TransDeptDailyRiders>();
        String queryString = "SELECT SUM( passengers_added ) as passengers, " +
                "DATE( submission_time ) as day, " +
                "HOUR( submission_time ) as hour FROM ebdb.CabStatus GROUP BY DAY( submission_time ), " +
                "HOUR( submission_time );";

        logger.info( "Running query: " + queryString );
        Connection connection = null;

        try {
            connection = DriverManager.getConnection( jdbcUrl );
            Statement setupStatement = connection.createStatement();
            ResultSet results = setupStatement.executeQuery( queryString );
            logger.debug( "Results found: " + results.toString() );
            results.first();

            // TODO: Make data
            String currentDate = "";
            Map<Integer, Integer> dateValueMap = new HashMap<Integer, Integer>();
            int index = 0;
            while( results.next() ) {
                if( !results.getString( "day" ).equals( currentDate ) ) {
                    logger.debug( "New day found: " + results.getString( "day" ) );

                    if( metricsList.size() > 0 ) {
                        metricsList.get(index).setDate(currentDate);
                        metricsList.get(index).setSeven(dateValueMap.get(7) == null ? 0 : dateValueMap.get(7));
                        metricsList.get(index).setEight(dateValueMap.get(8) == null ? 0 : dateValueMap.get(8));
                        metricsList.get(index).setNine(dateValueMap.get(9) == null ? 0 : dateValueMap.get(9));
                        metricsList.get(index).setTen(dateValueMap.get(10) == null ? 0 : dateValueMap.get(10));
                        metricsList.get(index).setEleven(dateValueMap.get(11) == null ? 0 : dateValueMap.get(11));
                        metricsList.get(index).setTwelve(dateValueMap.get(12) == null ? 0 : dateValueMap.get(12));
                        metricsList.get(index).setThirteen(dateValueMap.get(13) == null ? 0 : dateValueMap.get(13));
                        metricsList.get(index).setFourteen(dateValueMap.get(14) == null ? 0 : dateValueMap.get(14));
                        metricsList.get(index).setFifteen(dateValueMap.get(15) == null ? 0 : dateValueMap.get(15));
                        metricsList.get(index).setSixteen(dateValueMap.get(16) == null ? 0 : dateValueMap.get(16));
                        metricsList.get(index).setSeventeen(dateValueMap.get(17) == null ? 0 : dateValueMap.get(17));
                        metricsList.get(index).setEighteen(dateValueMap.get(18) == null ? 0 : dateValueMap.get(18));
                        metricsList.get(index).setNineteen(dateValueMap.get(19) == null ? 0 : dateValueMap.get(19));
                        metricsList.get(index).setTwenty(dateValueMap.get(20) == null ? 0 : dateValueMap.get(20));
                        metricsList.get(index).setTwentyOne(dateValueMap.get(21) == null ? 0 : dateValueMap.get(21));
                    }

                    currentDate = results.getString( "day" );
                    dateValueMap.clear();
                    metricsList.add( new TransDeptDailyRiders() );
                    index = metricsList.size() - 1;
                }

                dateValueMap.put( results.getInt("hour"), results.getInt( "passengers" ) );
            }

            metricsList.get( index ).setDate(currentDate);
            metricsList.get( index ).setSeven(dateValueMap.get( 7 ) == null ? 0 : dateValueMap.get( 7 ));
            metricsList.get( index ).setEight(dateValueMap.get( 8 ) == null ? 0 : dateValueMap.get( 8 ));
            metricsList.get( index ).setNine(dateValueMap.get( 9 ) == null ? 0 : dateValueMap.get( 9 ));
            metricsList.get( index ).setTen(dateValueMap.get( 10 ) == null ? 0 : dateValueMap.get( 10 ));
            metricsList.get( index ).setEleven(dateValueMap.get( 11 ) == null ? 0 : dateValueMap.get( 11 ));
            metricsList.get( index ).setTwelve(dateValueMap.get( 12 ) == null ? 0 : dateValueMap.get( 12 ));
            metricsList.get( index ).setThirteen(dateValueMap.get( 13 ) == null ? 0 : dateValueMap.get( 13 ));
            metricsList.get( index ).setFourteen(dateValueMap.get( 14 ) == null ? 0 : dateValueMap.get( 14 ));
            metricsList.get( index ).setFifteen(dateValueMap.get( 15 ) == null ? 0 : dateValueMap.get( 15 ));
            metricsList.get( index ).setSixteen(dateValueMap.get( 16 ) == null ? 0 : dateValueMap.get( 16 ));
            metricsList.get( index ).setSeventeen(dateValueMap.get( 17 ) == null ? 0 : dateValueMap.get( 17  ) );
            metricsList.get( index ).setEighteen(dateValueMap.get( 18 ) == null ? 0 : dateValueMap.get( 18  ) );
            metricsList.get( index ).setNineteen(dateValueMap.get( 19 ) == null ? 0 : dateValueMap.get( 19 ) );
            metricsList.get( index ).setTwenty(dateValueMap.get( 20 ) == null ? 0 : dateValueMap.get( 20 ) );
            metricsList.get( index ).setTwentyOne(dateValueMap.get( 21  ) == null ? 0 : dateValueMap.get( 21 ) );

            setupStatement.close();
        } catch (SQLException ex) {
            logger.error("SQLException: " + ex.getMessage());
            logger.error("SQLState: " + ex.getSQLState());
            logger.error("VendorError: " + ex.getErrorCode());
        } finally {
            System.out.println("Closing the connection.");
            if (connection != null) try { connection.close(); } catch (SQLException ignore) {}
        }

        return metricsList;
    }

    public List<TransDeptDailyRiders> getMonthlyRidersMetrics( String routeId ) {
        List<TransDeptDailyRiders> metricsList = new ArrayList<TransDeptDailyRiders>();
        String queryString = "SELECT SUM( passengers_added ) as passengers, DATE( submission_time ) as day, " +
                "HOUR( submission_time ) as hour FROM \n" +
                "\tebdb.CabStatus statusData JOIN \n" +
                "    ( SELECT route_id, cab_session_id as cabId FROM ebdb.CabSession " +
                "WHERE route_id = '" + routeId + "' ) sessionData\n" +
                "    ON sessionData.cabId = statusData.cab_session_id\n" +
                "    GROUP BY DAY( submission_time ), HOUR( submission_time );";

        logger.info( "Running query: " + queryString );
        Connection connection = null;

        try {
            connection = DriverManager.getConnection( jdbcUrl );
            Statement setupStatement = connection.createStatement();
            ResultSet results = setupStatement.executeQuery( queryString );
            logger.debug( "Results found: " + results.toString() );
            results.first();

            // TODO: Make data
            String currentDate = "";
            Map<Integer, Integer> dateValueMap = new HashMap<Integer, Integer>();
            metricsList.add( new TransDeptDailyRiders() );
            int index = 0;
            while( results.next() ) {
                if( !results.getString( "day" ).equals( currentDate ) ) {
                    logger.debug( "New day found: " + results.getString( "day" ) );

                    if( metricsList.size() > 0 ) {
                        metricsList.get(index).setDate(currentDate);
                        metricsList.get(index).setSeven(dateValueMap.get(7) == null ? 0 : dateValueMap.get(7));
                        metricsList.get(index).setEight(dateValueMap.get(8) == null ? 0 : dateValueMap.get(8));
                        metricsList.get(index).setNine(dateValueMap.get(9) == null ? 0 : dateValueMap.get(9));
                        metricsList.get(index).setTen(dateValueMap.get(10) == null ? 0 : dateValueMap.get(10));
                        metricsList.get(index).setEleven(dateValueMap.get(11) == null ? 0 : dateValueMap.get(11));
                        metricsList.get(index).setTwelve(dateValueMap.get(12) == null ? 0 : dateValueMap.get(12));
                        metricsList.get(index).setThirteen(dateValueMap.get(13) == null ? 0 : dateValueMap.get(13));
                        metricsList.get(index).setFourteen(dateValueMap.get(14) == null ? 0 : dateValueMap.get(14));
                        metricsList.get(index).setFifteen(dateValueMap.get(15) == null ? 0 : dateValueMap.get(15));
                        metricsList.get(index).setSixteen(dateValueMap.get(16) == null ? 0 : dateValueMap.get(16));
                        metricsList.get(index).setSeventeen(dateValueMap.get(17) == null ? 0 : dateValueMap.get(17));
                        metricsList.get(index).setEighteen(dateValueMap.get(18) == null ? 0 : dateValueMap.get(18));
                        metricsList.get(index).setNineteen(dateValueMap.get(19) == null ? 0 : dateValueMap.get(19));
                        metricsList.get(index).setTwenty(dateValueMap.get(20) == null ? 0 : dateValueMap.get(20));
                        metricsList.get(index).setTwentyOne(dateValueMap.get(21) == null ? 0 : dateValueMap.get(21));
                    }

                    currentDate = results.getString( "day" );
                    dateValueMap.clear();
                    metricsList.add( new TransDeptDailyRiders() );
                    index = metricsList.size() - 1;
                }

                dateValueMap.put( results.getInt("hour"), results.getInt( "passengers" ) );
            }

            metricsList.get( index ).setDate(currentDate);
            metricsList.get( index ).setSeven(dateValueMap.get( 7 ) == null ? 0 : dateValueMap.get( 7 ));
            metricsList.get( index ).setEight(dateValueMap.get( 8 ) == null ? 0 : dateValueMap.get( 8 ));
            metricsList.get( index ).setNine(dateValueMap.get( 9 ) == null ? 0 : dateValueMap.get( 9 ));
            metricsList.get( index ).setTen(dateValueMap.get( 10 ) == null ? 0 : dateValueMap.get( 10 ));
            metricsList.get( index ).setEleven(dateValueMap.get( 11 ) == null ? 0 : dateValueMap.get( 11 ));
            metricsList.get( index ).setTwelve(dateValueMap.get( 12 ) == null ? 0 : dateValueMap.get( 12 ));
            metricsList.get( index ).setThirteen(dateValueMap.get( 13 ) == null ? 0 : dateValueMap.get( 13 ));
            metricsList.get( index ).setFourteen(dateValueMap.get( 14 ) == null ? 0 : dateValueMap.get( 14 ));
            metricsList.get( index ).setFifteen(dateValueMap.get( 15 ) == null ? 0 : dateValueMap.get( 15 ));
            metricsList.get( index ).setSixteen(dateValueMap.get( 16 ) == null ? 0 : dateValueMap.get( 16 ));
            metricsList.get( index ).setSeventeen(dateValueMap.get( 17 ) == null ? 0 : dateValueMap.get( 17  ) );
            metricsList.get( index ).setEighteen(dateValueMap.get( 18 ) == null ? 0 : dateValueMap.get( 18  ) );
            metricsList.get( index ).setNineteen(dateValueMap.get( 19 ) == null ? 0 : dateValueMap.get( 19 ) );
            metricsList.get( index ).setTwenty(dateValueMap.get( 20 ) == null ? 0 : dateValueMap.get( 20 ) );
            metricsList.get( index ).setTwentyOne(dateValueMap.get( 21  ) == null ? 0 : dateValueMap.get( 21 ) );

            setupStatement.close();
        } catch (SQLException ex) {
            logger.error("SQLException: " + ex.getMessage());
            logger.error("SQLState: " + ex.getSQLState());
            logger.error("VendorError: " + ex.getErrorCode());
        } finally {
            System.out.println("Closing the connection.");
            if (connection != null) try { connection.close(); } catch (SQLException ignore) {}
        }

        return metricsList;
    }

}