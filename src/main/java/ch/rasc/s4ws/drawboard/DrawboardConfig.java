package ch.rasc.s4ws.drawboard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.spring.context.config.EnableReactor;

@Configuration
@EnableReactor
public class DrawboardConfig implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(drawboardWebSocketHandler(), "/drawboardws");
	}

	@Bean
	public WebSocketHandler drawboardWebSocketHandler() {
		return new DrawboardWebSocketHandler();
	}

	@Bean
	public Room room(Environment env) {
		return new Room(reactor(env));
	}

	@Bean
	public Reactor reactor(Environment env) {
		Reactor reactor = Reactors.reactor().env(env).dispatcher(Environment.THREAD_POOL)
				.get();
		return reactor;
	}

}
