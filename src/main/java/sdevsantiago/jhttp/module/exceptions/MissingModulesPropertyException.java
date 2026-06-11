package sdevsantiago.jhttp.module.exceptions;

public final class MissingModulesPropertyException extends RuntimeException {
	public MissingModulesPropertyException(final String propertyKey) {
		super(("Required property '%s' is missing or empty in server.properties. " +
			"Example: %s=staticContent,fallback").formatted(propertyKey, propertyKey));
	}
}