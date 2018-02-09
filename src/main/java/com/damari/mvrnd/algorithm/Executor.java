package com.damari.mvrnd.algorithm;

import static com.damari.mvrnd.algorithm.Strategy.dateTimeFormatter;
import static com.damari.mvrnd.algorithm.Strategy.round;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.damari.mvrnd.coin.Coin;
import com.damari.mvrnd.data.DataGenerator;
import com.damari.mvrnd.data.OutOfMoneyException;
import com.damari.mvrnd.data.Statistics;
import com.damari.mvrnd.order.Broker;
import com.damari.mvrnd.order.NoCommissionException;
import com.damari.mvrnd.util.Timer;

public class Executor extends ExecutorAlgo {

	private static final Logger log = LoggerFactory.getLogger(Executor.class);

	private int taskId;
	private Class<?> algoClazz;
	private Config config;
	private Statistics stats;
	private Coin coin;
	private int risk;
	private int winClassification;
	private int deposit;
	private float commission;
	private long time;
	private int price;
	private int tradeSize;
	private int spread;
	private long timeStepMs;
	private int dataSizeReq;

	public Executor(int taskId, Class<?> algoClazz, Config config, Statistics stats, Coin coin,
			int risk, int winClassification, int deposit, float commission, long time, int price,
			int tradeSize, int spread, long timeStepMs, int dataSizeReq) {
		this.taskId = taskId;
		this.algoClazz = algoClazz;
		this.config = config;
		this.stats = stats;
		this.coin = coin;
		this.risk = risk;
		this.winClassification = winClassification;
		this.deposit = deposit;
		this.commission = commission;
		this.time = time;
		this.price = price;
		this.tradeSize = tradeSize;
		this.spread = spread;
		this.timeStepMs = timeStepMs;
		this.dataSizeReq = dataSizeReq;
	}

	@Override
	public boolean process() {
		stats.addJobStarted();

		Timer timer = new Timer();
		timer.start();

		DataGenerator asset = new DataGenerator();
		int bucket = asset.lockBucket();

		final StringBuilder r = new StringBuilder(1000);
		r.append("------ TASK ").append(taskId).append(" (T").append(bucket).append(") ------\n");

		Broker broker = new Broker(deposit)
			.setCommissionPercent(commission);

		int dataSizeGen = asset.generateRandomWalk(bucket, coin, dataSizeReq, time, price, spread, timeStepMs);
		timer.stop();
		long totTimeDataGenerate = stats.addTimeDataGenerate(timer.getMillis());
		r.append("Data generation took ").append(timer).append(" for ").append(dataSizeGen).append(" price points\n");

		timer.start();
		Strategy algo = null;
		Constructor<?> constructor;
		try {
			constructor = algoClazz.getConstructor(new Class[] {
					Config.class, Broker.class, int.class, int.class});
			algo = (Strategy) constructor.newInstance(new Object[] {
					config, broker, spread, tradeSize});
		} catch (NoSuchMethodException | SecurityException | InstantiationException |
				IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			asset.unlockBucket(bucket);
			throw new RuntimeException("Failed to create algorithm");
		}

		int wins = stats.getWins();
		int assetIdx = 0;
		for (; assetIdx < dataSizeGen; assetIdx++) {
			try {
				algo.process(asset.getTime(assetIdx), asset.getPrice(assetIdx));
			} catch (OutOfMoneyException e) {
				r.append("#").append(assetIdx).append(" Ran out of money\n");
				break;
			} catch (NoCommissionException e) {
				r.append("#").append(assetIdx).append(" Haven't specified commission costs\n");
				log.info(r.toString());
				asset.unlockBucket(bucket);
				throw new RuntimeException("Haven't specified commission costs");
			}

			if (broker.getBalance() < risk) {
				r.append("#").append(assetIdx).append(" Balance too low :(\n");
				break;
			}

			final int mark2market = algo.getMarkToMarket();
			if (mark2market > winClassification) {
				r.append("#").append(assetIdx).append(" Goal archived @ $").append(round(algo.getLastPrice())).append("\n");
				wins = stats.addWins();
				break;
			}
		}
		timer.stop();
		r.append("Algo process took ").append(timer).append(" to process ").append(assetIdx).append(" entries\n");
		final long totTimeAlgoProcess = stats.addTimeAlgoProcess(timer.getMillis());
		final int winLoss = (broker.getBalance() + algo.getNAV()) - deposit;
		final long totWinLoss = stats.addWinLoss(winLoss);
		final int totJobs = stats.addJobCompleted();

		r.append(algo.getSummary());
		r.append("        Probability: ").append(BigDecimal.valueOf((float)wins / totJobs * 100f).setScale(2, BigDecimal.ROUND_HALF_UP)).append("%\n")
		 .append("Asset pts requested: ").append(dataSizeReq).append("\n")
		 .append("Asset pts generated: ").append(dataSizeGen).append("\n")
		 .append("   Asset start time: ").append(dateTimeFormatter.print(asset.getStartTime())).append("\n")
		 .append("    Asset stop time: ").append(dateTimeFormatter.print(asset.getStopTime())).append("\n")
		 .append("Asset min/max price: $").append(round(asset.getMinPrice())).append("/$").append(round(asset.getMaxPrice())).append("\n")
		 .append(" Algo min/max price: $").append(round(algo.getMinPrice())).append("/$").append(round(algo.getMaxPrice())).append("\n")
		 .append("    Algo last price: $").append(round(algo.getLastPrice())).append("\n")
		 .append("      Broker orders: ").append(broker.getOrders().size()).append("\n")
		 .append(" Broker commissions: $").append(round(broker.getCommissionSum())).append("\n")
		 .append("  Losses (realised): $").append(round(broker.getLossSum())).append("\n")
		 .append("                NAV: $").append(round(algo.getNAV())).append("\n")
		 .append("            Balance: $").append(round(broker.getBalance())).append("\n")
		 .append("        Balance+NAV: $").append(round(broker.getBalance() + algo.getNAV())).append("\n")
		 .append("           Win/Loss: $").append(round(winLoss)).append("\n")
		 .append("     Total win/loss: $").append(round(totWinLoss)).append("\n")
		 .append(" Total win/loss avg: $").append(round(totWinLoss / totJobs)).append("\n")
		 .append("         Total wins: ").append(wins).append(" out of ").append(totJobs).append("\n")
		 .append("      Avg algo time: ").append(totTimeAlgoProcess / totJobs).append("ms").append("\n")
		 .append("  Avg time data gen: ").append(totTimeDataGenerate / totJobs).append("ms").append("\n");
		log.info(r.toString());

		if (broker.getOrders().size() == 0) {
			log.warn("Expected some orders");
			asset.unlockBucket(bucket);
			throw new RuntimeException("Expected some orders");
		}

		if (assetIdx == dataSizeReq) {
			log.warn("Ran out of asset data to test at {}, please increase sample size", assetIdx);
			asset.unlockBucket(bucket);
			throw new RuntimeException("Ran out of asset data to test at " + assetIdx + ", please increase sample size");
		}

		asset.unlockBucket(bucket);
		return true;
	}

	@Override
	public String toString() {
		return "Executor";
	}

}
