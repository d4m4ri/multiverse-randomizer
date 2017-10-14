package com.damari.mvrnd.util;

import java.util.concurrent.TimeUnit;

public class Timer {

	private long start;

	private long stop;

	public Timer() {
		start();
	}

	public void start() {
		start = System.currentTimeMillis();
	}

	public void stop() {
		stop = System.currentTimeMillis();
	}

	public long getMillis() {
		return stop - start;
	}

	public long getSecs() {
		return (stop - start) / 1000L;
	}

	public long getMins() {
		return (stop - start) / 1000L / 60L;
	}

	public String getMinutesAndSeconds() {
		long ms = stop - start;
		return String.format("%dm%ds", TimeUnit.MILLISECONDS.toMinutes(ms),
				TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
	}

	@Override
	public String toString() {
		return String.valueOf(stop - start) + "ms";
	}

}
