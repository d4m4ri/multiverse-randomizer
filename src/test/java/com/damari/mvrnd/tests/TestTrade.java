package com.damari.mvrnd.tests;

import static com.damari.mvrnd.algorithm.Strategy.round;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.damari.mvrnd.algorithm.Config;
import com.damari.mvrnd.algorithm.Executor;
import com.damari.mvrnd.algorithm.ExecutorJob;
import com.damari.mvrnd.coin.Coin;
import com.damari.mvrnd.data.Statistics;
import com.damari.mvrnd.data.MyThreadPoolExecutor;
import com.damari.mvrnd.util.Timer;

public class TestTrade {

	private static final Logger log = LoggerFactory.getLogger(TestTrade.class.getName());

	public static Statistics usingThreads(Class<?> algoClazz, Config config, int iters, Coin coin, int deposit,
			float commission, float goalPercent, float riskPercent, long time, int price, int tradeSize,
			int spread, long timeStepMs, int dataSizeReq) throws Exception {

		Timer totTime = new Timer();

		// Setup thread pool
		int cpus = Runtime.getRuntime().availableProcessors();
		int threads = cpus >= 2 ? cpus / 2 : cpus; // actual cores tends to be this
		MyThreadPoolExecutor executorService = new MyThreadPoolExecutor(threads, threads, 60);

		log.info("------ SETUP ------");
		log.info(" Logical CPUs: {}", cpus);
		log.info("Using threads: {}", threads);
		log.info("   Iterations: {}", iters);
		log.info("         Goal: {}%", goalPercent);
		log.info("         Risk: {}%", riskPercent);
		log.info("   Trade Size: {}", tradeSize);
		log.info("         Coin: {}", coin);
		log.info("       Spread: {}", spread);
		if (config.containsKey("maxPositions")) {
			log.info("Max Positions: {}", config.getInt("maxPositions"));
		}
		if (config.containsKey("positionDistance")) {
			log.info(" Pos.distance: {}", round(config.getInt("positionDistance")));
		}

		// Create jobs
		log.info("Creating jobs ...");
		int risk = (int)((float)deposit * (100f - riskPercent) / 100f);
		int winClassification = (int)((float)deposit * (1f + goalPercent / 100f));
		Statistics stats = new Statistics();
		int taskId = 0;
		List<ExecutorJob> todo = new ArrayList<>(iters);
		while (iters > 0) {
			Executor algoStrategy = new Executor(taskId++, algoClazz, config, stats, coin, risk, winClassification,
					deposit, commission, time, price, tradeSize, spread, timeStepMs, dataSizeReq);
			ExecutorJob algoJob = new ExecutorJob(algoStrategy);
			todo.add(algoJob);
			iters--;
		}

		// Submit jobs
		log.info("Submitting jobs ...");
		List<Future<ExecutorJob>> results = executorService.invokeAll(todo);

		// Shutdown
		log.info("Shutting down ...");
		executorService.shutdown();

		log.info("Waiting on work pool ...");
		boolean completedOk = false;
		try {
			completedOk = executorService.awaitTermination(5L, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			log.error("Exception while waiting on outstanding threads to complete.", e);
		}
		log.info(completedOk ? "All threads were consumed OK." : "Some threads did not complete and was terminated.");
		if (!completedOk) executorService.shutdownNow();

		totTime.stop();
		log.info("Total time: {}sec", totTime.getSecs());
		log.info("Total time: {}", totTime.getMinutesAndSeconds());

		// Check results
		iters = 0;
		StringBuilder sb = new StringBuilder(1024);
		for (Future<ExecutorJob> aj : results) {
			if (aj.isDone()) sb.append(" ").append(iters++);
		}
		log.info("Finished jobs:{}", (iters == 0 ? " N/A" : sb.toString()));
		assertTrue("Expected at least some finished jobs, got " + iters + " finished jobs", iters > 0);

		iters = 0;
		sb.setLength(0);
		for (Future<ExecutorJob> aj : results) {
			if (aj.isCancelled()) sb.append(" ").append(iters++);
		}
		log.info("Cancelled jobs:{}", (iters == 0 ? " N/A" : sb.toString()));
		assertTrue("Expected no cancelled jobs, got " + iters + " cancelled jobs", iters == 0);

		return stats;
	}

}
