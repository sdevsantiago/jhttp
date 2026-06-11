package sdevsantiago.jhttp.module.loader;

import lombok.extern.log4j.Log4j2;
import org.reflections.Reflections;
import sdevsantiago.jhttp.module.annotations.Module;
import sdevsantiago.jhttp.module.exceptions.*;
import sdevsantiago.jhttp.pipeline.Handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Log4j2
public class ModuleRegistry {

	private static final String BASE_PACKAGE = "sdevsantiago.jhttp.module.added";

	private final Map<String, Class<? extends Handler>> registry = new HashMap<>();

	private final Properties properties;

	public ModuleRegistry(final Properties properties) {
		this.properties = properties;
		scanClasspath();
	}

	public Handler resolve(final String name) {
		final var handlerClass = registry.get(name);

		if (handlerClass == null) {
			throw new ModuleNotFoundException(name, registry.keySet());
		}

		try {
			return handlerClass.getDeclaredConstructor().newInstance();
		} catch (final ReflectiveOperationException e) {
			throw new ModuleInstantiationException(name, handlerClass, e);
		}
	}

	public Set<String> registeredNames() {
		return registry.keySet();
	}

	private void scanClasspath() {
		log.debug("Scanning classpath for @Module under '{}'", BASE_PACKAGE);

		final var reflections = new Reflections(BASE_PACKAGE);
		final var annotated = reflections.getTypesAnnotatedWith(sdevsantiago.jhttp.module.annotations.Module.class);

		for (final var clazz : annotated) {
			if (!Handler.class.isAssignableFrom(clazz)) {
				log.warn("Class '{}' is annotated with @PipelineModule but does not implement Handler — skipped",
					clazz.getName());
				continue;
			}

			final var handlerClass = (Class<? extends Handler>) clazz;
			final var moduleName = clazz.getAnnotation(Module.class).name();

			if (registry.containsKey(moduleName)) {
				log.warn("Duplicate @Module name '{}': '{}' will override '{}'",
					moduleName, clazz.getName(), registry.get(moduleName).getName());
			}

			registry.put(moduleName, handlerClass);
			log.debug("Registered module '{}' → {}", moduleName, clazz.getName());
		}

		log.info("ModuleRegistry ready — {} module(s) discovered: {}", registry.size(), registry.keySet());
	}

}
