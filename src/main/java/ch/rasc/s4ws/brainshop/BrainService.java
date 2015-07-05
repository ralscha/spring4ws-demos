package ch.rasc.s4ws.brainshop;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BrainService {

	public final static ObjectMapper objectMapper = new ObjectMapper();

	@SuppressWarnings("unused")
	@PostConstruct
	public void init() {
		new Board("Brainstorm");
	}

	public void removeSession(String id) {
		Board.removeUserFromAllBoards(id);
	}

	public void handleIncomingMessage(WebSocketSession session, BrainMessage bm) {
		if (bm.getType().equals("idea")) {
			handleIncomingIdea(bm);
		}
		else if (bm.getType().equals("command")) {
			if (bm.getCommand().equals("init")) {
				handleInit(session, bm);
			}
			else if (bm.getCommand().equals("delete")) {
				handleDelete(bm);
			}
			else if (bm.getCommand().equals("like")) {
				handleLike(bm);
			}
			else if (bm.getCommand().equals("dislike")) {
				handleDislike(bm);
			}
			else if (bm.getCommand().equals("delete-board")) {
				handleDeleteBoard(bm);
			}
		}
	}

	private static void handleInit(WebSocketSession session, BrainMessage bm) {
		if (StringUtils.hasText(bm.getBoard())) {
			Board board = Board.get(bm.getBoard());
			if (board == null) {
				board = new Board(bm.getBoard());
				Board.broadcastAllBoards();
			}

			board.addUser(session);

			Map<String, Object> msg = new HashMap<>();
			Map<String, Object> data1 = new HashMap<>();
			data1.put("type", "board-list");
			data1.put("boards", Board.all());
			Map<String, Object> data2 = new HashMap<>();
			data2.put("type", "ideas");
			data2.put("ideas", board.getAllIdeas());

			msg.put("type", "init");
			msg.put("data", new Object[] { data1, data2 });
			try {
				TextMessage tm = new TextMessage(
						BrainService.objectMapper.writeValueAsString(msg));
				if (session.isOpen()) {
					session.sendMessage(tm);
				}
				else {
					board.removeUser(session);
				}
			}
			catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
			catch (IOException e) {
				e.printStackTrace();
				board.removeUser(session);
			}
		}
	}

	private static void handleDelete(BrainMessage bm) {
		Board board = Board.get(bm.getBoard());

		Map<String, Object> msg = new HashMap<>();
		msg.put("type", "command");
		msg.put("command", "delete");
		msg.put("board", bm.getBoard());
		msg.put("id", bm.getId());

		board.sendToAllUsers(msg);
		board.removeIdea(bm.getId());

	}

	private static void handleLike(BrainMessage bm) {
		Board board = Board.get(bm.getBoard());
		Idea idea = board.getIdea(bm.getId());
		idea.like(bm.getUser());
		idea.setLast(true);
		board.sendToAllUsers(idea);
	}

	private static void handleDislike(BrainMessage bm) {
		Board board = Board.get(bm.getBoard());
		Idea idea = board.getIdea(bm.getId());
		idea.dislike(bm.getUser());
		idea.setLast(true);
		board.sendToAllUsers(idea);
	}

	private static void handleDeleteBoard(BrainMessage bm) {
		Board.remove(bm.getName());
		Board.broadcastAllBoards();
	}

	private static void handleIncomingIdea(BrainMessage bm) {
		Board board = Board.get(bm.getBoard());

		boolean isNew = false;
		Idea idea;
		if (bm.getId() != null) {
			idea = board.getIdea(bm.getId());
		}
		else {
			isNew = true;
			idea = Idea.createIdea();
		}

		idea.setDate(LocalDateTime.now()
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
		idea.setGroup(bm.getGroup());
		idea.setText(bm.getText());
		idea.setNext(bm.getNext());

		if (isNew) {
			board.addIdea(idea);
		}
		else {
			board.moveIdea(idea);
		}

		board.sendToAllUsers(idea);

	}

}
