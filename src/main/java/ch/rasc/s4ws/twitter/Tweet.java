package ch.rasc.s4ws.twitter;

import java.io.Serializable;

public class Tweet implements Comparable<Tweet>, Serializable {

	private static final long serialVersionUID = 1L;

	private long id;

	private String profileImageUrl;

	private String fromUser;

	private String text;

	private long createdAt;

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getProfileImageUrl() {
		return this.profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public String getFromUser() {
		return this.fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public int compareTo(Tweet o) {
		return (int) (o.createdAt - this.createdAt);
	}

}
