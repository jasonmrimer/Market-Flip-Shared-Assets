/**
 * JUnit class for MF_ProductValidator
 * @author Dave Walters
 * Last Updated: 12/18/2015
 */
package com.marketflip.shared.products.util.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marketflip.shared.products.MF_Price;
import com.marketflip.shared.products.MF_Product;
import com.marketflip.shared.products.util.MF_ProductValidator;

public class MF_ProductValidatorTest {

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
	public void ValidateUPC_Sending56774_ExpectFalse() {
		String UPC = "56774";
		assertFalse(MF_ProductValidator.validate().UPC(UPC));
	}
	
	@Test
	public void ValidateUPC_Sending889661008491_ExpectTrue() {
		String UPC = "889661008491";
		assertTrue(MF_ProductValidator.validate().UPC(UPC));
	}
	
	@Test
	public void ValidateUPC_SendingNull_ExpectFalse(){
		String UPC = null;
		assertFalse(MF_ProductValidator.validate().UPC(UPC));
	}
	
	@Test
	public void ValidateProduct_SendingInvalidUPC_ExpectFalse() {
		String UPC = "56774";
		MF_Price price = new MF_Price(23.33, new Date());
		MF_Product product = new MF_Product(UPC, price);
		assertFalse(MF_ProductValidator.validate().Product(product));
	}

	@Test
	public void ValidateProduct_SendingValidUPC_ExpectTrue() {
		String UPC = "889661008491";
		MF_Price price = new MF_Price(45.33, new Date());
		MF_Product product = new MF_Product(UPC, price);
		assertTrue(MF_ProductValidator.validate().Product(product));
	}
	
	@Test
	public void ValidateProduct_SendingNullUPC_ExpectFalse() {
		String UPC = null;
		MF_Price price = new MF_Price(45.33, new Date());
		MF_Product product = new MF_Product(UPC, price);
		assertFalse(MF_ProductValidator.validate().Product(product));
	}
	
	@Test
	public void ValidateProduct_SendingNullProduct_ExpectFalse(){
		MF_Product product = null;
		assertFalse(MF_ProductValidator.validate().Product(product));
	}
	
	@Test
	public void ValidateProduct_SendingInvalidProductNullPrices_ExpectFalse() {
		String UPC = "889661008491";
		ArrayList<MF_Price> prices = null;
		MF_Product product = new MF_Product(UPC, prices);
		assertFalse(MF_ProductValidator.validate().Product(product));
		
	}
	
	@Test
	public void ValidatePrice_SendingValidPrice_ExpectTrue(){
		MF_Price testPrice = new MF_Price(266.33, new Date(), "Walmart");
		assertTrue(MF_ProductValidator.validate().Price(testPrice));		
	}
	@Test
	public void ValidatePrice_SendingInvalidPrice_ExpectFalse(){
		MF_Price testPrice = new MF_Price();
		assertFalse(MF_ProductValidator.validate().Price(testPrice));
	}
	@Test 
	public void ValidatePrice_Sending0_ExpectFalse() {
		MF_Price testPrice = new MF_Price(0.0, new Date());
		assertFalse(MF_ProductValidator.validate().Price(testPrice));
	}
	@Test
	public void ValidatePrice_SendingNullDate_ExpectFalse() {
		Date date = null;
		MF_Price testPrice = new MF_Price(26.33, date);
		assertFalse(MF_ProductValidator.validate().Price(testPrice));
	}
	@Test
	public void ValidatePrice_SendingNull_ExpectFalse() {
		MF_Price testPrice = null;
		assertFalse(MF_ProductValidator.validate().Price(testPrice));
	}
	@Test
	public void ValidatePrice_SendingNegativePrice_ExpectFalse() {
		MF_Price testPrice = new MF_Price(-33.22, new Date());
		assertFalse(MF_ProductValidator.validate().Price(testPrice));
	}
	
	@Test
	public void ValidatePriceList_SendingNull_ExpectFalse() {
		ArrayList<MF_Price> priceList = null;
		assertFalse(MF_ProductValidator.validate().PriceList(priceList));
	}
	@Test
	public void ValidatePriceList_SendingValidPriceList_ExpectTrue() {
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price> ();
		
		priceList.add(new MF_Price(45.33, new Date(), "Amazon"));
		priceList.add(new MF_Price(325.33, new Date(), "Walmart"));
		priceList.add(new MF_Price(535.25, new Date(), "Amazon"));
		priceList.add(new MF_Price(400.00, new Date(), "Walmart"));
		priceList.add(new MF_Price(99.99, new Date()));
		
		assertTrue(MF_ProductValidator.validate().PriceList(priceList));
	}
	@Test
	public void ValidatePriceList_SendingInvalidList_ExpectFalse() {
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price> ();
		
		MF_Price invalidPrice = new MF_Price(-23.55, new Date(), "Walmart");
		
		priceList.add(new MF_Price(45.33, new Date(), "Amazon"));
		priceList.add(new MF_Price(325.33, new Date(), "Walmart"));
		priceList.add(invalidPrice);
		priceList.add(new MF_Price(535.25, new Date(), "Amazon"));
		priceList.add(new MF_Price(400.00, new Date(), "Walmart"));
		priceList.add(new MF_Price(99.99, new Date()));
		
		assertFalse(MF_ProductValidator.validate().PriceList(priceList));
	}
	

}
