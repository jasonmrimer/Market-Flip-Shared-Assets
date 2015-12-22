package com.marketflip.shared.data.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marketflip.shared.data.MF_InsertDAO;
import com.marketflip.shared.products.MF_Price;
import com.marketflip.shared.products.MF_Product;

public class MF_InsertDAOTest {
	
	private MF_InsertDAO DAO;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		if (DAO != null) {
			if (!DAO.isOpen()){
				DAO.close();
			}
		}
		DAO = new MF_InsertDAO("testing");
	}

	@After
	public void tearDown() throws Exception {
		
		if (!DAO.isOpen()){
			DAO.close();
		}
	}
	
	@Test
	public void Environment_Testing_ExpectTesting () {
		assertTrue(DAO.getEnvironment().equals("testing"));
	}
	@Test
	public void ChildType_Implicit_ExpectInsertion () {
		assertTrue(DAO.getChildType().equals("Insertion"));
	}
	
	@Test
	public void AddProductToCommit_SendingNull_ExpectFalse () {
		MF_Product product = null;
		assertFalse(DAO.addProductToCommit(product));
	}
	
	@Test
	public void AddProductToCommit_SendingValidProduct_ExpectTrue() {
		
		String UPC = "889661008491";
		
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
		MF_Price price = new MF_Price(26.33, "Amazon");
		priceList.add(price);
		
		MF_Product product = new MF_Product(UPC, priceList);
		assertTrue(DAO.addProductToCommit(product));
	}
	
	@Test
	public void AddProductToCommit_SendingValidProduct_ExpectInArrayList () {
		String UPC = "889661008491";
		
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
		MF_Price price = new MF_Price(26.33, "Amazon");
		priceList.add(price);
		
		MF_Product product = new MF_Product(UPC, priceList);
		
		DAO.addProductToCommit(product);
		assertTrue(DAO.getCommitList().contains(product));
		
	}
	
	@Test
	public void AddProductToCommit_SendingInvalidProduct_ExpectFalse() {
		String UPC = "xcvxcv";

		ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
		MF_Price price = new MF_Price(26.33, "Amazon");
		priceList.add(price);
		
		MF_Product product = new MF_Product(UPC, priceList);
		assertFalse(DAO.addProductToCommit(product));
	}
	
	@Test
	public void AddProductToCommit_SendingInvalidProduct_ExpectNotInArrayList() {
		String UPC = "xcvxcv";

		ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
		MF_Price price = new MF_Price(26.33, "Amazon");
		priceList.add(price);
		
		MF_Product product = new MF_Product(UPC, priceList);
		DAO.addProductToCommit(product);
		
		assertFalse(DAO.getCommitList().contains(product));
	}
	
	@Test
	public void AddProductToCommit_SendingValidDuplicateProduct_ExpectFalse () {
		String UPC = "889661008491";
		
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
		MF_Price price = new MF_Price(26.33, "Amazon");
		priceList.add(price);
		
		MF_Product product = new MF_Product(UPC, priceList);
		DAO.addProductToCommit(product);
		
		String dupUPC = "889661008491";
		
		ArrayList<MF_Price> dupPriceList = new ArrayList<MF_Price>();
		MF_Price dupPrice = new MF_Price(26.33, "Amazon");
		dupPriceList.add(dupPrice);
		
		MF_Product dupProduct = new MF_Product(dupUPC, dupPriceList);
		
		assertFalse(DAO.addProductToCommit(dupProduct));
		
	}
	
	@Test
	public void CommitProductsToDatabase_SendingOneValidProduct_ExpectTrue () {
		
		String UPC = "885370951615";
		
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
		MF_Price price = new MF_Price(26.33, "Amazon");
		priceList.add(price);
		
		MF_Product product = new MF_Product(UPC, priceList);
		product.setName("Forza Blue XB1");
		product.setDescription("Xbox One video game");
		
		DAO.addProductToCommit(product);
		
		assertTrue(DAO.getProductSet().contains(UPC));
		
	}

}
