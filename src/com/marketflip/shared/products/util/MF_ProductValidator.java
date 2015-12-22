package com.marketflip.shared.products.util;

import java.util.ArrayList;

import org.apache.commons.validator.routines.checkdigit.EAN13CheckDigit;

import com.marketflip.shared.products.MF_Price;
import com.marketflip.shared.products.MF_Product;

/**
 * Singleton utility class to validate various MF_Product properties, or an entire MF_Product
 * @author David Walters
 * Last Updated: 12/21/2015
 */
public class MF_ProductValidator {
	
	private static final MF_ProductValidator validator = new MF_ProductValidator();
	
	/**
	 * Returns a product validator.
	 * @return ProductValidator.
	 */
	public static MF_ProductValidator validate () {
		return validator;
	}
	private MF_ProductValidator () {
		
	}
	
	/**
	 * Validates UPC for correct 13 digit syntax.
	 * @param 
	 * 		The UPC of the product to validate.
	 * @return Boolean
	 * 		Returns true if the UPC is validated.
	 */
	public boolean UPC(String UPC) {
	
		if (EAN13CheckDigit.EAN13_CHECK_DIGIT.isValid(UPC)){
			return true;
		} else {
			System.err.println("ERROR: UPC is not valid.");
			return false;
		}
	}
	
	/**
	 * Checks if the prices in the price list are valid.
	 * @param priceList the Pricelist to check.
	 * @return The validation state.
	 */
	public boolean PriceList(ArrayList<MF_Price> priceList) {
		
		if (priceList == null) {
			return false;
		}
		for (MF_Price price : priceList){
			if (!Price(price)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the price given is valid.
	 * @param price The price to check.
	 * @return The validation state.
	 */
	public boolean Price(MF_Price price) {
		if (price == null) {
			return false;
		} else if ((price.getPrice() == 0.00) || (price.getPrice() != Math.abs(price.getPrice()))){
			return false;
		} else if (price.getDate() == null) {
			return false;
		} else {
			return true;
		}
		
	}
	
	
	/**
	 * Validates an MF_Product based on it's properties and business logic of Market Flip.
	 * @param
	 * 		The MF_Product to be validated
	 * @return
	 * 		Returns true if the Product is validated.
	 */
	public boolean Product(MF_Product product){
		
		boolean valid = true;
		
		if (product != null){
			// Add all checks in here.
			if (!MF_ProductValidator.validate().UPC(product.getUPC())){
				valid = false;
			}
			if (product.getPrices() == null || product.getPrices().isEmpty()) {
				System.err.println ("ERROR: Price cannot be null or empty.");
				valid = false;
			}
			if (!MF_ProductValidator.validate().PriceList(product.getPrices())) {
				System.err.println ("ERROR: Prices cannot be validated.");
				valid = false;
			}
			
		} else {
			System.err.println("ERROR: Product cannot be null.");
			valid = false;
		}
		
		//TODO: Needs to be expanded significantly as the business logic is discussed.
		return valid;
	}

}
