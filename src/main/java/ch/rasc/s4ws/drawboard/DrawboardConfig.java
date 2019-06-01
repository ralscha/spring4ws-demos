package ch.rasc.s4ws.drawboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
public class DrawboardConfig implements WebSocketConfigurer {

	@Autowired
	private ApplicationEventPublisher publisher;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(drawboardWebSocketHandler(), "/drawboardws").setAllowedOrigins("*");
	}

	@Bean
	public WebSocketHandler drawboardWebSocketHandler() {
		return new DrawboardWebSocketHandler(this.publisher);
	}

	@Bean
	public Room room() {
		return new Room(this.publisher);
	}

}
