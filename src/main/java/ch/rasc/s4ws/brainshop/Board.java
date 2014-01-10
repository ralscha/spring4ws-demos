package ch.rasc.s4ws.brainshop;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class Board {
	private static final Map<String, Board> boards = Maps.newConcurrentMap();

	public static Collection<String> all() {
		ImmutableSet.Builder<String> builder = ImmutableSet.builder();
		for (Board board : boards.values()) {
			builder.add(board.name);
		}
		return builder.build();
	}

	public static Board get(String name) {
		return boards.get(name);
	}

	public static void remove(String name) {
		boards.remove(name);
	}

	public static void removeUserFromAllBoards(String sessionId) {
		for (Board board : boards.values()) {
			board.removeUser(sessionId);
		}
	}

	public static void broadcastAllBoards() {
		try {
			ImmutableMap<String, Object> response = ImmutableMap.<String, Object> of("type", "board-list", "boards",
					boards.keySet().toArray());
			TextMessage tm = new TextMessage(BrainService.objectMapper.writeValueAsString(response));

			for (Board board : boards.values()) {
				Set<String> usersWithErrors = Sets.newHashSet();
				for (String sessionId : board.users.keySet()) {
					try {
						WebSocketSession ws = board.users.get(sessionId);
						if (ws.isOpen()) {
							ws.sendMessage(tm);
						} else {
							usersWithErrors.add(sessionId);
						}
					} catch (IOException e) {
						e.printStackTrace();
						usersWithErrors.add(sessionId);
					}
				}

				for (String sessionId : usersWithErrors) {
					board.users.remove(sessionId);
				}
			}
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private final String name;

	private final Map<String, Group> groups = Maps.newConcurrentMap();

	private final Map<String, WebSocketSession> users = Maps.newConcurrentMap();

	public Board(String name) {
		this.name = name;
		boards.put(name, this);
	}

	public void addIdea(Idea idea) {
		Group group = groups.get(idea.getGroup());
		if (group == null) {
			group = new Group(idea.getGroup());
			groups.put(idea.getGroup(), group);
		}

		group.addIdea(idea);
	}

	public void removeIdea(Integer id) {
		for (Group group : groups.values()) {
			Idea idea = group.removeIdea(id);
			if (idea != null) {
				return;
			}
		}
	}

	public Idea getIdea(Integer id) {
		for (Group group : groups.values()) {
			Idea idea = group.getIdea(id);
			if (idea != null) {
				return idea;
			}
		}
		return null;
	}

	public Collection<Idea> getAllIdeas() {
		List<Idea> ideas = Lists.newArrayList();
		for (Group group : groups.values()) {
			for (Idea idea : group.getIdeas()) {
				idea.setGroup(group.getGroupId());
			}
			ideas.addAll(group.getIdeas());
		}
		return ideas;
	}

	public void removeUser(String sessionId) {
		users.remove(sessionId);
	}

	public void moveIdea(Idea idea) {

		Idea gIdea = null;
		Group gGroup = null;
		for (Group group : groups.values()) {
			gIdea = group.getIdea(idea.getId());
			if (gIdea != null) {
				gGroup = group;
				break;
			}
		}

		if (gGroup != null) {
			if (gGroup.getGroupId().equals(idea.getGroup())) {
				gGroup.moveIdea(idea);
				return;
			}

			gGroup.removeIdea(idea.getId());
			if (gGroup.isEmpty()) {
				groups.remove(gGroup.getGroupId());
			}
		}

		Group newGroup = groups.get(idea.getGroup());
		if (newGroup == null) {
			newGroup = new Group(idea.getGroup());
			groups.put(idea.getGroup(), newGroup);
		}
		newGroup.addIdea(idea);

	}

	public void sendToAllUsers(Object msg) {
		try {
			TextMessage tm = new TextMessage(BrainService.objectMapper.writeValueAsString(msg));
			Set<String> usersWithErrors = Sets.newHashSet();
			for (String sessionId : users.keySet()) {
				try {
					WebSocketSession ws = users.get(sessionId);
					if (ws.isOpen()) {
						ws.sendMessage(tm);
					} else {
						usersWithErrors.add(sessionId);
					}
				} catch (IOException e) {
					e.printStackTrace();
					usersWithErrors.add(sessionId);
				}
			}

			for (String sessionId : usersWithErrors) {
				users.remove(sessionId);
			}
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

	}

	public void addUser(WebSocketSession session) {
		users.put(session.getId(), session);
	}

	public void removeUser(WebSocketSession session) {
		users.remove(session.getId());
	}

}
