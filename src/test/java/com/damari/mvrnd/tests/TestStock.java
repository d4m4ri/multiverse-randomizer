package com.damari.mvrnd.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static com.damari.mvrnd.algorithm.Strategy.price;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import com.damari.mvrnd.coin.CoinSecureRandom;
import com.damari.mvrnd.data.DataGenerator;

public class TestStock {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");

	@Test
	public void givenGenerate10000StockDataEntriesUsingHeadsOnlyCoinThenExpect10000StockEntries() throws Exception {
		float coinSkew = 100.00f; // always heads
		long time = DATE_TIME_FORMATTER.parseDateTime("2016-11-18T09:00:00.000+0100").getMillis();
		int price = price(80.00f);
		int priceStep = price(0.05f);
		long timeStep = 300;
		int stockDataSizeReq = 10_000;

		DataGenerator stock = new DataGenerator();
		int stockDataSizeGen = stock.generateRandomWalk(new CoinSecureRandom(coinSkew), stockDataSizeReq, time, price,
				priceStep, timeStep);

		assertEquals("Stock data size should be equal to requested size", stockDataSizeReq, stockDataSizeGen);
	}


	@Test
	public void givenGenerate10000StockDataEntriesUsingTailsOnlyCoinThenExpectLessThan10000StockEntries() throws Exception {
		float coinSkew = 0.00f; // always tail
		long time = DATE_TIME_FORMATTER.parseDateTime("2016-11-18T09:00:00.000+0100").getMillis();
		int price = price(80.00f);
		int priceStep = price(0.05f);
		long timeStep = 300;
		int stockDataSizeReq = 10_000;

		DataGenerator stock = new DataGenerator();
		int stockDataSizeGen = stock.generateRandomWalk(new CoinSecureRandom(coinSkew), stockDataSizeReq, time, price, priceStep, timeStep);

		assertNotEquals("Stock data size shouldn't be equal to requested size considering coin tail probability is 100%",
				stockDataSizeReq, stockDataSizeGen);
	}

}
