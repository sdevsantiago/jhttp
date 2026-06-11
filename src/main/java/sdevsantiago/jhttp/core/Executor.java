package sdevsantiago.jhttp.core;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import sdevsantiago.jhttp.http.response.HttpResponse;
import sdevsantiago.jhttp.pipeline.Context;
import sdevsantiago.jhttp.pipeline.Pipeline;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.function.Predicate;

@Getter
@Log4j2
public class Executor {

	private final ExecutorService executorService;
	private final Pipeline pipeline;

	public Executor(final @NonNull Pipeline pipeline) {
		this.pipeline = pipeline;

		final var virtualThreadFactory = Thread.ofVirtual()
			.name("executor")
			.factory();
		this.executorService = Executors.newThreadPerTaskExecutor(virtualThreadFactory);
	}

	public void handleConnection(final @NonNull Socket socket) {
		try {
			final var request = HttpRequestDecoder.readRequest(socket);

			final var context = new Context(socket);
			context.setRequest(request);

			pipeline.execute(context);

			final var response = context.getResponse();
			response.headers()
				.contentLength(response.body().length);
			HttpResponseEncoder.sendResponse(socket.getOutputStream(), response);

			log.info("{} -> {} [{}]",
				request.getRequestLine(),
				response.status().toString(),
				socket.getInetAddress());
		} catch (final Exception exception) {
			log.error("Unhandled exception", exception);

			try {
				HttpResponseEncoder.sendResponse(
					socket.getOutputStream(),
					HttpResponse.internalServerError().build()
				);
			} catch (final IOException ioException) {
				log.debug("Failed to send error response", ioException);
			}
		} finally {
			closeSocket(socket);
		}
	}

	public void shutdown() {
		log.info("Shutting down executor");
		this.executorService.shutdown();
	}

	private boolean shouldKeepAlive(final Context context) {
		final var request = context.getRequest();
		final var response = context.getResponse();

		final Predicate<String> connectionClose = s -> s.equalsIgnoreCase("close");

		final var clientWantsClose = request.headers()
			.get("Connection")
			.stream()
			.anyMatch(connectionClose);

		final var serverForcesClose = response.headers()
			.get("Connection")
			.stream()
			.anyMatch(connectionClose);

		return !clientWantsClose && !serverForcesClose;
	}

	private void closeSocket(final @NonNull Socket socket) {
		try {
			socket.close();
		} catch (final IOException ignored) {}
	}

}
