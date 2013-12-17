package ch.rasc.s4ws.brainshop;

import java.io.IOException;
import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

public class BrainService {

	private final static Log logger = LogFactory.getLog(BrainService.class);

	public final static ObjectMapper objectMapper = new ObjectMapper();

	private final Executor taskExecutor;

	public BrainService(ThreadPoolTaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

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
		} else if (bm.getType().equals("command")) {
			if (bm.getCommand().equals("init")) {
				handleInit(session, bm);
			} else if (bm.getCommand().equals("delete")) {
				handleDelete(bm);
			} else if (bm.getCommand().equals("like")) {
				handleLike(bm);
			} else if (bm.getCommand().equals("dislike")) {
				handleDislike(bm);
			} else if (bm.getCommand().equals("delete-board")) {
				handleDeleteBoard(bm);
			}
		}
	}

	private void handleInit(WebSocketSession session, BrainMessage bm) {
		if (StringUtils.hasText(bm.getBoard())) {
			Board board = Board.get(bm.getBoard());
			if (board == null) {
				board = new Board(bm.getBoard());
				Board.broadcastAllBoards();
			}

			board.addUser(session);

			Object msg = ImmutableMap.of(
					"type",
					"init",
					"data",
					new Object[] { ImmutableMap.of("type", "board-list", "boards", Board.all()),
							ImmutableMap.of("type", "ideas", "ideas", board.getAllIdeas()) });
			try {
				TextMessage tm = new TextMessage(BrainService.objectMapper.writeValueAsString(msg));
				session.sendMessage(tm);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				e.printStackTrace();
				board.removeUser(session);
			}
		}
	}

	private void handleDelete(BrainMessage bm) {
		Board board = Board.get(bm.getBoard());
		board.sendToAllUsers(ImmutableMap.of("type", "command", "command", "delete", "board", bm.getBoard(), "id",
				bm.getId()));
		board.removeIdea(bm.getId());

	}

	private void handleLike(BrainMessage bm) {
		Board board = Board.get(bm.getBoard());
		Idea idea = board.getIdea(bm.getId());
		idea.like(bm.getUser());
		idea.setLast(true);
		board.sendToAllUsers(idea);
	}

	private void handleDislike(BrainMessage bm) {
		Board board = Board.get(bm.getBoard());
		Idea idea = board.getIdea(bm.getId());
		idea.dislike(bm.getUser());
		idea.setLast(true);
		board.sendToAllUsers(idea);
	}

	private void handleDeleteBoard(BrainMessage bm) {
		Board.remove(bm.getName());
		Board.broadcastAllBoards();
	}

	private static void handleIncomingIdea(BrainMessage bm) {
		Board board = Board.get(bm.getBoard());

		boolean isNew = false;
		Idea idea;
		if (bm.getId() != null) {
			idea = board.getIdea(bm.getId());
		} else {
			isNew = true;
			idea = Idea.createIdea();
		}

		idea.setDate(DateTime.now().toString("yyyy-MM-dd HH:mm"));
		idea.setGroup(bm.getGroup());
		idea.setText(bm.getText());
		idea.setNext(bm.getNext());

		if (isNew) {
			board.addIdea(idea);
		} else {
			board.moveIdea(idea);
		}

		board.sendToAllUsers(idea);

	}

	private void sendMessage(final WebSocketSession session, final TextMessage textMessage) {

		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if (session.isOpen()) {
					try {
						session.sendMessage(textMessage);
					} catch (IOException e) {
						logger.error("sendMessage to session", e);
					}
				}
			}
		}

		);
	}

}
