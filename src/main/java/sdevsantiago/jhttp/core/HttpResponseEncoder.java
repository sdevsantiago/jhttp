package sdevsantiago.jhttp.core;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import sdevsantiago.jhttp.http.response.HttpResponse;
import sdevsantiago.jhttp.pipeline.Context;
import sdevsantiago.jhttp.pipeline.Handler;
import sdevsantiago.jhttp.util.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

@Log4j2
@NoArgsConstructor
public class HttpResponseEncoder {

	private static final byte[] LINE_SEPARATOR = "\r\n".getBytes(Charset.defaultCharset());

	public static void sendResponse(final @NonNull OutputStream outputStream,
									HttpResponse response) throws IOException {
		if (response == null) {
			response = HttpResponse.internalServerError().build();
		}

		outputStream.write(response.statusLine().getBytes());
		outputStream.write(LINE_SEPARATOR);

		for (final var header : response.headers().getHeaders().entrySet()) {
			final var headerLine = StringUtils.concatWith(
				": ",
				header.getKey(),
				String.join("", header.getValue())
			);
			outputStream.write(headerLine.getBytes());

			outputStream.write(LINE_SEPARATOR);
		}

		outputStream.write(LINE_SEPARATOR);

		outputStream.write(response.body());
	}

}
