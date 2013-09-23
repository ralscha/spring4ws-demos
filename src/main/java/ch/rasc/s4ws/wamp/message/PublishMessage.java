package ch.rasc.s4ws.wamp.message;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PublishMessage extends BaseMessage {

	private final String topicURI;

	private final Object event;

	private final Set<String> exclude;

	private final Set<String> eligible;

	@SuppressWarnings("unchecked")
	public PublishMessage(String sessionId, List<Object> wampMessage) {
		super(WampMessageType.PUBLISH, sessionId);
		this.topicURI = replacePrefix((String) wampMessage.get(1));
		this.event = wampMessage.get(2);

		if (wampMessage.size() >= 4) {
			Object excludeObj = wampMessage.get(3);
			if (excludeObj instanceof Boolean) {
				if ((Boolean) excludeObj) {
					this.exclude = Collections.singleton(sessionId);
				} else {
					this.exclude = null;
				}
			} else {
				this.exclude = (Set<String>) excludeObj;
			}
		} else {
			this.exclude = null;
		}

		if (wampMessage.size() == 5) {
			this.eligible = (Set<String>) wampMessage.get(4);
		} else {
			this.eligible = null;
		}
	}

	public String getTopicURI() {
		return topicURI;
	}

	public Object getEvent() {
		return event;
	}

	public Set<String> getExclude() {
		return exclude;
	}

	public Set<String> getEligible() {
		return eligible;
	}

}
