package com.marketflip.shared;

import java.net.URL;
import java.util.ArrayList;

/*
 * @author David Walters
 * Product holds all information on price points, descriptors and URL to the product on the company's website.
 * Updated - 09/28/2015
 * TODO:
 * 	-comments
 */
public class MF_Product {

	private String name;
	private String description;
	private String UPC;
	private String UNSPSC;
	private String company;
	private URL linkToProduct;
	private ArrayList<MF_Price> prices;
	private double height;
	private double width;
	private double length;
	private double weight;

	public MF_Product() {
		this.name = null;
		this.description = null;
		this.UPC = null;
		this.UNSPSC = null;
		this.linkToProduct = null;
		this.prices = null;
		this.height = 0.0f;
		this.width = 0.0f;
		this.length = 0.0f;
		this.weight = 0.0f;
	}

	public MF_Product(String name, String description, String UPC, String UNSPSC, String company, URL linkToProduct,
			ArrayList<MF_Price> prices, double height, double width, double length, double weight) {
		this.name = name;
		this.description = description;
		this.UPC = UPC;
		this.UNSPSC = UNSPSC;
		this.linkToProduct = linkToProduct;
		this.prices = prices;
		this.height = height;
		this.width = width;
		this.length = length;
		this.weight = weight;
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

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
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
		if (this.UPC == product.getUPC()) {
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
		if (this.UPC == product.getUPC()) {
			return true;
		} else if (this.UNSPSC == product.getUNSPSC()) {
			return true;
		} else if (this.description == product.getDescription()) {
			return true;
		} else if (this.name == product.getName()) {
			if (this.weight * 1.1 >= product.getWeight() && this.weight * 0.9 <= product.getWeight()) {
				return true;
			}
		}
		return false;
	}

}
