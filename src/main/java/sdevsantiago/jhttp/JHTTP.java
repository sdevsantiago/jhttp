package sdevsantiago.jhttp;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import sdevsantiago.jhttp.config.ConfigLoader;
import sdevsantiago.jhttp.config.ServerBootstrap;
import sdevsantiago.jhttp.util.ObjectUtils;
import sdevsantiago.jhttp.util.PropertiesUtils;
import sdevsantiago.jhttp.util.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.Properties;

@Log4j2
public class JHTTP {

	private static final Properties properties = PropertiesUtils.initProperties("application.properties");

	static void main(String[] args) {
		final var config = !ObjectUtils.isEmpty(args)
			? ConfigLoader.load(args[0])
			: ConfigLoader.load("server.properties");

		try {
			new ServerBootstrap(config).start();
		} catch (Exception exception) {
			log.fatal("A fatal error occurred while executing the server", exception);
		}
	}

	public static String getProperty(final @NonNull String key) {
		if (properties == null) {
			return Strings.EMPTY;
		}
		return properties.getProperty(key, Strings.EMPTY);
	}

	public static String getVersion() {
		return getProperty("application.version");
	}

}
