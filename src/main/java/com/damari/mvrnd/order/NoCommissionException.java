package com.damari.mvrnd.order;

public class NoCommissionException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoCommissionException(String message) {
		super(message);
	}

}
