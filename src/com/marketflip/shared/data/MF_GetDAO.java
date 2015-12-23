package com.marketflip.shared.data;

import com.marketflip.shared.products.MF_Product;
import com.marketflip.shared.products.util.MF_ProductValidator;

public class MF_GetDAO  extends MF_DataAccessObject {

	public MF_GetDAO(String environment) {
		super(environment, "Get");
	}
	
	/**
	 * Get a product by unique identifier (UPC).
	 * To be expanded upon, can take a UNSCPC identifier, use regex to check.
	 * @param UPC The unique identifier to find the product.
	 * @return The full MF_Product object.
	 */
	public MF_Product getProduct(String UPC) {
		
		if (UPC == null) {
			System.err.println("ERROR: Product cannot be null.");
			return null;
		} else if (!MF_ProductValidator.validate().UPC(UPC)) {
			System.err.println("ERROR: UPC is not valid.");
			return null;
		} else if (!super.getProductSet().contains(UPC)) {
			System.err.println("ERROR: Product not contained in database.");
			return null;
		}
		
		return new MF_Product();
	}
	
	/**
	 * Get a product from the database that closely matches the MF_Product.
	 * To be expanded upon, UPC will always be fastest because we use this as table names.
	 * @param The MF_Product to find in the database.
	 * @return The full MF_Product object returned from the database.
	 */
	public MF_Product getProduct(MF_Product product) {
		
		String UPC = product.getUPC();
		
		if (UPC == null) {
			System.err.println("ERROR: Product cannot be null.");
			return null;
		} else if (!MF_ProductValidator.validate().Product(product)) {
			System.err.println("ERROR: UPC is not valid.");
			return null;
		} else if (!super.getProductSet().contains(UPC)) {
			System.err.println("ERROR: Product not contained in database.");
			return null;
		}
		
		
		
		return new MF_Product();
	}

}
