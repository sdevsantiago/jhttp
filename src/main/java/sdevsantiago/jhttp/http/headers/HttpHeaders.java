package sdevsantiago.jhttp.http.headers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import sdevsantiago.jhttp.JHTTP;
import sdevsantiago.jhttp.pipeline.Context;

import java.util.*;

@Getter
@NoArgsConstructor
public class HttpHeaders {

	private final Map<String, List<String>> headers = new LinkedHashMap<>();

	public HttpHeaders put(final @NonNull String name,
						   final @NonNull String value) {
		headers.computeIfAbsent(name, _ -> new ArrayList<>())
			.add(value);
		return this;
	}

	public List<String> get(final @NonNull String name) {
		return headers.getOrDefault(name, List.of());
	}

	public HttpHeaders defaultHeaders(final @NonNull Context context) {
		final var defaultHeaders = Map.of(
			"Date", new Date().toString(),
			"Server", JHTTP.class.getSimpleName(),
			"X-Client-Ip", context.getSocket().getInetAddress().getHostAddress()
		);

		defaultHeaders.forEach(this::put);

		return this;
	}

	public HttpHeaders connection(final @NonNull String value) {
		headers.put("Connection", List.of(value));
		return this;
	}

	public HttpHeaders contentType(final @NonNull ContentType contentType) {
		headers.put("Content-Type", List.of(contentType.getValue()));
		return this;
	}

	public HttpHeaders contentLength(final int contentLength) {
		headers.put("Content-Length", List.of(String.valueOf(contentLength)));
		return this;
	}

}
