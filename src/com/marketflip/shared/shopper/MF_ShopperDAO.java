package com.marketflip.shared.shopper;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;

/**
 * The Market Flip Application Shopper Database Access Object grants read/write access to the
 * Shopper Database that holds all informatino about shoppers such as usernames/email addresses and
 * price point lists.
 * 
 * @author highball
 *
 */
public class MF_ShopperDAO {

	private ArrayList<String>	tableNameArrayList;
	private Connection			conn;
	private String				tableNameShoppers					= "Shoppers";
	private String				columnNameShopperID					= "ShopperID";
	private String				columnNameShopperEmail				= "ShopperEmail";
	private String				columnNameShopperPricePointTableRef	= "PricePointTableReference";
	private String				hostURL								= "jdbc:mysql://173.194.249.229:3306/Shoppers?user=root";	//:3306/Shoppers?user=root
	private String				username							= "Jason";
	private String				password							= ***REMOVED***;
	private String				tableNamePrefixPricePoint			= "PPT_";
	private String				columnNameProductTableID			= "ProductTableID";
	private String				columnNamePricePoint				= "PricePoint";

	public MF_ShopperDAO() {
		this.tableNameArrayList = null;
		this.conn = null;
	}

	public MF_ShopperDAO(boolean isMock) {
		this();
		if (!isMock) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				this.conn = DriverManager.getConnection(hostURL, username, password);
				this.tableNameArrayList = new ArrayList<String>();
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

	/**
	 * The purose of this method is to add a shopper to the Shoppers Table. Every shopper will
	 * automitically receive a PricePoint table (referenced by PPT_ShopperID) that will contain all
	 * the shopper's price point inputs.
	 *
	 * @param emailBeforeHash
	 */
	public void addShopper(String emailBeforeHash) {
		String emailAfterHash = DigestUtils.md5Hex(emailBeforeHash);
		// add shopper row to Shoppers table
		PreparedStatement prepStatement = null;
		String insertRecordSQLString = "INSERT INTO " + tableNameShoppers + " ("
				+ columnNameShopperID + ", " + columnNameShopperEmail + ") VALUES (?,?)";
		try {
			prepStatement = conn.prepareStatement(insertRecordSQLString);
			prepStatement.setString(1, emailAfterHash);
			prepStatement.setString(2, emailBeforeHash);
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
		createPricePointTable(emailBeforeHash);
	}

	/**
	 * The purose of this method is to create a Shopper Table that will contain every shopper (i.e.,
	 * user) and information regarding that shopper: shopper email address (i.e., username) and the
	 * unique identification hashed from the email address.
	 */
	public void createShopperTable() {
		String sqlString = "CREATE TABLE " + tableNameShoppers + "(" + columnNameShopperID
				+ " varchar(32)," + columnNameShopperEmail + " varchar(255));";
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

	/**
	 * The purose of this method is to create a Price Point Table for every shopper added to the
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
			System.err.println("from creatPricePointTable: Price point table already exists for " + shopperEmail);
		}
	}

	/**
	 * The purose of this method is to delete all the contents of the database for testing (i.e., a
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
			System.err.println("SQLException in clearAllTables");
			e.printStackTrace();
		}
	}

	/**
	 * The purose of this method is to select all of the price points from a shopper's price point
	 * table then insert those prices into a portable array list for analysis and testing.
	 *
	 * @return
	 */
	public ArrayList<MF_PricePoint> getPricePointArrayList(String shopperEmail) {
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

	/**
	 * The purose of this method is to insert all the table names into an array list in order to
	 * analyze/test those tables creation and persistence.
	 *
	 */
	public void populateTableNameArrayList() {
		DatabaseMetaData dbMetaData;
		ResultSet rsTables;
		tableNameArrayList.clear();
		try {
			dbMetaData = conn.getMetaData();
			String[] types = {"TABLE"};
			rsTables = dbMetaData.getTables(null, null, "%", types);
			while (rsTables.next()) {
				tableNameArrayList.add(rsTables.getString("TABLE_NAME"));
			}
		}
		catch (SQLException e) {
			System.err.println("SQLException in populateTableNameArrayList");
			e.printStackTrace();
		}
	}

	/**
	 * The purose of this method is to verify that the price point table for a user already exists
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
		tableNameArrayList.clear();
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
	 * The purose of this method is to verify that a shopper record exists by checking the shopper
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

	@Override
	public void finalize() {
		try {
			if (conn != null || !conn.isClosed()) {
				conn.close();
			}
		}
		catch (SQLException e) {
			System.err.println("SQLException in MF_ShopperDAO finalize");
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		String toString;
		toString = "Instance of Market Flip Application Shopping Database Access Object without parameters.";
		return toString;
	}

	// GETTERS
	public ArrayList<String> getTableNameArrayList() {
		return tableNameArrayList;
	}

}
