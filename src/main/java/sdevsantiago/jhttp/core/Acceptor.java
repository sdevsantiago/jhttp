package sdevsantiago.jhttp.core;

import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

@RequiredArgsConstructor
@Log4j2
public class Acceptor implements Runnable {

	private final @NonNull ServerSocketChannel serverSocket;
	private final @NonNull Selector selector;
	private final @NonNull Dispatcher dispatcher;

	private volatile boolean running = true;

	@Override
	public void run() {
		log.info("Server running");

		while (running) {
			try {
				selector.select();
			} catch (final IOException e) {
				log.error("An error occurred while selecting keys: ", e);
			}

			final var keys = selector.selectedKeys();
			for (final var key : keys) {
				if (!key.isValid()) continue;

				if (key.isAcceptable()) {
					try {
						accept(key);
					} catch (final IOException e) {
						log.error("An error occurred while accepting a key: ", e);
					}
				}
			}
			keys.clear();
		}
	}

	private void accept(final @NonNull SelectionKey key) throws IOException {
		final var server = (ServerSocketChannel) key.channel();
		final var client = server.accept();

		if (client == null) return;

		client.configureBlocking(false);
		dispatcher.dispatch(client);
	}

	public void stop() {
		running = false;
		try {
			serverSocket.close();
		} catch (final IOException e) {
			log.fatal("Failed to close server socket, forcing shut down", e);
			System.exit(1);
		}
	}

}
