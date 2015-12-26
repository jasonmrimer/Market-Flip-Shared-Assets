package com.marketflip.shared.data.test;
/**
 * Tests for Insert and Get DAOs.
 * @author David Walters
 * Last updated: 12/23/2015
 */
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marketflip.shared.data.MF_ProductsDAO;
import com.marketflip.shared.products.MF_Price;
import com.marketflip.shared.products.MF_Product;
import com.marketflip.shared.products.util.MF_ProductValidator;

public class MF_InsertAndGetDAOTest {
	
	private MF_ProductsDAO DAO;

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
		
		if (!DAO.isOpen()){
			DAO.close();
		}
	}
	
	@Test
	public void Environment_Testing_ExpectTesting () throws SQLException {
		if (DAO != null) {
			if (DAO.isOpen()) {
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		assertTrue(DAO.getEnvironment().equals("testing"));
	}
	@Test
	public void ChildType_Implicit_ExpectProducts () throws SQLException {
		if (DAO != null) {
			if (DAO.isOpen()) {
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		assertTrue(DAO.getChildType().equals("Products"));
	}
	
	@Test
	public void AddProductToCommit_SendingNull_ExpectFalse () throws SQLException {
		if (DAO != null) {
			if (DAO.isOpen()) {
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		MF_Product product = null;
		assertFalse(DAO.addProductToCommit(product));
	}
	
	@Test
	public void AddProductToCommit_SendingValidProduct_ExpectTrue() throws SQLException {
		if (DAO != null) {
			if (DAO.isOpen()) {
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		
		String UPC = "889661008491";
		
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
		MF_Price price = new MF_Price(26.33, "Amazon");
		priceList.add(price);
		
		MF_Product product = new MF_Product(UPC, priceList);
		assertTrue(DAO.addProductToCommit(product));
	}
	
	@Test
	public void AddProductToCommit_SendingValidProduct_ExpectInArrayList () throws SQLException {
		if (DAO != null) {
			if (DAO.isOpen()) {
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		
		String UPC = "889661008491";
		
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
		MF_Price price = new MF_Price(26.33, "Amazon");
		priceList.add(price);
		
		MF_Product product = new MF_Product(UPC, priceList);
		
		DAO.addProductToCommit(product);
		assertTrue(DAO.getCommitList().contains(product));
		
	}
	
	@Test
	public void AddProductToCommit_SendingInvalidProduct_ExpectFalse() throws SQLException {
		if (DAO != null) {
			if (DAO.isOpen()) {
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		String UPC = "xcvxcv";

		ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
		MF_Price price = new MF_Price(26.33, "Amazon");
		priceList.add(price);
		
		MF_Product product = new MF_Product(UPC, priceList);
		assertFalse(DAO.addProductToCommit(product));
	}
	
	@Test
	public void AddProductToCommit_SendingInvalidProduct_ExpectNotInArrayList() throws SQLException {
		if (DAO != null) {
			if (DAO.isOpen()) {
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		String UPC = "xcvxcv";

		ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
		MF_Price price = new MF_Price(26.33, "Amazon");
		priceList.add(price);
		
		MF_Product product = new MF_Product(UPC, priceList);
		DAO.addProductToCommit(product);
		
		assertFalse(DAO.getCommitList().contains(product));
	}
	
	@Test
	public void AddProductToCommit_SendingValidDuplicateProduct_ExpectFalse () throws SQLException {
		if (DAO != null) {
			if (DAO.isOpen()) {
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
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
	public void CommitProductsToDatabase_SendingOneValidProduct_ExpectTrue () throws SQLException {
		if (DAO != null) {
			if (DAO.isOpen()) {
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		
		String UPC = "0044600301853";
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price> ();
		priceList.add(new MF_Price(15.75, "Walmart"));
		priceList.add(new MF_Price(12.95, "Amazon"));
		MF_Product product = new MF_Product(UPC, priceList);
		product.setName("Clorox Bleach");
		product.setDescription("Clothes detergent, obviously.");
		
		DAO.addProductToCommit(product);
		DAO.commitProductsToDatabase();

		assertTrue(DAO.getProductSet().contains(UPC));
		DAO.delete(product);
	}
	
	@Test
	public void CommitProductsToDatabase_SendingMultipleValidProducts_ExpectTrue () throws SQLException {
		if (DAO != null) {
			if (DAO.isOpen()) {
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		
		ArrayList<MF_Product> testingList = new ArrayList<MF_Product> ();
		String UPC1 = "0041215021107";
		ArrayList<MF_Price> priceList1 = new ArrayList<MF_Price> ();
		priceList1.add(new MF_Price(56.33, "Amazon"));
		priceList1.add(new MF_Price(43.55, "Walmart"));
		MF_Product product1 = new MF_Product(UPC1, priceList1);
		product1.setDescription("Delicious Tequila");
		product1.setName("Happy Tequila");
		DAO.addProductToCommit(product1);
		testingList.add(product1);
		
		String UPC2 = "0027086169308";
		ArrayList<MF_Price> priceList2 = new ArrayList<MF_Price> ();
		priceList2.add(new MF_Price(22.22, "Amazon"));
		priceList2.add(new MF_Price(99.99, "Walmart"));
		MF_Product product2 = new MF_Product(UPC2, priceList2);
		product2.setDescription("Newest Console from Microsoft");
		product2.setName("Xbox One");
		DAO.addProductToCommit(product2);
		testingList.add(product2);
		
		String UPC3 = "0041565145270";
		ArrayList<MF_Price> priceList3 = new ArrayList<MF_Price> ();
		priceList3.add(new MF_Price(58.42, "Amazon"));
		priceList3.add(new MF_Price(45.99, "Walmart"));
		MF_Product product3 = new MF_Product(UPC3, priceList3);
		product3.setDescription("Red Jacket for Women");
		product3.setName("Levi Strauss Women's Jacket");
		DAO.addProductToCommit(product3);
		testingList.add(product3);
		
		DAO.commitProductsToDatabase();
		
		//multiple asserts are normally bad form, but I want to make sure ALL products were added.
		assertTrue(DAO.getProductSet().contains(product1.getUPC()));
		assertTrue(DAO.getProductSet().contains(product2.getUPC()));
		assertTrue(DAO.getProductSet().contains(product3.getUPC()));
		
		DAO.delete(product1);
		DAO.delete(product2);
		DAO.delete(product3);
	}
	
	@Test
	public void GetProduct_SendingOneValidProduct_ExpectEqualProduct () throws SQLException {
		
		if (DAO != null) {
			if (DAO.isOpen()) {
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		
		String UPC1 = "0018964059224";
		ArrayList<MF_Price> priceList1 = new ArrayList<MF_Price> ();
		priceList1.add(new MF_Price(58.22, "Amazon"));
		priceList1.add(new MF_Price(49.99, "Walmart"));
		MF_Product product1 = new MF_Product(UPC1, priceList1);
		product1.setDescription("Durable dog toy");
		product1.setName("Kong");
		
		DAO.addProductToCommit(product1);
		DAO.commitProductsToDatabase();
		
		MF_Product testProduct;
		testProduct = DAO.getProduct(product1);
		
		assertTrue(product1.equals(testProduct));
		DAO.delete(product1);
	}
	
	@Test
	public void GetProduct_SendingUPC_ExpectNotInDatabaseNull() throws SQLException {
		if (DAO != null) {
			if (DAO.isOpen()) {
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		
		String UPC = "0003084262814";
		MF_Product testProduct = DAO.getProduct(UPC);
		
		assertNull(testProduct);
	}
	
	@Test
	public void GetProduct_SendingInvalidUPC_ExpectNotInDatabaseNull() throws SQLException {
		if (DAO != null) {
			if (DAO.isOpen()) {
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		
		String UPC = "sdfsdfsdfsdfsd";
		MF_Product testProduct = DAO.getProduct(UPC);
		
		assertNull(testProduct);
	}
	
	@Test
	public void GetProduct_SendingInvalidProduct_ExpectNotInDatabaseNull() throws SQLException {
		if (DAO != null) {
			if (DAO.isOpen()) {
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		MF_Product product = new MF_Product("dgdgd", new ArrayList<MF_Price>());
		MF_Product testProduct = DAO.getProduct(product);
		
		assertNull(testProduct);
	}
	
	@Test
	public void GetProduct_SendingOneValidProduct_ExpectValidatedProduct() throws SQLException {
		if (DAO != null) {
			if (DAO.isOpen()){
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		
		String UPC = "0676108080581";
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price> ();
		priceList.add(new MF_Price(15.75, "Walmart"));
		MF_Product product = new MF_Product(UPC, priceList);
		
		DAO.addProductToCommit(product);
		DAO.commitProductsToDatabase();
		
		MF_Product testProduct;
		testProduct = DAO.getProduct(product);
		
		assertTrue(MF_ProductValidator.validate().Product(testProduct));
		DAO.delete(product);
	}
	
	@Test
	public void GetProductUPC_SendingOneValidProduct_ExpectEqualProduct() throws SQLException {
		if (DAO != null) {
			if (DAO.isOpen()){
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		
		String UPC = "0097368886025";
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price> ();
		priceList.add(new MF_Price(22.55, "Walmart"));
		MF_Product product = new MF_Product(UPC, priceList);
		
		DAO.addProductToCommit(product);
		DAO.commitProductsToDatabase();

		
		MF_Product testProduct;
		testProduct = DAO.getProduct(UPC);
		
		assertTrue(product.equals(testProduct));
		DAO.delete(product);
	}
	
	@Test
	public void GetProductUPC_SendingValidUPC_ExpectValidatedProduct() throws SQLException {
		if (DAO != null) {
			if (!DAO.isOpen()){
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		
		String UPC = "0811406171399";
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price> ();
		priceList.add(new MF_Price(56.33, "Amazon"));
		MF_Product product = new MF_Product(UPC, priceList);
		
		DAO.addProductToCommit(product);
		DAO.commitProductsToDatabase();
		
		MF_Product testProduct;
		testProduct = DAO.getProduct(UPC);
		assertTrue(MF_ProductValidator.validate().Product(testProduct));
		DAO.delete(product);
	}
	
	@Test
	public void DeleteUPC_InvalidUPC_ExpectFalse() throws SQLException {
		if (DAO != null) {
			if (!DAO.isOpen()){
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		String UPC = "5643211";
		assertFalse(DAO.delete(UPC));
	}
	
	@Test
	public void DeleteProduct_InvalidProduct_ExpectFalse() throws SQLException {
		if (DAO != null) {
			if (!DAO.isOpen()){
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		MF_Product product = new MF_Product("33333", new ArrayList<MF_Price>());
		assertFalse(DAO.delete(product));
	}
	
	@Test
	public void DeleteProduct_ValidProductNotInDatabase_ExpectFalse() throws SQLException {
		if (DAO != null) {
			if (!DAO.isOpen()){
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		
		String UPC = "0083791070100";
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price> ();
		priceList.add(new MF_Price(782.55, "Amazon"));
		MF_Product product = new MF_Product(UPC, priceList);
		
		assertFalse(DAO.delete(product));
	}
	
	@Test
	public void DeleteUPC_ValidUPCNotInDatabase_ExpectFalse() throws SQLException {
		if (DAO != null) {
			if (!DAO.isOpen()){
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		
		String UPC = "0083791070100";
		
		assertFalse(DAO.delete(UPC));
	}
	
	
	@Test
	public void DeleteUPC_ValidUPC_ExpectTrue() throws SQLException {
	
		if (DAO != null) {
			if (!DAO.isOpen()){
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		
		String UPC = "0813263011900";
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price> ();
		priceList.add(new MF_Price(35.12, "Amazon"));
		MF_Product product = new MF_Product(UPC, priceList);
		
		DAO.addProductToCommit(product);
		DAO.commitProductsToDatabase();
		
		assertTrue(DAO.delete(UPC));
	}
	
	@Test
	public void DeleteUPC_ValidUPC_ExpectNotInProductSet() throws SQLException {
	
		if (DAO != null) {
			if (!DAO.isOpen()){
				DAO.close();
			}
		}
		DAO = new MF_ProductsDAO ("testing");
		
		String UPC = "0735340904465";
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price> ();
		priceList.add(new MF_Price(25.22, "Amazon"));
		MF_Product product = new MF_Product(UPC, priceList);
		
		DAO.addProductToCommit(product);
		DAO.commitProductsToDatabase();
		DAO.delete(UPC);
		assertFalse(DAO.getProductSet().contains(UPC));
	}
	
	

}
