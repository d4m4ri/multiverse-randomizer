package com.damari.mvrnd.tests.data;

import org.joda.time.DateTime;
import org.junit.Test;

import com.damari.mvrnd.data.DataSet;
import com.damari.mvrnd.data.DataSetException;

public class TestDataSet {

	// TODO - valid data sets

	@Test(expected=DataSetException.class)
	public void givenAppendDataOfNullValuesThenExpectException() throws DataSetException {
		long[] timeSeries = {new DateTime().getMillis(), new DateTime().getMillis(), new DateTime().getMillis()};
		int[] priceSeries = {1, 2, 3};
		DataSet dataSet = new DataSet(timeSeries, priceSeries);
		DataSet nullDataSet = new DataSet();
		dataSet.append(nullDataSet);
	}

	@Test(expected=DataSetException.class)
	public void givenAppendNullThenExpectException() throws DataSetException {
		long[] timeSeries = {new DateTime().getMillis(), new DateTime().getMillis(), new DateTime().getMillis()};
		int[] priceSeries = {1, 2, 3};
		DataSet dataSet = new DataSet(timeSeries, priceSeries);
		dataSet.append(null);
	}

}
