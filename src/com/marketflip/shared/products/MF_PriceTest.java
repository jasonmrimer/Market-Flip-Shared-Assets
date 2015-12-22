/**
 * JUnit class for MF_Price
 * @author Dave Walters
 * Last Updated: 12/4/2015
 */
package com.marketflip.shared.products;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MF_PriceTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void Constructor_SendingDefault_ExpectDefault() {
		
		MF_Price testPrice = new MF_Price();
		
		double price = 0.0;
		
		// Date needs to be within a few ms.
		Date date = (Date) Calendar.getInstance().getTime();
		long upperLimit = date.getTime()+100;
		long lowerLimit = date.getTime()-100;
		
		String company = null;
		
		assertTrue(testPrice.getCompany() == company);
		assertTrue(testPrice.getDate().getTime() <= upperLimit);
		assertTrue(testPrice.getDate().getTime() >= lowerLimit);
		assertTrue(testPrice.getPrice() == price);
			
	}
	
	@Test
	public void ConstructorCompanyPrice_SendingWalmart155Point33_ExpectWalmart155Point33() {
		
		double price = 155.33;
		String company = "Walmart";
		
		// Date needs to be within a few ms.
		Date date = (Date) Calendar.getInstance().getTime();
		long upperLimit = date.getTime()+100;
		long lowerLimit = date.getTime()-100;
		
		MF_Price testPrice = new MF_Price(price,company);
		
		assertTrue(testPrice.getPrice() == price);
		assertTrue(testPrice.getCompany() == company);
		
		// Ensure date was initialized and within a few ms.
		assertTrue(testPrice.getDate().getTime() <= upperLimit);
		assertTrue(testPrice.getDate().getTime() >= lowerLimit);
		
	}
	
	@Test
	public void ConstructorDatePrice_Sending1449181566155Point33_Expect1449181566155Point33() {
		
		long timestamp = 1449181566*1000;
		Date date = new Date(timestamp);
		double price = 155.33;
		
		MF_Price testPrice = new MF_Price(price,date);
		
		assertTrue(testPrice.getPrice() == price);
		assertTrue(testPrice.getDate() == date);
		
		long testTimestamp = testPrice.getDate().getTime();
		assertTrue(testTimestamp == timestamp);
		
	}
	
	@Test
	public void GettersAndSetters_SendingExactData_ExpectExactData() {
		
		MF_Price testPrice = new MF_Price();
		
		long timestamp = 1381016442;
		String company = "Amazon";
		Date dateObject = new Date(timestamp);
		
		double price = 266.33;
		
		testPrice.setCompany(company);
		assertTrue(testPrice.getCompany() == company);
		
		testPrice.setDate(dateObject);
		assertTrue(testPrice.getDate() == dateObject);
		assertTrue(testPrice.getDate().getTime() == dateObject.getTime());
		
		Date testDate = testPrice.getDate();
		testPrice.setDate(timestamp);
		
		assertTrue(testPrice.getDate().getTime() == testDate.getTime());
		
		testPrice.setPrice(price);
		assertTrue(testPrice.getPrice() == price);	
		
	}
	
	@Test
	public void Equals_SendingEqualPrice_ExpectTrue() {
		
		MF_Price testPrice = new MF_Price(28.33, new Date());
		MF_Price testAgainstPrice = new MF_Price(28.33, new Date());
		
		assertTrue(testPrice.equals(testAgainstPrice));
	}
	
	@Test
	public void Equals_SendingUnequalDate_ExpectFalse() {
		
		MF_Price testPrice = new MF_Price (28.33, new Date());
		MF_Price testAgainstPrice = new MF_Price (28.33, new Date(1449407412));
		
		assertFalse(testPrice.equals(testAgainstPrice));
	}
	
	@Test
	public void Equals_SendingUnequalPrice_ExpectFalse() {
		
		MF_Price testPrice = new MF_Price(5656.22, new Date());
		MF_Price testAgainstPrice = new MF_Price(333.33, new Date());
		
		assertFalse(testPrice.equals(testAgainstPrice));
	}
	
	@Test 
	public void Equals_SendingEqualCompany_ExpectTrue () {
		MF_Price testPrice = new MF_Price(223.33, new Date(), "Walmart");
		MF_Price testAgainstPrice = new MF_Price(223.33, new Date(), "Walmart");
		
		assertTrue(testPrice.equals(testAgainstPrice));
	}

}
