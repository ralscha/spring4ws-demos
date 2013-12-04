package ch.rasc.s4ws.scheduler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import ch.rasc.s4ws.wamp.EventMessenger;
import ch.rasc.s4ws.wamp.handler.AnnotationMethodHandler;
import ch.rasc.s4ws.wamp.handler.PubSubHandler;
import ch.rasc.s4ws.wamp.handler.WampWebsocketHandler;

@Configuration
@EnableWebMvc
@EnableWebSocket
@ComponentScan(basePackages = "ch.rasc.s4ws.scheduler")
public class WebConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index.html");
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(wampWebsocketHandler(), "/scheduler").withSockJS();
	}

	@Bean
	public Reactor reactor() {
		Environment env = new Environment();
		Reactor reactor = Reactors.reactor().env(env).dispatcher(Environment.THREAD_POOL).get();
		return reactor;
	}

	@Bean
	public WampWebsocketHandler wampWebsocketHandler() {
		return new WampWebsocketHandler(reactor());
	}

	@Bean
	public EventMessenger eventMessenger() {
		return new EventMessenger(reactor());
	}

	@Bean
	public PubSubHandler pubSubHandler() {
		return new PubSubHandler(reactor());
	}

	@Bean
	public AnnotationMethodHandler annotationMethodHandler() {
		return new AnnotationMethodHandler(reactor());
	}

}