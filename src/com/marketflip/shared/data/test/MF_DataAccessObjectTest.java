package com.marketflip.shared.data.test;


/**
 * Tests the DAO for all edge cases.
 * @author David Walters
 * Last updated 12/13/2015
 */
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marketflip.shared.data.MF_DataAccessObject;
import com.marketflip.shared.products.MF_Price;
import com.marketflip.shared.products.MF_Product;



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
	
//  @Deprecated
//	@Test
//	public void Insert_NullProduct_ExpectFalse () throws SQLException {
//		MF_Product product = null;
//		assertFalse(MF_DataAccessObject.getDAO().insertProduct(product));
//	}
//	
//	@Test
//	public void Insert_NullUPC_ExpectFalse () throws SQLException {
//		String upc = null;
//		MF_Product product = new MF_Product(upc);
//		assertFalse(MF_DataAccessObject.getDAO().insertProduct(product));			
//	}
	
	@Test
	public void AddProductToCommit_SendingNull_ExpectFalse () {
		MF_Product product = null;
		assertFalse(MF_DataAccessObject.getDAO().addProductToCommit(product));
	}
	
	@Test
	public void AddProductToCommit_SendingValidProduct_ExpectTrue() {
		
		String UPC = "889661008491";
		
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
		MF_Price price = new MF_Price(26.33, "Amazon");
		priceList.add(price);
		
		MF_Product product = new MF_Product(UPC, priceList);
		assertFalse(MF_DataAccessObject.getDAO().addProductToCommit(product));
	}
	
	@Test
	public void AddProductToCommit_SendingValidProduct_ExpectInArrayList () {
		String UPC = "889661008491";
		
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
		MF_Price price = new MF_Price(26.33, "Amazon");
		priceList.add(price);
		
		MF_Product product = new MF_Product(UPC, priceList);
		
		MF_DataAccessObject.getDAO().addProductToCommit(product);
		assertTrue(MF_DataAccessObject.getDAO().getCommitList().contains(product));
		
	}
	
	@Test
	public void AddProductToCommit_SendingInvalidProduct_ExpectFalse() {
		String UPC = "xcvxcv";

		ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
		MF_Price price = new MF_Price(26.33, "Amazon");
		priceList.add(price);
		
		MF_Product product = new MF_Product(UPC, priceList);
		assertFalse(MF_DataAccessObject.getDAO().addProductToCommit(product));
	}
	
	@Test
	public void AddProductToCommit_SendingInvalidProduct_ExpectNotInArrayList() {
		String UPC = "xcvxcv";

		ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
		MF_Price price = new MF_Price(26.33, "Amazon");
		priceList.add(price);
		
		MF_Product product = new MF_Product(UPC, priceList);
		MF_DataAccessObject.getDAO().addProductToCommit(product);
		
		assertFalse(MF_DataAccessObject.getDAO().getCommitList().contains(product));
	}

}
