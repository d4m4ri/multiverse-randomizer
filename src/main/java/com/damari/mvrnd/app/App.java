package com.damari.mvrnd.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.damari.mvrnd.ui.Panel;

public class App {

	private static final Logger log = LoggerFactory.getLogger(App.class.getName());

	public App() {
		log.info("Found {} processors", Runtime.getRuntime().availableProcessors());
		new Panel(1600, 700);
		log.info("Random Walk Algos started.");
	}

}
