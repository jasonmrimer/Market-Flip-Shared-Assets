package com.marketflip.shared.shopper;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.codec.digest.DigestUtils;
/**
 * The Market Flip Application Shopper Database Access Object grants read/write access to the
 * Shopper Database that holds all informatino about shoppers such as usernames/email addresses and
 * price point lists.
 * 
 * @author highball
 *
 */
public class MF_ShopperDAO {

	private ArrayList<String>	arrayListOfTableNames;
	private Connection			conn;
	private String				columnNameProductTableID	= "ProductTableID";
	private String				columnNamePricePoint		= "PricePoint";
	private String				columnNameShopperID			= "ShopperID";
	private String				columnNameShopperEmail		= "ShopperEmail";
	private String				columnNameShopperUsername	= "ShopperUsername";
	//	private String				hostURL						= "jdbc:mysql://2001:4860:4864:1:3384:d4f9:83c3:a882"; //:3306/Shoppers?user=root";	//:3306/Shoppers?user=root
	private String				hostURL						= "jdbc:mysql://173.194.80.142:3306/shoppers?user=root";	//:3306/Shoppers?user=root sharedassets-database-v2
	//	private String				hostURL						= "jdbc:mysql://(host=[2001-4860-4864-1-3384-d4f9-83c3-a882])(port=3306)"; //:3306/Shoppers?user=root";	//:3306/Shoppers?user=root
//	private String				hostURL						= "jdbc:mysql://";	//2001:4860:4864:1:3384:d4f9:83c3:a882"; //:3306/Shoppers?user=root";	//:3306/Shoppers?user=root
	private boolean				isClosed;
	private String				password					= ***REMOVED***;
	//	private String				password					= "root";
	private String				tableNameShoppers			= "Shoppers";
	private String				tableNamePrefixPricePoint	= "PPT_";
	private String				username					= "Jason";
	//	private String				username					= "localhost";

	public MF_ShopperDAO() {
		this.arrayListOfTableNames = null;
		this.conn = null;
		this.isClosed = false;
	}
/*
 * This is a draft block of code to connect to SQL using IPv6 instead of IPv4: never got it to work 20160803.
 */
//	public MF_ShopperDAO(boolean isMock) {
//		this();
//		if (!isMock) {
//			try {
//				Class.forName("com.mysql.jdbc.Driver");
//				Properties pro = new Properties();
//				pro.setProperty("2001:4860:4864:1:3384:d4f9:83c3:a882", "address");
//				pro.setProperty("3306", "port");
//				pro.setProperty("shoppers", "database");
//				//				this.conn = DriverManager.getConnection(hostURL);
//				this.arrayListOfTableNames = new ArrayList<String>();
//				//				clearAllTables();
//				//				createWebsitesTable();
//				/*
//				 * The following code block comes from GDC by clicking "Connect to SQL" in the SQL
//				 * area.
//				 */
//				String url = null;
////				if (SystemProperty.environment
////						.value() == SystemProperty.Environment.Value.Production) {
//					// Connecting from App Engine.
//					// Load the class that provides the "jdbc:google:mysql://"
//					// prefix.
////					Class.forName("com.mysql.jdbc.GoogleDriver");
////					url = "jdbc:google:mysql://marketflip-sharedassets:sharedassets-database?user=root";
//				 // Alternatively, connect to a Google Cloud SQL instance using:
//		        // jdbc:mysql://ip-address-of-google-cloud-sql-instance:3306/guestbook?user=root
//				url = "jdbc:mysql://[2001:4860:4864:1:3384:d4f9:83c3:a882]:3306/shoppers?user=root";
//
////				}
////				else {
////					// You may also assign an IP Address from the access control
////					// page and use it to connect from an external network.
////				}
//				this.conn = DriverManager.getConnection(url);
//			}
//			catch (SQLException e) {
//				e.printStackTrace();
//			}
//			catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	public MF_ShopperDAO(boolean isMock) {
		this();
		if (!isMock) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				this.conn = DriverManager.getConnection(hostURL, username, password);
				this.arrayListOfTableNames = new ArrayList<String>();
				//				clearAllTables();
				//				createWebsitesTable();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void addPricePoint(String shopperEmail, String productUPC, Double price) {
		// Ensure shopper & shopper's price point tables exist
		if (shopperRecordExists(shopperEmail)) {
			if (pricePointTableExists(shopperEmail)) {
				// if exists, add record to price point table
				String shopperID = DigestUtils.md5Hex(shopperEmail);
				String productTableID = "UPC_" + productUPC;
				String tableNamePricePoint = tableNamePrefixPricePoint + shopperID;
				// add shopper row to Shoppers table
				PreparedStatement prepStatement = null;
				String insertRecordSQLString = "INSERT INTO " + tableNamePricePoint + " ("
						+ columnNameProductTableID + ", " + columnNamePricePoint + ") VALUES (?,?)";
				try {
					prepStatement = conn.prepareStatement(insertRecordSQLString);
					prepStatement.setString(1, productTableID);
					prepStatement.setDouble(2, price);
					// execute
					prepStatement.executeUpdate();
				}
				catch (SQLException e) {
					System.err.println("SQLException in MF_ShopperDAO addShopper.");
					e.printStackTrace();
				}
				finally {
					if (prepStatement != null) {
						try {
							prepStatement.close();
						}
						catch (SQLException e) {
							System.err.println(
									"SQLException in MF_ShopperDAO addShopper closing prepared statement.");
							e.printStackTrace();
						}
					}
				}
				// create shopper's price point table
				createPricePointTable(shopperID);
			}
			else {
				System.err.println("Error in addPricePoint: Price Point table does not exist for "
						+ shopperEmail);
			}
		}
		else {
			System.err.println(
					"Error in addPricePoint: Shopper Table does not exist for " + shopperEmail);
		}
	}

	public void addShopper(MF_Shopper shopper) {
		String shopperEmail, shopperUsername, shopperEmailHashed;
		shopperEmail = shopper.getEmail();
		shopperUsername = shopper.getUserName();
		shopperEmailHashed = DigestUtils.md5Hex(shopperEmail);
		// add shopper row to Shoppers table
		PreparedStatement prepStatement = null;
		String insertRecordSQLString = "INSERT INTO " + tableNameShoppers + " ("
				+ columnNameShopperID + ", " + columnNameShopperUsername + ", "
				+ columnNameShopperEmail + ") VALUES (?,?,?)";
		try {
			prepStatement = conn.prepareStatement(insertRecordSQLString);
			prepStatement.setString(1, shopperEmailHashed);
			prepStatement.setString(2, shopperUsername);
			prepStatement.setString(3, shopperEmail);
			// execute
			prepStatement.executeUpdate();
		}
		catch (SQLException e) {
			System.err.println("SQLException in MF_ShopperDAO addShopper.");
			e.printStackTrace();
		}
		finally {
			if (prepStatement != null) {
				try {
					prepStatement.close();
				}
				catch (SQLException e) {
					System.err.println(
							"SQLException in MF_ShopperDAO addShopper closing prepared statement.");
					e.printStackTrace();
				}
			}
		}
		// create shopper's price point table
		createPricePointTable(shopper);
	}

	/**
	 * The purpose of this method is to add a shopper to the Shoppers Table. Every shopper will
	 * automitically receive a PricePoint table (referenced by PPT_ShopperID) that will contain all
	 * the shopper's price point inputs.
	 *
	 * @param shopperEmail
	 */
	public void addShopper(String shopperEmail) {
		String emailAfterHash = DigestUtils.md5Hex(shopperEmail);
		// add shopper row to Shoppers table
		PreparedStatement prepStatement = null;
		String insertRecordSQLString = "INSERT INTO " + tableNameShoppers + " ("
				+ columnNameShopperID + ", " + columnNameShopperEmail + ") VALUES (?,?)";
		try {
			prepStatement = conn.prepareStatement(insertRecordSQLString);
			prepStatement.setString(1, emailAfterHash);
			prepStatement.setString(2, shopperEmail);
			// execute
			prepStatement.executeUpdate();
		}
		catch (SQLException e) {
			System.err.println("SQLException in MF_ShopperDAO addShopper.");
			e.printStackTrace();
		}
		finally {
			if (prepStatement != null) {
				try {
					prepStatement.close();
				}
				catch (SQLException e) {
					System.err.println(
							"SQLException in MF_ShopperDAO addShopper closing prepared statement.");
					e.printStackTrace();
				}
			}
		}
		// create shopper's price point table
		createPricePointTable(shopperEmail);
	}

	public void close() {
		if (isClosed) {
			return;
		}
		else {
			try {
				if (conn != null || !conn.isClosed()) {
					conn.close();
					isClosed = true;
				}
			}
			catch (SQLException e) {
				System.err.println("SQLException in MF_ShopperDAO close");
				e.printStackTrace();
			}
		}
	}

	/**
	 * The purpose of this method is to create a Shopper Table that will contain every shopper
	 * (i.e.,
	 * user) and information regarding that shopper: shopper email address (i.e., username) and the
	 * unique identification hashed from the email address.
	 */
	public void createShoppersTable() {
		String sqlString = "CREATE TABLE " + tableNameShoppers + "(" + columnNameShopperID
				+ " varchar(32)," + columnNameShopperUsername + " varchar(255),"
				+ columnNameShopperEmail + " varchar(255));";
		Statement sqlStatement = null;
		try {
			sqlStatement = conn.createStatement();
			sqlStatement.execute(sqlString);
		}
		catch (SQLException e) {
			System.err.println("SQLException in MF_ShopperDAO createShopperTable");
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

	private void createPricePointTable(MF_Shopper shopper) {
		String shopperEmail, tableNamePricePoints, productTableID;
		shopperEmail = shopper.getEmail();
		// add all price points to the table
		if (pricePointTableExists(shopperEmail)) {
			updatePricePoints(shopper);
		}
		else {
			// create the table using the email address
			createPricePointTable(shopperEmail);
			// add all pricepoints
			updatePricePoints(shopper);
		}
	}

	/**
	 * The purpose of this method is to create a Price Point Table for every shopper added to the
	 * Shoppers Table. The table will contain all the products and price points the shopper adds
	 * while shopping and will be used to iterate through in order to find price point matches and
	 * email the shoppers. The table is named using the hased ShopperID in order to concatenate easy
	 * references between the two tables. It also uses the table names for individual products for
	 * cross references.
	 *
	 * @param emailAfterHash
	 */
	private void createPricePointTable(String shopperEmail) {
		String emailAfterHash = DigestUtils.md5Hex(shopperEmail);
		if (!pricePointTableExists(shopperEmail)) {
			String tableNamePricePointTable = tableNamePrefixPricePoint + emailAfterHash;
			String sqlString = "CREATE TABLE " + tableNamePricePointTable + "("
					+ columnNameProductTableID + " varchar(31)," + columnNamePricePoint
					+ " DECIMAL(8,2));"; // TODO get product table IDs from shared assets
			Statement sqlStatement = null;
			try {
				sqlStatement = conn.createStatement();
				sqlStatement.execute(sqlString);
			}
			catch (SQLException e) {
				System.err.println("SQLException in MF_ShopperDAO createPricePointTable");
				e.printStackTrace();
			}
			finally {
				if (sqlStatement != null) {
					try {
						sqlStatement.close();
					}
					catch (SQLException e) {
						System.err.println(
								"Caught error trying to close statment in createPricePointTable:");
						e.printStackTrace();
					}
				}
			}
		}
		else {
			System.err.println("from creatPricePointTable: Price point table already exists for "
					+ shopperEmail);
		}
	}

	/**
	 * The purpose of this method is to delete all the contents of the database for testing (i.e., a
	 * rollback method)
	 */
	public void deleteAllTables() {
		DatabaseMetaData dbMetaData;
		ResultSet rsTables;
		Statement sqlStatement;
		String sqlString;
		try {
			dbMetaData = conn.getMetaData();
			String[] types = {"TABLE"};
			rsTables = dbMetaData.getTables(null, null, "%", types);
			while (rsTables.next()) {
				sqlString = "DROP TABLE " + rsTables.getString("TABLE_NAME");
				sqlStatement = conn.createStatement();
				sqlStatement.execute(sqlString);
				sqlStatement.close();
			}
			rsTables.close();
		}
		catch (SQLException e) {
			System.err.println("SQLException in MF_ShopperDAO clearAllTables");
			e.printStackTrace();
		}
	}

	/**
	 * The purpose of this method is to insert all the table names into an array list in order to
	 * analyze/test those tables creation and persistence.
	 *
	 */
	public void populateTableNameArrayList() {
		DatabaseMetaData dbMetaData;
		ResultSet rsTables;
		arrayListOfTableNames.clear();
		try {
			dbMetaData = conn.getMetaData();
			String[] types = {"TABLE"};
			rsTables = dbMetaData.getTables(null, null, "%", types);
			while (rsTables.next()) {
				arrayListOfTableNames.add(rsTables.getString("TABLE_NAME"));
			}
		}
		catch (SQLException e) {
			System.err.println("SQLException in populateTableNameArrayList");
			e.printStackTrace();
		}
	}

	/**
	 * The purpose of this method is to verify that the price point table for a user already exists
	 * as it should be created upon user sign-up.
	 *
	 * @param shopperEmail
	 * @return
	 */
	public boolean pricePointTableExists(String shopperEmail) {
		boolean exists = false;
		String tableNameToCheck = "PPT_" + DigestUtils.md5Hex(shopperEmail);
		DatabaseMetaData dbMetaData;
		ResultSet rsTables;
		arrayListOfTableNames.clear();
		try {
			dbMetaData = conn.getMetaData();
			String[] types = {"TABLE"};
			rsTables = dbMetaData.getTables(null, null, tableNameToCheck, types);
			if (rsTables.next()) {
				exists = true;
			}
		}
		catch (SQLException e) {
			System.err.println("SQLException in pricePointTableExists");
			e.printStackTrace();
		}
		return exists;
	}

	/**
	 * The purpose of this method is to verify that a shopper record exists by checking the shopper
	 * table.
	 *
	 * @param shopperEmail
	 * @return
	 */
	public boolean shopperRecordExists(String shopperEmail) {
		boolean exists = false;
		String emailAfterHash = DigestUtils.md5Hex(shopperEmail);
		// add shopper row to Shoppers table
		ResultSet selectRS = null;
		PreparedStatement prepStatement = null;
		String selectRecordSQLString = "SELECT * FROM " + tableNameShoppers + " WHERE "
				+ columnNameShopperID + " = '" + emailAfterHash + "'";
		try {
			prepStatement = conn.prepareStatement(selectRecordSQLString);
			//			prepStatement.setString(1, emailAfterHash);
			// execute
			selectRS = prepStatement.executeQuery(selectRecordSQLString);
			if (selectRS.next()) {
				exists = true;
			}
		}
		catch (SQLException e) {
			System.err.println("SQLException in MF_ShopperDAO shopperRecordExists.");
			e.printStackTrace();
		}
		finally {
			if (prepStatement != null) {
				try {
					prepStatement.close();
				}
				catch (SQLException e) {
					System.err.println(
							"SQLException in MF_ShopperDAO shopperRecordExists closing prepared statement.");
					e.printStackTrace();
				}
			}
			if (selectRS != null) {
				try {
					selectRS.close();
				}
				catch (SQLException e) {
					System.err.println(
							"SQLException in MF_ShopperDAO shopperRecordExists closing result set.");
					e.printStackTrace();
				}
			}
		}
		return exists;
	}

	private void updatePricePoints(MF_Shopper shopper) {
		String shopperEmail, tableNamePricePoints, productTableID;
		HashMap<String, MF_PricePoint> hashMapOfPricePoints;
		ArrayList<MF_PricePoint> arrayListOfPricePoints;
		shopperEmail = shopper.getEmail();
		hashMapOfPricePoints = shopper.getPricePointMapKeyProductTableID();
		// create the table name
		tableNamePricePoints = tableNamePrefixPricePoint + DigestUtils.md5Hex(shopperEmail);
		// update the price points 
		arrayListOfPricePoints = this.getArrayListOfPricePoints(shopper);
		// for each price point in the db, update if exists yet changed and add if does not exist
		for (MF_PricePoint shopperPricePoint : hashMapOfPricePoints.values()) {
			//		for (MF_PricePoint pricePoint : arrayListOfPricePoints) {
			// update/replace
			if (arrayListOfPricePoints.contains(shopperPricePoint)) {
				//			if (hashMapOfPricePoints.containsValue(pricePoint)) {
				// update with new hashmap value to replace old DB value
				productTableID = shopperPricePoint.getProductTableID();
				// 'Id' is whatever your PK column is
				String sqlStatement = "UPDATE " + tableNamePricePoints + " SET "
						+ columnNamePricePoint + " = ? WHERE " + columnNameProductTableID + " = ?;";
				PreparedStatement preparedStatement = null;
				try {
					preparedStatement = conn.prepareStatement(sqlStatement);
					preparedStatement.setDouble(1, shopperPricePoint.getPrice());
					preparedStatement.setString(2, productTableID);
					preparedStatement.executeUpdate();
				}
				catch (SQLException e) {
					System.err.println(
							"SQLException in MF_ShopperDAO createPricePointTable(MF_Shopper) executing prepared statement.");
					e.printStackTrace();
				}
				finally {
					if (preparedStatement != null) {
						try {
							preparedStatement.close();
						}
						catch (SQLException e) {
							System.err.println(
									"SQLException in MF_ShopperDAO createPricePointTable(MF_Shopper) closing prepared statement.");
							e.printStackTrace();
						}
					}
				}
			}
			// add because not in table
			else {
				String productUPC;
				productUPC = shopperPricePoint.getProductTableID()
						.replace(tableNamePrefixPricePoint, "");
				addPricePoint(shopperEmail, productUPC, shopperPricePoint.getPrice());
			}
		}
	}

	@Override
	public void finalize() {
		try {
			close();
		}
		finally {
			try {
				super.finalize();
			}
			catch (Throwable e) {
				System.err.println("Error in MF_ShopperDAO finalize:");
				e.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		String toString;
		toString = "Instance of Market Flip Application Shopping Database Access Object without parameters.";
		return toString;
	}

	public Connection getConn() {
		return conn;
	}

	// GETTERS

	public ArrayList<MF_PricePoint> getArrayListOfPricePoints(MF_Shopper shopper) {
		ArrayList<MF_PricePoint> pricePoints = new ArrayList<MF_PricePoint>();
		pricePoints = getArrayListOfPricePoints(shopper.getEmail());
		return pricePoints;
	}

	/**
	 * The purpose of this method is to select all of the price points from a shopper's price point
	 * table then insert those prices into a portable array list for analysis and testing.
	 *
	 * @return
	 */
	public ArrayList<MF_PricePoint> getArrayListOfPricePoints(String shopperEmail) {
		ArrayList<MF_PricePoint> pricePoints = new ArrayList<MF_PricePoint>();
		// Ensure shopper & shopper's price point tables exist
		if (shopperRecordExists(shopperEmail)) {
			if (pricePointTableExists(shopperEmail)) {
				// if exists, add record to price point table
				String shopperID = DigestUtils.md5Hex(shopperEmail);
				String tableNamePricePoint = tableNamePrefixPricePoint + shopperID;
				// add shopper row to Shoppers table
				ResultSet rs = null;
				PreparedStatement prepStatement = null;
				String selectSQLString = "SELECT * FROM " + tableNamePricePoint;
				try {
					// prep 
					prepStatement = conn.prepareStatement(selectSQLString);
					// execute
					rs = prepStatement.executeQuery(selectSQLString);
					// transfer to arraylist
					while (rs.next()) {
						String productTableName = rs.getString(columnNameProductTableID);
						String productUPC = productTableName.replace("UPC_", "");
						Double price = rs.getDouble(columnNamePricePoint);
						pricePoints.add(new MF_PricePoint(productUPC, price));
					}
				}
				catch (SQLException e) {
					System.err.println("SQLException in MF_ShopperDAO getPricePointArrayList.");
					e.printStackTrace();
				}
				finally {
					if (prepStatement != null) {
						try {
							prepStatement.close();
						}
						catch (SQLException e) {
							System.err.println(
									"SQLException in MF_ShopperDAO getPricePointArray closing prepared statement.");
							e.printStackTrace();
						}
					}
					if (rs != null) {
						try {
							rs.close();
						}
						catch (SQLException e) {
							System.err.println(
									"SQLException in MF_ShopperDAO getPricePointArray closing result set.");
							e.printStackTrace();
						}
					}
				}
			}
			else {
				System.err.println(
						"Error in getPricePointArray: Price Point table does not exist for "
								+ shopperEmail);
			}
		}
		else {
			System.err.println("Error in getPricePointArray: Shopper Table does not exist for "
					+ shopperEmail);
		}
		return pricePoints;
	}

	public MF_Shopper getShopper(MF_Shopper expectedShopper) {
		MF_Shopper retrievedShopper = null;
		String shopperEmail, retrievedShopperEmail, retrievedShopperUsername;
		shopperEmail = expectedShopper.getEmail();
		ArrayList<MF_PricePoint> pricePoints = new ArrayList<MF_PricePoint>();
		// Ensure shopper & shopper's price point tables exist
		ResultSet rs = null;
		PreparedStatement prepStatement = null;
		String selectSQLString = "SELECT * FROM " + tableNameShoppers + " WHERE "
				+ columnNameShopperEmail + "='" + shopperEmail + "'";
		try {
			// prep 
			prepStatement = conn.prepareStatement(selectSQLString);
			// execute
			rs = prepStatement.executeQuery(selectSQLString);
			// transfer to arraylist
			while (rs.next()) {
				retrievedShopperUsername = rs.getString(columnNameShopperUsername);
				retrievedShopperEmail = rs.getString(columnNameShopperEmail);
				retrievedShopper = new MF_Shopper(retrievedShopperUsername, retrievedShopperEmail);
			}
		}

		catch (SQLException e) {
			System.err.println("SQLException in MF_ShopperDAO getShopper.");
			e.printStackTrace();
		}
		finally {
			if (prepStatement != null) {
				try {
					prepStatement.close();
				}
				catch (SQLException e) {
					System.err.println(
							"SQLException in MF_ShopperDAO getShopper closing prepared statement.");
					e.printStackTrace();
				}
			}
			if (rs != null) {
				try {
					rs.close();
				}
				catch (SQLException e) {
					System.err.println(
							"SQLException in MF_ShopperDAO getShopper closing result set.");
					e.printStackTrace();
				}
			}
		}
		return retrievedShopper;
	}

	public ArrayList<String> getTableNameArrayList() {
		DatabaseMetaData dbMetaData;
		ResultSet rsTables;
		arrayListOfTableNames.clear();
		try {
			dbMetaData = conn.getMetaData();
			String[] types = {"TABLE"};
			rsTables = dbMetaData.getTables(null, null, "%", types);
			while (rsTables.next()) {
				arrayListOfTableNames.add(rsTables.getString("TABLE_NAME"));
			}
		}
		catch (SQLException e) {
			System.err.println("SQLException in populateTableNameArrayList");
			e.printStackTrace();
		}
		return arrayListOfTableNames;
	}

	public ArrayList<MF_Shopper> getArrayListOfShoppers() {
		ArrayList<MF_Shopper> arrayListOfShoppers = new ArrayList<MF_Shopper>();
		ResultSet rs = null;
		PreparedStatement prepStatement = null;
		String selectSQLString = "SELECT * FROM " + tableNameShoppers;
		try {
			// prep 
			prepStatement = conn.prepareStatement(selectSQLString);
			// execute
			rs = prepStatement.executeQuery(selectSQLString);
			// transfer to arraylist
			while (rs.next()) {
				String shopperUsername = rs.getString(columnNameShopperUsername);
				String shopperEmail = rs.getString(columnNameShopperEmail);
				String shopperID = rs.getString(columnNameShopperID);
				arrayListOfShoppers.add(new MF_Shopper(shopperUsername, shopperEmail, shopperID));
			}
		}
		catch (SQLException e) {
			System.err.println("SQLException in MF_ShopperDAO getArrayListOfShoppers.");
			e.printStackTrace();
		}
		finally {
			if (prepStatement != null) {
				try {
					prepStatement.close();
				}
				catch (SQLException e) {
					System.err.println(
							"SQLException in MF_ShopperDAO getArrayListOfShoppers closing prepared statement.");
					e.printStackTrace();
				}
			}
			if (rs != null) {
				try {
					rs.close();
				}
				catch (SQLException e) {
					System.err.println(
							"SQLException in MF_ShopperDAO getArrayListOfShoppers closing result set.");
					e.printStackTrace();
				}
			}
		}
		return arrayListOfShoppers;
	}
}
