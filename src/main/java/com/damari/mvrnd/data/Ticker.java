package com.damari.mvrnd.data;

import static com.damari.mvrnd.algorithm.Algorithm.price;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ticker {

	private static final Logger log = LoggerFactory.getLogger(Ticker.class);

	/**
	 * Transform ticker data to data set.
	 * @param tickerData string.
	 * @return DataSet with time- and price series.
	 * @throws DataSetException if dataset exception occurs.
	 */
	public static DataSet transform(String tickerData) throws DataSetException {
		final int tickerDate = 0;	// 1993-01-29
		final int tickerOpen = 1;	// 43.96
		final int tickerHigh = 2;	// 43.96
		final int tickerLow = 3;	// 43.75
		final int tickerClose = 4;	// 43.93
		String[] data = tickerData.split("\n");
		DataSet dataSet = new DataSet();
		for (int row = 1; row < data.length; row++) { // skip first row (TOC)
			String[] daily = data[row].split(",");
			int open = price(Float.valueOf(daily[tickerOpen]));
			int high = price(Float.valueOf(daily[tickerHigh]));
			int low = price(Float.valueOf(daily[tickerLow]));
			int close = price(Float.valueOf(daily[tickerClose]));

			// Chop the day into time chunks
			// 0930   1030   1130   1230   1330   1430   1530   1600   (6h30m)
			long begin = new DateTime(daily[tickerDate]).plusHours(9).plusMinutes(30).getMillis();	// 1993-01-29 09:30:00
			long earlyMidday = new DateTime(begin).plusHours(2).plusMinutes(10).getMillis();		// 1993-01-29 11:40:00
			long lateMidday = new DateTime(earlyMidday).plusHours(2).plusMinutes(10).getMillis();	// 1993-01-29 13:50:00
			long end = new DateTime(lateMidday).plusHours(2).plusMinutes(10).getMillis();			// 1993-01-29 16:00:00

			log.debug("begin={}; end={}; open={}; high={}; low={}; close={}", begin, end, open, high, low, close);
			DataSet dsChunk1 = createTickerData(begin, earlyMidday, open, high);		// open -> high
			DataSet dsChunk2 = createTickerData(earlyMidday, lateMidday, high, low);	// high -> low
			DataSet dsChunk3 = createTickerData(lateMidday, end, low, close);			// low -> close
			dataSet.append(dsChunk1);
			dataSet.append(dsChunk2);
			dataSet.append(dsChunk3);
		}
		return dataSet;
	}

	/**
	 * Generate time- and price series for a given time period with price linearly between two price points.
	 * @param begin of time series (ms since epoch).
	 * @param end of time series (ms since epoch).
	 * @param priceStarts at.
	 * @param priceEnds at.
	 * @return DataSet with time- and price series.
	 * @throws DataSetException if dataset exception occurs.
	 */
	private static DataSet createTickerData(long begin, long end, int priceStarts, int priceEnds) throws DataSetException {
		int tickTimeMs = 5 * 60 * 1000; // 5m
		int ticks = (int)((end - begin) / tickTimeMs);
		int priceDeltaPerTick = (priceEnds - priceStarts) / ticks;

		long[] timeSeries = new long[ticks];
		int[] priceSeries = new int[ticks];
		long iterTime = begin;
		int iterPrice = priceStarts;
		for (int i = 0; i < ticks; i++) {
			timeSeries[i] = iterTime;
			priceSeries[i] = iterPrice;
			iterTime += tickTimeMs;
			iterPrice += priceDeltaPerTick;
		}

		return new DataSet(timeSeries, priceSeries);
	}

	/**
	 * Fetch ticker data and write to file. If already fetched, return cached data on disk.
	 * @param ticker string.
	 * @return CSV content or null if fetch failed or content is empty.
	 * @throws IOException if there's net or disk error.
	 * @throws InterruptedException if retry idle fails.
	 */
	public static String readData(String ticker) throws IOException, InterruptedException {
		BufferedReader br = Files.newBufferedReader(tickerPath(ticker), StandardCharsets.UTF_8);
		String data = br.lines().collect(Collectors.joining("\n"));
		br.close();
		return data;
	}

	/**
	 * Get tickers resource path.
	 * @param ticker string.
	 * @return Path to ticker data.
	 */
	private static Path tickerPath(String ticker) {
		return FileSystems.getDefault().getPath("src", "main", "resources", "ticker", ticker.toLowerCase() + ".csv");
	}

}
