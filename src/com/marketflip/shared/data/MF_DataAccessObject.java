package com.marketflip.shared.data;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;

import com.marketflip.shared.products.MF_Price;
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
	private HashSet<String> 				productSet 			= new HashSet<String> ();
	private static MF_DataAccessObject 		MF_DAO				= null;
	private ArrayList<MF_Product>			productsToCommit 	= new ArrayList<MF_Product>();
	
	
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
	 * Adds a product to the list of products to be committed.
	 * @param product
	 * 	The product to be committed.
	 * @return
	 * 	Returns true if product passes validation and is not already in the products to commit.
	 */
	public boolean addProductToCommit (MF_Product product) {
		
		if (!MF_ProductValidator.validate().Product(product)) {
			return false;
		} else if (isInCommitList(product)) {
			return false;
		}
		productsToCommit.add(product);
		return true;
	}
	
	/**
	 * Insert a new Product into the database.
	 * @param MF_Product Product to be inserted
	 * @throws SQLException 
	 */
	public boolean insertProduct(MF_Product product) throws SQLException {

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
			String				company			= product.getCompany();
			double 				height			= product.getHeight();
			double 				width			= product.getWidth();
			double 				length			= product.getLength();
			double 				weight			= product.getWeight();
			URL					linkToProduct	= product.getLinkToProduct();
			ArrayList<MF_Price> priceList		= product.getPrices();
			double 				lowestPrice 	= product.getCurrentLowestPrice().getPrice();
			
			
			
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
			
			create_price_table 		= connection.createStatement();
			create_info_table 		= connection.createStatement();
			insert_product_statement= connection.createStatement();
			insert_price_statement	= connection.prepareStatement(sql_insert_price);
			insert_info_statement	= connection.prepareStatement(sql_insert_info);
			
			
			
			//Find the lowest price from all of the prices listed and set price batches.
			for (MF_Price price : priceList){
				
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
	
	private boolean isInCommitList(MF_Product product) {
		return true;
	}

}
