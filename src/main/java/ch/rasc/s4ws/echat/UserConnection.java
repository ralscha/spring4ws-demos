package ch.rasc.s4ws.echat;

public class UserConnection {
	private String sessionId;

	private String username;

	private boolean supportsWebRTC;

	private String browser;

	private String image;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public boolean isSupportsWebRTC() {
		return supportsWebRTC;
	}

	public void setSupportsWebRTC(boolean supportsWebRTC) {
		this.supportsWebRTC = supportsWebRTC;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

}
