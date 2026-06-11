package sdevsantiago.jhttp.core;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import sdevsantiago.jhttp.http.headers.HttpHeaders;
import sdevsantiago.jhttp.http.method.HttpMethod;
import sdevsantiago.jhttp.http.request.HttpRequest;
import sdevsantiago.jhttp.http.version.HttpVersion;
import sdevsantiago.jhttp.pipeline.Handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;

@Log4j2
public class HttpRequestDecoder {

	public static HttpRequest readRequest(final @NonNull Socket socket) {
		try {
			final var inputStream = socket.getInputStream();
			final var httpRequest = HttpRequest.builder();

			final var buffer = new ByteArrayOutputStream();

			int byteRead;
			var state = 0;

			while (true) {
				byteRead = inputStream.read();
				if (byteRead == -1) {
					return null;
				}

				buffer.write(byteRead);

				state = switch (state) {
					case 0 -> (byteRead == '\r') ? 1 : 0;
					case 1 -> (byteRead == '\n') ? 2 : 0;
					case 2 -> (byteRead == '\r') ? 3 : 0;
					case 3 -> (byteRead == '\n') ? 4 : 0;
					default -> 0;
				};

				if (state == 4) {
					break;
				}
			}

			final var request = buffer.toString().split("\r\n");

			final var startLine = request[0].split(" ");
			httpRequest.method(HttpMethod.valueOf(startLine[0]));
			httpRequest.uri(URI.create(startLine[1]));
			httpRequest.version(HttpVersion.of(startLine[2]));

			final var headers = new HttpHeaders();
			for (int i = 1; i < request.length; i++) {
				final var header = request[i];
				if (header.isEmpty()) continue;

				final var separatorIndex = header.indexOf(':');
				if (separatorIndex == -1) continue;

				final var name = header.substring(0, separatorIndex).trim();
				final var value = header.substring(separatorIndex + 1).trim();
				headers.put(name, value);
			}
			httpRequest.headers(headers);

			final var contentLengthHeader = headers.get("Content-Length");
			if (!contentLengthHeader.isEmpty()) {
				int contentLength = Integer.parseInt(contentLengthHeader.getFirst());
				httpRequest.body(inputStream.readNBytes(contentLength));
			}

			return httpRequest.build();
		} catch (final IOException ioException) {
			log.error("Failed to parse HTTP request", ioException);
			return null;
		}
	}

}
