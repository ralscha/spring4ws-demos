package ch.rasc.s4ws.wamp.message;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.TextMessage;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class BaseMessage {

	private static final Map<String, Map<String, String>> sessionPrefixMap = new ConcurrentHashMap<>();

	private final WampMessageType type;

	private String sessionId;

	BaseMessage(WampMessageType type) {
		this.type = type;
	}

	BaseMessage(WampMessageType type, String sessionId) {
		this.type = type;
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public WampMessageType getType() {
		return type;
	}

	protected String replacePrefix(String curie) {
		int colonPos = curie.indexOf(':');
		if (colonPos != -1) {
			Map<String, String> prefixMap = sessionPrefixMap.get(sessionId);
			if (prefixMap != null) {
				String procURIPrefix = curie.substring(0, colonPos);
				String uri = prefixMap.get(procURIPrefix);
				if (uri != null) {
					return uri + curie.substring(colonPos + 1);
				}
			}
		}
		return curie;
	}

	public static void unregisterPrefix(String sessionId) {
		sessionPrefixMap.remove(sessionId);
	}

	public static BaseMessage createMessage(ObjectMapper objectMapper, String sessionId, TextMessage textMessage)
			throws JsonParseException, JsonMappingException, IOException {
		List<Object> wampMessage = objectMapper.readValue(textMessage.getPayload(), new TypeReference<List<Object>>() {
			// nothing_here
		});
		int typeID = (int) wampMessage.get(0);
		if (typeID == 1) {

			PrefixMessage prefixMessage = new PrefixMessage(sessionId, wampMessage);
			Map<String, String> mapping = sessionPrefixMap.get(sessionId);
			if (mapping == null) {
				mapping = new HashMap<>();
				sessionPrefixMap.put(sessionId, mapping);
			}
			mapping.put(prefixMessage.getPrefix(), prefixMessage.getURI());
			return null;

		} else if (typeID == 2) {
			return new CallMessage(sessionId, wampMessage);
		} else if (typeID == 5) {
			return new SubscribeMessage(sessionId, wampMessage);
		} else if (typeID == 6) {
			return new UnsubscribeMessage(sessionId, wampMessage);
		} else if (typeID == 7) {
			return new PublishMessage(sessionId, wampMessage);
		}

		return null;

	}

}
