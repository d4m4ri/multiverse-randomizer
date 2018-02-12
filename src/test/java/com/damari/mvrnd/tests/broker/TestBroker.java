package com.damari.mvrnd.tests.broker;

import org.joda.time.DateTime;
import org.junit.Test;

import com.damari.mvrnd.data.OutOfMoneyException;
import com.damari.mvrnd.order.Broker;
import com.damari.mvrnd.order.CommissionUndefinedException;

public class TestBroker {

	@Test(expected=OutOfMoneyException.class)
	public void givenNoMoneyAndBuyThenExpectOutOfMoneyException() throws Exception {
		Broker broker = new Broker();
		broker
			.deposit(0)
			.setCommissionPercent(0.123456f);

		int price = (int)(1.00f * 100f);
		int spread = (int)(0.01f * 100f);
		int size = 500;
		broker.buy(new DateTime().getMillis(), price, spread, size);
	}

	@Test(expected=CommissionUndefinedException.class)
	public void givenUndefinedCommissionThenExpectException() throws Exception {
		Broker broker = new Broker();
		broker.deposit(100_000);

		int price = (int)(1.00f * 100f);
		int spread = (int)(0.01f * 100f);
		int size = 500;
		broker.buy(new DateTime().getMillis(), price, spread, size);
	}

}