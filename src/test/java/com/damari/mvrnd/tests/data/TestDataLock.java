package com.damari.mvrnd.tests.data;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.damari.mvrnd.data.DataGenerator;
import com.damari.mvrnd.data.DataLockException;

public class TestDataLock {

	@Test
	public void givenUnlockRequestsThenExpectLockToBeAcquired() throws DataLockException {
		DataGenerator asset = new DataGenerator();
		asset.unlock(asset.lock());
	}

	@Test
	public void givenMultipleLockRequestsThenExpectLocksToBeAcquired() throws DataLockException {
		DataGenerator asset = new DataGenerator();
		int datasets = DataGenerator.maxDatasets;
		assertTrue("Expected multiple datasets", datasets >= 2);
		try {
			for (int i = 0; i < datasets; i++) {
				int datasetId = asset.lock();
				assertTrue("Expected int >= 0", datasetId >= 0);
			}
		} finally {
			for (int datasetId = 0; datasetId < datasets; datasetId++) {
				asset.unlock(datasetId);
			}
		}
	}

	@Test(expected=DataLockException.class)
	public void givenLockRequestsWhenNoneAvailableThenExpectLockDataExeption() throws DataLockException {
		DataGenerator asset = new DataGenerator();
		int datasets = DataGenerator.maxDatasets;
		try {
			datasets++;
			for (int i = 0; i < datasets; i++) {
				int datasetId = asset.lock();
				assertTrue("Expected int >= 0", datasetId >= 0);
			}
		} finally {
			--datasets;
			for (int datasetId = 0; datasetId < datasets; datasetId++) {
				asset.unlock(datasetId);
			}
		}
	}

}
