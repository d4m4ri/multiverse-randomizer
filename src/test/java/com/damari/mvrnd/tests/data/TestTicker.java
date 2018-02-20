package com.damari.mvrnd.tests.data;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.damari.mvrnd.data.Ticker;

public class TestTicker {

	@Test
	public void givenReadDataThenExpectValidFormat() throws Exception {
		String data = Ticker.readData("SPY");
		assertNotNull("Expected ticker data", data);
	}

}
