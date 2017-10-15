package ch.rasc.s4ws.drawboard;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import ch.rasc.s4ws.drawboard.DrawMessage.ParseException;

public final class Room {

	private final BufferedImage roomImage = new BufferedImage(900, 600,
			BufferedImage.TYPE_INT_RGB);

	private final Graphics2D roomGraphics = this.roomImage.createGraphics();

	private final Map<String, Long> playerMap = new ConcurrentHashMap<>();

	private final ApplicationEventPublisher publisher;

	public Room(ApplicationEventPublisher publisher) {
		this.publisher = publisher;

		this.roomGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		this.roomGraphics.setBackground(Color.WHITE);
		this.roomGraphics.clearRect(0, 0, this.roomImage.getWidth(),
				this.roomImage.getHeight());
	}

	@EventListener
	public void newPlayer(NewPlayerEvent newPlayerEvent) {
		this.playerMap.put(newPlayerEvent.getSessionId(), 0L);

		this.publisher.publishEvent(new SendMessageEvent(null,
				newPlayerEvent.getSessionId(), MessageType.PLAYER_CHANGED.flag + "+"));

		this.publisher.publishEvent(new SendMessageEvent(newPlayerEvent.getSessionId(),
				null,
				MessageType.IMAGE_MESSAGE.flag + String.valueOf(this.playerMap.size())));

		// Store image as PNG
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			ImageIO.write(this.roomImage, "PNG", bout);
		}
		catch (IOException e) { /* Should never happen */
		}

		this.publisher.publishEvent(new SendMessageEvent(newPlayerEvent.getSessionId(),
				null, ByteBuffer.wrap(bout.toByteArray())));
	}

	@EventListener
	public void removePlayer(RemovePlayerEvent removePlayerEvent) {
		this.playerMap.remove(removePlayerEvent.getSessionId());
		this.publisher.publishEvent(
				new SendMessageEvent(null, null, MessageType.PLAYER_CHANGED.flag + "-"));
	}

	@EventListener
	public void handleIncomingData(IncomingMessageEvent incomingMessageEvent) {
		String msg = incomingMessageEvent.getPayload();
		String sessionId = incomingMessageEvent.getSessionId();

		char messageType = msg.charAt(0);
		String messageContent = msg.substring(1);

		if (messageType == '1') {
			try {
				int indexOfChar = messageContent.indexOf('|');
				Long msgId = Long.valueOf(messageContent.substring(0, indexOfChar));
				this.playerMap.put(sessionId, msgId);

				DrawMessage drawMessage = DrawMessage
						.parseFromString(messageContent.substring(indexOfChar + 1));
				drawMessage.draw(this.roomGraphics);
				String drawMessageString = drawMessage.toString();

				for (String playerSessionId : this.playerMap.keySet()) {

					this.publisher.publishEvent(new SendMessageEvent(playerSessionId,
							null, "1" + this.playerMap.get(playerSessionId) + ","
									+ drawMessageString));
				}

			}
			catch (NumberFormatException e) {
				e.printStackTrace();
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}

	}

}
