package com.damari.mvrnd.broker;

public class Order {

	private long time;

	private long price;

	public Order(long time, long price) {
		this.time = time;
		this.price = price;
	}

	public long getTime() {
		return time;
	}

	public long getPrice() {
		return price;
	}

}
