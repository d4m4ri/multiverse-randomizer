package com.damari.mvrnd.tests.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.damari.mvrnd.util.StringHelper;

public class TestStringHelper {

	@Test
	public void givenCamelCaseWordThenExpectFirstWordToBeCroppedAndBothCapitalized() {
		String s = StringHelper.crop("positionDistance", 13);
		assertEquals(13, s.length());
		assertEquals("Posi.Distance", s);
	}

	@Test
	public void givenTwoShortWordsWithinBoundaryThenExpectNoCropping() {
		String s = StringHelper.crop("Max Positions", 13);
		assertEquals(13, s.length());
		assertEquals("Max Positions", s);
	}

	@Test
	public void givenTwoShortCamelCaseWordsWithinBoundaryThenExpectNoCropping() {
		String s = StringHelper.crop("maxPositions", 13);
		assertEquals(13, s.length());
		assertEquals("Max Positions", s);
	}

	@Test
	public void givenVeryLongWordsThenBothToBeCropped() {
		String s = StringHelper.crop("suchverylongWordItSureIs", 13);
		assertEquals(13, s.length());
		assertEquals("Suchve.WordIt", s);
	}

	@Test
	public void givenCropStringThenExpectCroppedString() {
		String s = StringHelper.crop("something", 4);
		assertEquals("Some", s);
	}

	@Test
	public void givenNullStringThenExpectNulLString() {
		String s = StringHelper.crop(null, 4);
		assertEquals(null, s);
	}

	@Test
	public void givenEmptyStringThenExpectEmptyString() {
		String s = StringHelper.crop("", 4);
		assertEquals("", s);
	}

}
