package ch.rasc.s4ws.wamp.message;

import java.util.List;

public class PrefixMessage extends BaseMessage {
	private final String prefix;

	private final String URI;

	public PrefixMessage(String sessionId, List<Object> wampMessage) {
		super(WampMessageType.PREFIX, sessionId);
		this.prefix = (String) wampMessage.get(1);
		this.URI = (String) wampMessage.get(2);
	}

	public String getPrefix() {
		return prefix;
	}

	public String getURI() {
		return URI;
	}

}
