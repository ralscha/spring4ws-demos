package ch.rasc.s4ws.wamp.message;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.socket.TextMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CallResultMessage extends BaseMessage implements OutboundMessage {

	private final String callID;

	private final Object result;

	public CallResultMessage(CallMessage callMessage, Object result) {
		super(WampMessageType.CALLRESULT, callMessage.getSessionId());
		this.callID = callMessage.getCallID();
		this.result = result;
	}

	@Override
	public TextMessage toTextMessage(ObjectMapper objectMapper) throws JsonProcessingException {
		List<Object> wampMessage = new ArrayList<>();
		wampMessage.add(getType().getTypeId());
		wampMessage.add(callID);
		wampMessage.add(result);
		return new TextMessage(objectMapper.writeValueAsString(wampMessage));
	}

}
