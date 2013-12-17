package ch.rasc.s4ws.brainshop;

import java.util.List;
import java.util.ListIterator;

import com.google.common.collect.Lists;

public class Group {
	private final List<Idea> ideas = Lists.newLinkedList();

	private final String groupId;

	public Group(String groupId) {
		this.groupId = groupId;
	}

	public List<Idea> getIdeas() {
		return ideas;
	}

	public void addIdea(Idea idea) {
		if (idea.getNext() != null && idea.getNext() > 0) {
			ideas.add(indexOf(idea.getNext()), idea);
		} else {
			ideas.add(0, idea);
		}
	}

	private int indexOf(Integer next) {
		int pos = 0;
		for (Idea idea : ideas) {
			if (idea.getId().equals(next)) {
				return pos;
			}
			pos++;
		}
		return -1;
	}

	public Idea getIdea(Integer id) {
		for (Idea idea : ideas) {
			if (idea.getId().equals(id)) {
				return idea;
			}
		}
		return null;
	}

	public Idea removeIdea(Integer id) {
		ListIterator<Idea> li = ideas.listIterator();
		while (li.hasNext()) {
			Idea idea = li.next();
			if (idea.getId().equals(id)) {
				li.remove();
				return idea;
			}
		}
		return null;
	}

	public boolean isEmpty() {
		return ideas.isEmpty();
	}

	public void moveIdea(Idea idea) {
		int currentIndex = indexOf(idea.getId());
		if (currentIndex < 0 || idea.getNext() == null) {
			return;
		}

		ideas.remove(currentIndex);

		if (idea.getNext() < 0) {
			ideas.add(idea);
		} else {
			int nextIdx = indexOf(idea.getNext());
			if (nextIdx < 0) {
				ideas.add(idea);
			} else {
				ideas.add(nextIdx, idea);
			}

		}
	}

	public String getGroupId() {
		return groupId;
	}

}
