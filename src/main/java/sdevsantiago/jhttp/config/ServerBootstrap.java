package sdevsantiago.jhttp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import sdevsantiago.jhttp.JHTTP;
import sdevsantiago.jhttp.core.*;
import sdevsantiago.jhttp.module.loader.ModuleRegistry;
import sdevsantiago.jhttp.module.loader.PipelineModuleLoader;
import sdevsantiago.jhttp.pipeline.Pipeline;
import sdevsantiago.jhttp.pipeline.handlers.*;
import sdevsantiago.jhttp.util.StringUtils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.security.KeyStore;

@Log4j2
@RequiredArgsConstructor
public class ServerBootstrap {

	private final ServerConfig config;

	private ServerSocket httpSocket;
	private SSLServerSocket httpsSocket;

	public void start() throws Exception {
		printBanner();

		log.info("Serving static files from {}", config.getRootDirectory().toString());

		httpSocket = createServerSocket();

		if (config.isHttpsEnabled()) {
			httpsSocket = createSSLServerSocket();

			final var httpsExecutor = new Executor(createPipeline());
			final var httpsAcceptor = new Acceptor(httpsSocket, httpsExecutor);
			Thread.ofPlatform()
				.name("acceptor")
				.daemon(false)
				.start(httpsAcceptor);
		}

		final var httpPipeline = config.isHttpsEnabled()
			? createRedirectPipeline()
			: createPipeline();
		final var httpAcceptorName = config.isHttpsEnabled()
			? "redirector"
			: "acceptor";

		final var httpExecutor = new Executor(httpPipeline);
		final var httpAcceptor = new Acceptor(httpSocket, httpExecutor);
		Thread.ofPlatform()
			.name(httpAcceptorName)
			.daemon(false)
			.start(httpAcceptor);

		if (config.isHttpsEnabled()) {
			log.info("HTTPS socket bound to port '{}'",  config.getHttpsPort());
		}

		log.info("HTTP socket bound to port '{}'", config.getHttpPort());

		log.info("Server started");
	}

	public void stop() throws IOException {
		httpSocket.close();
	}

	private SSLServerSocket createSSLServerSocket() throws Exception {
		if (!config.isHttpsEnabled()) return null;

		final var keyStore = KeyStore.getInstance("PKCS12");

		final var certificateFile = config.getHttpsCertificateFile();
		if (!certificateFile.exists()) {
			throw new FileNotFoundException("Certificate %s not found".formatted(certificateFile.getName()));
		}

		try (final var inputStream = new FileInputStream(config.getHttpsCertificateFile())) {
			keyStore.load(inputStream, config.getHttpsCertificatePassword().toCharArray());
		}

		final var keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keyStore, config.getHttpsCertificatePassword().toCharArray());

		final var sslContext = SSLContext.getInstance("TLS");

		sslContext.init(
			keyManagerFactory.getKeyManagers(),
			null,
			null
		);

		return (SSLServerSocket) sslContext
			.getServerSocketFactory()
			.createServerSocket(config.getHttpsPort());
	}

	private ServerSocket createServerSocket() throws IOException {
		final var serverSocket = new ServerSocket();

		serverSocket.bind(new InetSocketAddress(config.getHttpPort()));

		return serverSocket;
	}

	private Pipeline createPipeline() {
		final var pipeline = new Pipeline();

		pipeline.addHandler(new StaticContentHandler(config.getRootDirectory()));

		final var pipelineModuleLoader = new PipelineModuleLoader(new ModuleRegistry(config.getProperties()));
		pipelineModuleLoader.load(config.getProperties()).forEach(pipeline::addHandler);

		pipeline.addHandler(new FallbackHandler());

		return pipeline;
	}

	private Pipeline createRedirectPipeline() {
		final var pipeline = new Pipeline();

		pipeline
			.addHandler(new HttpsRedirectHandler(config.getHttpsPort()));

		return pipeline;
	}

	private void printBanner() {
		System.out.println(StringUtils.concatLn(
			"       ____  __________________",
				"      / / / / /_  __/_  __/ __ \\",
				" __  / / /_/ / / /   / / / /_/ /",
				"/ /_/ / __  / / /   / / / ____/",
				"\\____/_/ /_/ /_/   /_/ /_/",
				"===============================",
				StringUtils.concat("(v. ", JHTTP.getVersion(), ")")
		));
	}

}
