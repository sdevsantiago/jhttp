package sdevsantiago.jhttp.core;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class Dispatcher {

	private final Worker[] workers;
	private final AtomicInteger counter = new AtomicInteger(0);

	/**
	 * Assigns a channel to a worker.
	 * @param channel The channel to assign. Must not be null.
	 */
	public void dispatch(final @NonNull SocketChannel channel) {
		final var index = Math.abs(counter.getAndIncrement() % workers.length);
		workers[index].register(channel);
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
