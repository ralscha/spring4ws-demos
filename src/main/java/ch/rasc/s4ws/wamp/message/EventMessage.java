package ch.rasc.s4ws.wamp.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.web.socket.TextMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventMessage extends BaseMessage implements OutboundMessage {

	private final String topicURI;

	private final Object event;

	private final Set<String> exclude;

	private final Set<String> eligible;

	// copy
	public EventMessage(String sessionId, EventMessage eventMessage) {
		super(WampMessageType.EVENT, sessionId);
		this.topicURI = eventMessage.topicURI;
		this.event = eventMessage.event;
		this.exclude = eventMessage.exclude;
		this.eligible = eventMessage.eligible;
	}

	public EventMessage(String sessionId, String topicURI, Object event, Set<String> exclude, Set<String> eligible) {
		super(WampMessageType.EVENT, sessionId);
		this.topicURI = topicURI;
		this.event = event;
		this.exclude = exclude;
		this.eligible = eligible;
	}

	public EventMessage(String sessionId, String topicURI, Object event) {
		this(sessionId, topicURI, event, null, null);
	}

	public EventMessage(PublishMessage publishMessage) {
		super(WampMessageType.EVENT, null);
		this.topicURI = publishMessage.getTopicURI();
		this.event = publishMessage.getEvent();
		this.exclude = publishMessage.getExclude();
		this.eligible = publishMessage.getEligible();
	}

	public String getTopicURI() {
		return topicURI;
	}

	public boolean isSessionEligible(String sessionId) {
		if (eligible != null) {
			if (eligible.contains(sessionId)) {
				return true;
			}
			return false;
		}

		if (exclude != null) {
			if (exclude.contains(sessionId)) {
				return false;
			}
			return true;
		}

		return true;
	}

	@Override
	public TextMessage toTextMessage(ObjectMapper objectMapper) throws JsonProcessingException {
		List<Object> wampMessage = new ArrayList<>();
		wampMessage.add(getType().getTypeId());
		wampMessage.add(topicURI);
		wampMessage.add(event);
		return new TextMessage(objectMapper.writeValueAsString(wampMessage));
	}

}
