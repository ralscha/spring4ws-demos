package ch.rasc.s4ws.drawboard;

public class IncomingMessageEvent {
	private final String payload;

	private final String sessionId;

	public IncomingMessageEvent(String sessionId, String payload) {
		this.payload = payload;
		this.sessionId = sessionId;
	}

	public String getPayload() {
		return this.payload;
	}

	public String getSessionId() {
		return this.sessionId;
	}

}
