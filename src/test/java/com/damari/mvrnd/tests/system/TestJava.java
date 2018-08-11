package com.damari.mvrnd.tests.system;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestJava {

	@Test
	public void givenJavaVersionThenExpectJava10() {
		assertTrue("Unexpected java version", System.getProperty("java.version").startsWith("10."));
	}

}
