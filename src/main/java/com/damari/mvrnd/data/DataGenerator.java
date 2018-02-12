/*
 * Generate datasets of time and price thread safe.
 */
package com.damari.mvrnd.data;

import static com.damari.mvrnd.coin.Coin.head;

import java.util.concurrent.atomic.AtomicBoolean;

import com.damari.mvrnd.app.App;
import com.damari.mvrnd.coin.Coin;

public class DataGenerator {

	private static final Object lock = new Object();

	public static final int maxDatasets = App.getPhysicalCores();
	private static int maxData = 60_000_000; // >= max requested data points in a thread

	private static long[][] datasetTime;
	private static int[][] datasetPrice;
	private static AtomicBoolean[] datasetUsed;

	private static int datasets = -1;

	/** Used as cache for performance */
	private int minPrice;
	private int maxPrice;
	private long startTime;
	private long stopTime;

	public DataGenerator() {
		// Init data once
		synchronized (lock) {
			if (datasets == -1) {
				datasets = maxDatasets;
				System.err.println("INITIALIZING DATA SERIES MEMORY (" + memUsage() + "M / " + maxDatasets + "T)");
				datasetTime = new long[datasets][maxData];
				datasetPrice = new int[datasets][maxData];
				datasetUsed = new AtomicBoolean[datasets];
				for (int i = 0; i < datasets; i++) {
					datasetUsed[i] = new AtomicBoolean(false);
				}
			}
		}
	}

	/**
	 * Calculate memory usage based on time- and price series.
	 * @return int with memory usage in MB.
	 */
	public int memUsage() {
		float muTimeSerieB = datasets * maxData * (Long.SIZE / 8f);
		float muPriceSerieB = datasets * maxData * (Integer.SIZE / 8f);
		float sum = (muTimeSerieB + muPriceSerieB) / 1000f / 1000f;
		return (int) sum;
	}

	/**
	 * Generate time x price entries without drift. May generate no data if price is at zero + spread.
	 * Investopedia (http://www.investopedia.com/articles/trading/07/stationary.asp):
	 * "Pure Random Walk (Yt = Yt-1 + εt)
	 * Random walk predicts that the value at time "t" will be equal to the last period value plus a
	 * stochastic (non-systematic) component that is a white noise, which means εt is independent and
	 * identically distributed with mean "0" and variance "σ²". Random walk can also be named a process
	 * integrated of some order, a process with a unit root or a process with a stochastic trend. It is
	 * a non mean reverting process that can move away from the mean either in a positive or negative
	 * direction. Another characteristic of a random walk is that the variance evolves over time and
	 * goes to infinity as time goes to infinity; therefore, a random walk cannot be predicted."
	 * @param datasetId Which dataset to put the generated data in.
	 * @param coin Coin to use for randomization.
	 * @param size Number of price points to generate.
	 * @param time Time inception.
	 * @param price Price inception.
	 * @param spread Spread between buyers and sellers.
	 * @param timeStep Time step in ms.
	 * @return Size of data actually generated.
	 */
	public int generate(int datasetId, final Coin coin, final int size, long time, int price,
			final int spread, final long timeStep) {
		startTime = time;

		minPrice = Integer.MAX_VALUE;
		maxPrice = 0;
		int i = 0;
		for (; i < size; i++) {
			if (coin.toss() == head) {
				price += spread;
				if (price > maxPrice) {
					maxPrice = price;
				}
			} else {
				price -= spread;
				if (price < minPrice) {
					minPrice = price;
					if (price <= spread) {
						break; // asset bankrupt
					}
				}
			}
			datasetTime[datasetId][i] = time;
			datasetPrice[datasetId][i] = price;
			time += timeStep;
		}

		stopTime = time - timeStep;
		return i;
	}

	/**
	 * Lock available dataset.
	 * @return Locked dataset Id.
	 */
	public int lock() {
		for (int bucket = 0; bucket < datasets; bucket++) {
			if (datasetUsed[bucket].compareAndSet(false, true)) {
				return bucket;
			}
		}
		throw new RuntimeException("Couldn't find available dataset. Make sure threads don't exceeds the number of datasets");
	}

	/**
	 * Unlock dataset.
	 * @param datasetId to unlock.
	 */
	public void unlock(int datasetId) {
		datasetUsed[datasetId].set(false);
	}

	/**
	 * Apply custom time- and price series.
	 * @param datasetId to use.
	 * @param timeSerie long array.
	 * @param priceSerie int array.
	 */
	public void apply(int datasetId, final long[] timeSerie, final int[] priceSerie) {
		for (int i = 0; i < timeSerie.length; i++) {
			datasetTime[datasetId][i] = timeSerie[i];
			datasetPrice[datasetId][i] = priceSerie[i];
		}
	}

	public long getTime(final int i) {
		return getTime(0, i);
	}
	public long getTime(final int bucket, final int i) {
		return datasetTime[bucket][i];
	}

	public int getPrice(final int i) {
		return getPrice(0, i);
	}
	public int getPrice(final int bucket, final int i) {
		return datasetPrice[bucket][i];
	}

	public int getMinPrice() {
		return minPrice;
	}

	public int getMaxPrice() {
		return maxPrice;
	}

	public int getMidPrice() {
		return (maxPrice - minPrice) / 2 + minPrice;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getStopTime() {
		return stopTime;
	}

}
