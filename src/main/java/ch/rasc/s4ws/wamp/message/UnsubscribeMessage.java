package ch.rasc.s4ws.wamp.message;

import java.util.List;

public class UnsubscribeMessage extends BaseMessage {

	private final String topicURI;

	public UnsubscribeMessage(String sessionId, List<Object> wampMessage) {
		super(WampMessageType.UNSUBSCRIBE, sessionId);
		this.topicURI = replacePrefix((String) wampMessage.get(1));
	}

	public String getTopicURI() {
		return topicURI;
	}

}
