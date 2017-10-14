package com.damari.mvrnd.data;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Statistics {

	private AtomicInteger jobsStarted;
	private AtomicInteger jobsCompleted;
	private AtomicInteger wins;
	private AtomicLong winLoss;
	private AtomicLong timeAlgoProcess;
	private AtomicLong timeDataGenerate;

	public Statistics() {
		jobsStarted = new AtomicInteger(0);
		jobsCompleted = new AtomicInteger(0);
		wins = new AtomicInteger(0);
		winLoss = new AtomicLong(0);
		timeAlgoProcess = new AtomicLong(0);
		timeDataGenerate = new AtomicLong(0);
	}

	public int addJobStarted() {
		return jobsStarted.incrementAndGet();
	}

	public AtomicInteger getJobsCompleted() {
		return jobsCompleted;
	}
	public int addJobCompleted() {
		return jobsCompleted.incrementAndGet();
	}

	public int getWins() {
		return wins.get();
	}
	public int addWins() {
		return wins.incrementAndGet();
	}

	public long addWinLoss(long delta) {
		return winLoss.addAndGet(delta);
	}
	public long getWinLoss() {
		return winLoss.get();
	}

	public long getTimeAlgoProcess() {
		return timeAlgoProcess.get();
	}
	public long addTimeAlgoProcess(long delta) {
		return timeAlgoProcess.addAndGet(delta);
	}

	public long addTimeDataGenerate(long delta) {
		return timeDataGenerate.addAndGet(delta);
	}

}
