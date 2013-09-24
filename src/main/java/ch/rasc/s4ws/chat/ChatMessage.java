package ch.rasc.s4ws.chat;

public class ChatMessage {
	private String username;

	private String message;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ChatMessage [username=" + username + ", message=" + message + "]";
	}

}
