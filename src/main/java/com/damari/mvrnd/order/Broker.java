package com.damari.mvrnd.order;

import static com.damari.mvrnd.algorithm.Strategy.round;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.damari.mvrnd.data.OutOfMoneyException;

public class Broker {

	private static final Logger log = LoggerFactory.getLogger(Broker.class.getName());

	private static final boolean DEBUG = false;

	private static final int UNDEFINED = -1;

	private Orders orders;

	private int balance;

	private int commissionFixed;
	private float commissionPercent;

	private int commissionSum;
	private int lossSum;

	public Broker() {
		this(0);
	}

	public Broker(int balance) {
		this.orders = new Orders();
		this.balance = balance;
		this.commissionFixed = UNDEFINED;
		this.commissionPercent = UNDEFINED;
		this.commissionSum = 0;
		this.lossSum = 0;
	}

	public Broker reset(int balance) {
		this.orders.clear();
		this.balance = balance;
		this.commissionFixed = UNDEFINED;
		this.commissionPercent = UNDEFINED;
		this.commissionSum = 0;
		this.lossSum = 0;
		return this;
	}

	public Broker deposit(long amount) {
		balance += amount;
		return this;
	}

	public Broker setCommissionFixed(int commissionFixed) {
		this.commissionFixed = commissionFixed;
		this.commissionPercent = UNDEFINED;
		return this;
	}

	/**
	 * Commission cost in percent.
	 * @param commissionPercent with commission percent per transaction.
	 * @return this
	 */
	public Broker setCommissionPercent(float commissionPercent) {
		this.commissionPercent = commissionPercent;
		this.commissionFixed = UNDEFINED;
		return this;
	}

	private int calculateCommission(int price, int spread, int size) {
		if (commissionFixed != UNDEFINED) {
			return commissionFixed;
		} else {
			return (int)((price + spread) * size * commissionPercent);
		}
	}

	public void buy(long time, int price, int spread, int size)
			throws NoCommissionException, OutOfMoneyException {
		checkCommissionDefined();

		int askPrice = price + spread;
		orders.add(new Order(time, askPrice));
		if (DEBUG) log.info("Buy @ {}", round(askPrice));

		long commission = calculateCommission(price, spread, size);
		balance -= commission;
		if (balance < 0) {
			throw new OutOfMoneyException("Not enough money to buy.");
		}
		commissionSum += commission;
	}

	public void sell(long time, int price, int spread, int size)
			throws NoCommissionException, OutOfMoneyException {
		checkCommissionDefined();

		int bidPrice = price - spread;
		orders.add(new Order(time, bidPrice));
		if (DEBUG) log.info("Sell @ {}", round(bidPrice));

		int commission = calculateCommission(price, spread, size);
		balance -= commission;
		commissionSum += commission;

		if (balance < 0) {
			throw new OutOfMoneyException("Not enough money after sale.");
		}

	}

	private void checkCommissionDefined() throws NoCommissionException {
		if (commissionFixed == UNDEFINED && commissionPercent == UNDEFINED) {
			throw new NoCommissionException("Neither fixed or percent commissions defined.");
		}
	}

	public void loss(int amount) {
		balance -= amount;
		lossSum += amount;
	}

	public Orders getOrders() {
		return orders;
	}

	/**
	 * Get realized balance, not mark to market of open positions. Use NAV for this.
	 * @return BigDecimal with realized mark-to-market balance.
	 */
	public int getBalance() {
		return balance;
	}

	public int getCommissionSum() {
		return commissionSum;
	}

	public int getLossSum() {
		return lossSum;
	}

}
