/*
 * Buy and hold is the best alternative if prices has a positive skew.
 * Used to test against other algorithms.
 */
package com.damari.mvrnd.algorithm;

import com.damari.mvrnd.data.OutOfMoneyException;
import com.damari.mvrnd.broker.Broker;
import com.damari.mvrnd.broker.CommissionUndefinedException;

public class BuyAndHold extends Algorithm {

	public static final int undefined = -1;

	private int position;

	private int size;

	public BuyAndHold(Config config, Broker broker, int spread, int size) {
		super(broker, spread);
		this.size = size;
		this.position = undefined;
	}

	@Override
	public void process(long time, int price) throws CommissionUndefinedException, OutOfMoneyException {
		super.process(time, price);
		trade(time, price);
	}

	private void trade(long time, int price) throws CommissionUndefinedException, OutOfMoneyException {
		if (position == undefined) {
			position = price + spread;
			broker.buy(time, price, spread, size);
		}
	}

	public int getPositionPrice() {
		return position;
	}

	public int getNAV() {
		int posBidPrice = position - spread;
		return (lastPrice - posBidPrice) * size;
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
