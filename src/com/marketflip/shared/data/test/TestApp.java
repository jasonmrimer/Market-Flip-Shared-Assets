package com.marketflip.shared.data.test;

import com.marketflip.shared.data.MF_DatabaseAccessObject;
import com.marketflip.shared.products.*;
/**
 * Test application to test the DAO during development.
 * @author David Walters
 * @updated 10/25/2015
 */
public class TestApp {

	public static void main(String[] args) {
		
		
		String UPC = "889661008491";
		
		MF_DatabaseAccessObject DAO = MF_DatabaseAccessObject.newInstance();
		
		MF_Product laptop = DAO.getProductByUPC(UPC);
		
		laptop.print();
				
		DAO.close();
	}

}
