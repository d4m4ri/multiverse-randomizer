package com.damari.mvrnd.tests.coin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static com.damari.mvrnd.coin.Coin.fair;
import static com.damari.mvrnd.coin.Coin.head;
import static com.damari.mvrnd.coin.Coin.tail;
import static com.damari.mvrnd.coin.Coin.headsOnly;
import static com.damari.mvrnd.coin.Coin.tailsOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.damari.mvrnd.coin.Coin;
import com.damari.mvrnd.coin.CoinNeumannENIACRandom;
import com.damari.mvrnd.coin.CoinRandom;
import com.damari.mvrnd.coin.CoinSecureRandom;
import com.damari.mvrnd.coin.CoinSplittableRandom;
import com.damari.mvrnd.coin.CoinThreadLocalRandom;
import com.damari.mvrnd.coin.CoinXoRoShiRo128PlusRandom;

public class TestCoin {

	@Test
	public void givenFairCoinThenExpectCoinTossesToBeInRangeOfTwoStandardDeviations() {
		long tossCount = 3_000_000;
		float twoSD = 13.6f / 100f;
		long[] sum = tossLotsOfFairCoins(tossCount);
		for (int i = 0; i < sum.length; i++) {
			assertTrue("Sum of coins should be >= -" + twoSD + " (2SD)", sum[i] >= -((1f - twoSD) * tossCount));
			assertTrue("Sum of coins should be <= " + twoSD + " (2SD)", sum[i] <= ((1f - twoSD) * tossCount));
		}
	}

	@Test
	public void givenFairCoinThenExpectCoinTossesToBeInRangeOfOneStandardDeviations() {
		long tossCount = 3_000_000;
		float oneSD = 34.1f / 100f;
		long[] sum = tossLotsOfFairCoins(tossCount);
		for (int i = 0; i < sum.length; i++) {
			assertTrue("Sum of coins should be >= -" + oneSD + " (1SD)", sum[i] >= -((1f - oneSD) * tossCount));
			assertTrue("Sum of coins should be <= " + oneSD + " (1SD)", sum[i] <= ((1f - oneSD) * tossCount));
		}
	}

	@Test
	public void givenTailsOnlyProbabilityThenExpectTailsOnly() {
		List<Coin> coins = coins(tailsOnly);
		for (int i = 0; i < coins.size(); i++) {
			for (int n = 0; n < 100_000; n++) {
				assertEquals("Expected tails only using coin " + coins.get(i), tail, coins.get(i).toss());
			}
		}
	}

	@Test
	public void givenHeadsOnlyProbabilityThenExpectHeadsOnly() {
		List<Coin> coins = coins(headsOnly);
		for (int i = 0; i < coins.size(); i++) {
			for (int n = 0; n < 100_000; n++) {
				assertEquals("Expected heads only using coin " + coins.get(i), head, coins.get(i).toss());
			}
		}
	}

	@Test
	public void givenCoinSeedThenExpectDeterministicOutcome() throws Exception {
		List<Coin> coins = seedlessCoins(fair);
		boolean[] tosses = new boolean[500];
		for (int c = 0; c < coins.size(); c++) {
			Coin coin = coins.get(c);
			coin.setSeed(123L);
			for (int i = 0; i < 500; i++) {
				tosses[i] = coin.toss();
			}
			coin.setSeed(123L);
			for (int i = 0; i < 500; i++) {
				assertEquals("Expected deterministic coin outcome for " + coin + " at iteration " + i,
						tosses[i], coin.toss());
			}
		}
	}

	@Test
	public void givenCoinTossThenDontExpectAnyLongSerieOfHeadsOrTails() {
		List<Coin> coins = coins(fair);
		for (int c = 0; c < coins.size(); c++) {
			Coin coin = coins.get(c);
			boolean prevToss = false;
			int seq = 0;
			for (int i = 0; i < 10_000; i++) {
				boolean toss = coin.toss();
				if (i != 0 && toss == prevToss) {
					seq++;
					assertTrue("Got " + Coin.describe(toss) + " " + seq +
							" times in a row using coin " + coin, seq < 19);
				} else {
					seq = 0;
				}
				prevToss = toss;
			}
		}
	}

	private long[] tossLotsOfFairCoins(long tossCount) {
		List<Coin> coins = coins(fair);
		long sum1 = 0, sum2 = 0, sum3 = 0, sum4 = 0;
		for (long i = 0; i < tossCount; i++) {
			sum1 += coins.get(0).toss() == head ? 1 : -1;
			sum2 += coins.get(1).toss() == head ? 1 : -1;
			sum3 += coins.get(2).toss() == head ? 1 : -1;
			sum4 += coins.get(3).toss() == head ? 1 : -1;
		}
		assertFalse("Coin outside positive boundary", sum1 > tossCount);
		assertFalse("Coin outside negative boundary", sum1 <= -tossCount);
		assertFalse("Coin outside positive boundary", sum2 > tossCount);
		assertFalse("Coin outside negative boundary", sum2 <= -tossCount);
		assertFalse("Coin outside positive boundary", sum3 > tossCount);
		assertFalse("Coin outside negative boundary", sum3 <= -tossCount);
		assertFalse("Coin outside positive boundary", sum4 > tossCount);
		assertFalse("Coin outside negative boundary", sum4 <= -tossCount);
		return new long[] {sum1, sum2, sum3, sum4};
	}

	private List<Coin> coins(float probability) {
		return new ArrayList<>(Arrays.asList(
				new CoinNeumannENIACRandom(probability),
				new CoinRandom(probability),
				new CoinSecureRandom(probability),
				new CoinSplittableRandom(probability),
				new CoinThreadLocalRandom(probability),
				new CoinXoRoShiRo128PlusRandom(probability)));
	}

	private List<Coin> seedlessCoins(float probability) {
		List<Coin> seedlessCoins = new ArrayList<>();
		for (Coin coin : coins(probability)) {
			if (!(coin instanceof CoinSecureRandom) &&
				!(coin instanceof CoinSplittableRandom) &&
				!(coin instanceof CoinThreadLocalRandom)) {
				seedlessCoins.add(coin);
			}
		}
		return seedlessCoins;
	}

}