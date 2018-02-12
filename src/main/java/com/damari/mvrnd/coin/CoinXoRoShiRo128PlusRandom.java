package com.damari.mvrnd.coin;

import it.unimi.dsi.util.XoRoShiRo128PlusRandom;

public class CoinXoRoShiRo128PlusRandom extends Coin {

	private final XoRoShiRo128PlusRandom randomNum;

	public CoinXoRoShiRo128PlusRandom() {
		headProbability = 50f / 100f;
		randomNum = new XoRoShiRo128PlusRandom();
	}

	public CoinXoRoShiRo128PlusRandom(float headProbability) {
		this.headProbability = headProbability / 100f;
		randomNum = new XoRoShiRo128PlusRandom();
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
		return "XoRoShiRo128PlusRandom";
	}

	@Override
	public boolean isCPU() {
		return true;
	}

}
