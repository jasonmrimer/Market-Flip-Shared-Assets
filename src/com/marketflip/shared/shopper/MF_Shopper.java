package com.marketflip.shared.shopper;

import java.util.HashMap;
import com.marketflip.shared.shopper.MF_PricePoint;
import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.marketflip.shared.products.MF_Product;

/**
 * description: The Shopper class will identify a shopper
 * and their desired PricePoint for a product.
 * 
 * @author Tiffany Mathers
 *         date modified: 12/27/2015
 */

public class MF_Shopper {

	private String								userName;
	private String								firstName;
	private String								lastName;
	private String								email;
	private String								phoneNumber;

	// hashmap will match a key (MF_Product) with the desired value (MF_PricePoint)
	private HashMap<MF_Product, MF_PricePoint>	pricePointMap;
	// revised hashmap with PricePoint already including product data
	private HashMap<String, MF_PricePoint>		pricePointMapKeyProductTableID;
	private String								shopperID;

	/**
	 * Default constructor for MF_Shopper
	 */
	public MF_Shopper() {
		this.userName = null;
		this.firstName = null;
		this.lastName = null;
		this.email = null;
		this.phoneNumber = null;
		this.pricePointMap = new HashMap<MF_Product, MF_PricePoint>();
	}

	/**
	 * Minimum information needed for a shopper is a username and email.
	 * 
	 * @param userName
	 *            - username of shopper
	 * @param email
	 *            - email address of shopper
	 */
	public MF_Shopper(String userName, String email) {
		this.userName = userName;
		this.email = email;
		this.firstName = null;
		this.lastName = null;
		this.phoneNumber = null;
		this.pricePointMap = new HashMap<MF_Product, MF_PricePoint>();
		this.pricePointMapKeyProductTableID = new HashMap<String, MF_PricePoint>();
	}

	public MF_Shopper(String shopperUsername, String shopperEmail, String shopperID) {
		this(shopperUsername, shopperEmail);
		if (shopperID != null) {
			this.shopperID = shopperID;
		}
		else {
			this.shopperID = DigestUtils.md5Hex(shopperEmail);
		}

	}

	public void addPricePoint(MF_PricePoint pricePoint) {
		if (pricePointMapKeyProductTableID.containsKey(pricePoint.getProductTableID())) {
			pricePointMapKeyProductTableID.remove(pricePoint);
		}
		pricePointMapKeyProductTableID.put(pricePoint.getProductTableID(), pricePoint);
	}

	/**
	 * This method adds a pricepoint to a shopper's profile and matches it to a
	 * product. It also deletes a previous pricepoint if it exists for that product.
	 * 
	 * @param product
	 *            - the product for which the shopper wants to input a pricepoint
	 * @param pricePoint
	 *            -the price the shopper would need in order to complete a sale
	 */
	public void addPricePoint(MF_Product product, MF_PricePoint pricePoint) {

		if (pricePointMap.containsKey(product)) {
			pricePointMap.remove(product);
		}
		pricePointMap.put(product, pricePoint);
	}

	public int compareTo(MF_Shopper inShopper) {
		int lastCmp = shopperID.compareTo(inShopper.getShopperID());
		return (lastCmp != 0 ? lastCmp : userName.compareTo(inShopper.getUserName()));
	}

	/**
	 * This method checks to see if the shopper is reasonably considered to be equal.
	 * 
	 * @param shopper
	 *            - an MF_Shopper object
	 * @return true if the shopper's username matches itself and isn't a default value
	 */
	public boolean equals(MF_Shopper shopper) {
		if (this.userName.equals(shopper.getUserName()) && shopper.getUserName() != null) {
			return true;
		}
		System.out.println(shopper.toString());
		return false;
	}

	/*
	 * @rc from hb
	 * dateReviewed: Dec 31, 2015
	 * TODO add toString methods
	 */
	@Override
	public String toString() {
		String returnString;
		returnString = "MF_Shopper object for username: " + userName + " & email " + email + ".";
		return returnString;
	}

	/**
	 * Returns the user's username.
	 * 
	 * @return - String User's username.
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Set the user's username.
	 * 
	 * @param userName
	 *            - The username of the shopper.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Returns the user's first name.
	 * 
	 * @return - String - User's first name.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Set the user's first name.
	 * 
	 * @param firstName
	 *            - The user's first name.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Returns shopper's last name.
	 * 
	 * @return String The shopper's last name.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Set shopper's last name.
	 * 
	 * @param lastName
	 *            - The shopper's last name.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Returns the shopper's email.
	 * 
	 * @return String Shopper's email address.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Set the user's email.
	 * 
	 * @param email
	 *            - The user's email.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Returns the user's phone number.
	 * 
	 * @return String The user' email.
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * Set the shopper's phone number.
	 * 
	 * @param phoneNumber
	 *            - The user's phone number.
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * Returns a HashMap<MF_Product, MF_PricePoint> of the shopper's
	 * products and respective pricepoints
	 * 
	 * @return - HashMap<MF_Product, MF_PricePoint> - shopper's
	 *         products and respective pricepoints.
	 */
	public HashMap<MF_Product, MF_PricePoint> getPricePointMap() {
		return pricePointMap;
	}

	public HashMap<String, MF_PricePoint> getPricePointMapKeyProductTableID() {
		return pricePointMapKeyProductTableID;
	}

	public void setPricePointMapKeyProductTableID(
			HashMap<String, MF_PricePoint> pricePointMapKeyProductTableID) {
		this.pricePointMapKeyProductTableID = pricePointMapKeyProductTableID;
	}

	public String getShopperID() {
		return shopperID;
	}
}
