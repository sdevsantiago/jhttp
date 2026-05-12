package sdevsantiago.jhttp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import sdevsantiago.jhttp.core.Acceptor;
import sdevsantiago.jhttp.core.Dispatcher;
import sdevsantiago.jhttp.core.Worker;
import sdevsantiago.jhttp.core.WorkerPool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

@Log4j2
@RequiredArgsConstructor
public class ServerBootstrap {

	private final ServerConfig config;

	private ServerSocketChannel serverSocket;

	public void start() throws Exception {
		serverSocket = ServerSocketChannel.open();

		final var address = new InetSocketAddress(config.getPort());
		serverSocket.bind(address);
		log.info("Server socket bound to port '{}'", serverSocket.socket().getLocalPort());
		serverSocket.configureBlocking(false);

		final var selector = Selector.open();
		serverSocket.register(selector, SelectionKey.OP_ACCEPT);

		final var workerPool = new WorkerPool(config.getWorkerPoolSize());
		final var dispatcher = new Dispatcher(workerPool);

		final var acceptor = new Acceptor(serverSocket, selector, dispatcher);
		final var acceptorThread = new Thread(acceptor, "acceptor");
		acceptorThread.setDaemon(false);

		workerPool.start();
		acceptorThread.start();
	}

	public void stop() throws IOException {
		serverSocket.close();
	}

}
