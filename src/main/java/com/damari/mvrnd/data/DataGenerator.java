/*
 * Generate time and price series simulating an asset.
 * Thread safe.
 */
package com.damari.mvrnd.data;

import static com.damari.mvrnd.coin.Coin.head;

import java.util.concurrent.atomic.AtomicBoolean;

import com.damari.mvrnd.coin.Coin;

public class DataGenerator {

	private static final Object lock = new Object();

	private static int MAX_BUCKETS = 4; // has to be equal/or greater than number of threads
	private static int MAX_DATA = 60_000_000; // has to be equal/or greater than number of requests data points

	private static long[][] bucketTimeSerie;
	private static int[][] bucketPriceSerie;
	private static AtomicBoolean[] bucketUsed;

	private static int buckets = -1;
	private int curBucket;

	/** Used as cache for performance */
	private int minPrice;
	private int maxPrice;
	private long startTime;
	private long stopTime;

	public DataGenerator() {
		// Init data once
		synchronized (lock) {
			if (buckets == -1) {
				buckets = MAX_BUCKETS;
				System.err.println("INITIALIZING DATA SERIES MEMORY");
				bucketTimeSerie = new long[buckets][MAX_DATA];
				bucketPriceSerie = new int[buckets][MAX_DATA];
				bucketUsed = new AtomicBoolean[buckets];
				for (int i = 0; i < buckets; i++) {
					bucketUsed[i] = new AtomicBoolean(false);
				}
			}
		}
	}

	/**
	 * Lock bucket.
	 * @param size Requested data size.
	 */
	private void lockBucket() {
		// Find available bucket and lock it
		for (int i = 0; i < buckets; i++) {
			if (bucketUsed[i].compareAndSet(false, true)) {
				curBucket = i;
				return;
			}
		}
		throw new RuntimeException("Couldn't find free bucket. Make sure threads don't exceed number of buckets");
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
	 * @param coin Coin to use for randomization.
	 * @param size Number of price points to generate.
	 * @param time Time inception.
	 * @param price Price inception.
	 * @param spread Spread between buyers and sellers.
	 * @param timeStep Time step in ms.
	 * @return Size of data actually generated.
	 */
	public int generateRandomWalk(final Coin coin, final int size, long time, int price, final int spread,
			final long timeStep) {
		lockBucket();
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
			bucketTimeSerie[curBucket][i] = time;
			bucketPriceSerie[curBucket][i] = price;
			time += timeStep;
		}

		stopTime = time - timeStep;
		unlockBucket();
		return i;
	}

	private void unlockBucket() {
		bucketUsed[curBucket].set(false);
	}

	/**
	 * Apply data on first available bucket.
	 * @param timeSerie long array.
	 * @param priceSerie int array.
	 * @return bucket used.
	 */
	public int apply(final long[] timeSerie, final int[] priceSerie) {
		lockBucket();
		for (int i = 0; i < timeSerie.length; i++) {
			bucketTimeSerie[curBucket][i] = timeSerie[i];
			bucketPriceSerie[curBucket][i] = priceSerie[i];
		}
		unlockBucket();
		return curBucket;
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
