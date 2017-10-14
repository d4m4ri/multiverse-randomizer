/*
 * Thread pool executor with exception handling.
 */
package com.damari.mvrnd.data;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyThreadPoolExecutor extends ThreadPoolExecutor {

	public MyThreadPoolExecutor(int corePoolSize, int maxPoolSize, long keepAliveSeconds) { 
		super(corePoolSize, maxPoolSize, keepAliveSeconds, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10000));
	}

	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		if (t == null && r instanceof Future<?>) {
			try {
				@SuppressWarnings("unused")
				Object result = ((Future<?>) r).get();
				//System.out.println("Job completed (" + result + ")");
			} catch (CancellationException ce) {
				System.err.println("CancellationException: " + ce.toString());
				t = ce;
			} catch (ExecutionException ee) {
				System.err.println("ExecutionException: " + ee.toString());
				t = ee.getCause();
			} catch (InterruptedException ie) {
				System.err.println("InterruptedException: " + ie.toString());
				Thread.currentThread().interrupt(); // ignore/reset
			}
		}
		if (t != null) {
			t.printStackTrace();
			throw new RuntimeException("Thread error");
		}
	}

}
