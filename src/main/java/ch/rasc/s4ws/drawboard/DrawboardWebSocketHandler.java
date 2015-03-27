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
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.spring.context.annotation.Consumer;
import reactor.spring.context.annotation.Selector;

@Consumer
public class DrawboardWebSocketHandler extends AbstractWebSocketHandler {

	private static final Log log = LogFactory.getLog(DrawboardWebSocketHandler.class);

	@Autowired
	public EventBus eventBus;

	private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

	@SuppressWarnings("resource")
	@Selector("sendString")
	public void consumeSendString(Event<String> event) {
		String receiver = event.getHeaders().get("sessionId");
		TextMessage txtMessage = new TextMessage(event.getData());
		if (receiver != null) {
			WebSocketSession session = this.sessions.get(receiver);
			if (session != null) {
				try {
					session.sendMessage(txtMessage);
				}
				catch (IOException e) {
					log.error("sendMessage", e);
				}
			}
		}
		else {
			String excludeId = event.getHeaders().get("excludeId");
			for (WebSocketSession session : this.sessions.values()) {
				if (!session.getId().equals(excludeId)) {
					try {
						session.sendMessage(txtMessage);
					}
					catch (IOException e) {
						log.error("sendMessage", e);
					}
				}
			}
		}
	}

	@SuppressWarnings("resource")
	@Selector("sendBinary")
	public void consumeSendBinary(Event<ByteBuffer> event) {

		String receiver = event.getHeaders().get("sessionId");
		BinaryMessage binMsg = new BinaryMessage(event.getData());
		if (receiver != null) {
			WebSocketSession session = this.sessions.get(receiver);
			if (session != null) {
				try {
					session.sendMessage(binMsg);
				}
				catch (IOException e) {
					log.error("sendMessage", e);
				}
			}
		}
		else {
			String excludeId = event.getHeaders().get("excludeId");
			for (WebSocketSession session : this.sessions.values()) {
				if (!session.getId().equals(excludeId)) {
					try {
						session.sendMessage(binMsg);
					}
					catch (IOException e) {
						log.error("sendMessage", e);
					}
				}
			}
		}

	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		this.sessions.put(session.getId(), session);
		this.eventBus.notify("newPlayer", Event.wrap(session.getId()));
	}

	@Override
	public void handleTextMessage(WebSocketSession session, final TextMessage message)
			throws Exception {

		Event<String> event = Event.wrap(message.getPayload());
		event.getHeaders().set("sessionId", session.getId());
		this.eventBus.notify("incomingMessage", event);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
			throws Exception {
		this.sessions.remove(session.getId());
		this.eventBus.notify("removePlayer", Event.wrap(session.getId()));
	}

}
