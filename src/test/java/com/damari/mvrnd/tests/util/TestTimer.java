package com.damari.mvrnd.tests.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.damari.mvrnd.util.Timer;

public class TestTimer {

	@Test
	public void givenInitTimerThenExpectItToAutostart() throws InterruptedException {
		Timer t = new Timer();
		Thread.sleep(50);
		t.stop();
		assertNotEquals("Expected timer to autostart", t.getMillis(), 0);
	}

	@Test
	public void givenStartAndStopTimerThenExpectTimerToStop() throws InterruptedException {
		Timer t = new Timer();
		t.start();
		Thread.sleep(50);
		t.stop();
		Thread.sleep(100);
		assertTrue("Expected timer to stop", t.getMillis() < (50 + 10));
	}

	@Test
	public void givenStartTimerAndWaitOneSecondThenExpectTimerToIncrease() throws InterruptedException {
		Timer t = new Timer();
		t.start();
		Thread.sleep(1000);
		t.stop();
		assertEquals("Expected one second", t.getSecs(), 1);
	}

}
