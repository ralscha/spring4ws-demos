package ch.rasc.s4ws.wamp.message;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.socket.TextMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CallErrorMessage extends BaseMessage implements OutboundMessage {

	private final String callID;

	private final String errorURI;

	private final String errorDesc;

	private final String errorDetails;

	public CallErrorMessage(CallMessage callMessage, String errorURI, String errorDesc) {
		this(callMessage, errorURI, errorDesc, null);
	}

	public CallErrorMessage(CallMessage callMessage, String errorURI, String errorDesc, String errorDetails) {
		super(WampMessageType.CALLERROR, callMessage.getSessionId());
		this.callID = callMessage.getCallID();
		this.errorURI = errorURI;
		this.errorDesc = errorDesc;
		this.errorDetails = errorDetails;
	}

	@Override
	public TextMessage toTextMessage(ObjectMapper objectMapper) throws JsonProcessingException {
		List<Object> wampMessage = new ArrayList<>();
		wampMessage.add(getType().getTypeId());
		wampMessage.add(callID);
		wampMessage.add(errorURI);
		wampMessage.add(errorDesc);
		if (errorDetails != null) {
			wampMessage.add(errorDetails);
		}
		return new TextMessage(objectMapper.writeValueAsString(wampMessage));
	}

}
