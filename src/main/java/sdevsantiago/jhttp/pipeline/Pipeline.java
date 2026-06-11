package sdevsantiago.jhttp.pipeline;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class Pipeline {

	private final List<Handler> handlers = new ArrayList<>();

	public Pipeline addHandler(final @NonNull Handler handler) {
		handlers.add(handler);
		return this;
	}

	public void execute(final Context context) {
		for (final var handler : handlers) {
			handler.handle(context);

			if (contextIsTerminated(context)) {
				return;
			}
		}
	}

	private boolean contextIsTerminated(final Context context) {
		return context == null || Thread.currentThread().isInterrupted();
	}

}
