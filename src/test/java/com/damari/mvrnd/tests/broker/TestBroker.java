/*
 * TODO: balance
 * TODO: commissionPercent
 */
package com.damari.mvrnd.tests.broker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static com.damari.mvrnd.algorithm.Algorithm.price;

import org.joda.time.DateTime;
import org.junit.Test;

import com.damari.mvrnd.data.OutOfMoneyException;
import com.damari.mvrnd.broker.Broker;
import com.damari.mvrnd.broker.CommissionUndefinedException;

public class TestBroker {

	@Test
	public void givenNoCommissionsAndNoSpreadThenExpectZeroSumGame() throws CommissionUndefinedException, OutOfMoneyException {
		int deposit = price(100_000f);
		Broker broker = new Broker();
		broker
			.deposit(deposit)
			.setCommissionPercent(0f);

		int spread = price(0f);
		int price = price(6000f);
		int size = 10;
		int time = 0;
		for (int i = 0; i < 10_000; i++) {
			broker.buy(++time, price, spread, size);
			broker.sell(++time, price, spread, size);
		}

		assertEquals("Expected zero-sum outcome", deposit, broker.getBalance());
	}

	@Test
	public void givenCommissionsButNoSpreadThenExpectNegativeSumGame() throws CommissionUndefinedException, OutOfMoneyException {
		int deposit = price(100_000f);
		Broker broker = new Broker();
		broker
			.deposit(deposit)
			.setCommissionPercent(0.0069f);

		int spread = price(0f);
		int price = price(6000f);
		int size = 10;
		int time = 0;
		for (int i = 0; i < 10_000; i++) {
			broker.buy(++time, price, spread, size);
			broker.sell(++time, price, spread, size);
		}

		assertTrue("Expected negative-sum outcome", broker.getBalance() < deposit);
	}

	@Test
	public void givenNoSpreadButCommissionsThenExpectNegativeSumGame() throws CommissionUndefinedException, OutOfMoneyException {
		int deposit = price(100_000f);
		Broker broker = new Broker();
		broker
			.deposit(deposit)
			.setCommissionPercent(0.0001f);

		int spread = price(0f);
		int price = price(6000f);
		int size = 10;
		int time = 0;
		for (int i = 0; i < 10_000; i++) {
			broker.buy(++time, price, spread, size);
			broker.sell(++time, price, spread, size);
		}

		assertTrue("Expected negative-sum outcome", broker.getBalance() < deposit);
	}

	@Test
	public void givenDepositThenExpectDepositToMatch() {
		int deposit = price(10_000.00f);
		Broker broker = new Broker();
		broker.deposit(deposit);

		assertEquals("Unexpected deposit", deposit, broker.getBalance());
	}

	@Test(expected=OutOfMoneyException.class)
	public void givenNoMoneyAndBuyThenExpectOutOfMoneyException() throws Exception {
		Broker broker = new Broker();
		broker
			.deposit(0)
			.setCommissionPercent(0.123456f);

		int price = price(1.00f);
		int spread = price(0.01f);
		int size = 500;
		broker.buy(new DateTime().getMillis(), price, spread, size);
	}

	@Test(expected=CommissionUndefinedException.class)
	public void givenUndefinedCommissionThenExpectException() throws Exception {
		Broker broker = new Broker();
		broker.deposit(100_000);

		int price = price(1.00f);
		int spread = price(0.01f);
		int size = 500;
		broker.buy(new DateTime().getMillis(), price, spread, size);
	}

}
