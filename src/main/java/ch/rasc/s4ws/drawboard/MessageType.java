package ch.rasc.s4ws.drawboard;

public enum MessageType {

	ERROR('0'), DRAW_MESSAGE('1'), IMAGE_MESSAGE('2'), PLAYER_CHANGED('3');

	final char flag;

	private MessageType(char flag) {
		this.flag = flag;
	}

}