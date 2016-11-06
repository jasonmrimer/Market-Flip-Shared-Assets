/**
 * DAO specific to Products table.
 * 
 * @author David Walters
 *         Last Updated: 12/23/2015
 */

package com.marketflip.shared.data;

import java.net.URL;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import com.marketflip.shared.products.MF_Price;
import com.marketflip.shared.products.MF_Product;
import com.marketflip.shared.products.util.MF_ProductValidator;

public class MF_ProductsDAO extends MF_DataAccessObject {
	//testing the new setup
	private ArrayList<MF_Product>	productsToCommit		= new ArrayList<MF_Product>();
	private String					tableNameProducts		= "PRODUCTS";
	private String					columnNameUPC			= "UPC";
	private String					columnNameDeprecated	= "DEPRECATED";
	private boolean					reset;

	public MF_ProductsDAO(String environment) {
		super(environment, "Products");
		//		if (environment.equalsIgnoreCase("testing")) { // TODO put this in to use a blank slate; revised: made methods public to use when appropriate
		//			this.deleteAllTables();
		//			this.addProductsTable();
		//		}
	}

	public MF_ProductsDAO(String environment, boolean reset) {
		this(environment);
		this.reset = reset;
		if (reset) {
			this.deleteAllTables();
			this.addProductsTable();
		}
	}

	public void addProductsTable() {
		//		"INSERT INTO PRODUCTS.PRODUCTS " + " (UPC, DEPRECATED) "
		//				+ " VALUES ('" + upc + "', 0);";		
		String sqlString = "CREATE TABLE " + tableNameProducts + "(" + columnNameUPC
				+ " varchar(32)," + columnNameDeprecated + " int);";
		Statement sqlStatement = null;
		try {
			sqlStatement = connection.createStatement();
			sqlStatement.execute(sqlString);
		}
		catch (SQLException e) {
			System.err.println("SQLException in MF_ProductsDAO createProductsTable");
			e.printStackTrace();
		}
		finally {
			if (sqlStatement != null) {
				try {
					sqlStatement.close();
				}
				catch (SQLException e) {
					System.err.println("Caught error trying to close statment:");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Adds a product to the list of products to be committed.
	 * 
	 * @param product
	 *            The product to be committed.
	 * @return
	 * 		Returns true if product passes validation and is not already in the products to
	 *         commit.
	 */
	public boolean addProductToCommit(MF_Product product) {

		if (productsToCommit.size() == 20) {
			System.err.println(
					"ERROR: Maximum number of products to be committed at once completed.");
			return false;
		}

		if (isInCommitList(product)) {
			System.err.println("ERROR: Product is already in queue to be committed.");
			return false;
		}

		if (!MF_ProductValidator.validate().Product(product)) {
			return false;
		}
		else if (isInCommitList(product)) {
			return false;
		}
		productsToCommit.add(product);
		return true;
	}

	/**
	 * Commits the list of products to the database.
	 */
	public boolean commitProductsToDatabase() {

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
		}
		catch (SQLException e) {
			System.err.println("ERROR: Insertion of products failed: ");
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * The purpose of this method is to clear the entire database so testing can take place in a
	 * clean environment.
	 *
	 */
	public void deleteAllTables() {
		DatabaseMetaData dbMetaData;
		ResultSet rsTables = null;
		Statement sqlStatement = null;
		String sqlDisableForeignKeys = "SET FOREIGN_KEY_CHECKS=0;";
		String sqlEnableForeignKeys = "SET FOREIGN_KEY_CHECKS=1;";
		String sqlString;
		try {
			sqlStatement = connection.createStatement();
			sqlStatement.execute(sqlDisableForeignKeys);
			sqlStatement.close();
			dbMetaData = connection.getMetaData();
			String[] types = {"TABLE"};
			rsTables = dbMetaData.getTables(null, null, "%", types);
			while (rsTables.next()) {
				sqlString = "DROP TABLE " + rsTables.getString("TABLE_NAME");
				sqlStatement = connection.createStatement();
				sqlStatement.execute(sqlString);
				sqlStatement.close();
			}
			sqlStatement = connection.createStatement();
			sqlStatement.execute(sqlEnableForeignKeys);
			sqlStatement.close();
		}
		catch (SQLException e) {
			System.err.println("SQLException in MF_ProductsDAO deleteAllTables");
			e.printStackTrace();
		}
		finally {
			if (sqlStatement != null) {
				try {
					sqlStatement.close();
				}
				catch (SQLException e) {
					System.err.println("Error in ProductsDAO deleteAlltables closing sqlStatement");
					e.printStackTrace();
				}
			}
			if (rsTables != null) {
				try {
					rsTables.close();
				}
				catch (SQLException e) {
					System.err.println("Error in ProductsDAO deleteAlltables closing resultSet");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Insert a new Product into the database.
	 * 
	 * @param MF_Product
	 *            Product to be inserted
	 * @throws SQLException
	 */
	private boolean insertProduct(MF_Product product) throws SQLException {
		System.out.println("inside insertProduct");
		try {

			String sql_create_info_table;
			String sql_create_price_table;
			String sql_insert_info;
			String sql_insert_price;
			String sql_insert_product;

			Statement create_info_table;
			Statement create_price_table;
			Statement insert_product_statement;
			PreparedStatement insert_info_statement;
			PreparedStatement insert_price_statement;

			// Mandatory parameters to be entered. No checking necessary.
			String upc = product.getUPC();
			ArrayList<MF_Price> priceList = product.getPrices();

			// The following parameters are not necessary to insert but should be set to something
			// Either "" or 0 depending on variable type.
			String name;
			if (product.getName() == null) {
				name = "_";
			}
			else {
				name = product.getName();
			}

			String UNSPSC;
			if (product.getUNSPSC() == null) {
				UNSPSC = "_";
			}
			else {
				UNSPSC = product.getUNSPSC();
			}

			String description;
			if (product.getDescription() == null) {
				description = "_";
			}
			else {
				description = product.getDescription();
			}

			double height = product.getHeight();
			double width = product.getWidth();
			double length = product.getLength();

			URL linkToProduct;
			if (product.getLinkToProduct() == null) {
				linkToProduct = new URL("http://www.google.com"); //TODO: What should be done here?

			}
			else {
				linkToProduct = product.getLinkToProduct();
			}
			String company = "1"; //TODO: This is broken :( We need to discuss company in the database for PROTO 2.
			double lowestPrice = product.getCurrentLowestPrice().getPrice();

			// Prepare statements.
			sql_create_info_table = "CREATE TABLE UPC_" + upc + "_INFO ("
					+ " COMPANY			int(11)			DEFAULT NULL, "
					+ " UPC				varchar(45)		NOT NULL,"
					+ " NAME				varchar(100)	DEFAULT NULL,"
					+ " HEIGHT			varchar(15)		DEFAULT NULL,"
					+ " WIDTH				varchar(15)		DEFAULT	NULL,"
					+ " LENGTH			varchar(15)		DEFAULT NULL,"
					+ " DESCRIPTION		varchar(300)	DEFAULT NULL,"
					+ " URL				varchar(300)	DEFAULT NULL,"
					+ " CURRENT_PRICE		varchar(45)		DEFAULT NULL);";
			//					+ " KEY UPC_FK_idx		(UPC)," + " KEY COMPANY_FK_idx	(COMPANY);"; // TODO re-do foreign keys cuz broken
			//					+ " CONSTRAINT COMPANY_FK_" + upc
			//					+ " FOREIGN KEY (COMPANY) REFERENCES COMPANIES"
			//					+ " (COMPANY_ID) ON DELETE NO ACTION ON UPDATE NO ACTION,"
			//					+ " CONSTRAINT UPC_FK_" + upc + " FOREIGN KEY (UPC) REFERENCES PRODUCTS(UPC)"
			//					+ " ON DELETE NO ACTION ON UPDATE NO ACTION ); ";

			sql_create_price_table = "CREATE TABLE UPC_" + upc + "_PRICE ("
					+ " DATE 						datetime 	NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+ " COMPANY_" + company + " 	double 		DEFAULT NULL);"; // TODO added prices to this table

			sql_insert_product = "INSERT INTO PRODUCTS.PRODUCTS " + " (UPC, DEPRECATED) "
					+ " VALUES ('" + upc + "', 0);";

			sql_insert_info = "INSERT INTO PRODUCTS.UPC_" + upc + "_INFO "
					+ " (COMPANY, UPC, NAME, HEIGHT, WIDTH, LENGTH, DESCRIPTION, URL, CURRENT_PRICE) "
					+ "VALUES (?,?,?,?,?,?,?,?,?); ";

			sql_insert_price = "INSERT INTO PRODUCTS.UPC_" + upc + "_PRICE " + " (DATE, COMPANY_"
					+ company + ") " + " VALUES (?,?);";

			create_price_table = connection.createStatement();
			create_info_table = connection.createStatement();
			insert_product_statement = connection.createStatement();
			insert_price_statement = connection.prepareStatement(sql_insert_price);
			insert_info_statement = connection.prepareStatement(sql_insert_info);

			insert_product_statement.executeUpdate(sql_insert_product);

			//Find the lowest price from all of the prices listed and set price batches.
			for (MF_Price price : priceList) {
				System.out.println("inside insertProduct setting price " + price.toString());
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
			description = (description.length() > 256) ? description.substring(0, 255)
					: description; // TODO review - need to trim long descriptions or find a way to keep more data
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
			System.out.println("finishing insertProduct");
			return true;

		}
		catch (Exception e) {
			System.err.println("ERROR: Failed to insert new product: ");
			e.printStackTrace();
			connection.rollback();
			return false;
		}
	}

	/**
	 * Checks to see if product is in productsToCommit
	 * 
	 * @param product
	 *            to check
	 * @return True if product is in the list.
	 */
	private boolean isInCommitList(MF_Product product) {
		for (MF_Product testProduct : getCommitList()) {
			if (testProduct.equals(product)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the current product list for the DAO of items not inserted to the database.
	 * 
	 * @return ArrayList<MF_Product> The products awaiting insertion to database.
	 */
	public ArrayList<MF_Product> getCommitList() {
		return this.productsToCommit;
	}

	/**
	 * Get a product by unique identifier (UPC).
	 * To be expanded upon, can take a UNSCPC identifier, use regex to check.
	 * 
	 * @param UPC
	 *            The unique identifier to find the product.
	 * @return The full MF_Product object.
	 * @throws SQLException
	 */
	public MF_Product getProduct(String UPC) throws SQLException {

		if (UPC == null) {
			System.err.println("ERROR: Product cannot be null.");
			return null;
		}
		else if (!MF_ProductValidator.validate().UPC(UPC)) {
			System.err.println("ERROR: UPC is not valid.");
			return null;
		}
		try {

			String sql_info;
			String sql_price;
			String companyColumnString;
			String companyID;
			Statement info_statement;
			Statement price_statement;
			ResultSet product_info_results;
			ResultSet product_price_results;
			ResultSetMetaData priceRSdata;
			ArrayList<String> companiesList = new ArrayList<String>();
			MF_Product returnProduct;

			String upc_result = null;
			String name = null;
			String UNSPSC = null;
			String description = null;
			double height;
			double width;
			double length;
			double weight;
			URL linkToProduct = null;
			ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
			Date date = null;
			double price = 0.0;

			sql_info = "SELECT * FROM UPC_" + UPC + "_INFO";
			sql_price = "SELECT * FROM UPC_" + UPC + "_PRICE";
			info_statement = connection.createStatement();
			price_statement = connection.createStatement();

			product_info_results = info_statement.executeQuery(sql_info);
			product_price_results = price_statement.executeQuery(sql_price);

			while (product_info_results.next()) {
				name = product_info_results.getString(INFO_NAME_INDEX);
				description = product_info_results.getString(INFO_DESCRIPTION_INDEX);
				upc_result = product_info_results.getString(INFO_UPC_INDEX);
				UNSPSC = null; // TODO: This is not held in the database (YET);
				String URL = product_info_results.getString(INFO_URL_INDEX);
				if (URL != null) linkToProduct = new URL(URL);
				height = product_info_results.getDouble(INFO_HEIGHT_INDEX);
				width = product_info_results.getDouble(INFO_WIDTH_INDEX);
				length = product_info_results.getDouble(INFO_LENGTH_INDEX);
				weight = 0.0f; // TODO: This is not held in the database (YET);
			}

			//Create list of companies.
			//First column (index 1) is always 'DATE'. Therefore we want all columns after that.
			priceRSdata = product_price_results.getMetaData();
			for (int i = 2; i < priceRSdata.getColumnCount() + 1; i++) {
				companyColumnString = priceRSdata.getColumnLabel(i);
				companyColumnString.trim();
				int beginIndex = companyColumnString.indexOf('_') + 1;
				companyID = companyColumnString.substring(beginIndex);
				companiesList.add(companyID);
				System.out.println("productDAO retrieve companyID " + companyID);
			}

			String companyColumnLabel;
			while (product_price_results.next()) {
				System.out.println("productDAO while");
				date = new Date();

				//date = product_info_results.getDate(PRICE_DATE_INDEX);

				for (String company : companiesList) {
					System.out.println("productDAO getting prices");
					companyColumnLabel = "COMPANY_".concat(company);
					price = product_price_results.getDouble(companyColumnLabel);
					if (!(product_price_results.wasNull())) {
						priceList.add(new MF_Price(price, date, company));
						System.out.println("productDAO added price");
					}
				}
			}

			returnProduct = new MF_Product();
			returnProduct.setUPC(upc_result);
			returnProduct.setPrices(priceList);

			if (name != null) returnProduct.setName(name);
			//if (UNSPSC != null) returnProduct.setUNSPSC(UNSPSC); //TODO
			if (description != null) returnProduct.setDescription(description);
			if (linkToProduct != null) returnProduct.setLinkToProduct(linkToProduct);

			info_statement.close();
			price_statement.close();

			connection.commit();
			return returnProduct;
		}
		catch (Exception e) {
			System.err.println("ERROR: Unabe to retrieve from database instance.");
			e.printStackTrace();
			connection.rollback();
			return new MF_Product();
		}
	}

	/**
	 * Get a product from the database that closely matches the MF_Product.
	 * To be expanded upon, UPC will always be fastest because we use this as table names.
	 * 
	 * @param The
	 *            MF_Product to find in the database.
	 * @return The full MF_Product object returned from the database.
	 * @throws SQLException
	 */
	public MF_Product getProduct(MF_Product product) throws SQLException {

		String UPC = product.getUPC();

		if (UPC == null) {
			System.err.println("ERROR: Product cannot be null.");
			return null;
		}
		else if (!MF_ProductValidator.validate().Product(product)) {
			System.err.println("ERROR: Product is not valid.");
			return null;
		}
		try {

			String sql_info;
			String sql_price;
			String companyColumnString;
			String companyID;
			Statement info_statement;
			Statement price_statement;
			ResultSet product_info_results;
			ResultSet product_price_results;
			ResultSetMetaData priceRSdata;
			ArrayList<String> companiesList = new ArrayList<String>();
			MF_Product returnProduct;

			String upc_result = null;
			String name = null;
			String UNSPSC = null;
			String description = null;
			double height;
			double width;
			double length;
			double weight;
			URL linkToProduct = null;
			ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
			Date date = null;
			double price = 0.0;

			sql_info = "SELECT * FROM UPC_" + UPC + "_INFO";
			sql_price = "SELECT * FROM UPC_" + UPC + "_PRICE";
			info_statement = connection.createStatement();
			price_statement = connection.createStatement();

			product_info_results = info_statement.executeQuery(sql_info);
			product_price_results = price_statement.executeQuery(sql_price);

			while (product_info_results.next()) {
				name = product_info_results.getString(INFO_NAME_INDEX);
				description = product_info_results.getString(INFO_DESCRIPTION_INDEX);
				upc_result = product_info_results.getString(INFO_UPC_INDEX);
				UNSPSC = null; // TODO: This is not held in the database (YET);
				String URL = product_info_results.getString(INFO_URL_INDEX);
				if (URL != null) linkToProduct = new URL(URL);
				height = product_info_results.getDouble(INFO_HEIGHT_INDEX);
				width = product_info_results.getDouble(INFO_WIDTH_INDEX);
				length = product_info_results.getDouble(INFO_LENGTH_INDEX);
				weight = 0.0f; // TODO: This is not held in the database (YET);
			}

			//Create list of companies.
			//First column (index 1) is always 'DATE'. Therefore we want all columns after that.
			priceRSdata = product_price_results.getMetaData();
			for (int i = 2; i < priceRSdata.getColumnCount() + 1; i++) {
				companyColumnString = priceRSdata.getColumnLabel(i);
				companyColumnString.trim();
				int beginIndex = companyColumnString.indexOf('_') + 1;
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

			returnProduct = new MF_Product();
			returnProduct.setUPC(upc_result);
			returnProduct.setPrices(priceList);

			if (name != null) returnProduct.setName(name);
			if (description != null) returnProduct.setDescription(description);
			if (linkToProduct != null) returnProduct.setLinkToProduct(linkToProduct);

			info_statement.close();
			price_statement.close();

			connection.commit();
			return returnProduct;
		}
		catch (Exception e) {
			System.err.println("ERROR: Unabe to retrieve from database instance.");
			e.printStackTrace();
			connection.rollback();
			return new MF_Product();
		}
	}

	/**
	 * Delete the specified product from the database.
	 * 
	 * @param Product
	 *            The Product to be deleted from the database.
	 * @return True if successful.
	 * @throws SQLException
	 */
	public boolean delete(MF_Product product) throws SQLException {
		String UPC = product.getUPC();
		if (UPC == null) {
			System.err.println("ERROR: Product cannot be null.");
			return false;
		}
		else if (!MF_ProductValidator.validate().Product(product)) {
			System.err.println("ERROR: Product is not valid.");
			return false;
		}
		try {
			String sql_info;
			String sql_price;
			String sql_products;
			Statement info_statement;
			Statement price_statement;
			Statement delete_statement;

			sql_info = "DROP TABLE UPC_" + UPC + "_INFO";
			sql_price = "DROP TABLE UPC_" + UPC + "_PRICE";
			sql_products = "DELETE FROM PRODUCTS " + "WHERE UPC = " + UPC;
			info_statement = connection.createStatement();
			price_statement = connection.createStatement();
			delete_statement = connection.createStatement();

			info_statement.executeUpdate(sql_info);
			price_statement.executeUpdate(sql_price);
			delete_statement.executeUpdate(sql_products);

			delete_statement.close();
			info_statement.close();
			price_statement.close();

			connection.commit();
			return true;

		}
		catch (Exception e) {
			System.err.println("ERROR: Unable to delete table.");
			e.printStackTrace();
			connection.rollback();
			return false;
		}

	}

	/**
	 * Delete the specified product from the database.
	 * 
	 * @param UPC
	 *            The UPC of the product to be deleted.
	 * @return True if successful.
	 * @throws SQLException
	 */
	public boolean delete(String UPC) throws SQLException {

		if (UPC == null) {
			System.err.println("ERROR: Product cannot be null.");
			return false;
		}
		else if (!MF_ProductValidator.validate().UPC(UPC)) {
			System.err.println("ERROR: UPC is not valid.");
			return false;
		}
		try {
			String sql_info;
			String sql_price;
			String sql_products;
			Statement info_statement;
			Statement price_statement;
			Statement delete_statement;

			sql_info = "DROP TABLE UPC_" + UPC + "_INFO";
			sql_price = "DROP TABLE UPC_" + UPC + "_PRICE";
			sql_products = "DELETE FROM PRODUCTS " + "WHERE UPC = " + UPC;
			info_statement = connection.createStatement();
			price_statement = connection.createStatement();
			delete_statement = connection.createStatement();

			info_statement.executeUpdate(sql_info);
			price_statement.executeUpdate(sql_price);
			delete_statement.executeUpdate(sql_products);

			delete_statement.close();
			info_statement.close();
			price_statement.close();

			connection.commit();
			return true;

		}
		catch (Exception e) {
			System.err.println("ERROR: Unable to delete table.");
			e.printStackTrace();
			connection.rollback();
			return false;
		}

	}

}
