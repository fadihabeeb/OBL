/**
 * server package contains server side code layer and connection to the MySQL database
 */
package server;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import common.PDFfile;

/**
 * this class responsible about executing the queries from the clients
 * @author Saleh Kasem
 *
 */
public class MySQLConnection {
	final String DATABASE_URL = "jdbc:mysql://localhost/";
	private Connection conn;
	private String schema;

	/**
	 * MYSQLConnection constructor witch prepared the connection to DB 
	 * @param schema
	 * @param userName
	 * @param password
	 */
	public MySQLConnection(String schema, String userName, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			/* handle the error */}

		try {
			this.schema = schema;
			conn = DriverManager.getConnection(DATABASE_URL + schema, userName, password);
			System.out.println("SQL connection succeed");
		} catch (SQLException ex) {/* handle any errors */
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			System.exit(0);
		}
	}

	/**
	 * execute the queries arrived from client
	 * @param msg
	 * @return object
	 */
	@SuppressWarnings("unchecked")
	public Object executeQuery(Object msg) {
		String query;
		try {
			//if message arrived as ArrayList then extract the query from the last index
			if (msg instanceof ArrayList) {
				//the arrayList contains the data witch need to saved in DB and the query
				ArrayList<String> arr = (ArrayList<String>) msg;
				query = String.valueOf(arr.get(arr.size() - 1));
				if (query.startsWith("INSERT") || query.startsWith("UPDATE") || query.startsWith("DELETE")) {
					int i;
					PreparedStatement ps = conn.prepareStatement(query);
					for (i = 0; i < arr.size() - 1; i++) {
						ps.setString(i + 1, arr.get(i));
					}
					ps.executeUpdate();
					System.out.println("DB: Query => Executed Successfully");
					return null;
				} else
					return executeSelectQuery(msg);
				//handle String message
			} else if (msg instanceof String) {
				query = msg.toString();
				if (query.startsWith("INSERT") || query.startsWith("UPDATE") || query.startsWith("DELETE")) {
					Statement stmt = conn.createStatement();
					stmt.executeUpdate(query);
					System.out.println("DB: " + query + " => Executed Successfully");
					return null;
				} else
					return executeSelectQuery(msg);
			}

		} catch (SQLException sqlException) {
			System.out.println("Couldn't execute query");
			sqlException.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * execute only select queries
	 * @param msg
	 * @return object
	 */
	public Object executeSelectQuery(Object msg) {
		String query = msg.toString();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			System.out.println("DB: " + query + " => Executed Successfully");
			//return the result set as ArrayList
			return parseResultSet(rs);

		} catch (SQLException sqlException) {
			System.out.println("Couldn't execute query");
			sqlException.printStackTrace();
			return null;
		}
	}
	
	/**
	 * save the file in bookContentsFile table
	 * @param inputStream
	 * @param id
	 */
	public void updateFile(InputStream inputStream, Object msg) {
		String sql;
		if(((PDFfile)msg).getArrName().get(0).equals("&"))
			 sql = "UPDATE Book SET tableOfContents =? WHERE bookID = '" + ((PDFfile)msg).getValue() + "';";
		else {
			 sql = "INSERT INTO Reports(Date,report) VALUES  ('"+ ((PDFfile)msg).getValue() + "',?);";
		}
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setBinaryStream(1, inputStream);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parse database result set into an ArrayList with rows separated by commas
	 * 
	 * @param rs
	 * @return ArrayList<String>
	 */
	public ArrayList<String> parseResultSet(ResultSet rs) {
		ArrayList<String> arr = new ArrayList<>();
		int i;

		try {//convert the the rs to ArrayList 
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				i = 1;
				while (i <= rsmd.getColumnCount()) {
					arr.add(rs.getString(i++));
				}
			}
		} catch (SQLException Exception) {
			System.out.println("ERROR while parsing array!");
		}
		return arr;
	}
	
	/*
	 * execute only the file query
	 */
	public ResultSet executeFileQuery(ArrayList<String> arr) {
		String query;
		if(arr.get(arr.size()-1).equals("@"))
			query = "SELECT tableOfContents FROM Book WHERE bookID = '" + arr.get(0) + "';";
		else
			query = "SELECT report FROM Reports WHERE Date = '" + arr.get(0) + "';";
		
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			System.out.println("DB: " + query + " => Executed Successfully");
			//return it ass result set
			return rs;
		} catch (SQLException sqlException) {
			System.out.println("Couldn't execute query");
			sqlException.printStackTrace();
			return null;
		}
	}
	
}