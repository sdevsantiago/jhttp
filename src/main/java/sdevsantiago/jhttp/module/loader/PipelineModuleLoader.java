package sdevsantiago.jhttp.module.loader;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import sdevsantiago.jhttp.pipeline.Handler;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Log4j2
@RequiredArgsConstructor
public class PipelineModuleLoader {

	public static final String MODULES_PROPERTY = "modules";

	private final @NonNull ModuleRegistry registry;

	public List<Handler> load(final @NonNull Properties properties) {
		return load(properties, MODULES_PROPERTY);
	}

	private List<Handler> load(final Properties properties, final String propertyKey) {
		final var raw = properties.getProperty(propertyKey);

		final var names = Arrays.stream(raw.split(","))
			.map(String::trim)
			.filter(s -> !s.isEmpty())
			.toList();

		log.info("Loading pipeline from property '{}': {}", propertyKey, names);

		final var handlers = names.stream()
			.map(name -> {
				final var handler = registry.resolve(name);
				log.debug("Resolved module '{}' → {}", name, handler.getClass().getSimpleName());
				return handler;
			})
			.toList();

		log.info("Pipeline built with {} handler(s)", handlers.size());
		return handlers;
	}


}
