package ch.rasc.s4ws.wamp.message;

import org.springframework.web.socket.TextMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface OutboundMessage {
	TextMessage toTextMessage(ObjectMapper objectMapper) throws JsonProcessingException;

	String getSessionId();
}
