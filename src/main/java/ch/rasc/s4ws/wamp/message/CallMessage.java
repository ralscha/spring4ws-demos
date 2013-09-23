package ch.rasc.s4ws.wamp.message;

import java.util.List;

public class CallMessage extends BaseMessage {

	private final String callID;

	private final String procURI;

	private final List<Object> arguments;

	public CallMessage(String sessionId, List<Object> wampMessage) {
		super(WampMessageType.CALL, sessionId);
		this.callID = (String) wampMessage.get(1);
		this.procURI = replacePrefix((String) wampMessage.get(2));
		if (wampMessage.size() > 3) {
			this.arguments = wampMessage.subList(3, wampMessage.size());
		} else {
			this.arguments = null;
		}
	}

	public String getCallID() {
		return callID;
	}

	public String getProcURI() {
		return procURI;
	}

	public List<Object> getArguments() {
		return arguments;
	}

}
