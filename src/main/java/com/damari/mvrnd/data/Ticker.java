package com.damari.mvrnd.data;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ticker {

	private static final Logger log = LoggerFactory.getLogger(Ticker.class);

	/**
	 * Fetch ticker data and write to file. If already fetched, return cached data on disk.
	 * @param ticker string.
	 * @return CSV content or null if fetch failed.
	 * @throws IOException if there's net or disk error.
	 * @throws InterruptedException if retry idle fails.
	 */
	public static String readData(String ticker) throws IOException, InterruptedException {
		if (fileExists(ticker)) {
			String data = fetchFile(ticker);
			return data;
		} else {
			ImmutablePair<String, String> config = fetchConfig(ticker);
			if (config == null) return null;
			String data = fetchNet(ticker, config.left, config.right);
			if (data == null) return null;
			writeData(ticker, data);
			return data;
		}
	}

	/**
	 * Fetch ticker data from file.
	 * @throws IOException if file error.
	 */
	private static String fetchFile(String ticker) throws IOException {
		Path path = FileSystems.getDefault().getPath("src/main/resources/ticker", ticker.toLowerCase() + ".csv");
		BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8);
		String data = br.lines().collect(Collectors.joining("\n"));
		br.close();
		return data;
	}

	/**
	 * Check if ticker file already exists.
	 * @param ticker string.
	 * @return boolean with file exists or not.
	 */
	private static boolean fileExists(String ticker) {
		File file = FileSystems.getDefault().getPath("src/main/resources/ticker", ticker.toLowerCase() + ".csv").toFile();
		return file.exists();
	}

	/**
	 * Write ticker data to file.
	 * @param ticker string.
	 * @param data string.
	 * @throws IOException if write fails.
	 */
	private static void writeData(String ticker, String data) throws IOException {
		Path path = FileSystems.getDefault().getPath("src/main/resources/ticker", ticker.toLowerCase() + ".csv");
		BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
		bw.write(data, 0, data.length());
		bw.close();
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
