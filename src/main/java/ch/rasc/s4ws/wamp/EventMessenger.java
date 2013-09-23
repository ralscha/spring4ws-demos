package ch.rasc.s4ws.wamp;

import java.util.Set;

import reactor.core.Reactor;
import reactor.event.Event;
import ch.rasc.s4ws.wamp.message.EventMessage;
import ch.rasc.s4ws.wamp.message.WampMessageType;

public class EventMessenger {

	private final Reactor reactor;

	public EventMessenger(Reactor reactor) {
		this.reactor = reactor;
	}

	public void send(String topicURI, Object event) {
		send(topicURI, event, null, null);
	}

	public void send(String topicURI, Object event, Set<String> exclude) {
		send(topicURI, event, exclude, null);
	}

	public void send(String topicURI, Object event, Set<String> exclude, Set<String> eligible) {
		EventMessage eventMessage = new EventMessage(null, topicURI, event, exclude, eligible);
		reactor.notify(WampMessageType.EVENT, Event.wrap(eventMessage));
	}

}
