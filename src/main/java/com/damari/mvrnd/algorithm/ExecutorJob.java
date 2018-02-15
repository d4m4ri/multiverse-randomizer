package com.damari.mvrnd.algorithm;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorJob implements Callable<ExecutorJob> {

	private static final Logger log = LoggerFactory.getLogger(ExecutorJob.class);

	private ExecutorAlgo execAlgo;

	public ExecutorJob(ExecutorAlgo execAlgo) {
		this.execAlgo = execAlgo;
	}

	@Override
	public ExecutorJob call() {
		try {
			execAlgo.process();
		} catch (Exception e) {
			log.error("Failed to process thread.", e);
		}
		return this;
	}

}
