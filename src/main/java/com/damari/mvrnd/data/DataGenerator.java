/*
 * Generate time and price series simulating an asset.
 * Thread safe.
 */
package com.damari.mvrnd.data;

import static com.damari.mvrnd.coin.Coin.head;

import java.util.concurrent.atomic.AtomicBoolean;

import com.damari.mvrnd.app.App;
import com.damari.mvrnd.coin.Coin;

public class DataGenerator {

	private static final Object lock = new Object();

	public static final int maxBuckets = App.getPhysicalCores();
	private static int maxData = 60_000_000; // has to be equal/or greater than number of requests data points

	private static long[][] bucketTimeSerie;
	private static int[][] bucketPriceSerie;
	private static AtomicBoolean[] bucketUsed;

	private static int buckets = -1;

	/** Used as cache for performance */
	private int minPrice;
	private int maxPrice;
	private long startTime;
	private long stopTime;

	public DataGenerator() {
		// Init data once
		synchronized (lock) {
			if (buckets == -1) {
				buckets = maxBuckets;
				System.err.println("INITIALIZING DATA SERIES MEMORY (" + memUsage() + "M / " + maxBuckets + "T)");
				bucketTimeSerie = new long[buckets][maxData];
				bucketPriceSerie = new int[buckets][maxData];
				bucketUsed = new AtomicBoolean[buckets];
				for (int i = 0; i < buckets; i++) {
					bucketUsed[i] = new AtomicBoolean(false);
				}
			}
		}
	}

	/**
	 * Calculate memory usage based on time- and price series.
	 * @return int with memory usage in MB.
	 */
	public int memUsage() {
		float muTimeSerieB = buckets * maxData * (Long.SIZE / 8f);
		float muPriceSerieB = buckets * maxData * (Integer.SIZE / 8f);
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
	 * @param bucket Which bucket to put generated data.
	 * @param coin Coin to use for randomization.
	 * @param size Number of price points to generate.
	 * @param time Time inception.
	 * @param price Price inception.
	 * @param spread Spread between buyers and sellers.
	 * @param timeStep Time step in ms.
	 * @return Size of data actually generated.
	 */
	public int generateRandomWalk(int bucket, final Coin coin, final int size, long time, int price,
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
			bucketTimeSerie[bucket][i] = time;
			bucketPriceSerie[bucket][i] = price;
			time += timeStep;
		}

		stopTime = time - timeStep;
		return i;
	}

	/**
	 * Find available bucket and lock it.
	 * @return int with bucket to use.
	 */
	public int lockBucket() {
		for (int bucket = 0; bucket < buckets; bucket++) {
			if (bucketUsed[bucket].compareAndSet(false, true)) {
				return bucket;
			}
		}
		throw new RuntimeException("Couldn't find free bucket. Make sure threads don't exceeds the number of buckets");
	}

	/**
	 * Unlock a specific bucket.
	 * @param bucket to unlock.
	 */
	public void unlockBucket(int bucket) {
		bucketUsed[bucket].set(false);
	}

	/**
	 * Apply custom time- and price series.
	 * @param bucket to use.
	 * @param timeSerie long array.
	 * @param priceSerie int array.
	 */
	public void apply(int bucket, final long[] timeSerie, final int[] priceSerie) {
		for (int i = 0; i < timeSerie.length; i++) {
			bucketTimeSerie[bucket][i] = timeSerie[i];
			bucketPriceSerie[bucket][i] = priceSerie[i];
		}
	}

	public long getTime(final int i) {
		return getTime(0, i);
	}
	public long getTime(final int bucket, final int i) {
		return bucketTimeSerie[bucket][i];
	}

	public int getPrice(final int i) {
		return getPrice(0, i);
	}
	public int getPrice(final int bucket, final int i) {
		return bucketPriceSerie[bucket][i];
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
