/*
 * Middle square method trying to mimic ENIAC randomizer created by John von Neumann.
 * The size of the seed defines the size of the period.
 * @see https://en.wikipedia.org/wiki/Middle-square_method
 */
package com.damari.mvrnd.coin;

import java.security.SecureRandom;

public class CoinNeumannENIACRandom extends Coin {

	private long seedInit;
	private int seedLen;

	public CoinNeumannENIACRandom() {
		headProbability = 50f / 100f;
		setSeed(new SecureRandom().nextLong() / 10_000L);
	}

	public CoinNeumannENIACRandom(float headProbability) {
		this.headProbability = headProbability / 100f;
		setSeed(new SecureRandom().nextLong() / 10_000L);
	}

	@Override
	public boolean toss() {
		// Square
		long square = seed * seed;
		if (square == 0) {
			// Got squared to 0, restart
			seed = seedInit;
			square = seed * seed;
		} else if (square < 0) {
			square = -square;
		}

		// Middle
		int squareLen = longLen(square);
		int lenDiff = squareLen - seedLen;
		if (lenDiff == 0) {
			// Same size, use as-is without crop
			seed = square;
		} else {
			// Remove prefix
			int rPrefix = lenDiff / 2;
			long middleSquare = square % (long)Math.pow(10, squareLen - rPrefix);

			// Remove suffix
			squareLen = longLen(middleSquare);
			if (squareLen > seedLen) {
				int rSuffix = squareLen - seedLen;
				if (rSuffix > 0) {
					middleSquare /= (long)Math.pow(10, rSuffix);
				}
			}

			seed = middleSquare;
		}

		// Toss
		float rnd = seed % 1_00_000L; // 00 000   - 99 999
		rnd /= 1_00_000f;             // 0.00000f - 0.99999f
		return rnd < headProbability ? head : tail;
	}

	/**
	 * Get the long values string length. Does not support negative values. Optimized for length 19:
	 * Long.MAX_VALUE, 0x7fffffffffffffffL, 9223372036854775807, 1234567890123456789.
	 * @param n being the long value to be evaluated.
	 * @return int with length.
	 */
	public static int longLen(long n) {
		if (n >= 0) {
			if (n > 999999999999999999L) return 19;
			if (n > 99999999999999999L) return 18;
			if (n > 9999999999999999L) return 17;
			if (n > 999999999999999L) return 16;
			if (n > 99999999999999L) return 15;
			if (n > 9999999999999L) return 14;
			if (n > 999999999999L) return 13;
			if (n > 99999999999L) return 12;
			if (n > 9999999999L) return 11;
			if (n > 999999999L) return 10;
			if (n > 99999999L) return 9;
			if (n > 9999999L) return 8;
			if (n > 999999L) return 7;
			if (n > 99999L) return 6;
			if (n > 9999L) return 5;
			if (n > 999L) return 4;
			if (n > 99L) return 3;
			if (n > 9L) return 2;
		} else {
			if (n < -999999999999999999L) return 20;
			if (n < -99999999999999999L) return 19;
			if (n < -9999999999999999L) return 18;
			if (n < -999999999999999L) return 17;
			if (n < -99999999999999L) return 16;
			if (n < -9999999999999L) return 15;
			if (n < -999999999999L) return 14;
			if (n < -99999999999L) return 13;
			if (n < -9999999999L) return 12;
			if (n < -999999999L) return 11;
			if (n < -99999999L) return 10;
			if (n < -9999999L) return 9;
			if (n < -999999L) return 8;
			if (n < -99999L) return 7;
			if (n < -9999L) return 6;
			if (n < -999L) return 5;
			if (n < -99L) return 4;
			if (n < -9L) return 3;
			if (n < 0L) return 2;
		}
		return 1;
	}

	@Override
	public Long getSeed() {
		return this.seed;
	}

	@Override
	public void setSeed(Long seed) {
		if (seed < 0)
			seed = -seed;
		this.seed = seed;
		seedLen = longLen(seed);
		seedInit = seed;
	}

	@Override
	protected String getLabel() {
		return "CoinNeumannENIACRandom";
	}

}
