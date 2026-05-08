package sdevsantiago.jhttp.http.parser;

import lombok.NonNull;
import sdevsantiago.jhttp.util.StringUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class HttpResponseBuilder {

	private static final String LINE_SEPARATOR = System.lineSeparator();

	/**
	 * Builds a valid HTTP response.
	 * @param status The status code of the response.
	 * @param reason The status reason of the response. Must not be null.
	 * @param body   The body attached to the response.
	 * @throws NullPointerException If the reason is null.
	 */
	public static @NonNull ByteBuffer of(final int status,
	                                     final @NonNull String reason,
	                                     String body) {
		if (!StringUtils.hasLength(body)) {
			body = "";
		}

		final var bodyBytes = body.getBytes(StandardCharsets.UTF_8);

		final var startLine = "HTTP/1.1 " +
							  status + ' ' +
							  reason +
							  LINE_SEPARATOR;
		final var startLineBytes = startLine.getBytes(StandardCharsets.UTF_8);

		final var headers = "Content-Type: text/html; charset=utf-8" + LINE_SEPARATOR +
							"Content-Length: " + bodyBytes.length + LINE_SEPARATOR +
							"Connection: close" + LINE_SEPARATOR +
							LINE_SEPARATOR;
		final var headerBytes = headers.getBytes(StandardCharsets.UTF_8);

		final var response = ByteBuffer.allocate(startLineBytes.length + headerBytes.length + bodyBytes.length);
		response.put(startLineBytes).put(headerBytes).put(bodyBytes);

		response.flip();
		return response;
	}

	/**
	 * Builds a {@code 200 OK} response.
	 * @param body The body to attach to the response.
	 * @return A flipped {@link ByteBuffer} with the contents of the response.
	 * @see #of(int, String, String)
	 */
	public static @NonNull ByteBuffer ok(String body) {
		return of(200, "OK", body);
	}

	/**
	 * Builds a {@code 400 Bad Request} response.
	 * @param body The body to attach to the response.
	 * @return A flipped {@link ByteBuffer} with the contents of the response.
	 * @see #of(int, String, String)
	 */
	public static @NonNull ByteBuffer badRequest(String body) {
		return of(400, "Bad Request", body);
	}

	/**
	 * Builds a {@code 404 Not Found} response.
	 * @param body The body to attach to the response.
	 * @return A flipped {@link ByteBuffer} with the contents of the response.
	 * @see #of(int, String, String)
	 */
	public static @NonNull ByteBuffer notFound(String body) {
		return of(404, "Not Found", body);
	}

	/**
	 * Builds a {@code 500 Internal Server Error} response.
	 * @param body The body to attach to the response.
	 * @return A flipped {@link ByteBuffer} with the contents of the response.
	 * @see #of(int, String, String)
	 */
	public static @NonNull ByteBuffer internalServerError(String body) {
		return of(500, "Internal Server Error", body);
	}

}
