package com.damari.mvrnd.algorithm;

import com.damari.mvrnd.data.DataLockException;

public abstract class ExecutorAlgo {

	public abstract boolean process() throws DataLockException;

	@Override
	public abstract String toString();

}
