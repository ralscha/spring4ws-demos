package ch.rasc.s4ws.smoothie;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
public class SmoothieConfig implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(cpuDataHandler(), "/smoothieSockJS").withSockJS();
	}

	@Bean
	public WebSocketHandler cpuDataHandler() {
		return new CpuDataHandler(cpuDataService());
	}

	@Bean
	public CpuDataService cpuDataService() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setMaxPoolSize(10);
		executor.setThreadNamePrefix("SmoothieHandler-");
		executor.afterPropertiesSet();

		return new CpuDataService(executor);
	}

}
