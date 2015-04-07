/*******************************************************************************
 * Copyright (c) 2015 Peter Cashel (pacas00@petercashel.net). All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Creative Commons Attribution-NoDerivatives 4.0 International License
 * which accompanies this distribution, and is available at
 * http://creativecommons.org/licenses/by-nd/4.0/.
 *
 * Contributors:
 *     Peter Cashel - initial implementation
 *******************************************************************************/
package net.petercashel.commonlib.threading;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("rawtypes")
public class threadManager {
	private int processors = Runtime.getRuntime().availableProcessors();
	public ExecutorService exec = Executors.newFixedThreadPool(processors);
	public static threadManager threadMan;
	
	public static threadManager getInstance() {
		if (threadMan == null) threadMan = new threadManager();
		return threadMan;
	}
	public threadManager() {
		
	}
	
	public Future addRunnable(threadRunnable r) {
		return exec.submit(r);
	}

	public Future addRunnable(Runnable r) {
		return exec.submit(r);
	}

	@SuppressWarnings("unchecked")
	public Future addCallable(Callable r) {
		return exec.submit(r);
	}
	
	public void shutdown() {
		exec.shutdown();
	}
}
