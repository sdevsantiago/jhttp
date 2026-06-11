package sdevsantiago.jhttp.module.exceptions;

import java.util.Set;

public final class ModuleNotFoundException extends RuntimeException {
	public ModuleNotFoundException(final String name, final Set<String> available) {
		super("Pipeline module '%s' not found. Available modules: %s".formatted(name, available));
	}
}
