package ch.rasc.s4ws.brainshop;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Idea {
	private static AtomicInteger lastId = new AtomicInteger(0);

	private final String type = "idea";

	private boolean last;

	private Integer next;

	private Integer id;

	private String group;

	private String text;

	private String date;

	private Set<String> likes = new HashSet<>();

	private Set<String> dislikes = new HashSet<>();

	public static Idea createIdea() {
		Idea newIdea = new Idea();
		newIdea.setId(lastId.incrementAndGet());
		return newIdea;
	}

	public String getType() {
		return this.type;
	}

	public Integer getNext() {
		return this.next;
	}

	public void setNext(Integer next) {
		this.next = next;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getGroup() {
		return this.group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean isLast() {
		return this.last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	public Set<String> getLikes() {
		return this.likes;
	}

	public void setLikes(Set<String> likes) {
		this.likes = likes;
	}

	public Set<String> getDislikes() {
		return this.dislikes;
	}

	public void setDislikes(Set<String> dislikes) {
		this.dislikes = dislikes;
	}

	public void dislike(String user) {
		this.likes.remove(user);
		this.dislikes.add(user);
	}

	public void like(String user) {
		this.dislikes.remove(user);
		this.likes.add(user);
	}
}
