package com.damari.mvrnd.data;

import java.util.Arrays;

public class DataSet {

	private long[] timeSeries;
	private int[] priceSeries;

	public DataSet() {
	}

	public DataSet(long[] timeSeries, int[] priceSeries) throws DataSetException {
		if (timeSeries.length != priceSeries.length) {
			throw new DataSetException("Asymmetrical time- and price series.");
		}
		this.setTimeSeries(timeSeries);
		this.setPriceSeries(priceSeries);
	}

	public long[] getTimeSeries() {
		return this.timeSeries;
	}

	public void setTimeSeries(long[] timeSeries) {
		this.timeSeries = timeSeries;
	}

	public int[] getPriceSeries() {
		return this.priceSeries;
	}

	public void setPriceSeries(int[] priceSeries) {
		this.priceSeries = priceSeries;
	}

	/**
	 * Append data set to existing data set.
	 * @param appendDataSet to append.
	 * @throws DataSetException if dataset exception occurs.
	 */
	public void append(DataSet appendDataSet) throws DataSetException {
		if (getTimeSeries() != null && getPriceSeries() != null &&
			getTimeSeries().length != getPriceSeries().length) {
			throw new DataSetException("Asymmetrical time- and price series in existing data set");
		}
		if (appendDataSet == null || appendDataSet.getTimeSeries() == null || appendDataSet.getPriceSeries() == null) {
			throw new DataSetException("Cannot append null data set");
		}
		if (appendDataSet.getTimeSeries().length != appendDataSet.getPriceSeries().length) {
			throw new DataSetException("Asymmetrical time- and price series in append data set");
		}

		if (timeSeries == null) {
			// Existing data set empty, use append data as is
			setTimeSeries(appendDataSet.getTimeSeries());
			setPriceSeries(appendDataSet.getPriceSeries());
			return;
		}

		long[] t1 = timeSeries;
		int[] p1 = priceSeries;
		int l1 = t1.length;

		long[] t2 = appendDataSet.getTimeSeries();
		int[] p2 = appendDataSet.getPriceSeries();
		int l2 = t2.length;

		t1 = Arrays.copyOf(t1, l1 + t2.length);
		p1 = Arrays.copyOf(p1, l1 + p2.length);
		System.arraycopy(t2, 0, t1, l1, l2);
		System.arraycopy(p2, 0, p1, l1, l2);

		setTimeSeries(t1);
		setPriceSeries(p1);
	}

}
