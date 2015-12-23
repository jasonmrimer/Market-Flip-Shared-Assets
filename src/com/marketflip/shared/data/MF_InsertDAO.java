/**
 * Insert products to the database using this class ,extended from MF_DataAccessObject
 * @author David Walters
 * Last Updated: 12/22/2015
 */

package com.marketflip.shared.data;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.marketflip.shared.products.MF_Price;
import com.marketflip.shared.products.MF_Product;
import com.marketflip.shared.products.util.MF_ProductValidator;

public class MF_InsertDAO extends MF_DataAccessObject {
		
	private ArrayList<MF_Product>			productsToCommit 	= new ArrayList<MF_Product>();
	
	public MF_InsertDAO(String environment) {
		super(environment, "Insertion");
	}
	
	/**
	 * Adds a product to the list of products to be committed.
	 * @param product
	 * 	The product to be committed.
	 * @return
	 * 	Returns true if product passes validation and is not already in the products to commit.
	 */
	public boolean addProductToCommit (MF_Product product) {
		
		if (productsToCommit.size() == 20) {
			System.err.println("ERROR: Maximum number of products to be committed at once completed.");
			return false;
		}
		
		if (isInCommitList(product)){
			System.err.println("ERROR: Product is already in queue to be committed.");
			return false;
		}
		
		if (!MF_ProductValidator.validate().Product(product)) {
			return false;
		} else if (isInCommitList(product)) {
			return false;
		}
		productsToCommit.add(product);
		return true;
	}
	
	/**
	 * Commits the list of products to the database.
	 */
	public boolean commitProductsToDatabase () {
		
		if (productsToCommit.isEmpty()) {
			System.err.println("ERROR: No products to commit to database.");
			return false;
		}
		try {
			for (MF_Product product : productsToCommit) {
					if (!insertProduct(product)) {
						super.connection.rollback();
						return false;
					}
			}
			super.connection.commit();
		} catch (SQLException e) {
			System.err.println("ERROR: Insertion of products failed: ");
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * Insert a new Product into the database.
	 * @param MF_Product Product to be inserted
	 * @throws SQLException 
	 */
	private boolean insertProduct(MF_Product product) throws SQLException {

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
			
			
			// Mandatory parameters to be entered. No checking necessary.
			String 				upc				= product.getUPC();
			ArrayList<MF_Price> priceList		= product.getPrices();
			
			
			// The following parameters are not necessary to insert but should be set to something
			// Either "" or 0 depending on variable type.
			String				name;
			if (product.getName() == null) {
				name = "_";
			} else {
				name = product.getName();
			}
			
			String 				UNSPSC;
			if (product.getUNSPSC() == null) {
				UNSPSC = "_";
			} else {
				UNSPSC = product.getUNSPSC();
			}
			
			String description;
			if (product.getDescription() == null) {
				description = "_";
			} else {
				description = product.getDescription();
			}
			
			double 				height			= product.getHeight();
			double 				width			= product.getWidth();
			double 				length			= product.getLength();
			
			URL linkToProduct;
			if (product.getLinkToProduct() == null) {
				linkToProduct = new URL("http://www.google.com"); //TODO: What should be done here?
				
			} else {
				linkToProduct = product.getLinkToProduct();
			}
			String				company 		= "1"; //TODO: This is broken :( We need to discuss company in the database for PROTO 2.
			double 				lowestPrice 	= product.getCurrentLowestPrice().getPrice();		
			
			// Prepare statements.
			sql_create_info_table	 =		"CREATE TABLE UPC_" + upc + "_INFO ("
										+	" COMPANY			int(11)			DEFAULT NULL, "
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
										+	" COMPANY_" + company + " 	double 		DEFAULT NULL);";
					
			sql_insert_product =			"INSERT INTO PRODUCTS.PRODUCTS "
										+ 	" (UPC, DEPRECATED) "
										+ 	" VALUES ('" + upc + "', 0);";
			
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
			
			insert_product_statement.executeUpdate(sql_insert_product);
			
			
			
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

			insert_info_statement.executeUpdate();
			insert_price_statement.executeBatch();
			
			// Close the statements.
			create_info_table.close();
			create_price_table.close();
			insert_product_statement.close();
			insert_info_statement.close();
			insert_price_statement.close();
			
			super.getProductSet().add(product.getUPC());
			return true;
			
			
		} catch (Exception e) {
			System.err.println("ERROR: Failed to insert new product: ");
			e.printStackTrace();
			connection.rollback();
			return false;
		}
	}
	
	/**
	 * Checks to see if product is in productsToCommit
	 * @param product to check
	 * @return True if product is in the list.
	 */
	private boolean isInCommitList(MF_Product product) {
		for (MF_Product testProduct : getCommitList()) {
			if (testProduct.equals(product)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the current product list for the DAO of items not inserted to the database.
	 * @return ArrayList<MF_Product> The products awaiting insertion to database.
	 */
	public ArrayList<MF_Product> getCommitList () {
		return this.productsToCommit;
	}

}
