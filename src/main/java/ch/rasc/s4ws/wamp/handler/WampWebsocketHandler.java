package ch.rasc.s4ws.wamp.handler;

import static reactor.event.selector.Selectors.$;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;
import ch.rasc.s4ws.wamp.message.BaseMessage;
import ch.rasc.s4ws.wamp.message.OutboundMessage;
import ch.rasc.s4ws.wamp.message.WampMessageType;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WampWebsocketHandler implements WebSocketHandler {

	private final static Log logger = LogFactory.getLog(WampWebsocketHandler.class);

	private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

	private final static ObjectMapper objectMapper = new ObjectMapper();

	private final Reactor reactor;

	public WampWebsocketHandler(Reactor reactor) {
		this.reactor = reactor;

		this.reactor.on($(WampMessageType.INTERNAL_OUTBOUND), new Consumer<Event<OutboundMessage>>() {
			@Override
			public void accept(Event<OutboundMessage> event) {
				sendMessageToClient(event.getData());
			}
		});
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.put(session.getId(), session);
		session.sendMessage(new TextMessage("[0 , \"" + session.getId() + "\" , 1, \"MyServer/0.1\" ]"));
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> webSocketMessage) throws Exception {
		Assert.isInstanceOf(TextMessage.class, webSocketMessage);
		BaseMessage message = BaseMessage.createMessage(objectMapper, session.getId(), (TextMessage) webSocketMessage);
		if (message != null) {
			reactor.notify(message.getType(), Event.wrap(message));
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		String sessionId = session.getId();
		sessions.remove(sessionId);
		BaseMessage.unregisterPrefix(sessionId);
		reactor.notify(WampMessageType.INTERNAL_DISCONNECT, Event.wrap(sessionId));
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

	public void sendMessageToClient(OutboundMessage message) {

		String sessionId = message.getSessionId();

		if (sessionId == null) {
			logger.error("sessionId not found in message " + message);
			return;
		}

		WebSocketSession session = sessions.get(sessionId);
		if (session == null) {
			logger.error("Session not found for session with id " + sessionId);
			return;
		}

		try {
			session.sendMessage(message.toTextMessage(objectMapper));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
