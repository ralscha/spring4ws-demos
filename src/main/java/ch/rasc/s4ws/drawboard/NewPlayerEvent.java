package ch.rasc.s4ws.drawboard;

public class NewPlayerEvent {
	private final String sessionId;

	public NewPlayerEvent(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return this.sessionId;
	}

}
