package com.marketflip.shared.data;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Date;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.validator.routines.checkdigit.EAN13CheckDigit;

import com.marketflip.shared.products.MF_Price;
import com.marketflip.shared.products.MF_Product;

/**
 * @author David Walters
 * The library to access the products CloudSQL instance using google's Cloud SQL API.
 * SINGLETON class- not meant to be instantiated more than once to prevent overflow/multiple connections open at once.
 * Updated - 11/8/2015
 * 
 */

/*
 * TODO: 
 * 	-BUGS: Get Data functionality working.
 *  -FEATURES: End the shame of providing personal credentials.
 *
 */

public class MF_DatabaseAccessObject {
	
	private static final String IP 			= ***REMOVED***;
	private static final String URL 		= "jdbc:mysql://" + IP + ":3306/PRODUCTS";
	private static final String USERNAME 	= ***REMOVED***;   //SHAME!! TODO: End the shame.
	private static final String PASSWORD	= ***REMOVED***;       //SHAME!! TODO: End the shame.
	
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
								
	private Connection 						connection;
	private HashSet<String> 				productSet 	= new HashSet<String> ();
	private static MF_DatabaseAccessObject 	MF_DAO		= new MF_DatabaseAccessObject();
	
	
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
	
	public boolean insertProduct (MF_Product product) throws SQLException {
		
		if (MF_DatabaseAccessObject.MF_DAO == null) {
			System.err.println("ERROR: Connection has not been created.");
			return false;
		} else if (product.getUPC() == null) {
			System.err.println("ERROR: UPC cannot be null.");
			return false;
		} else if (!EAN13CheckDigit.EAN13_CHECK_DIGIT.isValid(product.getUPC())){
			System.err.println("ERROR: UPC cannot be resolved as EAN/ISBN-13/UPC compliant.");
			return false;
		} else if (productSet.contains(product.getUPC())) {
			System.err.println("ERROR: Product already exists in database");
			return false;
		}
		
		try {
			
			String				sql_create_info_table;
			String				sql_create_price_table;
			String				sql_insert_info;
			String				sql_insert_price;
			String				sql_insert_product;
			
			Statement			create_info_table;
			Statement			create_price_table;
			Statement			insert_product_statement;
			PreparedStatement	insert_info_statement;
			PreparedStatement	insert_price_statement;
			
			String 				upc				= product.getUPC();
			String 				name			= product.getName();
			String 				UNSPSC			= product.getUNSPSC();
			String 				description		= product.getDescription();
			//This is broken at the moment. Logged in Asana.
			//String				company			= product.getCompany();
			//Developing with company 1 (Walmart)
			String	company = "1";
			double 				height			= product.getHeight();
			double 				width			= product.getWidth();
			double 				length			= product.getLength();
			double 				weight			= product.getWeight();
			URL					linkToProduct	= product.getLinkToProduct();
			ArrayList<MF_Price> priceList		= product.getPrices();
			double 				lowestPrice 	= 0.0;
			
			
			// Prepare statements.
			sql_create_info_table	 =		"CREATE TABLE UPC_" + upc + "_INFO ("
										+	" COMPANY			int(11)			NOT NULL, "
										+	" UPC				varchar(45)		NOT NULL,"
										+ 	" NAME				varchar(100)	DEFAULT NULL,"
										+ 	" HEIGHT			varchar(15)		DEFAULT NULL,"
										+	" WIDTH				varchar(15)		DEFAULT	NULL,"
										+ 	" LENGTH			varchar(15)		DEFAULT NULL,"
										+ 	" DESCRIPTION		varchar(300)	DEFAULT NULL,"
										+	" URL				varchar(300)	DEFAULT NULL,"
										+	" CURRENT_PRICE		varchar(45)		DEFAULT NULL,"
										+	" KEY UPC_FK_idx		(UPC),"
										+ 	" KEY COMPANY_FK_idx	(COMPANY),"
										+ 	" CONSTRAINT COMPANY_FK_" + upc + " FOREIGN KEY (COMPANY) REFERENCES COMPANIES"
										+ 	" (COMPANY_ID) ON DELETE NO ACTION ON UPDATE NO ACTION,"
										+	" CONSTRAINT UPC_FK_" + upc + " FOREIGN KEY (UPC) REFERENCES PRODUCTS(UPC)"
										+	" ON DELETE NO ACTION ON UPDATE NO ACTION ); ";
			
			sql_create_price_table	=		"CREATE TABLE UPC_" + upc + "_PRICE ("
										+	" DATE 						datetime 	NOT NULL DEFAULT CURRENT_TIMESTAMP,"
										+	" COMPANY_" + company + " 	double 		DEFAULT NULL,"
										+	" UNIQUE KEY DATE_UNIQUE_" + upc + " (DATE) ); ";
					
			sql_insert_product =			"INSERT INTO PRODUCTS.PRODUCTS "
										+ 	" (UPC, DEPRECATED) "
										+ 	" VALUES (" + upc + ", 0);";
			
			sql_insert_info =				"INSERT INTO PRODUCTS.UPC_" + upc + "_INFO "
										+	" (COMPANY, UPC, NAME, HEIGHT, WIDTH, LENGTH, DESCRIPTION, URL, CURRENT_PRICE) "
										+	"VALUES (?,?,?,?,?,?,?,?,?); ";
			
			sql_insert_price =				"INSERT INTO PRODUCTS.UPC_" + upc + "_PRICE "
										+	" (DATE, COMPANY_" + company + ") "
										+	" VALUES (?,?);";
					
			// Concat all statements into one statement and prepare it.
//			sql_statement = (sql_create_info_table + sql_create_price_table + sql_insert_info + sql_insert_price);
//			insert_statement = connection.prepareStatement(sql_statement);
			
			create_price_table 		= connection.createStatement();
			create_info_table 		= connection.createStatement();
			insert_product_statement= connection.createStatement();
			insert_price_statement	= connection.prepareStatement(sql_insert_price);
			insert_info_statement	= connection.prepareStatement(sql_insert_info);
			
			
			
			//Find the lowest price from all of the prices listed and set price batches.
			for (MF_Price price : priceList){
				if (price.getPrice() < lowestPrice) {
					lowestPrice = price.getPrice();
				}
				
				java.sql.Date convertedDate = dateToSQLDate(price.getDate());
				insert_price_statement.setDate(1, convertedDate);
				insert_price_statement.setDouble(2, price.getPrice());
				insert_price_statement.addBatch();
			}

			// Set the values for the info table.
			insert_info_statement.setString(1, company);
			insert_info_statement.setString(2, upc);
			insert_info_statement.setString(3, name);
			insert_info_statement.setDouble(4, height);
			insert_info_statement.setDouble(5, width);
			insert_info_statement.setDouble(6, length);
			insert_info_statement.setString(7, description);
			insert_info_statement.setString(8, linkToProduct.toExternalForm());
			insert_info_statement.setDouble(9, lowestPrice);		
			
			// Execute statements.
			create_info_table.executeUpdate(sql_create_info_table);
			create_price_table.executeUpdate(sql_create_price_table);
			insert_product_statement.executeUpdate(sql_insert_product);
			insert_info_statement.executeUpdate();
			insert_price_statement.executeBatch();
			
			// Close the statements.
			create_info_table.close();
			create_price_table.close();
			insert_product_statement.close();
			insert_info_statement.close();
			insert_price_statement.close();

			connection.commit();
			productSet.add(upc);
			return true;
			
			
		} catch (Exception e) {
			System.err.println("ERROR: Failed to insert new product: ");
			e.printStackTrace();
			connection.rollback();
			return false;
		}
		
	}
	
	public MF_Product getProductByUPC (String upc) throws SQLException {
		
		if (MF_DatabaseAccessObject.MF_DAO == null) {
			System.err.println("ERROR: Connection has not been created.");
			return new MF_Product();
		} else if (upc == null) {
			System.err.println("ERROR: UPC cannot be null.");
			return new MF_Product();
		} else if (!EAN13CheckDigit.EAN13_CHECK_DIGIT.isValid(upc)){
			System.err.println("ERROR: UPC cannot be resolved as EAN/ISBN-13/UPC compliant.");
			return new MF_Product();
		} else if (!(productSet.contains(upc))) {
			System.err.println("ERROR: Product does not exist in database");
			return new MF_Product();
		}
		
		try {
			
			String 				sql_info;
			String				sql_price;
			String				companyColumnString;
			String				companyID;
			Statement 			info_statement;
			Statement			price_statement;
			ResultSet			product_info_results;
			ResultSet			product_price_results;
			ResultSetMetaData	priceRSdata;
			ArrayList<String>	companiesList			= new ArrayList<String>();
			MF_Product			product;
			
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
			
			//Create list of companies.
			//First column (index 1) is always 'DATE'. Therefore we want all columns after that.
			priceRSdata = product_price_results.getMetaData();
			for (int i = 2; i < priceRSdata.getColumnCount()+1; i++) {
				companyColumnString = priceRSdata.getColumnLabel(i);
				companyColumnString.trim();
				int beginIndex = companyColumnString.indexOf('_')+1;
				companyID = companyColumnString.substring(beginIndex);
				companiesList.add(companyID);
			}
			
			String companyColumnLabel;
			while (product_price_results.next()) {
				date = new Date();
				
				//date = product_info_results.getDate(PRICE_DATE_INDEX);
				
				for (String company : companiesList) {
					companyColumnLabel = "COMPANY_".concat(company);
					price = product_price_results.getDouble(companyColumnLabel);
					if (!(product_price_results.wasNull())) {
						priceList.add(new MF_Price(price, date, company));
					}
				}
			}
			
			product = new MF_Product (name, description, upc_result, UNSPSC, linkToProduct, priceList, height, width, length, weight);
			info_statement.close();
			price_statement.close();
			
			connection.commit();
			return product;
		} catch (Exception e) {
			System.err.println("ERROR: Unabe to retrieve from database instance.");
			e.printStackTrace();
			connection.rollback();
			return new MF_Product();
		}	
		
	}
	
	/**
	 * 
	 * @param String UPC The UPC of the product that needs to be checked.
	 * @return boolean Whether the product exists in the database.
	 */
	public boolean productExists (String UPC) {
		return (productSet.contains(UPC));
	}
	
	private java.sql.Date dateToSQLDate(java.util.Date date) {
	    return new java.sql.Date(date.getTime());
	}
}
