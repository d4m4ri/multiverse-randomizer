package com.damari.mvrnd.algorithm;

import java.util.concurrent.Callable;

public class ExecutorJob implements Callable<ExecutorJob> {

	private ExecutorAlgo execAlgo;

	public ExecutorJob(ExecutorAlgo execAlgo) {
		this.execAlgo = execAlgo;
	}

	@Override
	public ExecutorJob call() {
		execAlgo.process();
		return this;
	}

}
