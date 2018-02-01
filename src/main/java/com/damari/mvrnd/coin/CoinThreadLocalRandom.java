/*
 * Okey coin tosser with multithreaded performance (without the singleton AtomicLong)
 */
package com.damari.mvrnd.coin;

import java.util.concurrent.ThreadLocalRandom;

public class CoinThreadLocalRandom extends Coin {

	public CoinThreadLocalRandom() {
		headProbability = 50f / 100f;
	}

	public CoinThreadLocalRandom(float headProbability) {
		this.headProbability = headProbability / 100f;
	}

	@Override
	public boolean toss() {
		if (ThreadLocalRandom.current().nextFloat() < headProbability) {
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
		ThreadLocalRandom.current().setSeed(seed);
		this.seed = seed;
	}

	@Override
	protected String getLabel() {
		return "ThreadLocalRandom";
	}

}
