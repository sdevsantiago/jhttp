package sdevsantiago.jhttp.pipeline;

import lombok.NonNull;

public interface Handler {

	void handle(final @NonNull Context context);

}
