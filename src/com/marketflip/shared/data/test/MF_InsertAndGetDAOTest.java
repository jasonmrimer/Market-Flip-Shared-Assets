package com.marketflip.shared.data.test;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marketflip.shared.data.MF_GetDAO;
import com.marketflip.shared.data.MF_InsertDAO;
import com.marketflip.shared.products.MF_Price;
import com.marketflip.shared.products.MF_Product;
import com.marketflip.shared.products.util.MF_ProductValidator;

public class MF_InsertAndGetDAOTest {
	
	private MF_InsertDAO InsertDAO;
	private MF_GetDAO GetterDAO;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		if (InsertDAO != null) {
			if (!InsertDAO.isOpen()){
				InsertDAO.close();
			}
		}
		InsertDAO = new MF_InsertDAO("testing");

	}

	@After
	public void tearDown() throws Exception {
		
		if (!InsertDAO.isOpen()){
			InsertDAO.close();
		}
		if (!GetterDAO.isOpen()) {
			GetterDAO.close();
		}
	}
	
	@Test
	public void Environment_Testing_ExpectTesting () {
		assertTrue(InsertDAO.getEnvironment().equals("testing"));
	}
	@Test
	public void ChildType_Implicit_ExpectInsertion () {
		assertTrue(InsertDAO.getChildType().equals("Insertion"));
	}
	
	@Test
	public void AddProductToCommit_SendingNull_ExpectFalse () {
		MF_Product product = null;
		assertFalse(InsertDAO.addProductToCommit(product));
	}
	
	@Test
	public void AddProductToCommit_SendingValidProduct_ExpectTrue() {
		
		String UPC = "889661008491";
		
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
		MF_Price price = new MF_Price(26.33, "Amazon");
		priceList.add(price);
		
		MF_Product product = new MF_Product(UPC, priceList);
		assertTrue(InsertDAO.addProductToCommit(product));
	}
	
	@Test
	public void AddProductToCommit_SendingValidProduct_ExpectInArrayList () {
		String UPC = "889661008491";
		
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
		MF_Price price = new MF_Price(26.33, "Amazon");
		priceList.add(price);
		
		MF_Product product = new MF_Product(UPC, priceList);
		
		InsertDAO.addProductToCommit(product);
		assertTrue(InsertDAO.getCommitList().contains(product));
		
	}
	
	@Test
	public void AddProductToCommit_SendingInvalidProduct_ExpectFalse() {
		String UPC = "xcvxcv";

		ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
		MF_Price price = new MF_Price(26.33, "Amazon");
		priceList.add(price);
		
		MF_Product product = new MF_Product(UPC, priceList);
		assertFalse(InsertDAO.addProductToCommit(product));
	}
	
	@Test
	public void AddProductToCommit_SendingInvalidProduct_ExpectNotInArrayList() {
		String UPC = "xcvxcv";

		ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
		MF_Price price = new MF_Price(26.33, "Amazon");
		priceList.add(price);
		
		MF_Product product = new MF_Product(UPC, priceList);
		InsertDAO.addProductToCommit(product);
		
		assertFalse(InsertDAO.getCommitList().contains(product));
	}
	
	@Test
	public void AddProductToCommit_SendingValidDuplicateProduct_ExpectFalse () {
		String UPC = "889661008491";
		
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
		MF_Price price = new MF_Price(26.33, "Amazon");
		priceList.add(price);
		
		MF_Product product = new MF_Product(UPC, priceList);
		InsertDAO.addProductToCommit(product);
		
		String dupUPC = "889661008491";
		
		ArrayList<MF_Price> dupPriceList = new ArrayList<MF_Price>();
		MF_Price dupPrice = new MF_Price(26.33, "Amazon");
		dupPriceList.add(dupPrice);
		
		MF_Product dupProduct = new MF_Product(dupUPC, dupPriceList);
		
		assertFalse(InsertDAO.addProductToCommit(dupProduct));
		
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
		
		InsertDAO.addProductToCommit(product);
		
		assertTrue(InsertDAO.getProductSet().contains(UPC));
	}
	
	@Test
	public void CommitProductsToDatabase_SendingMultipleValidProducts_ExpectTrue () {
		
		ArrayList<MF_Product> testingList = new ArrayList<MF_Product> ();
		String UPC1 = "835847000216";
		ArrayList<MF_Price> priceList1 = new ArrayList<MF_Price> ();
		priceList1.add(new MF_Price(56.33, "Amazon"));
		priceList1.add(new MF_Price(43.55, "Walmart"));
		MF_Product product1 = new MF_Product(UPC1, priceList1);
		product1.setDescription("Delicious Tequila");
		product1.setName("Happy Tequila");
		InsertDAO.addProductToCommit(product1);
		testingList.add(product1);
		
		String UPC2 = "835345000216";
		ArrayList<MF_Price> priceList2 = new ArrayList<MF_Price> ();
		priceList2.add(new MF_Price(22.22, "Amazon"));
		priceList2.add(new MF_Price(99.99, "Walmart"));
		MF_Product product2 = new MF_Product(UPC2, priceList2);
		product2.setDescription("Newest Console from Microsoft");
		product2.setName("Xbox One");
		InsertDAO.addProductToCommit(product2);
		testingList.add(product2);
		
		String UPC3 = "835345450217";
		ArrayList<MF_Price> priceList3 = new ArrayList<MF_Price> ();
		priceList3.add(new MF_Price(58.42, "Amazon"));
		priceList3.add(new MF_Price(45.99, "Walmart"));
		MF_Product product3 = new MF_Product(UPC3, priceList3);
		product3.setDescription("Red Jacket for Women");
		product3.setName("Levi Strauss Women's Jacket");
		InsertDAO.addProductToCommit(product3);
		testingList.add(product3);
		
		InsertDAO.commitProductsToDatabase();
		
		//I know multiple asserts are normally bad form, but I want to make sure ALL products were added.
		for (MF_Product product : testingList) {
			assertTrue(InsertDAO.getProductSet().contains(product));
		}
	}
	
	@Test
	public void GetProduct_SendingOneValidProduct_ExpectEqualProduct () throws SQLException {
		
		if (GetterDAO != null) {
			if (!GetterDAO.isOpen()){
				GetterDAO.close();
			}
		}
		GetterDAO = new MF_GetDAO ("testing");
		
		String UPC = "0700604908640";
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price> ();
		priceList.add(new MF_Price(13.99, "Toys R Us"));
		MF_Product product = new MF_Product(UPC, priceList);
		
		InsertDAO.addProductToCommit(product);
		InsertDAO.commitProductsToDatabase();
		
		MF_Product testProduct;
		testProduct = GetterDAO.getProduct(product);
		
		assertTrue(product.equals(testProduct));
	}
	
	@Test
	public void GetProduct_SendingOneValidProduct_ExpectValidatedProduct() throws SQLException {
		if (GetterDAO != null) {
			if (!GetterDAO.isOpen()){
				GetterDAO.close();
			}
		}
		GetterDAO = new MF_GetDAO ("testing");
		
		String UPC = "0700604905340";
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price> ();
		priceList.add(new MF_Price(15.75, "Walmart"));
		MF_Product product = new MF_Product(UPC, priceList);
		
		InsertDAO.addProductToCommit(product);
		InsertDAO.commitProductsToDatabase();
		
		MF_Product testProduct;
		testProduct = GetterDAO.getProduct(product);
		
		assertTrue(MF_ProductValidator.validate().Product(testProduct));
	}
	
	@Test
	public void GetProductUPC_SendingOneValidProduct_ExpectEqualProduct() throws SQLException {
		if (GetterDAO != null) {
			if (!GetterDAO.isOpen()){
				GetterDAO.close();
			}
		}
		GetterDAO = new MF_GetDAO ("testing");
		
		String UPC = "0700344905340";
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price> ();
		priceList.add(new MF_Price(55.23, "Walmart"));
		MF_Product product = new MF_Product(UPC, priceList);
		
		InsertDAO.addProductToCommit(product);
		InsertDAO.commitProductsToDatabase();
		
		MF_Product testProduct;
		testProduct = GetterDAO.getProduct(UPC);
		
		assertTrue(product.equals(testProduct));
	}
	
	@Test
	public void GetProductUPC_SendingOneValidProduct_ExpectValidatedProduct() throws SQLException {
		if (GetterDAO != null) {
			if (!GetterDAO.isOpen()){
				GetterDAO.close();
			}
		}
		GetterDAO = new MF_GetDAO ("testing");
		
		String UPC = "0700344905340";
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price> ();
		priceList.add(new MF_Price(55.23, "Walmart"));
		MF_Product product = new MF_Product(UPC, priceList);
		
		InsertDAO.addProductToCommit(product);
		InsertDAO.commitProductsToDatabase();
		
		MF_Product testProduct;
		testProduct = GetterDAO.getProduct(UPC);
		
		assertTrue(MF_ProductValidator.validate().Product(testProduct));
	}
	
	

}
