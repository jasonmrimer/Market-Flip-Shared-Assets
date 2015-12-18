package com.marketflip.shared.data.test;


/**
 * Tests the DAO for all edge cases.
 * @author David Walters
 * Last updated 12/13/2015
 */
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.marketflip.shared.data.*;
import com.marketflip.shared.products.*;



public class MF_DataAccessObjectTest {

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
		
		MF_DataAccessObject.openConnection();
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		
		//Ensure the DAO is closed.
		if (MF_DataAccessObject.getDAO() != null) {
			MF_DataAccessObject.getDAO().close();
		}
	}
	
	@Test
	public void getDAO_Singleton_ExpectNoExceptions () {
		assertFalse (MF_DataAccessObject.getDAO() == null);
	}
	
	@Test
	public void Close_Singleton_ExpectNull () {
		MF_DataAccessObject.getDAO().close();
		assertTrue(MF_DataAccessObject.getDAO() == null);
	}
	
	@Test
	public void Insert_NullProduct_ExpectFalse () {
		
		MF_Product product = null;
		assertFalse(MF_DataAccessObject.getDAO().insertProduct(product));	
		
	}
	
	@Test
	public void Insert_NullUPC_ExpectFalse () {
		String upc = null;
		MF_Product product = new MF_Product(upc);
		assertFalse(MF_DataAccessObject.getDAO().insertProduct(product));			
	}
	
	
}
