package com.marketflip.shared.products;

import java.util.HashMap;
import com.marketflip.shared.products.MF_PricePoint;
import com.marketflip.shared.products.MF_Product;

/**
 * description: The Shopper class will identify a shopper
 * and their desired PricePoint for a product.
 * 
 * @author Tiffany Mathers
 * date modified: 12/27/2015
 */

public class MF_Shopper {

	private String userName;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	
	// hashmap will match a key (MF_Product) with the desired value (MF_PricePoint)
	private HashMap<MF_Product, MF_PricePoint> pricePointMap;
	
	/**
	 * default constructors for MF_Shopper
	 */
	public MF_Shopper () {
		this.userName 		= null;
		this.firstName 		= null;
		this.lastName		= null;
		this.email			= null;
		this.phoneNumber	= null;
		this.pricePointMap	= new HashMap<MF_Product, MF_PricePoint> ();
	}
	
	/**
	 * sets minimum information needed to userName and email
	 * @param userName - username of shopper
	 * @param email - email address of shopper
	 */
	public MF_Shopper (String userName, String email) {
		this.userName		= userName;
		this.email 			= email;
		this.firstName		= null;
		this.lastName 		= null;
		this.phoneNumber	= null;
		this.pricePointMap	= new HashMap<MF_Product, MF_PricePoint> ();
	}
	
	
	/**
	 * This method adds a pricepoint to a shopper's profile and matches it to a
	 * product. It also deletes a previous pricepoint if it exists for that product.
	 * @param product - the product for which the shopper wants to input a pricepoint
	 * @param pricePoint -the price the shopper would need in order to complete a sale
	 */
	public void addPricePoint(MF_Product product, MF_PricePoint pricePoint) {
		
		if (pricePointMap.containsKey(product)) {
			pricePointMap.remove(product);
		}		
		pricePointMap.put(product, pricePoint);
	}
	
	/**
	 * This method checks to see if the shopper is the same
	 * @param shopper - an MF_Shopper object 
	 * @return true if the shopper's username matches itself and isn't a default value
	 */
	public boolean equals(MF_Shopper shopper) {
		if (this.userName.equals(shopper.getUserName()) && shopper.getUserName() != null) {
			return true;
		}
		return false;
	}

	/**
	 * getter method for userName
	 * @return - userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * setter method for userName
	 * @param userName - value assigned to this.userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * getter method for firstName
	 * @return - firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * setter method for firstName
	 * @param firstName - value assigned to this.firstName
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * getter method for lastName
	 * @return - lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * setter method for lastName
	 * @param lastName - value assigned to this.lastName
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * getter method for email
	 * @return - email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * setter method for email
	 * @param email - value assigned to this.email
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * getter method for phoneNumber
	 * @return - phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * setter method for phoneNumber
	 * @param phoneNumber - value assigned to this.phoneNumber
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}
