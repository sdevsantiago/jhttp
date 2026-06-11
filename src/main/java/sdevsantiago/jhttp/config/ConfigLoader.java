package sdevsantiago.jhttp.config;

import java.io.File;
import java.io.FileNotFoundException;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ConfigLoader {

	/**
	 * Loads the server with the specified configuration file.
	 * @param configPath The configuration file path. Must not be null.
	 * @return A {@link ServerConfig} with the default configuration.
	 * @throws NullPointerException If {@code configPath} is null.
	 * @throws IllegalArgumentException If {@code configPath} is not a file or can't be read.
	 */
	public static @NonNull ServerConfig load(final String configPath) {
		return ServerConfig.builder()
			.configFilePath(configPath)
			.build();
	}

}
