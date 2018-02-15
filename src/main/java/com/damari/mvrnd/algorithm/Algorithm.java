package com.damari.mvrnd.algorithm;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.damari.mvrnd.data.OutOfMoneyException;
import com.damari.mvrnd.order.Broker;
import com.damari.mvrnd.order.CommissionUndefinedException;

public abstract class Algorithm {

	public static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");

	private static final BigDecimal bigDecimal100 = BigDecimal.valueOf(100);

	protected final Broker broker;

	protected int lastPrice;

	protected final int spread;

	protected int minPrice;
	protected int maxPrice;

	public Algorithm(final Broker broker, final int spread) {
		this.broker = broker;
		this.spread = spread;

		this.minPrice = Integer.MAX_VALUE;
		this.maxPrice = -1;
	}

	/**
	 * Mark to market, closed and open positions
	 * @return int with current market value.
	 */
	public abstract int getMarkToMarket();

	/**
	 * Get Net Asset Value.
	 * @return current NAV.
	 */
	public abstract int getNAV();

	/**
	 * Get summary.
	 * @return String with algo specific summary.
	 */
	public abstract String getSummary();

	/**
	 * Process time and price.
	 * @param time as long.
	 * @param price as int.
	 * @throws CommissionUndefinedException if broker misconfiguration.
	 * @throws OutOfMoneyException if money ran out.
	 */
	public void process(final long time, final int price) throws CommissionUndefinedException, OutOfMoneyException {
		lastPrice = price;
		updateMinMax(price);
	}

	protected void updateMinMax(final int price) {
		if (price < minPrice) {
			minPrice = price;
		} else if (price > maxPrice) {
			maxPrice = price;
		}
	}

	/**
	 * Round price with 2 decimals.
	 * @param value to be rounded.
	 * @return rounded BigDecimal value.
	 */
	public static String round(final long value) {
		return BigDecimal.valueOf(value).divide(bigDecimal100, 2, RoundingMode.HALF_UP).toString();
	}
	public static String round(final float value) {
		return BigDecimal.valueOf(value).divide(bigDecimal100, 2, RoundingMode.HALF_UP).toString();
	}

	/**
	 * Convert price from float to integer using factor 100.
	 * @param price as float.
	 * @return price as int.
	 */
	public static int price(final float price) {
		return (int) (price * 100f);
	}

	/**
	 * Get last price that passed through algorithm.
	 * @return long with last price.
	 */
	public int getLastPrice() {
		return lastPrice;
	}

	/**
	 * Get lowest price that passed through algorithm.
	 * @return long with lowest price.
	 */
	public int getMinPrice() {
		return minPrice;
	}

	/**
	 * Get highest price that passed through algorithm.
	 * @return long with highest price.
	 */
	public int getMaxPrice() {
		return maxPrice;
	}

}
