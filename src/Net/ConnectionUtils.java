package Net;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtils {

	private Connection connection;

	public Connection connect(String databaseURL, String userName, String userPassword) 
			throws SQLException, ClassNotFoundException {

		Class.forName("oracle.jdbc.driver.OracleDriver");

		Properties connectionProps = new Properties();
		connectionProps.put("user", userName);
		connectionProps.put("password", userPassword);
		connection = DriverManager.getConnection(databaseURL, userName, userPassword);
		return connection;
	}

	public void disconnect() throws SQLException {
		if (connection != null)
			connection.close();
	}

	
}
