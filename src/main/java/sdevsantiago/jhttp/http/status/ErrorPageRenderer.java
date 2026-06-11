package sdevsantiago.jhttp.http.status;

import lombok.NonNull;
import sdevsantiago.jhttp.JHTTP;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ErrorPageRenderer {

	private final static String ERROR_PAGE_PATH = "templates/error.html";

	public static byte[] renderErrorPage(final @NonNull HttpStatus status) {
		final var template = loadTemplate();

		return template
			.replace("${statusCode}", String.valueOf(status.getCode()))
			.replace("${statusMessage}", status.getReasonPhrase())
			.replace("${statusDescription}", status.getDescription())
			.replace("${serverVersion}", JHTTP.getVersion())
			.getBytes();
	}

	private static String loadTemplate() {
		try (final var inputStream = ErrorPageRenderer.class.getClassLoader().getResourceAsStream(ERROR_PAGE_PATH)) {
			if (inputStream == null) {
				throw new IllegalStateException("Template not found: " + ERROR_PAGE_PATH);
			}
			return new String(inputStream.readAllBytes());
		} catch (final IOException ioException)	{
			throw new UncheckedIOException(ioException);
		}
	}

}
