package sdevsantiago.jhttp.core;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import sdevsantiago.jhttp.http.parser.HttpResponseBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Log4j2
public class Worker implements Runnable {

	private static final long SELECT_TIMEOUT_MS = 1000;
	private static final long IDLE_TIMEOUT_MS = 30000;
	private static final int BUFFER_SIZE = 4096;

	private final Selector selector;

	private volatile boolean running = true;

	private final Queue<SocketChannel> pendingRegistrations = new ConcurrentLinkedQueue<>();

	public Worker() throws IOException {
		this.selector = Selector.open();
	}

	/**
	 * Adds a channel to the queue.
	 * @param channel
	 */
	public void register(final @NonNull SocketChannel channel) {
		pendingRegistrations.add(channel);
		selector.wakeup();
	}

	public void stop() {
		running = false;
		selector.wakeup();
	}

	@Override
	public void run() {
		log.info("Worker started");

		try {
			while (running) {
				selector.select(SELECT_TIMEOUT_MS);
				processPendingRegistrations();
				processSelectedKeys();
				evictIdleConnections();
			}
		} catch (final IOException e) {
			log.error("An error occurred while selecting keys: ", e);
		} finally {
			closeAll();
		}
	}

	private void processPendingRegistrations() {
		SocketChannel channel;

		while ((channel = pendingRegistrations.poll()) != null) {
			try {
				channel.configureBlocking(false);
				channel.register(selector, SelectionKey.OP_READ, System.currentTimeMillis());
				log.debug("Registered channel {}", channel);
			} catch (final IOException e) {
				log.warn("Failed to register channel: ", e);
				closeChannel(channel);
			}
		}
	}

	private void processSelectedKeys() {
		final var keys = selector.selectedKeys();
		for (final var key : keys) {
			if (!key.isValid()) continue;

			try {
				if (key.isReadable()) {
					read(key);
				} else if (key.isWritable()) {
					write(key);
				}
			} catch (final IOException e) {
				log.error("An error occurred while processing a key: ", e);
				key.cancel();
			}
		}
		keys.clear();
	}

	private void read(final @NonNull SelectionKey key) throws IOException {
		final var clientSocket = (SocketChannel) key.channel();
		final var buffer = ByteBuffer.allocate(BUFFER_SIZE);

		final var bytesRead = clientSocket.read(buffer);

		if (bytesRead == -1) {
			closeKey(key);
			return;
		} else if (bytesRead == 0) {
			return;
		}

		buffer.flip();
		touchActivity(key);

		key.attach(
			HttpResponseBuilder.ok("<!DOCTYPE html>"
				+ "<html><head lang=es><title>JHTTP</title><meta charset=\"UTF-8\"></head>"
				+ "<body>Hello World!</body>"
				+ "</html>")
		);

		key.interestOps(SelectionKey.OP_WRITE);
		log.debug("client ready to write");
	}

	private void write(final @NonNull SelectionKey key) throws IOException {
		final var clientSocket = (SocketChannel) key.channel();
		final var response = (ByteBuffer) key.attachment();

		clientSocket.write(response);

		if (response.hasRemaining()) {
			return;
		}

		closeKey(key);
	}

	private void evictIdleConnections() {
		final var now = System.currentTimeMillis();
		for (final var key : selector.keys()) {
			if (!key.isValid()) continue;
			if (key.attachment() instanceof Long lastActivity
				&& (now - lastActivity) > IDLE_TIMEOUT_MS) {
				closeKey(key);
				log.debug("Evicted idle connection {}", key);
			}
		}
	}

	/**
	 * Updates the key's attached timestamp to avoid classifying it as idle.
	 * @param key The key to update.
	 */
	private void touchActivity(final @NonNull SelectionKey key) {
		key.attach(System.currentTimeMillis());
	}

	private void closeAll() {
		pendingRegistrations.forEach(this::closeChannel);
	}

	private void closeKey(final @NonNull SelectionKey key) {
		key.cancel();
		closeChannel(key.channel());
	}


	private void closeChannel(final @NonNull Channel channel) {
		try {
			channel.close();
		} catch (final IOException e) {
			log.error("An error occurred while closing a channel: ", e);
		}
	}

}
