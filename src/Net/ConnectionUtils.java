package Net;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtils {
	
	/**
	 * Creates a Connection to a Database
	 * @param databaseURL URL of the Database
	 * @param userName Username for the Database
	 * @param userPassword Password from the User for the Database
	 * @return The Connection, if connection was successfull, otherwise null
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static Connection connect(String databaseURL, String userName, String userPassword) 
			throws SQLException, ClassNotFoundException {

		Class<?> driver = Class.forName("oracle.jdbc.driver.OracleDriver");

		Properties connectionProps = new Properties();
		connectionProps.put("user", userName);
		connectionProps.put("password", userPassword);
		return DriverManager.getConnection(databaseURL, userName, userPassword);
	}

	/**
	 * Disconnect the connection.
	 * @param connection
	 * @throws SQLException
	 */
	public static void disconnect(Connection connection) throws SQLException {
		if (connection != null)
			connection.close();
	}
	
	/**
	 * Returns the Driver of a Connection
	 * @param connection which driver will be returned
	 * @return the Driver from the connection.
	 */
	public static Class<?> getDriver(Connection connection){
		return connection.getClass();
	}

	
}
