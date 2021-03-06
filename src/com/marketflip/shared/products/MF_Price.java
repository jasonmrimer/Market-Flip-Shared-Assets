package com.marketflip.shared.products;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*
 * @author David Walters
 * Price holds date related to the price of the object.
 * Updated - 09/28/2015
 */

public class MF_Price {

	private double price;
	private Date date;
	private String company;

	/**
	 * Default constructor for Price, sets the price at 0.00, the date to the
	 * current time and the company to null.
	 */
	public MF_Price() {
		this.price = 0.00;
		this.date = (Date) Calendar.getInstance().getTime();
		this.company = null;
	}

	/**
	 * Constructor for Price.
	 * 
	 * @param price
	 *            The price for the product.
	 * @param date
	 *            The date and time that the price was found.
	 * @param company
	 *            The company where the price was found.
	 */
	public MF_Price(double price, Date date, String company) {
		this.price = price;
		this.date = date;
		this.company = company;
	}

	/**
	 * Constructor for Price. Company is set to null.
	 * 
	 * @param price
	 *            The price for the product.
	 * @param date
	 *            The date and time that the price was found.
	 */
	public MF_Price(double price, Date date) {
		this.price = price;
		this.date = date;
		this.company = null;
	}

	/**
	 * Constructor for Price. Date is set to the current date and time.
	 * 
	 * @param price
	 *            The price for the product.
	 * @param company
	 *            The company where the prices was found.
	 */
	public MF_Price(double price, String company) {
		this.price = price;
		this.company = company;
		this.date = Calendar.getInstance().getTime();
	}

	/**
	 * Returns the price of the Price object
	 * 
	 * @return double The price of the price object.
	 */
	public double getPrice() {
		return this.price;
	}

	/**
	 * Returns the date and time associated with this Price object.
	 * 
	 * @return Date The date associated to this Price object.
	 */
	public Date getDate() {
		return this.date;
	}
	
	//TODO: getDate that returns a string here.

	/**
	 * Set the price of this Price object
	 * 
	 * @param price
	 *            The price to be set to the Price object.
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * Set the date object associated with the price from java.util.date
	 * 
	 * @param date
	 *            The java.util.Date object associated with the Price object.
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	
	/**
	 * Set the date object associated with the price using an epoch timestamp.
	 * 
	 * @param long date
	 * 	Time in millisecons since the epoch
	 */
	public void setDate(long date) {
		this.date = new Date(date);
	}
	
	/**
	 * Set the date associated with the price from java.util.date
	 */
	
	/**
	 * Returns the name of the company where the price was found.
	 * 
	 * @return String The name of the company where the price was found.
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * Sets the name of the company where the price was found.
	 * 
	 * @param company
	 *            The name of the company where the price was found.
	 */
	public void setCompany(String company) {
		this.company = company;
	}

	/**
	 * @return String The object represented as a string in the format of Price:
	 *         ... Date: dow mon dd hh:mm:ss zzz yyyy ... Company: ...
	 */
	@Override
	public String toString() {
		return "Price: " + this.price + " Date: " + this.date.toString() +
				" Company: " + this.company;
	}
	
	/**
	 * @return Boolean If the price values are equal.
	 * @param The MF_Price object to check against this.
	 */
	public boolean equals (MF_Price price) {
		if (this.price == price.getPrice()) {
			if (this.date.equals(price.getDate())){
				
				if (this.company != null && price.getCompany() != null){
					if (this.company.equals(price.getCompany())){
						return true;
					}
				} else {
					return true;
				}
				
			}
		}
		return false;
	}

}
