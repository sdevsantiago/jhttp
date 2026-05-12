package sdevsantiago.jhttp.core;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class Dispatcher {

	private final WorkerPool workerPool;
	private final AtomicInteger counter = new AtomicInteger(0);

	/**
	 * Assigns a channel to a worker.
	 * @param channel The channel to assign. Must not be null.
	 */
	public void dispatch(final @NonNull SocketChannel channel) {
		final var workers = workerPool.getWorkers();
		final var index = Math.abs(counter.getAndIncrement() % workers.length);
		workers[index].register(channel);
	}

}
