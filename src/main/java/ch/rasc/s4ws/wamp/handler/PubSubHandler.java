package ch.rasc.s4ws.wamp.handler;

import static reactor.event.selector.Selectors.$;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;
import ch.rasc.s4ws.wamp.message.EventMessage;
import ch.rasc.s4ws.wamp.message.PublishMessage;
import ch.rasc.s4ws.wamp.message.SubscribeMessage;
import ch.rasc.s4ws.wamp.message.UnsubscribeMessage;
import ch.rasc.s4ws.wamp.message.WampMessageType;

public class PubSubHandler {

	protected final Log logger = LogFactory.getLog(getClass());

	private final Map<String, Set<String>> subscriptionsByTopicURI = new ConcurrentHashMap<>();

	private final Object monitor = new Object();

	private final Reactor reactor;

	public PubSubHandler(Reactor reactor) {
		this.reactor = reactor;

		this.reactor.on($(WampMessageType.SUBSCRIBE), new Consumer<Event<SubscribeMessage>>() {
			@Override
			public void accept(Event<SubscribeMessage> event) {
				registerSubscription(event.getData());
			}
		});
		this.reactor.on($(WampMessageType.UNSUBSCRIBE), new Consumer<Event<UnsubscribeMessage>>() {
			@Override
			public void accept(Event<UnsubscribeMessage> event) {
				unregisterSubscription(event.getData());
			}
		});
		this.reactor.on($(WampMessageType.EVENT), new Consumer<Event<EventMessage>>() {
			@Override
			public void accept(Event<EventMessage> event) {
				sendMessageToSubscribers(event.getData());
			}
		});
		this.reactor.on($(WampMessageType.PUBLISH), new Consumer<Event<PublishMessage>>() {
			@Override
			public void accept(Event<PublishMessage> event) {
				sendMessageToSubscribers(new EventMessage(event.getData()));
			}
		});

		this.reactor.on($(WampMessageType.INTERNAL_DISCONNECT), new Consumer<Event<String>>() {
			@Override
			public void accept(Event<String> event) {
				unregisterAll(event.getData());
			}
		});
	}

	protected void sendMessageToSubscribers(EventMessage eventMessage) {
		Set<String> sessions = findSubscriptions(eventMessage);
		for (String sessionId : sessions) {
			if (eventMessage.isSessionEligible(sessionId)) {
				EventMessage copyEventMessage = new EventMessage(sessionId, eventMessage);
				reactor.notify(WampMessageType.INTERNAL_OUTBOUND, Event.wrap(copyEventMessage));
			}
		}
	}

	public void registerSubscription(SubscribeMessage message) {

		String sessionId = message.getSessionId();
		if (sessionId == null) {
			logger.error("Ignoring subscription. No sessionId in message: " + message);
			return;
		}

		String topicURI = message.getTopicURI();
		if (topicURI == null) {
			logger.error("Ignoring topicURI. No topicURI in message: " + message);
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Adding session id=" + sessionId + ", topicURI=" + message.getTopicURI());
		}

		synchronized (this.monitor) {
			Set<String> sessions = subscriptionsByTopicURI.get(topicURI);
			if (sessions == null) {
				sessions = new CopyOnWriteArraySet<>();
				subscriptionsByTopicURI.put(topicURI, sessions);
			}
			sessions.add(sessionId);
		}

	}

	public void unregisterSubscription(UnsubscribeMessage message) {
		String sessionId = message.getSessionId();
		if (sessionId == null) {
			logger.error("Ignoring subscription. No sessionId in message: " + message);
			return;
		}
		String topicURI = message.getTopicURI();
		if (topicURI == null) {
			logger.error("Ignoring subscription. No topicURI in message: " + message);
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Unubscribe request: " + message);
		}

		synchronized (this.monitor) {
			Set<String> sessions = subscriptionsByTopicURI.get(topicURI);
			if (sessions != null) {
				sessions.remove(sessionId);
			}
		}

	}

	public void unregisterAll(String sessionId) {
		for (Set<String> sessions : subscriptionsByTopicURI.values()) {
			sessions.remove(sessionId);
		}
	}

	public Set<String> findSubscriptions(EventMessage message) {

		String topicURI = message.getTopicURI();
		if (topicURI == null) {
			logger.error("Ignoring destination. No topicURI in message: " + message);
			return null;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Find subscriptions, topicURI=" + topicURI);
		}

		Set<String> sessions = subscriptionsByTopicURI.get(topicURI);
		if (sessions == null) {
			return Collections.emptySet();
		}

		return sessions;

	}

}
