package com.marketflip.shared.data.exceptions;

public class ProductValidationException extends Exception {
	
	public ProductValidationException () {
		super ("MF_Product object cannot be empty");
	}
	
	public ProductValidationException (String message) {
		super(message);
	}
	
	public ProductValidationException (Throwable cause) {
		super(cause);
	}
	
	public ProductValidationException (String message, Throwable cause) {
		super(message, cause);
	}
	
	

}
