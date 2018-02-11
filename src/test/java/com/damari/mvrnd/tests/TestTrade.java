package com.damari.mvrnd.tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.damari.mvrnd.algorithm.Config;
import com.damari.mvrnd.algorithm.Executor;
import com.damari.mvrnd.algorithm.ExecutorJob;
import com.damari.mvrnd.app.App;
import com.damari.mvrnd.coin.Coin;
import com.damari.mvrnd.data.Statistics;
import com.damari.mvrnd.data.MyThreadPoolExecutor;
import com.damari.mvrnd.util.StringHelper;
import com.damari.mvrnd.util.Timer;

public class TestTrade {

	private static final Logger log = LoggerFactory.getLogger(TestTrade.class.getName());

	public static Statistics usingThreads(Class<?> algoClazz, Config config, int iters, Coin coin, int deposit,
			float commission, float goalPercent, float riskPercent, long startTime, int price, int tradeSize,
			int spread, long timeStepMs, int dataSizeReq) throws Exception {

		Timer time = new Timer();
		time.start();
		MyThreadPoolExecutor executorService = setupThreadPool(config, coin, iters, goalPercent, riskPercent, tradeSize, spread);
		Statistics stats = new Statistics();
		List<ExecutorJob> todos = createJobs(stats, algoClazz, config, coin, iters, deposit, goalPercent, riskPercent,
				tradeSize, commission, startTime, price, spread, timeStepMs, dataSizeReq);
		List<Future<ExecutorJob>> jobs = submitJobs(executorService, todos);
		shutdown(executorService);
		awaitTermination(executorService);
		time.stop();
		log.info("Total time: {}", time.getMinutesAndSeconds());

		report(jobs);

		return stats;
	}

	private static MyThreadPoolExecutor setupThreadPool(Config config, Coin coin, int iters,
			float goalPercent, float riskPercent, int tradeSize, int spread) {
		int threads = App.getPhysicalCores();
		log.info("------ SETUP ------");
		log.info("Using threads: {}", threads);
		log.info("   Iterations: {}", iters);
		log.info("         Goal: {}%", goalPercent);
		log.info("         Risk: {}%", riskPercent);
		log.info("   Trade Size: {}", tradeSize);
		log.info("         Coin: {}", coin);
		log.info("       Spread: {}", spread);
		Iterator<String> keys = config.getKeys();
		while (keys.hasNext()) {
			String key = keys.next();
			Object value = config.getProperty(key);
			String keyDesc = StringHelper.crop(key, 13);
			keyDesc = Strings.padStart(keyDesc, 13 - keyDesc.length(), " ".charAt(0));
			log.info("{}: {}", keyDesc, value);
		}
		return new MyThreadPoolExecutor(threads, threads, 60);
	}

	private static List<ExecutorJob> createJobs(Statistics stats, Class<?> algoClazz, Config config, Coin coin, int iters,
			int deposit, float goalPercent, float riskPercent, int tradeSize, float commission, long time, int price,
			int spread, long timeStepMs, int dataSizeReq) {
		log.info("Creating jobs ...");
		int risk = (int)((float)deposit * (100f - riskPercent) / 100f);
		int winClassification = (int)((float)deposit * (1f + goalPercent / 100f));
		int taskId = 0;
		List<ExecutorJob> todo = new ArrayList<>(iters);
		while (iters > 0) {
			Executor algoStrategy = new Executor(taskId++, algoClazz, config, stats, coin, risk, winClassification,
					deposit, commission, time, price, tradeSize, spread, timeStepMs, dataSizeReq);
			ExecutorJob algoJob = new ExecutorJob(algoStrategy);
			todo.add(algoJob);
			iters--;
		}
		return todo;
	}

	private static List<Future<ExecutorJob>> submitJobs(MyThreadPoolExecutor executorService, List<ExecutorJob> todos)
			throws InterruptedException {
		log.info("Submitting jobs ...");
		return executorService.invokeAll(todos);
	}

	private static void shutdown(MyThreadPoolExecutor executorService) {
		log.info("Shutting down ...");
		executorService.shutdown();
	}

	private static void awaitTermination(MyThreadPoolExecutor executorService) {
		log.info("Waiting on work pool ...");
		boolean completedOk = false;
		try {
			completedOk = executorService.awaitTermination(5L, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			log.error("Exception while waiting on outstanding threads to complete.", e);
		}
		log.info(completedOk ? "All threads were consumed OK." : "Some threads did not complete and was terminated.");
		if (!completedOk) executorService.shutdownNow();
	}

	private static void report(List<Future<ExecutorJob>> jobs) {
		int i = 0;
		StringBuilder sb = new StringBuilder(1024);
		for (Future<ExecutorJob> job : jobs) {
			if (job.isDone()) sb.append(" ").append(i++);
		}
		log.info("Finished jobs:{}", (i == 0 ? " N/A" : sb.toString()));
		assertTrue("Expected at least some finished jobs, got " + i + " finished jobs", i > 0);

		i = 0;
		sb.setLength(0);
		for (Future<ExecutorJob> job : jobs) {
			if (job.isCancelled()) sb.append(" ").append(i++);
		}
		log.info("Cancelled jobs:{}", (i == 0 ? " N/A" : sb.toString()));
		assertTrue("Expected no cancelled jobs, got " + i + " cancelled jobs", i == 0);
	}

}
