package sdevsantiago.jhttp.core;

import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

@RequiredArgsConstructor
@Log4j2
public class Acceptor implements Runnable {

	private final @NonNull ServerSocket serverSocket;
	private final @NonNull Executor executor;

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				final var socket = serverSocket.accept();

				final var address = socket.getInetAddress();
				log.debug("Accepted connection from {}", address);

				executor.getExecutorService().submit(() -> executor.handleConnection(socket));
			} catch (final IOException e) {
				log.error("Error handling connection", e);
			}
		}
	}

}
