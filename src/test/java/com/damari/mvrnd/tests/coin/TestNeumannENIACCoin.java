package com.damari.mvrnd.tests.coin;

import static org.junit.Assert.assertEquals;
import static com.damari.mvrnd.coin.CoinNeumannENIACRandom.longLen;

import org.junit.Test;

public class TestNeumannENIACCoin {

	@Test
	public void givenValueZeroThenExpectLengthToBeOne() {
		assertEquals("Unexpected length of long value 0", 1, longLen(0L));
	}

	@Test
	public void givenPositiveLongsThenExpectMatchingLength() {
		assertEquals("Unexpected length of long value 1", 1, longLen(1L));
		assertEquals("Unexpected length of long value 10", 2, longLen(10L));
		assertEquals("Unexpected length of long value 100", 3, longLen(100L));
		assertEquals("Unexpected length of long value 1000", 4, longLen(1000L));
		assertEquals("Unexpected length of long value 10000", 5, longLen(10000L));
		assertEquals("Unexpected length of long value 100000", 6, longLen(100000L));
		assertEquals("Unexpected length of long value 1000000", 7, longLen(1000000L));
		assertEquals("Unexpected length of long value 10000000", 8, longLen(10000000L));
		assertEquals("Unexpected length of long value 100000000", 9, longLen(100000000L));
		assertEquals("Unexpected length of long value 1000000000", 10, longLen(1000000000L));
		assertEquals("Unexpected length of long value 10000000000", 11, longLen(10000000000L));
		assertEquals("Unexpected length of long value 100000000000", 12, longLen(100000000000L));
		assertEquals("Unexpected length of long value 1000000000000", 13, longLen(1000000000000L));
		assertEquals("Unexpected length of long value 10000000000000", 14, longLen(10000000000000L));
		assertEquals("Unexpected length of long value 100000000000000", 15, longLen(100000000000000L));
		assertEquals("Unexpected length of long value 1000000000000000", 16, longLen(1000000000000000L));
		assertEquals("Unexpected length of long value 10000000000000000", 17, longLen(10000000000000000L));
		assertEquals("Unexpected length of long value 100000000000000000", 18, longLen(100000000000000000L));
		assertEquals("Unexpected length of long value 1000000000000000000", 19, longLen(1000000000000000000L));
	}

	@Test
	public void givenNegativeLongsThenExpectMatchingLength() {
		assertEquals("Unexpected length of long value -1", 2, longLen(-1L));
		assertEquals("Unexpected length of long value -10", 3, longLen(-10L));
		assertEquals("Unexpected length of long value -100", 4, longLen(-100L));
		assertEquals("Unexpected length of long value -1000", 5, longLen(-1000L));
		assertEquals("Unexpected length of long value -10000", 6, longLen(-10000L));
		assertEquals("Unexpected length of long value -100000", 7, longLen(-100000L));
		assertEquals("Unexpected length of long value -1000000", 8, longLen(-1000000L));
		assertEquals("Unexpected length of long value -10000000", 9, longLen(-10000000L));
		assertEquals("Unexpected length of long value -100000000", 10, longLen(-100000000L));
		assertEquals("Unexpected length of long value -1000000000", 11, longLen(-1000000000L));
		assertEquals("Unexpected length of long value -10000000000", 12, longLen(-10000000000L));
		assertEquals("Unexpected length of long value -100000000000", 13, longLen(-100000000000L));
		assertEquals("Unexpected length of long value -1000000000000", 14, longLen(-1000000000000L));
		assertEquals("Unexpected length of long value -10000000000000", 15, longLen(-10000000000000L));
		assertEquals("Unexpected length of long value -100000000000000", 16, longLen(-100000000000000L));
		assertEquals("Unexpected length of long value -1000000000000000", 17, longLen(-1000000000000000L));
		assertEquals("Unexpected length of long value -10000000000000000", 18, longLen(-10000000000000000L));
		assertEquals("Unexpected length of long value -100000000000000000", 19, longLen(-100000000000000000L));
		assertEquals("Unexpected length of long value -1000000000000000000", 20, longLen(-1000000000000000000L));
	}

}
