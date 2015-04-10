/*******************************************************************************
 *    Copyright 2015 Peter Cashel (pacas00@petercashel.net)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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
		if (threadMan == null)
			threadMan = new threadManager();
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
