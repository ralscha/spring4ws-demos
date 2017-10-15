package ch.rasc.s4ws.drawboard;

import java.nio.ByteBuffer;

public class SendMessageEvent {
	private final String textData;

	private final ByteBuffer binaryData;

	private final String receiver;

	private final String excludeId;

	public SendMessageEvent(String receiver, String excludeId, String textData) {
		this.textData = textData;
		this.binaryData = null;
		this.receiver = receiver;
		this.excludeId = excludeId;
	}

	public SendMessageEvent(String receiver, String excludeId, ByteBuffer binaryData) {
		this.textData = null;
		this.binaryData = binaryData;
		this.receiver = receiver;
		this.excludeId = excludeId;
	}

	public String getTextData() {
		return this.textData;
	}

	public ByteBuffer getBinaryData() {
		return this.binaryData;
	}

	public String getReceiver() {
		return this.receiver;
	}

	public String getExcludeId() {
		return this.excludeId;
	}

}
