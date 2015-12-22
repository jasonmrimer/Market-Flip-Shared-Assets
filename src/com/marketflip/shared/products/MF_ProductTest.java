/**
 * JUnit class for MF_ProductTest
 * @author Dave Walters
 * Last Updated: 12/22/2015
 */
package com.marketflip.shared.products;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MF_ProductTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void GetCurrentLowestPrice_Sending99CentsAmazon_Expecting99CentsAmazon () {
		
		MF_Product testProduct = new MF_Product();
		
		MF_Price truePrice = new MF_Price(0.99, new Date((long)1449275197 * 1000), "Amazon");
		
		ArrayList<MF_Price> testPrices =  new ArrayList();
		testPrices.add(new MF_Price(1.99, new Date((long)1441270800 * 1000), "Amazon"));
		testPrices.add(new MF_Price(1.98, new Date((long)605272271 * 1000), "Walmart"));
		testPrices.add(new MF_Price(1.96, new Date((long)1426852883 * 1000), "Amazon"));
		testPrices.add(new MF_Price(1.52, new Date((long)1414890121 * 1000), "Walmart"));
		testPrices.add(new MF_Price(1.23, new Date((long)1446118442 * 1000), "Amazon"));
		testPrices.add(new MF_Price(1.12, new Date((long)1433234712 * 1000), "Toys R Us"));
		testPrices.add(new MF_Price(1.78, new Date((long)1422922197 * 1000), "Walmart"));
		testPrices.add(new MF_Price(0.99, new Date((long)1449275197 * 1000), "Amazon"));
		testPrices.add(new MF_Price(1.00, new Date((long)1421992943 * 1000), "Amazon"));
		testProduct.setPrices(testPrices);
		
		MF_Price testPrice = testProduct.getCurrentLowestPrice();
		
		assertEquals(testPrice.getCompany(), truePrice.getCompany());
		assertTrue(testPrice.getDate().getTime() == truePrice.getDate().getTime());
		assertTrue(testPrice.getPrice() == truePrice.getPrice());	
	}
	
	@Test
	public void ConstructorUPCPrice_SendingOnePrice_ExpectInArrayList () {
		MF_Price price = new MF_Price(45.22, new Date(), "Walmart");
		String UPC = "889661008491";
		MF_Product product = new MF_Product(UPC, price);
		
		assertTrue (product.getPrices().contains(price));
	}

}
