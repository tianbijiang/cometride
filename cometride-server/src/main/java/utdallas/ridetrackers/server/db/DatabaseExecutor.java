package utdallas.ridetrackers.server.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: mlautz
 */
public class DatabaseExecutor {

    private final Logger logger = LoggerFactory.getLogger(DatabaseExecutor.class);

    private final String jdbcUrl;

    public DatabaseExecutor( String jdbcUrl ) {
        this.jdbcUrl = jdbcUrl;
    }

    public boolean executeStatement( String statementString ) {
        logger.info( "Running statement: " + statementString );
        Connection connection = null;
        boolean result = false;

        try {
            connection = DriverManager.getConnection(jdbcUrl);
            PreparedStatement setupStatement = connection.prepareStatement( statementString );
            result = setupStatement.execute( statementString );
            setupStatement.close();
        } catch (SQLException ex) {
            logger.error("SQLException: " + ex.getMessage());
            logger.error("SQLState: " + ex.getSQLState());
            logger.error("VendorError: " + ex.getErrorCode());
            // TODO: Throw error with condensed message
        } finally {
            System.out.println("Closing the connection.");
            if (connection != null) try { connection.close(); } catch (SQLException ignore) {}
        }
        return result;
    }

    public int executeUpdate( String statementString, Object ... values ) {
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
            // TODO: Throw error with condensed message
        } finally {
            System.out.println("Closing the connection.");
            if (connection != null) try { connection.close(); } catch (SQLException ignore) {}
        }
        return result;
    }

//    public ResultSet executeQuery( String queryString ) {
//        logger.info( "Running query: " + queryString );
//        Connection connection = null;
//        ResultSet results = null;
//
//        try {
//            connection = DriverManager.getConnection( jdbcUrl );
//            Statement setupStatement = connection.createStatement();
//            results = setupStatement.executeQuery( queryString );
//            setupStatement.close();
//        } catch (SQLException ex) {
//            logger.error("SQLException: " + ex.getMessage());
//            logger.error("SQLState: " + ex.getSQLState());
//            logger.error("VendorError: " + ex.getErrorCode());
//            // TODO: Throw error with condensed message
//        } finally {
//            System.out.println("Closing the connection.");
//            if (connection != null) try { connection.close(); } catch (SQLException ignore) {}
//        }
//        return results;
//    }

}
