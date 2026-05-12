package sdevsantiago.jhttp.core;

import lombok.Getter;

import java.io.IOException;

@Getter
public class WorkerPool {

	private final Worker[] workers;

	public WorkerPool(final int workerPoolSize) throws IOException {
		if (workerPoolSize <= 0) {
			throw new IllegalArgumentException("workerPoolSize must be greater than 0");
		}

		this.workers = new Worker[workerPoolSize];
		for (int i = 0; i < workerPoolSize; i++) {
			workers[i] = new Worker();
		}
	}

	/**
	 * Starts all workers.
	 */
	public void start() {
		for (int i = 0; i < workers.length; i++) {
			final var worker = workers[i];
			final var workerThread = new Thread(worker, "worker-" + (i + 1));
			workerThread.setDaemon(false);
			workerThread.start();
		}
	}

}
