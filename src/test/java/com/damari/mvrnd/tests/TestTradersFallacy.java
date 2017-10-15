package com.damari.mvrnd.tests;

import static com.damari.mvrnd.algorithm.Strategy.price;
import static com.damari.mvrnd.algorithm.Strategy.round;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.damari.mvrnd.coin.Coin;
import com.damari.mvrnd.coin.CoinXoRoShiRo128PlusRandom;
import com.damari.mvrnd.tests.algorithm.TestBuyAndHoldAlgoProfitability;

public class TestTradersFallacy {

	private static final Logger log = LoggerFactory.getLogger(TestBuyAndHoldAlgoProfitability.class.getName());

	@Test
	public void givenTrading10gAt50_50ThenExpectToLoseAllYourMoney() {
		for (int attempts = 0; attempts < 50; attempts++) {
			int balance = price(10_000);
			float coinProbability = 50.00f;
			Coin coin = new CoinXoRoShiRo128PlusRandom(coinProbability);
			int commissionAmount = price(0.25f);
			int winOrLoseAmount = price(200.00f);
			int busTicket = winOrLoseAmount + price(30.00f);
			long i = 0;
			while (balance > busTicket) {
				if (coin.toss() == Coin.HEAD) {
					balance -= winOrLoseAmount;
				} else {
					balance += winOrLoseAmount;
				}
				balance -= commissionAmount;
				i++;
			}
			assertTrue("Number of iterations should at least be 50, was " + i, i > 50);
			log.info("Trader lost fair and square @ iteration {} (${} should cover bus home)", i, round(balance));
		}
	}

}
