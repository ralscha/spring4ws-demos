package ch.rasc.s4ws.smoothie;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class CpuDataHandler extends TextWebSocketHandler {

	private final CpuDataService cpuDataService;

	public CpuDataHandler(CpuDataService cpuDataService) {
		this.cpuDataService = cpuDataService;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		this.cpuDataService.addSession(session.getId(), session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
			throws Exception {
		this.cpuDataService.removeSession(session.getId());
	}

}