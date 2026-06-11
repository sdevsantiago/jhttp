package sdevsantiago.jhttp.pipeline.handlers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import sdevsantiago.jhttp.http.headers.ContentType;
import sdevsantiago.jhttp.http.response.HttpResponse;
import sdevsantiago.jhttp.pipeline.Context;
import sdevsantiago.jhttp.pipeline.Handler;
import sdevsantiago.jhttp.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Log4j2
@RequiredArgsConstructor
public class StaticContentHandler implements Handler {

	private final @NonNull Path rootDirectory;

	@Override
	public void handle(final @NonNull Context context) {
		if (context.getRequest() == null) {
			return;
		}

		var requestPath = context.getRequest().uri().getPath();
		if (requestPath.endsWith("/")) {
			requestPath = StringUtils.concat(requestPath, "index.html");
		}

		final var requestFile = rootDirectory.resolve(requestPath.substring(1)).normalize();

		if (!Files.exists(requestFile) || Files.isDirectory(requestFile) || !Files.isReadable(requestFile)) {
			return;
		}

		try {
			context.setResponse(
				HttpResponse.ok()
					.headers(h -> h
						.contentType(ContentType.ofPath(requestFile)))
					.body(Files.readAllBytes(requestFile))
					.build()
			);
		} catch (final IOException | OutOfMemoryError exception) {
			log.error("Error while reading request file", exception);
		}
	}
}
