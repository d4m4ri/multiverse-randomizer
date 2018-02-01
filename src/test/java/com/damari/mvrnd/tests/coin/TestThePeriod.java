/*
 * Visualize the periods using random walk.
 * For more info see Khan Academy video
 * @see https://www.youtube.com/watch?v=GtOt7EBNEwQ
 */
package com.damari.mvrnd.tests.coin;

import static org.junit.Assert.assertTrue;

import static com.damari.mvrnd.coin.Coin.head;

import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.damari.mvrnd.coin.Coin;
import com.damari.mvrnd.coin.CoinNeumannENIACRandom;
import com.damari.mvrnd.coin.CoinRandom;
import com.damari.mvrnd.coin.CoinSecureRandom;
import com.damari.mvrnd.coin.CoinSplittableRandom;
import com.damari.mvrnd.coin.CoinXoRoShiRo128PlusRandom;
import com.damari.mvrnd.util.Timer;

public class TestThePeriod {

	@Test
	public void testThePeriodCoverageForEachFairCoinUsingRandomWalkVisualized() throws InterruptedException {
		int width = 1920;
		int height = 1080;
		int pixels = width * height;
		Color bgColor = new Color(50, 50, 50);
		FrameAndCanvas frame = new FrameAndCanvas("The Period for each Coin using Random Walk Visualized",
				width, height, bgColor);

		List<Coin> coins = Arrays.asList(
				new CoinSecureRandom(),
				new CoinRandom(),
				new CoinSplittableRandom(),
				new CoinXoRoShiRo128PlusRandom(),
				new CoinNeumannENIACRandom());
		int[] colors = {
				new Color(255,   0,   0).getRGB(),
				new Color(  0, 255,   0).getRGB(),
				new Color(255,   0, 255).getRGB(),
				new Color(  0, 255, 255).getRGB(),
				new Color(127, 127, 127).getRGB()};
		List<String> results = new ArrayList<>();

		Timer timer = new Timer();
		for (int c = 0; c < coins.size(); c++) {
			int x = width / 2;
			int y = height / 2;
			Coin coin = coins.get(c);
			int color = colors[c];
			boolean xAxis = true;

			// Coin info
			frame.copyCanvas();
			frame.text(200, 200, "Coin: " + coin, new Color(255,255,255), new Font("Serif", Font.BOLD, 24));
			Thread.sleep(4000);
			frame.restoreCanvas();

			// Random walk
			timer.start();
			for (int i = 0; i < 30_000_000; i++) {
				// Movement
				boolean toss = coin.toss();
				if (xAxis) {
					x = (toss == head) ? x + 1 : x - 1;
					xAxis = false;
				} else {
					y = (toss == head) ? y + 1 : y - 1;
					xAxis = true;
				}

				// Asteroids screen wrapper
				if (x < 0) x = width - 1;
				if (y < 0) y = height - 1;
				if (x >= width) x = 0;
				if (y >= height) y = 0;

				frame.plot(x, y, color);
			}
			timer.stop();

			// Coverage
			int pixelMatch = 0;
			for (x = 0; x < width; x++) {
				for (y = 0; y < height; y++) {
					if (frame.getPixelColor(x, y) == colors[c]) {
						pixelMatch++;
					}
				}
			}

			float matchPercent = 100f * pixelMatch / pixels;
			assertTrue("Found unfair coin: " + coin + ", match percent " + matchPercent + "%", matchPercent >= 90.000f);
			String matchPercentStr = new BigDecimal(matchPercent).setScale(3, RoundingMode.HALF_UP).toString();
			results.add("Matched: " + pixelMatch + "/" + pixels + " (~" + matchPercentStr + "%) - took " + timer.getMinutesAndSeconds());

			frame.copyCanvas();
			frame.text(200, 240, results.get(c), new Color(255,255,255), new Font("Serif", Font.BOLD, 24));
			Thread.sleep(6000);
			frame.restoreCanvas();
		}

		// Summary
		frame.fillCanvas(bgColor);
		for (int c = 0; c < coins.size(); c++) {
			frame.text(200, 180 + c * 40, coins.get(c) + " - " + results.get(c), new Color(colors[c]), new Font("Serif", Font.BOLD, 24));
		}
		Thread.sleep(15000);
	}

}
