/*
 * Middle square method trying to mimic ENIAC randomizer created by John von Neumann.
 * The size of the seed defines the size of the period.
 * @see https://en.wikipedia.org/wiki/Middle-square_method
 */
package com.damari.mvrnd.coin;

public class CoinNeumannENIACRandom extends Coin {

	private long seedInit;
	private int seedLen;

	public CoinNeumannENIACRandom() {
		if (seed == null) setSeed(327142633955463L);
	}

	@Override
	public boolean toss() {
		// Square
		if (seed == 0) setSeed(seedInit);
		long square = seed * seed;
		String squareStr = String.valueOf(square);

		// Middle
		int seedStart = (squareStr.length() - seedLen) / 2;
		if (seedStart < 0) seedStart = 0;
		int seedEnd = squareStr.length() - seedStart;
		if ((seedEnd - seedStart) > seedLen) {
			seedEnd--;
		}
		String middleSquare = squareStr.substring(seedStart, seedEnd);

		// Toss
		seed = Long.valueOf(middleSquare);
		int middleSquareLen = middleSquare.length();
		return Integer.valueOf(middleSquare.substring(middleSquareLen - 1, middleSquareLen)) < 5; // <5 true ; >=5 false
	}

	@Override
	public Long getSeed() {
		return this.seed;
	}

	@Override
	public void setSeed(Long seed) {
		this.seed = seed;
		seedInit = seed;
		seedLen = String.valueOf(seed).length();
	}

	@Override
	protected String getLabel() {
		return "CoinNeumannENIACRandom";
	}

}
