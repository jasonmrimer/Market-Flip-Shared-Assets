package com.marketflip.shared.products;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/*
 * @author David Walters
 * Product holds all information on price points, descriptors and URL to the product on the company's website.
 * @Updated - 12/21/2015
 * TODO:
 * 	-comments
 */
public class MF_Product {

	private String 					name;
	private String 					description;
	private String 					UPC;
	private String 					UNSPSC;
	private URL 					linkToProduct;
	private ArrayList<MF_Price> 	prices;
	private double 					height;
	private double 					width;
	private double 					length;
	private double 					weight;

	public MF_Product() {
		this.name	 		= null;
		this.description 	= null;
		this.UPC 			= null;
		this.UNSPSC 		= null;
		this.linkToProduct 	= null;
		this.prices 		= null;
		this.height 		= 0.0f;
		this.width 			= 0.0f;
		this.length 		= 0.0f;
		this.weight 		= 0.0f;
	}
	
// @Deprecated - business logic must be that a product needs a UPC and at least 1 price.
//	public MF_Product(String upc) {
//		this.name	 		= null;
//		this.description 	= null;
//		this.UPC 			= upc;
//		this.UNSPSC 		= null;
//		this.linkToProduct 	= null;
//		this.prices 		= null;
//		this.height 		= 0.0f;
//		this.width 			= 0.0f;
//		this.length 		= 0.0f;
//		this.weight 		= 0.0f;
//	}

	public MF_Product(String name, String description, String UPC, String UNSPSC, URL linkToProduct,
			ArrayList<MF_Price> prices, double height, double width, double length, double weight) {
		this.name 			= name;
		this.description 	= description;
		this.UPC 			= UPC;
		this.UNSPSC 		= UNSPSC;
		this.linkToProduct 	= linkToProduct;
		this.prices 		= prices;
		this.height 		= height;
		this.width 			= width;
		this.length 		= length;
		this.weight 		= weight;
	}
	
	public MF_Product(String UPC, ArrayList<MF_Price> priceList) {
		this.UPC = UPC;
		this.prices = priceList;
		this.name	 		= null;
		this.description 	= null;
		this.UNSPSC 		= null;
		this.linkToProduct 	= null;
		this.height 		= 0.0f;
		this.width 			= 0.0f;
		this.length 		= 0.0f;
		this.weight 		= 0.0f;
	}
	
	public MF_Product(String UPC, MF_Price price) {
		this.UPC = UPC;
		ArrayList<MF_Price> priceList = new ArrayList<MF_Price>();
		priceList.add(price);
		this.prices 		= priceList;
		this.name	 		= null;
		this.description 	= null;
		this.UNSPSC 		= null;
		this.linkToProduct 	= null;
		this.height 		= 0.0f;
		this.width 			= 0.0f;
		this.length 		= 0.0f;
		this.weight 		= 0.0f;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUPC() {
		return UPC;
	}

	public void setUPC(String uPC) {
		UPC = uPC;
	}

	public String getUNSPSC() {
		return UNSPSC;
	}

	public void setUNSPSC(String uNSPSC) {
		UNSPSC = uNSPSC;
	}

	public URL getLinkToProduct() {
		return linkToProduct;
	}

	public void setLinkToProduct(URL linkToProduct) {
		this.linkToProduct = linkToProduct;
	}

	public ArrayList<MF_Price> getPrices() {
		return prices;
	}

	public void setPrices(ArrayList<MF_Price> prices) {
		this.prices = prices;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public MF_Price getCurrentLowestPrice() {
		
		MF_Price lowestPrice = new MF_Price();
		lowestPrice.setPrice(99999999.99);
		HashMap<String, MF_Price> companyPriceMap = new HashMap();
		ArrayList<String> companyKeys = new ArrayList();
		
		for (int i = 0; i < prices.size(); i++) {
			
			double price = prices.get(i).getPrice();
			String company = prices.get(i).getCompany();
			Date date = prices.get(i).getDate();
			
			if (companyPriceMap.containsKey(company)){
				if (date.getTime() > companyPriceMap.get(company).getDate().getTime()) {
					companyPriceMap.put(company, new MF_Price(price, date, company));
				}
			} else {
				companyPriceMap.put(company, new MF_Price(price, date, company));
				companyKeys.add(company);
			}
		}
		
		for (int i = 0; i < companyKeys.size(); i++) {
			String company = companyKeys.get(i);
			if (companyPriceMap.get(company).getPrice() < lowestPrice.getPrice()){
				lowestPrice = companyPriceMap.get(company);
			}
		}
		
		return lowestPrice;
				
	}

	/**
	 * Returns if the product can reasonably be considered a match with the
	 * current Product.
	 * 
	 * @param product
	 *            The product to compare to this object.
	 * @return int Returns 1 if the product is a match, 0 if the product is not
	 *         a match.
	 */
	public int compareTo(MF_Product product) {
		if (this.UPC.equals(product.getUPC())) {
			return 1;
		} else if (this.UNSPSC == product.getUNSPSC()) {
			return 1;
		} else if (this.description == product.getDescription()) {
			return 1;
		} else if (this.name == product.getName()) {
			if (this.weight * 1.1 >= product.getWeight() && this.weight * 0.9 <= product.getWeight()) {
				return 1;
			}
		}
		return 0;
	}

	/**
	 * Returns if the product can reasonably be considered a match with the
	 * current Product.
	 * 
	 * @param product
	 *            The product to compare to this object.
	 * @return boolean Returns true if the product is a reasonable match.
	 */
	public boolean equals(MF_Product product) {
		if (this.UPC.equals(product.getUPC())) {
			return true;
		} else if (this.UNSPSC == product.getUNSPSC() && product.getUNSPSC() != null) {
			return true;
		} else if (this.description == product.getDescription()  && product.getDescription() != null) {
			return true;
		} else if (this.name.equals(product.getName())  && product.getName() != null) {
			if (product.getWeight() != 0) {
				if (this.weight * 1.1 >= product.getWeight() && this.weight * 0.9 <= product.getWeight()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Prints a stream with the variabes from this class. Primarily used for development and testing.
	 */
	public void print() {
		System.out.println ("Name: " + this.name);
		System.out.println ("Description: " + this.description);
		System.out.println ("UPC: " + this.UPC);
		System.out.println ("UNSPSC: " + this.UNSPSC);
		if (this.linkToProduct != null) {
			System.out.println ("URL: " + this.linkToProduct.toExternalForm());
		}
		System.out.println ("Height: " + this.height);
		System.out.println ("Width: " + this.width);
		System.out.println ("Length: " + this.length);
		System.out.println ("Weight: " + this.weight);
		System.out.println ("-----------------------------");
		System.out.println ("------------PRICES-----------");
		System.out.println ("-----------------------------");
		for (int i = 0; i < prices.size(); i++) {
			System.out.println ("Date: " + prices.get(i).getDate().toString());
			System.out.println ("Price: " + prices.get(i).getPrice());
			System.out.println ("Company ID: " + prices.get(i).getCompany());
		}
	}

}
