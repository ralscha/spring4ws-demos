package ch.rasc.s4ws.drawboard;

public class RemovePlayerEvent {
	private final String sessionId;

	public RemovePlayerEvent(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return this.sessionId;
	}

}
