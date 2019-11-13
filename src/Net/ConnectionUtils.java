package Net;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtils {
	
	public static Connection connect(String databaseURL, String userName, String userPassword) 
			throws SQLException, ClassNotFoundException {

		Class<?> driver = Class.forName("oracle.jdbc.driver.OracleDriver");

		Properties connectionProps = new Properties();
		connectionProps.put("user", userName);
		connectionProps.put("password", userPassword);
		return DriverManager.getConnection(databaseURL, userName, userPassword);
	}

	public static void disconnect(Connection connection) throws SQLException {
		if (connection != null)
			connection.close();
	}
	
	public static Class<?> getDriver(Connection connection){
		return connection.getClass();
	}

	
}
