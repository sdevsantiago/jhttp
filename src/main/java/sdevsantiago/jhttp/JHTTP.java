package sdevsantiago.jhttp;

import lombok.extern.log4j.Log4j2;
import sdevsantiago.jhttp.config.ConfigLoader;
import sdevsantiago.jhttp.config.ServerBootstrap;

@Log4j2
public class JHTTP {

	static void main(String[] args) throws Exception {
		final var defaultConfig = ConfigLoader.load();
		new ServerBootstrap(defaultConfig).start();
	}

}
