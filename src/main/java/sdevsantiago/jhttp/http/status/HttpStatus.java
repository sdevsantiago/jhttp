package sdevsantiago.jhttp.http.status;

import lombok.Getter;
import org.apache.logging.log4j.util.Strings;
import sdevsantiago.jhttp.util.StringUtils;

import java.util.Objects;

@Getter
public enum HttpStatus {

	/* 1xx Informational responses */

	/* 2xx Successful responses */
	OK(200, "OK"),

	/* 3xx Redirection messages */
	MOVED_PERMANENTLY(301, "Moved Permanently"),
	PERMANENT_REDIRECT(308, "Permanent Redirect"),

	/* 4xx Client error responses */
	FORBIDDEN(403, "Forbidden", "You don't have permission to access this resource"),
	NOT_FOUND(404, "Not Found", "The resource you requested does not exist or has been moved"),
	METHOD_NOT_ALLOWED(405, "Method Not Allowed", "The HTTP method used is not supported for this endpoint"),
	IM_A_TEAPOT(418, "I'm a teapot", "The server refuses to brew coffee because it is, permanently, a teapot"),

	/* 5xx Server error responses */
	INTERNAL_SERVER_ERROR(500, "Internal Server Error", "The server encountered an unexpected condition that prevented it from fulfilling the request");

	private final int code;
	private final int family;
	private final String reasonPhrase;
	private final String description;

	HttpStatus(final int code, final String reasonPhrase, final String description) {
		this.code = code;
		this.reasonPhrase = reasonPhrase;
		this.family = code / 100;
		this.description = Objects.requireNonNullElse(description, Strings.EMPTY);
	}

	HttpStatus(final int code, final String reasonPhrase) {
		this(code, reasonPhrase, null);
	}

	@Override
	public String toString() {
		return StringUtils.concatSpc(Integer.toString(code), reasonPhrase);
	}

}
