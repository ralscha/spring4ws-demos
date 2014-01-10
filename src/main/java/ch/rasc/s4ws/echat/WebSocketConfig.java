package ch.rasc.s4ws.echat;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;

import ch.rasc.wampspring.config.EnableWamp;
import ch.rasc.wampspring.config.WampConfigurerAdapter;

@Configuration
@EnableWamp
public class WebSocketConfig extends WampConfigurerAdapter {

	@Override
	public void configureWampWebsocketHandler(WebSocketHandlerRegistration reg) {
		reg.withSockJS();
	}

}
