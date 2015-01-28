package ch.rasc.s4ws.chat;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

public class SampleDecorator extends WebSocketHandlerDecorator {

	public SampleDecorator(WebSocketHandler delegate) {
		super(delegate);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		
		super.afterConnectionEstablished(session);
		
		System.out.println("connection established: " + session.getId());		
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
			throws Exception {
		
		System.out.println("connection closed: " + session.getId() + " CloseStatus: "
				+ closeStatus);
		
		super.afterConnectionClosed(session, closeStatus);
	}

}
