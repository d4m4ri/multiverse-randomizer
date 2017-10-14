package com.damari.mvrnd.tests;

import org.joda.time.DateTime;
import org.junit.Test;

import com.damari.mvrnd.data.OutOfMoneyException;
import com.damari.mvrnd.order.Broker;

public class TestOrders {

	@Test(expected=OutOfMoneyException.class)
	public void givenNoMoneyAndBuyThenExpectOutOfMoneyException() throws Exception {
		Broker broker = new Broker();
		broker
			.deposit((int)(0.00f * 100f))
			.setCommissionPercent(0.123456f);
		int price = (int)(1.00f * 100f);
		int spread = (int)(0.01f * 100f);
		int size = 500;
		broker.buy(new DateTime().getMillis(), price, spread, size);
	}

}
