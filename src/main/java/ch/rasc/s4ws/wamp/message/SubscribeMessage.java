package ch.rasc.s4ws.wamp.message;

import java.util.List;

public class SubscribeMessage extends BaseMessage {

	private final String topicURI;

	public SubscribeMessage(String sessionId, List<Object> wampMessage) {
		super(WampMessageType.SUBSCRIBE, sessionId);
		this.topicURI = replacePrefix((String) wampMessage.get(1));
	}

	public String getTopicURI() {
		return topicURI;
	}

}
