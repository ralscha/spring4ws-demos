package ch.rasc.s4ws.drawboard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import reactor.Environment;
import reactor.bus.EventBus;
import reactor.spring.context.config.EnableReactor;

@Configuration
@EnableReactor
public class DrawboardConfig implements WebSocketConfigurer {

	static {
		Environment.initializeIfEmpty().assignErrorJournal();
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(drawboardWebSocketHandler(), "/drawboardws");
	}

	@Bean
	public WebSocketHandler drawboardWebSocketHandler() {
		return new DrawboardWebSocketHandler();
	}

	@Bean
	public EventBus eventBus() {
		return EventBus.config().env(Environment.get())
				.dispatcher(Environment.THREAD_POOL).get();
	}

	@Bean
	public Room room() {
		return new Room(eventBus());
	}

}
