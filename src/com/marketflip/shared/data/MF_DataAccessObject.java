package com.marketflip.shared.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;

import com.marketflip.shared.products.MF_Product;
import com.marketflip.shared.products.util.MF_ProductValidator;

public class MF_DataAccessObject {
	
	private static final String IP 			= ***REMOVED***;
	private static final String URL 		= "jdbc:mysql://" + IP + ":3306/PRODUCTS";
	private static final String USERNAME 	= ***REMOVED***;   //SHAME!! TODO: End the shame.
	private static final String PASSWORD	= ***REMOVED***;       //SHAME!! TODO: End the shame.
	
	private static final int 	
		INFO_COMPANY_INDEX  		= 1,
		INFO_UPC_INDEX				= 2,
		INFO_NAME_INDEX				= 3,
		INFO_HEIGHT_INDEX			= 4,
		INFO_WIDTH_INDEX			= 5,
		INFO_LENGTH_INDEX			= 6,
		INFO_DESCRIPTION_INDEX 		= 7,
		INFO_URL_INDEX				= 8,
		INFO_CURRENT_PRICE_INDEX	= 9;
	
	private static final int	
		PRICE_DATE_INDEX			= 1,
		PRICE_COMPANY_INDEX			= 2;
	
	private Connection 						connection;
	private HashSet<String> 				productSet 	= new HashSet<String> ();
	private static MF_DataAccessObject 		MF_DAO		= null;
	
	public static void openConnection() {
		MF_DAO = new MF_DataAccessObject();
	}
	
	public static MF_DataAccessObject getDAO () {
		if (MF_DAO != null) {
			return MF_DAO;
		} else {
			return null;
		}
	}
	
	private MF_DataAccessObject () {
		try {
			
			String 		sql;
			Statement 	sqlStatement;
			ResultSet	rs;
			
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			
			connection.setAutoCommit(false);
			
			sql = "SELECT UPC FROM PRODUCTS";
			sqlStatement = connection.createStatement();
			rs = sqlStatement.executeQuery(sql);
			
			while (rs.next()) {
				String upc = rs.getString("UPC");
				productSet.add(upc);
			}
			
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
			MF_DataAccessObject.MF_DAO = null;
		} catch (Exception e) {
			System.err.println ("Error closing connection: ");
			e.printStackTrace();
		}
	}
	
	/**
	 * Insert a new Product into the database.
	 * @param MF_Product Product to be inserted
	 */
	public boolean insertProduct(MF_Product product) {
		
		if (!MF_ProductValidator.validate().Product(product)) {
			return false;
		}
		return true;
		
	}

}
