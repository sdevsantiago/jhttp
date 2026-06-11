package sdevsantiago.jhttp.http.response;

import lombok.Builder;
import lombok.NonNull;
import sdevsantiago.jhttp.http.headers.HttpHeaders;
import sdevsantiago.jhttp.http.status.ErrorPageRenderer;
import sdevsantiago.jhttp.http.status.HttpStatus;
import sdevsantiago.jhttp.http.version.HttpVersion;
import sdevsantiago.jhttp.util.StringUtils;

import java.util.*;
import java.util.function.Consumer;

import static sdevsantiago.jhttp.http.status.HttpStatus.*;

public record HttpResponse(

	HttpVersion version,
	HttpStatus status,
	HttpHeaders headers,
	byte[] body

) {

	@Builder
	public HttpResponse(final @NonNull HttpVersion version,
	                    final @NonNull HttpStatus status,
	                    final HttpHeaders headers,
	                    final byte[] body) {
		this.version = version;
		this.status = status;
		this.headers = Objects.requireNonNullElse(headers, new HttpHeaders());
		this.body = Objects.requireNonNullElse(body, new byte[0]);
	}

	public String statusLine() {
		return StringUtils.concatSpc(version.getValue(), status.toString());
	}

	public static HttpResponseBuilder status(final @NonNull HttpStatus status) {
		return builder().version(HttpVersion.HTTP_1_1).status(status);
	}

	public static HttpResponseBuilder ok() {
		return status(OK);
	}

	public static HttpResponseBuilder notFound() {
		return status(NOT_FOUND)
			.body(ErrorPageRenderer.renderErrorPage(NOT_FOUND));
	}

	public static HttpResponseBuilder internalServerError() {
		return status(INTERNAL_SERVER_ERROR)
			.headers(h -> h
				.connection("close"))
			.body(ErrorPageRenderer.renderErrorPage(INTERNAL_SERVER_ERROR));
	}

	public static HttpResponseBuilder permanentRedirect() {
		return status(PERMANENT_REDIRECT);
	}

	public static class HttpResponseBuilder {

		private HttpHeaders headers() {
			if (headers == null) {
				headers = new HttpHeaders();
			}
			return headers;
		}

		public HttpResponseBuilder header(final @NonNull String name, final @NonNull String value) {
			headers().put(name, value);
			return this;
		}

		public HttpResponseBuilder headers(final @NonNull Consumer<HttpHeaders> consumer) {
			consumer.accept(headers());
			return this;
		}

	}

}
