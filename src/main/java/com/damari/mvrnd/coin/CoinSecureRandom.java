/*
 * Best but slowest. Interesting reads:
 * https://tersesystems.com/2015/12/17/the-right-way-to-use-securerandom/
 * https://sockpuppet.org/blog/2014/02/25/safely-generate-random-numbers/
 * Supposed to be FIPS 140-2 approved by NIST: https://en.wikipedia.org/wiki/FIPS_140-2
 *   URL to support this claim in SecureRandom.class:43 is not to be found any
 *   longer (http://csrc.nist.gov/cryptval/140-2.htm). The test can be read about
 *   here: http://nvlpubs.nist.gov/nistpubs/Legacy/SP/nistspecialpublication800-22r1a.pdf
 */
package com.damari.mvrnd.coin;

import java.security.SecureRandom;

public class CoinSecureRandom extends Coin {

	private SecureRandom randomNum;

	public CoinSecureRandom() {
		headProbability = 50f / 100f;
		randomNum = new SecureRandom();
	}

	public CoinSecureRandom(float headProbability) {
		this.headProbability = headProbability / 100f;
		randomNum = new SecureRandom();
	}

	@Override
	public boolean toss() {
		if (randomNum.nextFloat() < headProbability) {
			return HEAD;
		}
		return TAIL;
	}

	@Override
	public Long getSeed() {
		return seed;
	}

	/**
	 * Doesn't work for SecureRandom because its seed is set at init,
	 * afterwards its incremental afaik.
	 */
	@Override
	public void setSeed(Long seed) throws UnsupportedSeed {
		if (seed == 0) {
			throw new UnsupportedSeed("Seed 0 is not supported by SecureRandom");
		}
		randomNum.setSeed(seed);
		this.seed = seed;
	}

	@Override
	protected String getLabel() {
		return "SecureRandom";
	}

}
