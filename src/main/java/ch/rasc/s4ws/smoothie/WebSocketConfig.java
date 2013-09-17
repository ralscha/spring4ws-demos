package ch.rasc.s4ws.smoothie;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.config.EnableWebSocket;
import org.springframework.web.socket.server.config.WebSocketConfigurer;
import org.springframework.web.socket.server.config.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@EnableScheduling
@ComponentScan(basePackages = "ch.rasc.s4ws.smoothie")
public class WebSocketConfig implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(cpuDataHandler(), "/cpuData").withSockJS();
	}

	@Bean
	public WebSocketHandler cpuDataHandler() {
		return new CpuDataHandler(cpuDataService());
	}

	@Bean
	public CpuDataService cpuDataService() {
		return new CpuDataService(taskExecutor());
	}

	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setMaxPoolSize(200);
		executor.setThreadNamePrefix("CpuDataHandler-");
		return executor;
	}
}
