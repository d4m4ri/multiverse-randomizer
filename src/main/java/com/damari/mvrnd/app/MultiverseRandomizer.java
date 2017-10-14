/*
 * "What will Apple's shares cost tomorrow?"
 *
 * TP = S * e^r
 * S = Today's Stock Price
 * e = Tomorrow's Stock Price
 * r = drift
 *   r (v1 none) = 0
 *   r (v2 stdev) = u - σ2/2
 *   r (v3 rf int & volat) = Risk Free Rate - (Variance / 2)
 * u = Average of the Historical Periodic Return (eg daily, hourly, tick etc)
 *
 * Online materials:
 * - https://www.youtube.com/watch?v=3gcLRU24-w0
 * - "Why might share prices follow a random walk?" https://www.tcd.ie/Economics/assets/pdf/SER/2007/Samuel_Dupernex.pdf
 *   "It seems that stocks do approximately follow a random walk, but there are other factors, such as those discussed
 *    by Fama and French (1995), which appear to affect stock prices as well."
 * - French Louis Bachelier did similar findings in his PhD thesis "The Theory of Speculation" (1900).
 * - Pseudorandom number generators: https://www.youtube.com/watch?v=GtOt7EBNEwQ
 * - Origin of Markov chains: https://www.youtube.com/watch?v=Ws63I3F7Moc
 *
 * "elegance is for tailors" - call me if you beat Miss Random! /DR :)
 */
package com.damari.mvrnd.app;

public class MultiverseRandomizer {

	public static void main(final String[] args) {
		new App();
	}

}