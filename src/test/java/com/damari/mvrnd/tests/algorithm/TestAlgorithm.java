package com.damari.mvrnd.tests.algorithm;

import static org.junit.Assert.assertEquals;

import static com.damari.mvrnd.algorithm.Algorithm.price;
import static com.damari.mvrnd.algorithm.Algorithm.round;

import org.junit.Test;

public class TestAlgorithm {

	@Test
	public void givenPriceMethodWithDecimalsThenExpectCorrectRounding() {
		assertEquals("Rounding error", 999655, price(9996.55f), 0f);
		assertEquals("Rounding error", 999655, price(9996.554f), 0f);
		assertEquals("Rounding error", 999655, price(9996.556f), 0f);
	}

	@Test
	public void givenRoundingOfDecimalsThenExpectCorrectRounding() {
		assertEquals("Rounding error", "12345", String.valueOf(price(123.45f)));
		assertEquals("Rounding error", "12345", String.valueOf(price(123.456f)));
		assertEquals("Rounding error", "123.00", String.valueOf(round(12300.12f)));
		assertEquals("Rounding error", "123.00", String.valueOf(round(12300)));
	}

}
