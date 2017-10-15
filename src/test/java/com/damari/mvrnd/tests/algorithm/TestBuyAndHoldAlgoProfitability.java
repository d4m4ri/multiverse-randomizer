package com.damari.mvrnd.tests.algorithm;

import static com.damari.mvrnd.algorithm.Strategy.dateTimeFormatter;
import static com.damari.mvrnd.algorithm.Strategy.price;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.damari.mvrnd.algorithm.BuyAndHold;
import com.damari.mvrnd.algorithm.BuyAndHoldConfig;
import com.damari.mvrnd.coin.Coin;
import com.damari.mvrnd.coin.CoinXoRoShiRo128PlusRandom;
import com.damari.mvrnd.data.Statistics;
import com.damari.mvrnd.tests.TestTrade;

public class TestBuyAndHoldAlgoProfitability {

	@Test
	public void testFingerprint() throws Exception {
		int iters = 8_000;
		float coinSkew = 49.750f;
		int deposit = price(100_000.00f);
		float commission = 0.0025f;
		float goalPercent = 8;
		float riskPercent = 100;
		long time = dateTimeFormatter.parseDateTime("2017-10-13T09:00:00.000+0100").getMillis();
		int price = price(22.40f);
		int tradeSize = 600;
		int spread = price(0.10f);
		long timeStepMs = 3_456;
		int stockDataSize = 1_400_000;
		BuyAndHoldConfig config = new BuyAndHoldConfig();
		Coin coin = new CoinXoRoShiRo128PlusRandom(coinSkew);
		Statistics stats = TestTrade.usingThreads(BuyAndHold.class, config, iters, coin, deposit,
				commission, goalPercent, riskPercent, time, price, tradeSize, spread, timeStepMs, stockDataSize);

		assertTrue("Expected loss, got $" + stats.getWinLoss(), stats.getWinLoss() < 0);
	}

}
