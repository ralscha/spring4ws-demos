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

import org.springframework.beans.factory.annotation.Autowired;

import ch.rasc.s4ws.drawboard.DrawMessage.ParseException;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.spring.context.annotation.Consumer;
import reactor.spring.context.annotation.Selector;

@Consumer
public final class Room {

	private final BufferedImage roomImage = new BufferedImage(900, 600,
			BufferedImage.TYPE_INT_RGB);

	private final Graphics2D roomGraphics = this.roomImage.createGraphics();

	private final Map<String, Long> playerMap = new ConcurrentHashMap<>();

	public final EventBus eventBus;

	@Autowired
	public Room(EventBus eventBus) {
		this.eventBus = eventBus;
		this.roomGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		this.roomGraphics.setBackground(Color.WHITE);
		this.roomGraphics.clearRect(0, 0, this.roomImage.getWidth(),
				this.roomImage.getHeight());
	}

	@Selector("newPlayer")
	public void newPlayer(String sessionId) {
		this.playerMap.put(sessionId, 0L);
		Event<String> event = Event.wrap(MessageType.PLAYER_CHANGED.flag + "+");
		event.getHeaders().set("excludeId", sessionId);
		this.eventBus.notify("sendString", event);

		event = Event.wrap(
				MessageType.IMAGE_MESSAGE.flag + String.valueOf(this.playerMap.size()));
		event.getHeaders().set("sessionId", sessionId);
		this.eventBus.notify("sendString", event);

		// Store image as PNG
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			ImageIO.write(this.roomImage, "PNG", bout);
		}
		catch (IOException e) { /* Should never happen */
		}

		Event<ByteBuffer> byteBufferEvent = Event
				.wrap(ByteBuffer.wrap(bout.toByteArray()));
		byteBufferEvent.getHeaders().set("sessionId", sessionId);
		this.eventBus.notify("sendBinary", byteBufferEvent);
	}

	@Selector("removePlayer")
	public void removePlayer(String sessionId) {
		Event<String> event = Event.wrap(MessageType.PLAYER_CHANGED.flag + "-");
		this.playerMap.remove(sessionId);
		this.eventBus.notify("sendString", event);
	}

	@Selector("incomingMessage")
	public void handleIncomingData(Event<String> inboundEvent) {
		String msg = inboundEvent.getData();
		String sessionId = inboundEvent.getHeaders().get("sessionId");

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
					Event<String> event = Event
							.wrap("1" + this.playerMap.get(playerSessionId) + ","
									+ drawMessageString);
					event.getHeaders().set("sessionId", playerSessionId);
					this.eventBus.notify("sendString", event);
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
