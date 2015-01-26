package ch.rasc.s4ws.chat;

public class ChatMessage {
	private String username;

	private String message;

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ChatMessage [username=" + this.username + ", message=" + this.message
				+ "]";
	}

}
