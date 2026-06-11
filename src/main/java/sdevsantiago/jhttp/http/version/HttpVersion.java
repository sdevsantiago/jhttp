package sdevsantiago.jhttp.http.version;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HttpVersion {

	HTTP_1_1("HTTP/1.1");

	private final String value;

	public static HttpVersion of(final @NonNull String version) {
		return switch (version) {
			case "HTTP/1.1" -> HTTP_1_1;
			default -> throw new IllegalArgumentException("Unknown version: " + version);
		};
	}

}
