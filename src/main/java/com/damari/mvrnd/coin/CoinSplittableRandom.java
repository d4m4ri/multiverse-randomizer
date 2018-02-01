package com.damari.mvrnd.coin;

import java.util.SplittableRandom;

public class CoinSplittableRandom extends Coin {

	private final SplittableRandom randomNum;

	public CoinSplittableRandom() {
		headProbability = 50f / 100f;
		randomNum = new SplittableRandom();
	}

	public CoinSplittableRandom(float headProbability) {
		this.headProbability = headProbability / 100f;
		randomNum = new SplittableRandom();
	}

	@Override
	public boolean toss() {
		if (randomNum.nextDouble() < headProbability) {
			return head;
		}
		return tail;
	}

	@Override
	public Long getSeed() {
		return null;
	}

	@Override
	public void setSeed(Long seed) {
		this.seed = null;
	}

	@Override
	protected String getLabel() {
		return "SplittableRandom";
	}

}
