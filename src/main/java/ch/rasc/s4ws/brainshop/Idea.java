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
		return type;
	}

	public Integer getNext() {
		return next;
	}

	public void setNext(Integer next) {
		this.next = next;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	public Set<String> getLikes() {
		return likes;
	}

	public void setLikes(Set<String> likes) {
		this.likes = likes;
	}

	public Set<String> getDislikes() {
		return dislikes;
	}

	public void setDislikes(Set<String> dislikes) {
		this.dislikes = dislikes;
	}

	public void dislike(String user) {
		likes.remove(user);
		dislikes.add(user);
	}

	public void like(String user) {
		dislikes.remove(user);
		likes.add(user);
	}
}
