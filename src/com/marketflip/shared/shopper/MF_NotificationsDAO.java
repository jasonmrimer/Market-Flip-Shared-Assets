package com.marketflip.shared.shopper;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MF_NotificationsDAO {

	private Connection			conn;
	private boolean				isClosed;
	private String				hostURL		= "jdbc:mysql://173.194.242.194:3306/notifications?user=root";	//:3306/Shoppers?user=root
	private String				username	= "Jason";
	private String				password	= ***REMOVED***;
	private ArrayList<String>	arrayListOfTableNames;
	private String tableNameNotifications;
	private String columnNameShopperID;
	private String columnNameShopperUsername;
	private String columnNameShopperEmail;

	public MF_NotificationsDAO() {
		this.arrayListOfTableNames = new ArrayList<String>();
		this.conn = null;
		this.isClosed = false;
	}

	public MF_NotificationsDAO(boolean isMock) {
		this();
		if (!isMock) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				this.conn = DriverManager.getConnection(hostURL, username, password);
				//				this.arrayListOfTableNames = new ArrayList<String>();
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

	/**
	 * The purpose of this method is to create a Notifications Table that will contain every shopper
	 * (i.e., user) and information regarding that shopper: shopper email address (i.e., username)
	 * and the
	 * unique identification hashed from the email address.
	 */
	public void createShoppersTable() {
		String sqlString = "CREATE TABLE " + tableNameNotifications + "(" + columnNameShopperID
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

	@Override
	public String toString() {
		String toString;
		toString = "Instance of Market Flip Notifications Database Access Object without parameters.";
		return toString;
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
}
