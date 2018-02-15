package com.damari.mvrnd.order;

import static com.damari.mvrnd.algorithm.Algorithm.round;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.damari.mvrnd.data.OutOfMoneyException;

public class Broker {

	private static final Logger log = LoggerFactory.getLogger(Broker.class.getName());

	private static final boolean debug = false;

	private static final int undefined = -1;

	private Orders orders;

	/* Initial deposit */
	private int deposit;

	/* Current balance */
	private int balance;

	/* Commission models */
	private float commissionFixed;
	private float commissionPercent;

	private int commissionSum;
	private int lossSum;

	public Broker() {
		this(0);
	}

	public Broker(int deposit) {
		this.orders = new Orders();
		this.deposit = deposit;
		this.balance = deposit;
		this.commissionFixed = undefined;
		this.commissionPercent = undefined;
		this.commissionSum = 0;
		this.lossSum = 0;
	}

	public Broker reset(int deposit) {
		this.orders.clear();
		this.deposit = deposit;
		this.balance = deposit;
		this.commissionFixed = undefined;
		this.commissionPercent = undefined;
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
		this.commissionPercent = undefined;
		return this;
	}

	/**
	 * Set commission percent.
	 * @param commissionPercent per transaction.
	 * @return this
	 */
	public Broker setCommissionPercent(float commissionPercent) {
		this.commissionPercent = commissionPercent / 100f; // 0.069% -> 0.00069
		this.commissionFixed = undefined;
		return this;
	}

	/**
	 * Calculate commission.
	 * @param price of asset.
	 * @param spread in asset.
	 * @param size of trade.
	 * @return
	 */
	private int calculateCommission(int price, int spread, int size) {
		if (commissionFixed != undefined) {
			return (int) commissionFixed;
		} else {
			return (int) ((price + spread) * size * commissionPercent);
		}
	}

	public void buy(long time, int price, int spread, int size)
			throws CommissionUndefinedException, OutOfMoneyException {
		checkCommissionDefined();

		int askPrice = price + spread;
		orders.add(new Order(time, askPrice));
		if (debug) log.info("Buy @ {}", round(askPrice));

		long commission = calculateCommission(price, spread, size);
		balance -= commission;
		if (balance < 0) {
			throw new OutOfMoneyException("Not enough money to buy.");
		}
		commissionSum += commission;
	}

	public void sell(long time, int price, int spread, int size)
			throws CommissionUndefinedException, OutOfMoneyException {
		checkCommissionDefined();

		int bidPrice = price - spread;
		orders.add(new Order(time, bidPrice));
		if (debug) log.info("Sell @ {}", round(bidPrice));

		int commission = calculateCommission(price, spread, size);
		balance -= commission;
		commissionSum += commission;

		if (balance < 0) {
			throw new OutOfMoneyException("Not enough money after sale.");
		}

	}

	private void checkCommissionDefined() throws CommissionUndefinedException {
		if (commissionFixed == undefined && commissionPercent == undefined) {
			throw new CommissionUndefinedException("Neither fixed or percent commissions defined.");
		}
	}

	public void loss(int amount) {
		balance -= amount;
		lossSum += amount;
	}

	public Orders getOrders() {
		return orders;
	}

	public int getDeposit() {
		return deposit;
	}

	/**
	 * Get realized balance, not mark to market of open positions. Use NAV for this.
	 * @return float with current balance.
	 */
	public int getBalance() {
		return balance;
	}

	public float getCommissionPercent() {
		return commissionPercent * 100f;
	}

	public int getCommissionSum() {
		return commissionSum;
	}

	public int getLossSum() {
		return lossSum;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer(150);
		sb.append("Deposit ").append(deposit);
		if (commissionFixed != undefined) {
			sb.append(", commission ").append(commissionFixed);
		} else if (commissionPercent != undefined) {
			BigDecimal bd = new BigDecimal(commissionPercent / 100f).setScale(3, RoundingMode.HALF_UP);
			sb.append(", commission ").append(bd.toString()).append("%");
		} else {
			sb.append("Unknown commission");
		}
		return sb.toString();
	}

}
