package com.damari.mvrnd.data;

public class OutOfMoneyException extends Exception {

	private static final long serialVersionUID = 1L;

	public OutOfMoneyException(String message) {
		super(message);
	}

}
