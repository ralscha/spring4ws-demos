package ch.rasc.s4ws.wamp.message;

public enum WampMessageType {

	// Server-to-client Auxiliary
	WELCOME(0),

	// Client-to-server Auxiliary
	PREFIX(1),

	// Client-to-server RPC
	CALL(2),

	// Server-to-client RPC
	CALLRESULT(3),

	// Server-to-client RPC
	CALLERROR(4),

	// Client-to-server PubSub
	SUBSCRIBE(5),

	// Client-to-server PubSub
	UNSUBSCRIBE(6),

	// Client-to-server PubSub
	PUBLISH(7),

	// Server-to-client PubSub
	EVENT(8);

	public final static String INTERNAL_DISCONNECT = "DISCONNECT";

	public final static String INTERNAL_OUTBOUND = "OUTBOUND";

	private Integer typeId;

	private WampMessageType(Integer typeId) {
		this.typeId = typeId;
	}

	public Integer getTypeId() {
		return typeId;
	}

}
