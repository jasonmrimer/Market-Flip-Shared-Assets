package com.marketflip.shared.data;

import java.sql.*;

/*
 * @author David Walters
 * The library to access the products CloudSQL instance using google's Cloud SQL API.
 * Updated - 10/17/2015
 */

public class MF_DatabaseAccessObject {
	private static final String IP = ***REMOVED***;                         //The IP address of the products database
	private static final String URL = "jdbc:google:mysql://marketflip-crawler"  //The URL of the database.
			+ ":products/products?user=root";
	private Connection connection;
	
	public MF_DatabaseAccessObject () {
		try {
			Class.forName("com.mysql.jdbc.Driver"); //TODO: Broken reference. Figure out how to load driver.
			connection = DriverManager.getConnection(URL);
		}
		catch (Exception e) {
			System.out.println("Error connecting to CloudSQL: ");
			e.printStackTrace();
		}
	}
}
