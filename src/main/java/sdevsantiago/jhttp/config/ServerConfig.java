package sdevsantiago.jhttp.config;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class ServerConfig {

	@Builder.Default
	int port = 8080;

	@Builder.Default
	int workerPoolSize = 4;

}
