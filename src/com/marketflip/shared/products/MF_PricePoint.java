package com.marketflip.shared.products;

/**
 * The purpose of this class is to act as a tuple then later as a integrity check when shoppers
 * commit price points while shopping. For example, it will interpret lightweight JSON packets into
 * this Java class in order to test whether the UPC found on the webpage actually exists in the
 * Product DB. It will add the product on the spot or validate that it already exists and
 * concatenate the prefic UPC_ to the UPC then feed the price point to the Shopper DB for insertion
 * to the shopper's Price Point Table with a valid product reference and price.
 *
 * @author highball
 *
 */
public class MF_PricePoint<String, Double> {

	private final String	productTableID;
	private final Double	price;

	public MF_PricePoint(String productUPC, Double price) {
		this.productTableID = (String) ("UPC_" + productUPC);
		this.price = price;
	}

	@Override
	public int hashCode() {
		return productTableID.hashCode() ^ price.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MF_PricePoint)) return false;
		MF_PricePoint ppO = (MF_PricePoint) o;
		return this.productTableID.equals(ppO.getProductTableID())
				&& this.price.equals(ppO.getPrice());
	}

	public String getProductTableID() {
		return productTableID;
	}

	public Double getPrice() {
		return price;
	}
}
