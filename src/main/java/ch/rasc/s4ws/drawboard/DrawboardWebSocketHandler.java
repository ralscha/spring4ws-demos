/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.rasc.s4ws.drawboard;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

public class DrawboardWebSocketHandler extends AbstractWebSocketHandler {

	private static final Log log = LogFactory.getLog(DrawboardWebSocketHandler.class);

	private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

	private final ApplicationEventPublisher publisher;

	public DrawboardWebSocketHandler(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	@EventListener
	public void consumeSendString(SendMessageEvent evt) {
		String receiver = evt.getReceiver();
		WebSocketMessage<?> message = null;

		if (evt.getBinaryData() != null) {
			message = new BinaryMessage(evt.getBinaryData());
		}
		else if (evt.getTextData() != null) {
			message = new TextMessage(evt.getTextData());
		}

		if (message != null) {
			if (receiver != null) {
				WebSocketSession session = this.sessions.get(receiver);
				if (session != null) {
					try {
						session.sendMessage(message);
					}
					catch (IOException e) {
						log.error("sendMessage", e);
					}
				}
			}
			else {
				String excludeId = evt.getExcludeId();
				for (WebSocketSession session : this.sessions.values()) {
					if (!session.getId().equals(excludeId)) {
						try {
							session.sendMessage(message);
						}
						catch (IOException e) {
							log.error("sendMessage", e);
						}
					}
				}
			}
		}
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		this.sessions.put(session.getId(), session);
		this.publisher.publishEvent(new NewPlayerEvent(session.getId()));
	}

	@Override
	public void handleTextMessage(WebSocketSession session, final TextMessage message)
			throws Exception {
		this.publisher.publishEvent(
				new IncomingMessageEvent(session.getId(), message.getPayload()));
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
			throws Exception {
		this.sessions.remove(session.getId());
		this.publisher.publishEvent(new RemovePlayerEvent(session.getId()));
	}

}
