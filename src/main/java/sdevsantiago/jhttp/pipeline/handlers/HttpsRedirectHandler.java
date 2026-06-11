package sdevsantiago.jhttp.pipeline.handlers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import sdevsantiago.jhttp.http.response.HttpResponse;
import sdevsantiago.jhttp.pipeline.Context;
import sdevsantiago.jhttp.pipeline.Handler;
import sdevsantiago.jhttp.util.StringUtils;

@RequiredArgsConstructor
public class HttpsRedirectHandler implements Handler {

	private final int httpsPort;

	@Override
	public void handle(final @NonNull Context context) {
		final var request = context.getRequest();

		final var host = request.headers()
			.get("Host")
			.stream()
			.findFirst()
			.orElse("localhost")
			.split(":")[0];

		final var location = StringUtils.concat(
			"https://",
			host,
			":",
			String.valueOf(httpsPort),
			request.uri().toString());

		context.setResponse(
			HttpResponse.permanentRedirect()
				.header("Location", location)
				.build()
		);
	}
}
