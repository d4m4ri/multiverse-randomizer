/*
 * WIP: Momentum test. If price > high then buy. If price below last high, sell. Repeat.
 */
package com.damari.mvrnd.algorithm;

import com.damari.mvrnd.data.OutOfMoneyException;
import com.damari.mvrnd.order.Broker;
import com.damari.mvrnd.order.NoCommissionException;

public class Momentum extends Strategy {

	public static final int undefined = -1;

	private int position;

	private int size;

	public Momentum(Config config, Broker broker, int spread, int size) {
		super(broker, spread);
		this.size = size;

		this.position = undefined;
	}

	@Override
	public void process(long time, int price) throws NoCommissionException, OutOfMoneyException {
		super.process(time, price);
		trade(time, price);
	}

	private void trade(long time, int price) throws NoCommissionException, OutOfMoneyException {
		if (position == undefined) {
			position = price + spread;
			broker.buy(time, price, spread, size);
		}
//
//					broker.sell(time, price, spread, size);
//					int bidPrice = price - spread;
//					int loss = (buckets[i] - bidPrice) * size;
//					broker.loss(loss);
	}

	/**
	 * Get average price.
	 *
	 * @return int with average price of all the buckets in use.
	 */
	public int getPositionAvgPrice() {
		//TODO
		int count = 0;
		int avgPrice = 0;
//			if (buckets[i] != UNDEFINED) {
//				avgPrice += buckets[i] - spread;
//				count++;
//			}

		return avgPrice / count;
	}

	/**
	 * Get Net Asset Value of current open positions.
	 *
	 * @return BigDecimal with NAV.
	 */
	public int getNAV() {
		//TODO
		int count = 0;
		int avgPrice = 0;
//		for (int i = 0; i < buckets.length; i++) {
//			if (buckets[i] != UNDEFINED) {
//				int bidPrice = buckets[i] - spread;
//				int bucketPrice = bidPrice;
//				avgPrice += bucketPrice;
				count++;
//			}
//		}
		if (count == 0 || avgPrice == 0) {
			return 0;
		}
		avgPrice = avgPrice / count;

		int nav = (lastPrice - avgPrice) * count * size;
		return nav;
	}

	@Override
	public int getMarkToMarket() {
		return broker.getBalance() + getNAV();
	}

	@Override
	public String getSummary() {
		return "";
	}

}
