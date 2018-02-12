package com.damari.mvrnd.tests.system;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestJava {

	@Test
	public void givenJavaVersionThenExpectJava1_9() {
		System.out.println(System.getProperty("java.version"));
		assertTrue("Unexpected java version", System.getProperty("java.version").startsWith("9."));
	}

}
