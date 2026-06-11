package sdevsantiago.jhttp.http.request;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import sdevsantiago.jhttp.http.headers.HttpHeaders;
import sdevsantiago.jhttp.http.method.HttpMethod;
import sdevsantiago.jhttp.http.version.HttpVersion;
import sdevsantiago.jhttp.util.StringUtils;

import java.io.*;
import java.net.URI;
import java.util.Objects;

@Log4j2
public record HttpRequest(

	@NonNull HttpMethod method,
	@NonNull URI uri,
	@NonNull HttpVersion version,
	@NonNull HttpHeaders headers,
	@NonNull byte[] body

) {

	@Builder
	public HttpRequest(final @NonNull HttpMethod method,
					   final @NonNull URI uri,
					   final @NonNull HttpVersion version,
					   final HttpHeaders headers,
					   final byte[] body) {
		this.method = method;
		this.uri = uri;
		this.version = version;
		this.headers = Objects.requireNonNullElse(headers, new HttpHeaders());
		this.body = Objects.requireNonNullElse(body, new byte[0]);
	}

	public String getRequestLine() {
		return StringUtils.concatSpc(method.name(), uri.toString());
	}

}
