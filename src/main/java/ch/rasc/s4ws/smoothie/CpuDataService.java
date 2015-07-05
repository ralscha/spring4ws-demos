package ch.rasc.s4ws.smoothie;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CpuDataService {

	private final static Log logger = LogFactory.getLog(CpuDataService.class);

	private final static ObjectMapper objectMapper = new ObjectMapper();

	private final Random random = new Random();

	private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

	private final Executor taskExecutor;

	public CpuDataService(ThreadPoolTaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void addSession(String id, WebSocketSession session) {
		this.sessions.put(id, session);
	}

	public void removeSession(String id) {
		this.sessions.remove(id);
	}

	@Scheduled(initialDelay = 1000, fixedDelay = 1000)
	public void sendData() throws JsonProcessingException {
		if (!this.sessions.isEmpty()) {
			final CpuData cpuData = new CpuData();
			cpuData.setHost1(
					new double[] { this.random.nextDouble(), this.random.nextDouble(),
							this.random.nextDouble(), this.random.nextDouble() });
			cpuData.setHost2(
					new double[] { this.random.nextDouble(), this.random.nextDouble(),
							this.random.nextDouble(), this.random.nextDouble() });
			cpuData.setHost3(
					new double[] { this.random.nextDouble(), this.random.nextDouble(),
							this.random.nextDouble(), this.random.nextDouble() });
			cpuData.setHost4(
					new double[] { this.random.nextDouble(), this.random.nextDouble(),
							this.random.nextDouble(), this.random.nextDouble() });

			TextMessage tm = new TextMessage(objectMapper.writeValueAsString(cpuData));
			for (WebSocketSession session : this.sessions.values()) {
				sendMessage(session, tm);
			}
		}
	}

	private void sendMessage(final WebSocketSession session,
			final TextMessage textMessage) {

		this.taskExecutor.execute(() -> {
			if (session.isOpen()) {
				try {
					session.sendMessage(textMessage);
				}
				catch (IOException e) {
					logger.error("sendMessage to session", e);
				}
			}
		}

		);
	}
}
