package Net;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse ist die Basis für Ihre Lösung. Mit Hilfe der Methode
 * reInitializeDB können Sie die beim Testen veränderte Datenbank
 * wiederherstellen.
 */
public class JDBCBikeShop {

	private final static String SQL_TYPE_CHAR = "CHAR";
	private final static String SQL_TYPE_NUMBER = "NUMBER";

	/**
	 * Stellt die Datenbank aus der SQL-Datei wieder her. - Alle Tabllen mit Inhalt
	 * ohne Nachfrage löschen. - Alle Tabellen wiederherstellen. - Tabellen mit
	 * Daten füllen.
	 * <p>
	 * Getestet mit MsSQL 12, MySql 8.0.8, Oracle 11g, Oracle 18 XE, PostgreSQL 11.
	 * <p>
	 * Das entsprechende Sql-Skript befindet sich im Ordner ./sql im Projekt.
	 * 
	 * @param connection Geöffnete Verbindung zu dem DBMS, auf dem die
	 *                   Bike-Datenbank wiederhergestellt werden soll.
	 */
	public static void reInitializeDB(Connection connection) {
		try {
			System.out.println("\nInitializing DB.");
			connection.setAutoCommit(true);
			String productName = connection.getMetaData().getDatabaseProductName();
			boolean isMsSql = productName.equals("Microsoft SQL Server");
			Statement statement = connection.createStatement();
			int numStmts = 0;

			// Liest den Inhalt der Datei ein.
			String[] fileContents = new String(Files.readAllBytes(Paths.get("src/sql/hska_oracle_bike.sql")),
					StandardCharsets.UTF_8).split(";");

			for (String sqlString : fileContents) {
				try {
					// Microsoft kenn den DATE-Operator nicht.
					if (isMsSql) {
						sqlString = sqlString.replace(", DATE '", ", '");
					}
					statement.execute(sqlString);
					System.out.print((++numStmts % 80 == 0 ? "/\n" : "."));
				} catch (SQLException e) {
					System.out.print("\n" + sqlString.replace('\n', ' ').trim() + ": ");
					System.out.println(e.getMessage());
				}
			}
			statement.close();
			System.out.println("\nBike database is reinitialized on " + productName + "\nat URL "
					+ connection.getMetaData().getURL());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int getMaxColumDisplaySize(ResultSetMetaData meta) {
		int max = 0;
		int tmp = 0;
		try {
			int columCount = meta.getColumnCount();
			for (int index = 1; index <= columCount; index++) {
				tmp = meta.getColumnDisplaySize(index);
				if (tmp > max)
					max = tmp;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return max;
	}

	public static String getResult(Connection connection, String sqlQuery) 
			throws SQLException {
		List<String> resultString = new ArrayList<String>();
		String result = new String();
		connection.setAutoCommit(false);
		Statement stmt = connection.createStatement();
		ResultSet resultSet = stmt.executeQuery(sqlQuery);
//    	connection.commit();
		ResultSetMetaData meta = resultSet.getMetaData();
		int columCount = meta.getColumnCount();
		int columIndex = 1;
		String columTypeName = new String();
		String columLabel = new String();
		String columSeperator = "|  ";
		String seperatorLine = new String();
//    	int colWidth = getMaxColumDisplaySize(meta);
		int colWidth;
		for (int i = 1; i <= columCount; i++) {
			colWidth = meta.getColumnDisplaySize(i);

			seperatorLine += String.format("%-" + colWidth + 
					"s", "-").replace(' ', '-');
			if (i < columCount)
				seperatorLine += "+--";

			columLabel += String.format("%-" + colWidth + "s",
					meta.getColumnName(i).toLowerCase());
			if (i < columCount)
				columLabel += columSeperator;

			columTypeName += String.format("%-" + colWidth + "s",
					meta.getColumnTypeName(i).toLowerCase());
			if (i < columCount)
				columTypeName += columSeperator;
		}

		System.out.println(columLabel);
		System.out.println(columTypeName);
		System.out.println(seperatorLine);
		resultString.add(columLabel);
		resultString.add(columTypeName);
		resultString.add(seperatorLine);
		
		if (columCount != 0) {
			while (resultSet.next()) {
				String line = new String();
				for (int i = 1; i <= columCount; i++) {
					colWidth = meta.getColumnDisplaySize(i);
					switch (meta.getColumnTypeName(i)) {
					case SQL_TYPE_CHAR:
						line += String.format("%-" + colWidth + "s", 
								resultSet.getString(i));
						if (i < columCount)
							line += columSeperator;
						break;
					case SQL_TYPE_NUMBER:
						line += String.format("%" + colWidth + "d", 
								resultSet.getInt(i));
						if (i < columCount)
							line += columSeperator;
						break;
					default:
						break;
					}
				}
				System.out.println(line);
				resultString.add(line);
			}
		}
		for (String string : resultString) {
			result += string + "\n";
		}

		stmt.close();
		return result;
	}

	public static List<String> createSQLStatements(List<String> lines) {
		List<String> result = new ArrayList<String>();

		return result;
	}

	/**
	 * @deprecated
	 * @param connection
	 * @param sqlQuery
	 * @return
	 * @throws SQLException
	 */
//    public static List<String> aufgabe1(Connection connection) throws SQLException {
//    	List<String> resultString = new ArrayList<String>();
//    	String sqlQuery = "SELECT persnr, name, ort, aufgabe FROM personal";
//    	Statement stmt = connection.createStatement();
//    	ResultSet resultSet = stmt.executeQuery(sqlQuery);
//    	ResultSetMetaData meta = resultSet.getMetaData();
//    	String columLabel = meta.getColumnLabel(1) + " | " + meta.getColumnLabel(2)+
//    			" | " + meta.getColumnLabel(3) + " | " + meta.getColumnLabel(4);
//    	String columTypeName = meta.getColumnTypeName(1) + " | " + meta.getColumnTypeName(2)+
//    			" | " + meta.getColumnTypeName(3) + " | " + meta.getColumnTypeName(4);
//    	resultString.add(columLabel);
//    	resultString.add(columTypeName);
//    	System.out.println(columLabel);
//    	System.out.println(columTypeName);
//    	while(resultSet.next()) {	
//    		int persnr = resultSet.getInt(1);
//    		String name = resultSet.getString(2);
//    		String ort = resultSet.getString(3);
//    		String aufgabe = resultSet.getString(4);
//    		String row = persnr + " | " + name + " | " + ort + " | " + aufgabe;
//    		resultString.add(row);
//    		System.out.println(persnr + " | " + name + " | " + ort + " | " + aufgabe);
//    	}
//    	return resultString;
//    }

}
