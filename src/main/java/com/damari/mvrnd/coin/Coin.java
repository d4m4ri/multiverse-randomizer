/*
 * http://java-performance.info/java-util-random-java-util-concurrent-threadlocalrandom-multithreaded-environments/
 * Summary:
 * "Do not share an instance of java.util.Random between several threads in any circumstances, wrap it in
 *  ThreadLocal instead. From Java 7 prefer java.util.concurrent.ThreadLocalRandom to java.util.Random in
 *  all circumstances - it is backwards compatible with existing code, but uses cheaper operations internally."
 */
package com.damari.mvrnd.coin;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class Coin {

	public final static boolean head = true;
	public final static boolean tail = false;

	public final static float fair = 50.00f;
	public final static float headsOnly = 100.00f;
	public final static float tailsOnly = 0.00f;

	protected float headProbability;

	protected Long seed;

	public abstract boolean toss();

	public abstract Long getSeed();

	public abstract void setSeed(Long seed) throws UnsupportedSeed;

	protected abstract String getLabel();

	public static String describe(boolean toss) {
		return (head ? "head" : "tail");
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(32);
		float pHead = headProbability * 100f;
		sb.append("P(");
		sb.append(getLabel());
		sb.append("): ");
		sb.append(new BigDecimal(pHead).setScale(3, RoundingMode.HALF_UP));
		sb.append("%-");
		sb.append(new BigDecimal(100.00 - pHead).setScale(3, RoundingMode.HALF_UP));
		sb.append("%; ");
		if (getSeed() == null) {
			sb.append("seed=?");
		} else {
			sb.append("seed=");
			sb.append(getSeed());
		}
		return sb.toString();
	}

}
