package utdallas.ridetrackers.test;

import javax.ws.rs.GET;  
import javax.ws.rs.Path;  
import javax.ws.rs.PathParam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

@Path("/")
public class HelloWorld {

    @GET
    public String sayHello(){
        StringBuilder stringBuilder = new StringBuilder("Hello generic user!" );

        return stringBuilder.toString();
    }

    @GET
    @Path("{name}")
    public String sayHello(@PathParam("name") String name){
        StringBuilder stringBuilder = new StringBuilder("Hello ");
        stringBuilder.append(name).append("!");

        return stringBuilder.toString();
    }
	
	@GET
	@Path("dbtest")
	public String helloDb(){
		// Read RDS Connection Information from the Environment
		  String dbName = System.getProperty("RDS_DB_NAME");
		  String userName = System.getProperty("RDS_USERNAME");
		  String password = System.getProperty("RDS_PASSWORD");
		  String hostname = System.getProperty("RDS_HOSTNAME");
		  String port = System.getProperty("RDS_PORT");
		  String jdbcUrl = "jdbc:mysql://" + hostname + ":" +
			port + "/" + dbName + "?user=" + userName + "&password=" + password;

		  // Load the JDBC Driver
		  try {
			System.out.println("Loading driver...");
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver loaded!");
		  } catch (ClassNotFoundException e) {
			throw new RuntimeException("Cannot find the driver in the classpath!", e);
		  }

		  Connection conn = null;
		  Statement setupStatement = null;
		  Statement readStatement = null;
		  ResultSet resultSet = null;
		  String results = "";
		  int numresults = 0;
		  String statement = null;

		  try {
			// Create connection to RDS instance
			conn = DriverManager.getConnection(jdbcUrl);

			// Create a table and write two rows
			setupStatement = conn.createStatement();
			String createTable = "CREATE TABLE Beanstalk (Resource char(50));";
			String insertRow1 = "INSERT INTO Beanstalk (Resource) VALUES ('EC2 Instance');";
			String insertRow2 = "INSERT INTO Beanstalk (Resource) VALUES ('RDS Instance');";

			setupStatement.addBatch(createTable);
			setupStatement.addBatch(insertRow1);
			setupStatement.addBatch(insertRow2);
			setupStatement.executeBatch();
			setupStatement.close();

		  } catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		  } finally {
			System.out.println("Closing the connection.");
			if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
		  }

		  try {
			conn = DriverManager.getConnection(jdbcUrl);

			readStatement = conn.createStatement();
			resultSet = readStatement.executeQuery("SELECT Resource FROM Beanstalk;");

			resultSet.first();
			results = resultSet.getString("Resource");
			resultSet.next();
			results += ", " + resultSet.getString("Resource");

			resultSet.close();
			readStatement.close();
			conn.close();

		  } catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		  } finally {
			   System.out.println("Closing the connection.");
			  if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
		  }

		return results;
	}

}