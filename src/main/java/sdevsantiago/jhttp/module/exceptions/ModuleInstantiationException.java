package sdevsantiago.jhttp.module.exceptions;

public final class ModuleInstantiationException extends RuntimeException {
	public ModuleInstantiationException(final String name,
	                                    final Class<?> clazz,
	                                    final Throwable cause) {
		super(("Failed to instantiate module '%s' (%s). " +
			"Ensure it has a public no-args constructor.").formatted(name, clazz.getName()), cause);
	}
}