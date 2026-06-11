package sdevsantiago.jhttp.pipeline;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import sdevsantiago.jhttp.http.request.HttpRequest;
import sdevsantiago.jhttp.http.response.HttpResponse;

import java.net.Socket;

@Getter
@Setter
@RequiredArgsConstructor
public class Context {

	private final @NonNull Socket socket;

	private HttpRequest request;
	private HttpResponse response;

	public boolean hasResponse() {
		return response != null;
	}

}
