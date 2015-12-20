package com.marketflip.shared.products.util;

import org.apache.commons.validator.routines.checkdigit.EAN13CheckDigit;

import com.marketflip.shared.products.MF_Product;

/**
 * Singleton utility class to validate various MF_Product properties, or an entire MF_Product
 * @author David Walters
 * Last Updated: 12/20/2015
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
			
		} else {
			System.err.println("ERROR: Product cannot be null.");
			valid = false;
		}
		
		//TODO: Needs to be expanded significantly as the business logic is discussed.
		return valid;
	}

}
