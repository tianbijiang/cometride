package utdallas.ridetrackers.server.datatypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utdallas.ridetrackers.server.datatypes.driver.DriverStatus;
import utdallas.ridetrackers.server.datatypes.driver.LocationUpdate;
import utdallas.ridetrackers.server.datatypes.driver.TallyUpdate;

import java.sql.*;

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

        createTables();
    }

    public void persistDriverStatus( DriverStatus driverStatus ) {


    }

    public void persistLocationUpdate( LocationUpdate locationUpdate ) {

        String insertStatement = "INSERT INTO Cabstatus (cab_id, lat, lng, passenger_count, submission_time) " +
                "VALUES (?, ?, ?, ?, ?)";

        executeUpdate( insertStatement, locationUpdate.getDriverId(), locationUpdate.getLat(),
                locationUpdate.getLng(), 3, locationUpdate.getTimestamp()  );
    }

    public void persistTallyUpdate( TallyUpdate tallyUpdate ) {


    }


    private void createTables() {
        // Create CabStatus Table
        String cabStatusTableStatement = "CREATE TABLE IF NOT EXISTS `ebdb`.`Cabstatus` (\n" +
                "  `cab_status_id` INT NOT NULL AUTO_INCREMENT,\n" +
                "  `cab_id` VARCHAR(45) NOT NULL,\n" +
                "  `lat` DOUBLE NOT NULL,\n" +
                "  `lng` DOUBLE NOT NULL,\n" +
                "  `passenger_count` INT NOT NULL,\n" +
                "  `submission_time` TIMESTAMP NOT NULL,\n" +
                "  PRIMARY KEY (`cab_status_id`),\n" +
                "  UNIQUE INDEX `cabStatusId_UNIQUE` (`cab_status_id` ASC)); ";
        executeStatement( cabStatusTableStatement );

    }

    private boolean executeStatement( String statementString ) {
        logger.info( "Running statement: " + statementString );
        Connection connection = null;
        boolean result = false;

        try {
            connection = DriverManager.getConnection( jdbcUrl );
            PreparedStatement setupStatement = connection.prepareStatement( statementString );
            result = setupStatement.execute( statementString );
            setupStatement.close();
        } catch (SQLException ex) {
            logger.error("SQLException: " + ex.getMessage());
            logger.error("SQLState: " + ex.getSQLState());
            logger.error("VendorError: " + ex.getErrorCode());
        } finally {
            System.out.println("Closing the connection.");
            if (connection != null) try { connection.close(); } catch (SQLException ignore) {}
        }
        return result;
    }

    private int executeUpdate( String statementString, Object ... values ) {
        logger.info( "Running statement: " + statementString );
        Connection connection = null;
        int result = -1;

        try {
            connection = DriverManager.getConnection( jdbcUrl );
            PreparedStatement setupStatement = connection.prepareStatement( statementString );
            for( int i = 0; i < values.length; i++ ) {
                setupStatement.setObject( i + 1, values[i] );
            }
            result = setupStatement.executeUpdate();
            setupStatement.close();
        } catch (SQLException ex) {
            logger.error("SQLException: " + ex.getMessage());
            logger.error("SQLState: " + ex.getSQLState());
            logger.error("VendorError: " + ex.getErrorCode());
        } finally {
            System.out.println("Closing the connection.");
            if (connection != null) try { connection.close(); } catch (SQLException ignore) {}
        }
        return result;
    }

    private ResultSet executeQuery( String queryString ) {
        logger.info( "Running query: " + queryString );
        Connection connection = null;
        ResultSet results = null;

        try {
            connection = DriverManager.getConnection( jdbcUrl );
            Statement setupStatement = connection.createStatement();
            results = setupStatement.executeQuery( queryString );
            setupStatement.close();
        } catch (SQLException ex) {
            logger.error("SQLException: " + ex.getMessage());
            logger.error("SQLState: " + ex.getSQLState());
            logger.error("VendorError: " + ex.getErrorCode());
        } finally {
            System.out.println("Closing the connection.");
            if (connection != null) try { connection.close(); } catch (SQLException ignore) {}
        }
        return results;
    }
}
