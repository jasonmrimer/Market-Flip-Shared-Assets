package com.marketflip.shared.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

public class MF_DataAccessObject {
	
	protected static final String PRODUCTION_IP 			= ***REMOVED***;
	protected static final String TESTING_IP				= ***REMOVED***;
	protected static final String PRODUCTION_URL 			= "jdbc:mysql://" + PRODUCTION_IP + ":3306/PRODUCTS";
	protected static final String TESTING_URL 			= "jdbc:mysql://" + TESTING_IP + ":3306/PRODUCTS";
	protected static final String USERNAME 				= ***REMOVED***;   //SHAME!! TODO: End the shame.
	protected static final String PASSWORD				= ***REMOVED***;       //SHAME!! TODO: End the shame.
	
	protected static final int 	
		INFO_COMPANY_INDEX  		= 1,
		INFO_UPC_INDEX				= 2,
		INFO_NAME_INDEX				= 3,
		INFO_HEIGHT_INDEX			= 4,
		INFO_WIDTH_INDEX			= 5,
		INFO_LENGTH_INDEX			= 6,
		INFO_DESCRIPTION_INDEX 		= 7,
		INFO_URL_INDEX				= 8,
		INFO_CURRENT_PRICE_INDEX	= 9;
	
	protected static final int	
		PRICE_DATE_INDEX			= 1,
		PRICE_COMPANY_INDEX			= 2;
	
	private String 			environment;
	private String			childType;
	protected Connection 	connection;
	
	/**
	 * Constructor for a DAO object.
	 * @param environment - The environment this is to be ran in. Production or testing.
	 * @param childType - The child type, Insert or Get
	 */
	public MF_DataAccessObject (String environment, String childType) {
		
		if (!environment.equals("production") && (!environment.equals("testing"))){
			System.err.println("ERROR: Invalid environment, using test mode by default.");
			this.environment = "testing";
		} else {
			this.environment = environment;
		}
		if (childType != null) {
			this.childType = childType;
		}
		
		try {
			
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			Class.forName("com.mysql.jdbc.Driver");
			
			if (environment.equals("production")) {
				connection = DriverManager.getConnection(PRODUCTION_URL, USERNAME, PASSWORD);
				System.out.println("Using production environment.");
			} else if (environment.equals("testing")) {
				connection = DriverManager.getConnection(TESTING_URL, USERNAME, PASSWORD);
				System.out.println("Using test environment.");
			}
			
			connection.setAutoCommit(false);
		}
		catch (Exception e) {
			System.err.println("Error connecting to CloudSQL: ");
			e.printStackTrace();
		}
	}

	/**
	 * Attempts to close the Connection for this instance.
	 * The MF_DatabaseAccessObject should always be closed when the program is done using
	 * it.
	 */
	public void close() {
		
		try {
			this.connection.close();
		} catch (Exception e) {
			System.err.println ("Error closing connection: ");
			e.printStackTrace();
		}
	}
	
	public String getEnvironment () {
		return environment;
	}
	
	/**
	 * Returns if the connection to the database is open.
	 * @return Boolean True if the connection is open.
	 * @throws SQLException 
	 */
	public boolean isOpen() throws SQLException {
		if (connection.isClosed()) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * If this class has a child class, returns the child type. If No child class, returns 'master'.
	 * @return String the child node type.
	 */
	public String getChildType() {
		return childType;
	}
	
	/**
	 * Utility class to convert util.Date object to sql.Date object.
	 * @param date The java.util.Data object to convert.
	 * @return date The converted java.sql.Date object.
	 */
	protected java.sql.Date dateToSQLDate(java.util.Date date) {
	    return new java.sql.Date(date.getTime());
	}
	
	/**
	 * Returns a list of UPCs in the database.
	 * @return HashSet<String> UPCs in the database.
	 * @throws SQLException 
	 */
	public HashSet<String> getUpdatedProductList() throws Exception {
		
		String 		sql;
		Statement 	sqlStatement;
		ResultSet	rs;
		HashSet<String> productSet = new HashSet<String>();
		
		sql = "SELECT UPC FROM PRODUCTS";
		sqlStatement = connection.createStatement();
		rs = sqlStatement.executeQuery(sql);
		
		if (!rs.next()) {
			throw new Exception();
		}
		
		while (rs.next()) {
			String upc = rs.getString("UPC");
			productSet.add(upc);
		}
		
		return productSet;
	}

}
