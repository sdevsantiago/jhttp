package sdevsantiago.jhttp.config;

import java.io.FileNotFoundException;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ConfigLoader {

	/**
	 * Loads the server with the default configuration.
	 * @return A {@link ServerConfig} with the default configuration.
	 */
	public static @NonNull ServerConfig load() {
		return ServerConfig.builder().build();
	}

	/**
	 * Loads the server with the specified configuration file.
	 * @param configPath The configuration file path. Must not be null.
	 * @return A {@link ServerConfig} with the default configuration.
	 * @throws NullPointerException If {@code configPath} is null.
	 * @throws FileNotFoundException If the path specified does not exist.
	 */
	public static @NonNull ServerConfig load(final @NonNull String configPath) {
		return ServerConfig.builder().build();
	}

}
