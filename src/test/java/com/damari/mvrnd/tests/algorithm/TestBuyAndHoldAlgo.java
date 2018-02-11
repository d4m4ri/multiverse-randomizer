package com.damari.mvrnd.tests.algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static com.damari.mvrnd.algorithm.Strategy.price;
import static com.damari.mvrnd.algorithm.Strategy.dateTimeFormatter;

import org.junit.Test;

import com.damari.mvrnd.algorithm.BuyAndHold;
import com.damari.mvrnd.algorithm.BuyAndHoldConfig;
import com.damari.mvrnd.coin.Coin;
import com.damari.mvrnd.data.DataGenerator;
import com.damari.mvrnd.data.Statistics;
import com.damari.mvrnd.order.Broker;
import com.damari.mvrnd.tests.TestTrade;

public class TestBuyAndHoldAlgo {

	@Test
	public void givenDoublingOfAssetThenExpectProfit() throws Exception {
		int deposit = price(10_000.00f);
		float commission = 0.00069f;
		float initialPrice = 100.00f;
		int price = price(initialPrice);
		int spread = price(0.10f);
		int tradeSize = 50;
		Broker broker = new Broker();
		broker.deposit(deposit).setCommissionPercent(commission);

		DataGenerator asset = new DataGenerator();
		int bucket = asset.lockBucket();
		long time = dateTimeFormatter.parseDateTime("2016-11-18T09:00:00.000+0100").getMillis();

		// Render doubling of price
		long[] timeSerie = new long[1000];
		int[] priceSerie = new int[1000];
		for (int i = 0; i < 1000; i++) {
			timeSerie[i] = time;
			priceSerie[i] = price;
			time += 60_000;
			price += spread;
		}
		asset.apply(bucket, timeSerie, priceSerie);

		// Run algo
		BuyAndHoldConfig config = new BuyAndHoldConfig();
		BuyAndHold algo = new BuyAndHold(config, broker, spread, tradeSize);
		for (int i = 0; i < timeSerie.length; i++) {
			algo.process(asset.getTime(i), asset.getPrice(i));
		}

		assertEquals("Unexpected commission", price(3.45f), price(tradeSize * initialPrice * commission));
		assertEquals("Unexpected balance", price(9996.55f), broker.getBalance());
		assertEquals("Unexpected NAV", price(4995.00f), algo.getNAV());

		asset.unlockBucket(bucket);
	}

	@Test
	public void givenLongSPY500InBearMarketThenExpectLosses() throws Exception {
		int iters = 1_000;
		float coinSkew = 49.750f;
		int deposit = price(100_000.00f);
		float commission = 0.00069f;
		float goalPercent = 8;
		float riskPercent = 100;
		long startTime = dateTimeFormatter.parseDateTime("2017-10-13T09:00:00.000+0100").getMillis();
		int price = price(2762.00f);
		int tradeSize = 600;
		int spread = price(0.25f);
		long timeStepMs = 3_456;
		int dataSize = 10_000_000;
		BuyAndHoldConfig config = new BuyAndHoldConfig();
		Coin coin = new com.damari.mvrnd.coin.CoinXoRoShiRo128PlusRandom(coinSkew);
		Statistics stats = TestTrade.usingThreads(BuyAndHold.class, config, iters, coin, deposit,
				commission, goalPercent, riskPercent, startTime, price, tradeSize, spread, timeStepMs, dataSize);

		assertTrue("Expected loss, got $" + stats.getWinLoss(), stats.getWinLoss() < 0);
	}

}
