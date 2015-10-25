package com.marketflip.shared.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import com.marketflip.shared.products.MF_Product;
import org.apache.commons.validator.routines.checkdigit.EAN13CheckDigit;
/*
 * @author David Walters
 * The library to access the products CloudSQL instance using google's Cloud SQL API.
 * SINGLETON class- not meant to be instantiated more than once to prevent overflow/multiple connections open at once.
 * Updated - 10/25/2015
 */

public class MF_DatabaseAccessObject {
	private static final String IP 	= ***REMOVED***;                         			//The IP address of the products database
	private static final String URL = "jdbc:mysql://" + IP + ":3306/products?user=root";
	
	private Connection connection;
	
	private static MF_DatabaseAccessObject MF_DAO = new MF_DatabaseAccessObject();
	
	public static MF_DatabaseAccessObject newInstance() {
		return MF_DAO;
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
	
	/**
	 * Instantiates a new MF_DatabaseAccessObject and establishes connection.
	 */
	private MF_DatabaseAccessObject () {
		
		try {
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(URL);
		}
		catch (Exception e) {
			System.err.println("Error connecting to CloudSQL: ");
			e.printStackTrace();
		}
	}
	
	private MF_Product getProductByUPC (String upc) {
		
		if (upc == null) {
			System.err.println("ERROR: UPC cannot be null.");
			return new MF_Product();
		}
		else if (!EAN13CheckDigit.EAN13_CHECK_DIGIT.isValid(upc)){
			System.err.println("ERROR: UPC cannot be resolved as EAN/ISBN-13/UPC compliant.");
			return new MF_Product();
		}
		
		
		String 				sql;
		PreparedStatement 	preparedStatement;
		
		sql = "SELECT * FROM UPC_" + upc + "_INFO";
		
		
		
		return new MF_Product ();
		
		
	}
	
}
