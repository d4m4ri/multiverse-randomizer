package com.damari.mvrnd.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.damari.mvrnd.ui.Panel;

public class App {

	private static final Logger log = LoggerFactory.getLogger(App.class.getName());

	public App() {
		log.info("Multiverse Randomizer starting ...");
		log.info("Using {} threads", getPhysicalCores());
		new Panel(1600, 700);
	}

	/**
	 * Get physical cores assuming hyper-threading (HT) is enabled.
	 * @return Number of physical cores.
	 */
	public static int getPhysicalCores() {
		int cores = Runtime.getRuntime().availableProcessors();
		return cores <= 1 ? 1 : Runtime.getRuntime().availableProcessors() / 2;
	}

}
