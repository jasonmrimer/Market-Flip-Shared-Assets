package com.marketflip.shared.data.test;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import com.marketflip.shared.data.MF_DatabaseAccessObject;
import com.marketflip.shared.products.*;
/**
 * Test application to test the DAO during development.
 * @author David Walters
 * @updated 11/1/2015
 */
public class TestApp {

	public static void main(String[] args) throws SQLException {
		
		
		long startTime;
		long endTime;
		long elapsedTime;
		long totalTime 	 = 0;
		
		String UPC = "889661008491";
		
		startTime = System.nanoTime();
		MF_DatabaseAccessObject DAO = MF_DatabaseAccessObject.newInstance();
		endTime = System.nanoTime();
		elapsedTime = TimeUnit.MILLISECONDS.convert((endTime-startTime), TimeUnit.NANOSECONDS);
		System.out.println("Time to make connection to database (ms): " + elapsedTime);
		System.out.println("\n\n\n");
		
		System.out.println("--------------SELECT FUNCTIONALITY-----------------");
		startTime = System.nanoTime();
		MF_Product laptop = DAO.getProductByUPC(UPC);
		endTime = System.nanoTime();
		elapsedTime = TimeUnit.MILLISECONDS.convert((endTime-startTime), TimeUnit.NANOSECONDS);
		System.out.println("Time to pull product from database (ms): " + elapsedTime);
		laptop.print();
		
		//Stress testing.
//		for (int i = 0; i < 200; i++) {
//			startTime = System.nanoTime();
//			laptop = DAO.getProductByUPC(UPC);
//			endTime = System.nanoTime();
//			elapsedTime = TimeUnit.MILLISECONDS.convert((endTime-startTime), TimeUnit.NANOSECONDS);
//			totalTime += elapsedTime;
//		}
//		System.out.println("Time to query the database 200 times (ms): " + totalTime);
		
		
		System.out.println("\n\n\n");
		System.out.println("--------------INSERT FUNCTIONALITY-----------------");
		
		MF_Product newLaptop = laptop;
		newLaptop.setUPC("889842013993");
		
		startTime = System.nanoTime();
		DAO.insertProduct(newLaptop);
		endTime = System.nanoTime();
		elapsedTime = TimeUnit.MILLISECONDS.convert((endTime-startTime), TimeUnit.NANOSECONDS);
		System.out.println("Time to insert product to database (ms): " + elapsedTime);
		System.out.println("\n\n\n");
		
		
		startTime = System.nanoTime();
		newLaptop = DAO.getProductByUPC("889842013993");
		endTime = System.nanoTime();
		elapsedTime = TimeUnit.MILLISECONDS.convert((endTime-startTime), TimeUnit.NANOSECONDS);
		System.out.println("Time to pull product from database (ms): " + elapsedTime);
				
		DAO.close();
	}

}
