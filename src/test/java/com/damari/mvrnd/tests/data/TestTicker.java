package com.damari.mvrnd.tests.data;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.damari.mvrnd.data.DataSet;
import com.damari.mvrnd.data.Ticker;

public class TestTicker {

	@Test
	public void givenReadDataThenExpectValidFormat() throws Exception {
		String tickerData = Ticker.readData("SPY");
		assertNotNull("Expected ticker data", tickerData);
		assertTrue("Expected at least 100 ticker data rows", tickerData.length() >= 100);
	}

	@Test
	public void givenTransformTickerDataThenExpectValidTransformedFormat() throws Exception {
		String tickerData = Ticker.readData("SPY");
		DataSet dataSet = Ticker.transform(tickerData);

		long[] timeSeries = dataSet.getTimeSeries();
		int[] priceSeries = dataSet.getPriceSeries();

		assertTrue("Expected at least 100 rows of time data", timeSeries.length >= 100);
		assertTrue("Expected at least 100 rows of price data", priceSeries.length >= 100);
		assertTrue("Expected same number of time- and price points", timeSeries.length == priceSeries.length);
	}

}
