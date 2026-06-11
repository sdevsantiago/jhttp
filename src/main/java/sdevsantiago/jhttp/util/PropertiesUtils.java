package sdevsantiago.jhttp.util;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import sdevsantiago.jhttp.JHTTP;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Log4j2
public class PropertiesUtils {

	public static InputStream getPropertiesStream(final @NonNull String propertiesFile) {
		return JHTTP.class.getResourceAsStream("/" + propertiesFile);
	}

	public static Properties initProperties(final @NonNull String propertiesFile) {
		try (final var inputStream = getPropertiesStream(propertiesFile)) {
			if (inputStream == null) return null;
			final var properties = new Properties();
			properties.load(inputStream);
			return properties;
		} catch (final IOException ioException) {
			log.error("Failed to load properties file {}", propertiesFile, ioException);
			return null;
		}
	}

}
