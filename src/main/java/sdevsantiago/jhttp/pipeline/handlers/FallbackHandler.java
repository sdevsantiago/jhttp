package sdevsantiago.jhttp.pipeline.handlers;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import sdevsantiago.jhttp.http.response.HttpResponse;
import sdevsantiago.jhttp.http.status.ErrorPageRenderer;
import sdevsantiago.jhttp.pipeline.Context;
import sdevsantiago.jhttp.pipeline.Handler;

import static sdevsantiago.jhttp.http.status.HttpStatus.NOT_FOUND;

@Log4j2
public class FallbackHandler implements Handler {

	@Override
	public void handle(final @NonNull Context context) {
		if (!context.hasResponse()) {
			final var defaultResponse = HttpResponse.notFound();

			defaultResponse
				.body(ErrorPageRenderer.renderErrorPage(NOT_FOUND));

			context.setResponse(defaultResponse.build());
		}
	}
}
