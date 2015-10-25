package com.marketflip.shared.data;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.validator.routines.checkdigit.EAN13CheckDigit;

import com.marketflip.shared.products.MF_Product;
import com.marketflip.shared.products.MF_Price;
/*
 * @author David Walters
 * The library to access the products CloudSQL instance using google's Cloud SQL API.
 * SINGLETON class- not meant to be instantiated more than once to prevent overflow/multiple connections open at once.
 * Updated - 10/25/2015
 * 
 * TODO: -Product exists? 
 * 		 -Get Product
 * 		 -if not create new Products
 * 		 -Update product info and product price
 * 		 -Weight???
 * 		 -UNSPSC???
 * 		 -Handle the company issue.. sleep on it.
 * 		 -Create specific database user for the crawler and json server. 
 * 		       REQUIRE application to set its own credentials.
 */

public class MF_DatabaseAccessObject {
	private static final String IP 	= ***REMOVED***;
	private static final String URL = "jdbc:mysql://" + IP + ":3306/PRODUCTS";
	private static final String USERNAME = ***REMOVED***;   //SHAME!! TODO: End the shame.
	private static final String PASSWORD = ***REMOVED***;       //SHAME!! TODO: End the shame.
	private static MF_DatabaseAccessObject MF_DAO = new MF_DatabaseAccessObject();
	
	private static final int 	INFO_COMPANY_INDEX  		= 1,
								INFO_UPC_INDEX				= 2,
								INFO_NAME_INDEX				= 3,
								INFO_HEIGHT_INDEX			= 4,
								INFO_WIDTH_INDEX			= 5,
								INFO_LENGTH_INDEX			= 6,
								INFO_DESCRIPTION_INDEX 		= 7,
								INFO_URL_INDEX				= 8,
								INFO_CURRENT_PRICE_INDEX	= 9;
	
	private static final int	PRICE_DATE_INDEX			= 1,
								PRICE_COMPANY_INDEX			= 2;
								
	private Connection connection;
	
	
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
			MF_DatabaseAccessObject.MF_DAO = null;
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
			//connection = DriverManager.getConnection(URL);
			connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		}
		catch (Exception e) {
			System.err.println("Error connecting to CloudSQL: ");
			e.printStackTrace();
		}
	}
	
	public MF_Product getProductByUPC (String upc) {
		
		if (MF_DatabaseAccessObject.MF_DAO == null) {
			System.err.println("ERROR: Connection has not been created.");
			return new MF_Product();
		} else if (upc == null) {
			System.err.println("ERROR: UPC cannot be null.");
			return new MF_Product();
		} else if (!EAN13CheckDigit.EAN13_CHECK_DIGIT.isValid(upc)){
			System.err.println("ERROR: UPC cannot be resolved as EAN/ISBN-13/UPC compliant.");
			return new MF_Product();
		}
		
		try {
			
			String 		sql_info;
			String		sql_price;
			Statement 	info_statement;
			Statement	price_statement;
			ResultSet	product_info_results;
			ResultSet	product_price_results;
			MF_Product	product;
			
			String 				upc_result		= null;
			String 				name			= null;
			String 				UNSPSC			= null;
			String 				description		= null;
			double 				height			= 0.0;
			double 				width			= 0.0;
			double 				length			= 0.0;
			double 				weight			= 0.0;
			URL					linkToProduct	= null;
			ArrayList<MF_Price> priceList		= new ArrayList<MF_Price>();
			Date				date			= null;
			double				price			= 0.0;
			
			
			sql_info = "SELECT * FROM UPC_" + upc + "_INFO";
			sql_price = "SELECT * FROM UPC_" + upc + "_PRICE";
			info_statement = connection.createStatement();
			price_statement = connection.createStatement();
			
			product_info_results = info_statement.executeQuery(sql_info);
			product_price_results = price_statement.executeQuery(sql_price);
			
			while (product_info_results.next()) {
				name 			= product_info_results.getString(INFO_NAME_INDEX);
				description 	= product_info_results.getString(INFO_DESCRIPTION_INDEX);
				upc_result 		= product_info_results.getString(INFO_UPC_INDEX);
				UNSPSC 			= null; // TODO: This is not held in the database (YET);
				String URL 		= product_info_results.getString(INFO_URL_INDEX);
				linkToProduct 	= new URL(URL);
				height 			= product_info_results.getDouble(INFO_HEIGHT_INDEX);
				width 			= product_info_results.getDouble(INFO_WIDTH_INDEX);
				length 			= product_info_results.getDouble(INFO_LENGTH_INDEX);
				weight 			= 0.0f; // TODO: This is not held in the database (YET);
			}
			
			while (product_price_results.next()) {
				//String dateString = product_info_results.getString(PRICE_DATE_INDEX);
				date = new Date();
				/**
				 * TODO: Need another way to handle this. The column is labeled
				 * by COMPANY_ + the Primary Key of the company in the table. Perhaps
				 * the COMPANY table needs to be queried first. This presents a problem
				 * because it will cost a lot of extra data. Sleep on this.
				 */
				price = product_price_results.getDouble("COMPANY_1");
				priceList.add(new MF_Price(price, date, "1"));
			}
			
			product = new MF_Product (name, description, upc_result, UNSPSC, linkToProduct, priceList, height, width, length, weight);
			
			info_statement.close();
			price_statement.close();
			
			return product;
		} catch (Exception e) {
			System.err.println("ERROR: Unabe to retrieve from database instance.");
			e.printStackTrace();
			return new MF_Product();
		}	
		
	}
	
}
