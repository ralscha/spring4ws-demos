package ch.rasc.s4ws.brainshop;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BrainHandler extends TextWebSocketHandler {

	private final BrainService brainService;

	private final static ObjectMapper objectMapper = new ObjectMapper();

	public BrainHandler(BrainService brainService) {
		this.brainService = brainService;
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
			throws Exception {
		this.brainService.removeSession(session.getId());
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message)
			throws Exception {
		BrainMessage bm = objectMapper.readValue(message.getPayload(),
				BrainMessage.class);
		this.brainService.handleIncomingMessage(session, bm);
	}

}