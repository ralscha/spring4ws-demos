package ch.rasc.s4ws.brainshop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
public class BrainConfig implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(brainHandler(), "/brainsockjs").withSockJS();
	}

	@Bean
	public WebSocketHandler brainHandler() {
		return new BrainHandler(brainService());
	}

	@Bean
	public BrainService brainService() {
		return new BrainService();
	}

}
