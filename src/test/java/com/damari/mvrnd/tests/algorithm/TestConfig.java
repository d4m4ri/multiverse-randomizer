package com.damari.mvrnd.tests.algorithm;

import static org.junit.Assert.assertEquals;

import static com.damari.mvrnd.algorithm.Algorithm.price;

import org.junit.Test;

import com.damari.mvrnd.algorithm.BuyAndHold;
import com.damari.mvrnd.algorithm.BuyAndHoldConfig;
import com.damari.mvrnd.order.Broker;

public class TestConfig {

	@Test
	public void givenAlgoConfigThenExpectItToPropagateToAlgo() {
		BuyAndHoldConfig config = new TestAlgoConfig();
		Object testObj = new Object();
		config.addProperty("testInt", 123);
		config.addProperty("testStr", "string");
		config.addProperty("testObj", testObj);

		int deposit = price(10_000.00f);
		float commission = 0.00069f;
		int spread = price(0.10f);
		int size = 100;

		Broker broker = new Broker();
		broker.deposit(deposit).setCommissionPercent(commission);

		TestAlgo algo = new TestAlgo(config, broker, spread, size);
		BuyAndHoldConfig verifyConfig = algo.getConfig();

		assertEquals(verifyConfig.getInt("testInt"), 123);
		assertEquals(verifyConfig.getString("testStr"), "string");
		assertEquals(verifyConfig.getProperty("testObj"), testObj);
	}

	@Test
	public void givenDefaultConfigThenExpectToFindIt() {
		TestAlgoConfig config = new TestAlgoConfig();
		assertEquals(config.getInt("defaultProperty"), 987);
	}

	private static class TestAlgo extends BuyAndHold {
		BuyAndHoldConfig config;
		public TestAlgo(BuyAndHoldConfig config, Broker broker, int spread, int size) {
			super(config, broker, spread, size);
			this.config = config;
		}
		public BuyAndHoldConfig getConfig() {
			return config;
		}
	}

	private static class TestAlgoConfig extends BuyAndHoldConfig {
		public TestAlgoConfig() {
			this.addProperty("defaultProperty", 987);
		}
	}

}
