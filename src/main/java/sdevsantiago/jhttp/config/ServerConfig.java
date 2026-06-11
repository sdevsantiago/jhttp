package sdevsantiago.jhttp.config;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.logging.log4j.util.Strings;
import sdevsantiago.jhttp.JHTTP;
import sdevsantiago.jhttp.util.PropertiesUtils;
import sdevsantiago.jhttp.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Getter
public class ServerConfig {

	private final Path rootDirectory;

	private final int httpPort;

	private int httpsPort;

	private File httpsCertificateFile;

	private String httpsCertificatePassword;

	private final boolean httpsEnabled;

	private final Properties properties;

	private final static int DEFAULT_HTTP_PORT = Integer.parseInt(JHTTP.getProperty("http.port"));
	private final static int DEFAULT_HTTPS_PORT = Integer.parseInt(JHTTP.getProperty("https.port"));
	private final static String DEFAULT_ROOT_DIRECTORY = JHTTP.getProperty("root.directory");


	@Builder
	public ServerConfig(final String configFilePath) throws IllegalArgumentException {
		this.properties = loadProperties(configFilePath);

		var rootDirectory = getProperty("root.directory");
		if (!StringUtils.hasLength(rootDirectory)) {
			rootDirectory = DEFAULT_ROOT_DIRECTORY;
		}

		this.rootDirectory = Paths.get(rootDirectory).toAbsolutePath().normalize();

		if (!Files.exists(this.rootDirectory) || !Files.isReadable(this.rootDirectory) || !Files.isDirectory(this.rootDirectory)) {
			throw new IllegalArgumentException(rootDirectory + " is invalid.");
		}

		final var httpPort = getProperty("http.port");
		this.httpPort = StringUtils.hasLength(httpPort)
			? Integer.parseInt(httpPort)
			: DEFAULT_HTTP_PORT;

		this.httpsEnabled = !Boolean.parseBoolean(getProperty("https.disabled"));

		if (this.httpsEnabled) {
			final var httpsPort = getProperty("https.port");
			this.httpsPort = StringUtils.hasLength(httpsPort)
				? Integer.parseInt(httpsPort)
				: DEFAULT_HTTPS_PORT;

			this.httpsCertificateFile = new File(getProperty("https.certificate.path"));
			if (!StringUtils.hasLength(this.httpsCertificateFile.getName())) {
				throw new IllegalArgumentException("https.certificate.path is invalid.");
			}

			this.httpsCertificatePassword = getProperty("https.certificate.password");
			if (!StringUtils.hasLength(this.httpsCertificatePassword)) {
				throw new IllegalArgumentException("https.certificate.password is invalid.");
			}
		}
	}

	public String getProperty(final @NonNull String key) {
		if (this.properties == null) {
			return Strings.EMPTY;
		}
		return this.properties.getProperty(key, Strings.EMPTY);
	}

	private Properties loadProperties(final String configFilePath) {
		final var file = new File(configFilePath);
		if (file.exists() && file.canRead()) {
			return PropertiesUtils.initProperties(configFilePath);
		}

		try (final var input =
				 ServerConfig.class.getClassLoader()
					 .getResourceAsStream(configFilePath)) {

			if (input == null) {
				throw new IllegalArgumentException(
					"Config not found in filesystem or classpath: " + configFilePath
				);
			}

			final var properties = new Properties();
			properties.load(input);
			return properties;

		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}