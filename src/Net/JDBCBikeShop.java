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

	/**
	 * SQL Types
	 */
	private final static String SQL_TYPE_CHAR = "CHAR";
	private final static String SQL_TYPE_NUMBER = "NUMBER";
	private final static String SQL_TYPE_DATE = "DATE";

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
			String[] fileContents = new String(Files.readAllBytes(Paths.get("src/sql/hska_bike.sql")),
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

	/**
	 * 
	 * @param connection of the Database
	 * @param sqlQuery to execute
	 * @return the Result of the SQL Statement
	 * @throws SQLException
	 */
	public static String getResult(Connection connection, String sqlQuery) 
			throws SQLException {
		List<String> resultString = new ArrayList<String>();
		String result = new String();
		String columTypeName = new String();
		String columLabel = new String();
		String columSeperator = "|  ";
		String seperatorLine = new String();
		int colWidth;

		//Creates Statement and executing a sqlQuery
		Statement stmt = connection.createStatement();
		ResultSet resultSet = stmt.executeQuery(sqlQuery);
		
		//Metadata, for Colum,-types, names and maxwidth
		ResultSetMetaData meta = resultSet.getMetaData();
		int columCount = meta.getColumnCount();

		//Iterate trough all Column to get the colum names and types
		for (int i = 1; i <= columCount; i++) {
			
			//Get the current Columnwidth
			colWidth = meta.getColumnDisplaySize(i);

			//Creates the bottom seperatorline for the current column
			seperatorLine += String.format("%-" + colWidth + 
					"s", "-").replace(' ', '-');
			if (i < columCount)
				seperatorLine += "+--";

			//Creates the Name of the Column
			columLabel += String.format("%-" + colWidth + "s",
					meta.getColumnName(i).toLowerCase());
			if (i < columCount)
				columLabel += columSeperator;

			//Creates the Type of the Column as String
			columTypeName += String.format("%-" + colWidth + "s",
					meta.getColumnTypeName(i).toLowerCase());
			if (i < columCount)
				columTypeName += columSeperator;
		}
		
		//Output on console
		System.out.println(columLabel);
		System.out.println(columTypeName);
		System.out.println(seperatorLine);
		
		//Adding the rows to a Result List
		resultString.add(columLabel);
		resultString.add(columTypeName);
		resultString.add(seperatorLine);
		
		//Check if there are Columns to iterate through
		if (columCount != 0) {
			//Iterate through all rows
			while (resultSet.next()) {
				String line = new String();
				
				//Go through all columns in the current row
				for (int i = 1; i <= columCount; i++) {
					
					//Getting the specific max size of the column
					colWidth = meta.getColumnDisplaySize(i);
					
					//Check the Type of the current column
					//and build the row together
					switch (meta.getColumnTypeName(i)) {
					case SQL_TYPE_CHAR:
						
						//Left bounded, length colWidth, String specifier
						line += String.format("%-" + colWidth + "s", 
								resultSet.getString(i));
						
						//Adds a Seperator between the column
						//if the current column isn't the last one
						if (i < columCount)
							line += columSeperator;
						break;

					case SQL_TYPE_NUMBER:
						
						//Right bounded, length colWidth, decimal specifier
						line += String.format("%" + colWidth + "d", 
								resultSet.getInt(i));

						//Adds a Seperator between the column
						//if the current column isn't the last one
						if (i < columCount)
							line += columSeperator;
						break;
					case SQL_TYPE_DATE:
						//Left bounded, length colWidth, String specifier
						line += String.format("%-" + colWidth + "s",
								resultSet.getDate(i));
						
						//Adds a Seperator between the column
						//if the current column isn't the last one
						if(i < columCount)
							line += columSeperator;
						break;
					default: break;
					}
				}
				System.out.println(line);

				//Adds the row to the result List
				resultString.add(line);
			}
		}
		//Adds a linebreak at every end of line
		for (String string : resultString) {
			result += string + "\n";
		}

		//clean up and return the List of rows.
		stmt.close();
		resultSet.close();
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
