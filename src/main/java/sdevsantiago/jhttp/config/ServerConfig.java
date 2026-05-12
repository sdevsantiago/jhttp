package sdevsantiago.jhttp.config;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class ServerConfig {

	@Builder.Default
	private int port = 8080;

	@Builder.Default
	int workerPoolSize = 4;

}
