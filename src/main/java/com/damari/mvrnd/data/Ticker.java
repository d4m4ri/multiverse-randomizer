package com.damari.mvrnd.data;

import static com.damari.mvrnd.algorithm.Algorithm.price;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
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
		final int tickerLow = 3;		// 43.75
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
			long earlyMidday = new DateTime(begin).plusHours(2).plusMinutes(10).getMillis();			// 1993-01-29 11:40:00
			long lateMidday = new DateTime(earlyMidday).plusHours(2).plusMinutes(10).getMillis();	// 1993-01-29 13:50:00
			long end = new DateTime(lateMidday).plusHours(2).plusMinutes(10).getMillis();			// 1993-01-29 16:00:00

			//log.info("begin={}; end={}; open={}; high={}; low={}; close={}",
			//		new DateTime(begin), new DateTime(end), round(open), round(high), round(low), round(close));
			DataSet dsChunk1 = createTickerData(begin, earlyMidday, open, high);			// open -> high
			DataSet dsChunk2 = createTickerData(earlyMidday, lateMidday, high, low);		// high -> low
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
		if (cacheExists(ticker)) {
			String data = fetchCache(ticker);
			return data;
		}

		ImmutablePair<String, String> config = fetchConfig(ticker);
		if (config == null) return null;
		String data = fetchNet(ticker, config.left, config.right);
		if (data == null) return null;
		storeData(ticker, data);
		return data;
	}

	/**
	 * Fetch ticker data cache.
	 * @throws IOException if file error.
	 */
	private static String fetchCache(String ticker) throws IOException {
		BufferedReader br = Files.newBufferedReader(tickerPath(ticker), StandardCharsets.UTF_8);
		String data = br.lines().collect(Collectors.joining("\n"));
		br.close();
		return data;
	}

	/**
	 * Check if ticker already exists.
	 * @param ticker string.
	 * @return boolean if cache exists.
	 */
	private static boolean cacheExists(String ticker) {
		File file = tickerPath(ticker).toFile();
		return file.exists();
	}

	/**
	 * Store ticker data to file.
	 * @param ticker string.
	 * @param data string.
	 * @throws IOException if store fails.
	 */
	private static void storeData(String ticker, String data) throws IOException {
		BufferedWriter bw = Files.newBufferedWriter(tickerPath(ticker), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
		bw.write(data, 0, data.length());
		bw.close();
	}

	/**
	 * Get tickers resource path.
	 * @param ticker string.
	 * @return Path to ticker data.
	 */
	private static Path tickerPath(String ticker) {
		return FileSystems.getDefault().getPath("src", "main", "resources", "ticker", ticker.toLowerCase() + ".csv");
	}

	/**
	 * Load ticker data from net. Will retry three times.
	 * @param ticker to load from.
	 * @param cookie to use.
	 * @param crumb to use.
	 * @return String in CSV format, headers on first row, rest data.
	 * @throws InterruptedException if thread sleep exception occurs.
	 * @throws MalformedURLException if URL is malformed.
	 */
	private static String fetchNet(String ticker, String cookie, String crumb) throws InterruptedException, MalformedURLException {
		int attempts = 3;
		StringBuilder data = new StringBuilder(4096);
		for (int attempt = 0; attempt < attempts; attempt++) {
			long from = 0;
			long to = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
			String interval = "1d"; // highest granularity
			String providerURL = "https://query1.finance.yahoo.com/v7/finance/download/" + ticker +
					"?period1=" + from + "&period2=" + to + "&interval=" + interval + "&events=history&crumb=" + crumb;
			log.debug("Loading ticker data from URL: {} (attempt {})", providerURL, attempt);
			URL url = new URL(providerURL);
			try {
				URLConnection con = url.openConnection();
				con.setRequestProperty("Cookie", cookie);

				InputStream is = con.getInputStream();
				InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
				BufferedReader br = new BufferedReader(isr);
				for (String line = br.readLine(); line != null; line = br.readLine()) {
					data.append(line + "\n");
				}
				br.close();
				isr.close();
				is.close();
				if (data.length() > 2) break; // they can give HTTP/200 but empty buffer
			} catch (IOException e) {
			}
			Thread.sleep(5000);
		}
		return data.toString();
	}

	/**
	 * Load cookie and crumb from Yahoo and return them as a pair.
	 * @param ticker to track.
	 * @return ImmutablePair of cookie and crumb.
	 * @throws IOException if there's net error.
	 */
	private static ImmutablePair<String, String> fetchConfig(String ticker) throws IOException {
		URL url = new URL("https://finance.yahoo.com/quote/" + ticker);
		URLConnection con = url.openConnection();
		String cookie = con.getHeaderField("set-cookie");
		if (cookie == null) return null;
		log.debug("Found provider cookie: {}", cookie);

		String crumb = null;
		InputStream is = con.getInputStream();
		InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
		BufferedReader br = new BufferedReader(isr);
		String line = br.readLine();
		if (line != null) {
			Pattern crumbPattern = Pattern.compile(".*\"CrumbStore\":\\{\"crumb\":\"([^\"]+)\"\\}.*");
			do {
				Matcher matcher = crumbPattern.matcher(line);
				if (matcher.matches()) {
					crumb = matcher.group(1);
					break;
				}
				line = br.readLine();
			} while (line != null);
		}
		br.close();
		isr.close();
		is.close();
		if (crumb == null) return null;
		log.debug("Found provider crumb: {}", crumb);

		return ImmutablePair.of(cookie,  crumb);
	}

}
