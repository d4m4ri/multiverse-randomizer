/*
 * Okey coin tosser.
 */
package com.damari.mvrnd.coin;

import java.util.Random;

public class CoinRandom extends Coin {

	private final Random randomNum;

	public CoinRandom() {
		headProbability = 50f / 100f;
		randomNum = new Random();
	}

	public CoinRandom(float headProbability) {
		this.headProbability = headProbability / 100f;
		randomNum = new Random();
	}

	@Override
	public boolean toss() {
		if (randomNum.nextFloat() < headProbability) {
			return head;
		}
		return tail;
	}

	@Override
	public Long getSeed() {
		return seed;
	}

	@Override
	public void setSeed(Long seed) {
		randomNum.setSeed(seed);
		this.seed = seed;
	}

	@Override
	protected String getLabel() {
		return "Random";
	}

}
