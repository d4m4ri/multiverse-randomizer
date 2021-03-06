package com.damari.mvrnd.tests.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import static com.damari.mvrnd.algorithm.Algorithm.price;
import static com.damari.mvrnd.coin.Coin.fair;
import static com.damari.mvrnd.coin.Coin.headsOnly;
import static com.damari.mvrnd.coin.Coin.tailsOnly;

import java.util.Arrays;
import java.util.List;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import com.damari.mvrnd.coin.Coin;
import com.damari.mvrnd.coin.CoinNeumannENIACRandom;
import com.damari.mvrnd.coin.CoinRandom;
import com.damari.mvrnd.coin.CoinSecureRandom;
import com.damari.mvrnd.coin.CoinSplittableRandom;
import com.damari.mvrnd.coin.CoinThreadLocalRandom;
import com.damari.mvrnd.coin.CoinXoRoShiRo128PlusRandom;
import com.damari.mvrnd.data.DataGenerator;
import com.damari.mvrnd.data.DataSet;

public class TestDataGeneration {

	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");

	@Test
	public void givenGenerate4000StockDataEntriesUsingHeadsOnlyCoinThenExpect4000StockEntries() throws Exception {
		for (Coin coin : coins(headsOnly)) {
			long time = dateTimeFormatter.parseDateTime("2016-11-18T09:00:00.000+0100").getMillis();
			int price = price(80.00f);
			int priceStep = price(0.05f);
			long timeStep = 300;
			int stockDataSizeReq = 4_000;

			DataGenerator stock = new DataGenerator();
			int datasetId = stock.lock();
			int stockDataSizeGen = stock.generate(datasetId, coin, stockDataSizeReq, time, price,
					priceStep, timeStep);

			assertEquals("Stock data size should be equal to requested size", stockDataSizeReq, stockDataSizeGen);
			stock.unlock(datasetId);
		}
	}

	@Test
	public void givenGenerate5000StockDataEntriesUsingTailsOnlyCoinThenExpectLessThan5000StockEntries() throws Exception {
		for (Coin coin : coins(tailsOnly)) {
			long time = dateTimeFormatter.parseDateTime("2017-05-18T09:00:00.000+0100").getMillis();
			int price = price(90.00f);
			int priceStep = price(0.05f);
			long timeStep = 300;
			int stockDataSizeReq = 5_000;

			DataGenerator stock = new DataGenerator();
			int datasetId = stock.lock();
			int stockDataSizeGen = stock.generate(datasetId, coin, stockDataSizeReq, time, price, priceStep, timeStep);

			assertNotEquals("Stock data size shouldn't be equal to requested size considering coin tail probability is 100%",
					stockDataSizeReq, stockDataSizeGen);
			stock.unlock(datasetId);
		}
	}

	@Test
	public void givenGenerateDataTwiceForEachBucketForEachCoinThenExpectFirstGenerationToBeOverwritten() throws Exception {
		for (Coin coin : coins(fair)) {
			for (int buckets = 0; buckets < DataGenerator.maxDatasets; buckets++) {
				// Generate random time and price data series
				long time = dateTimeFormatter.parseDateTime("2018-01-18T09:00:00.000+0100").getMillis();
				int price = price(50.00f);
				int priceStep = price(0.05f);
				long timeStep = 300;
				int dataSizeReq = 3_000;
				DataGenerator asset = new DataGenerator();
				int datasetId = asset.lock();

				int dataSizeGen = asset.generate(datasetId, coin, dataSizeReq, time, price, priceStep, timeStep);
				assertEquals("Asset data size should be equal to requested size", dataSizeReq, dataSizeGen);

				// Generate zero data price data series
				long[] timeSeries = new long[dataSizeReq];
				int[] priceSeries = new int[dataSizeReq];
				for (int i = 0; i < dataSizeReq; i++) {
					timeSeries[i] = time;
					priceSeries[i] = 0;
					time += 1;
				}
				DataSet dataSet = new DataSet(timeSeries, priceSeries);
				asset.apply(datasetId, dataSet);

				for (int i = 0; i < dataSizeReq; i++) {
					price = asset.getPrice(datasetId, i);
					assertEquals("Asset price should be zero", 0, price);
				}

				asset.unlock(datasetId);
			}
		}
	}

	private List<Coin> coins(float probability) {
		return Arrays.asList(
				new CoinNeumannENIACRandom(probability),
				new CoinRandom(probability),
				new CoinSecureRandom(probability),
				new CoinSplittableRandom(probability),
				new CoinThreadLocalRandom(probability),
				new CoinXoRoShiRo128PlusRandom(probability));
	}

}
