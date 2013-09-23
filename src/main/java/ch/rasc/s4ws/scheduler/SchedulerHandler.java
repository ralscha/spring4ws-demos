package ch.rasc.s4ws.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.rasc.s4ws.wamp.EventMessenger;
import ch.rasc.s4ws.wamp.annotation.WampPublishListener;
import ch.rasc.s4ws.wamp.message.PublishMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

@Service
public class SchedulerHandler {

	@Autowired
	private EventMessenger eventMessenger;

	private final static ObjectMapper mapper = new ObjectMapper();

	@WampPublishListener("http://demo.rasc.ch/spring4ws/schdemo#clientDoInitialLoad")
	public void clientDoInitialLoad(PublishMessage message) {
		eventMessenger.send("http://demo.rasc.ch/spring4ws/schdemo#serverDoInitialLoad",
				ImmutableMap.of("data", CustomEventDb.list()), null, Collections.singleton(message.getSessionId()));
	}

	@WampPublishListener("http://demo.rasc.ch/spring4ws/schdemo#clientDoUpdate")
	public void clientDoUpdate(PublishMessage message, CustomEvent record) {
		CustomEventDb.update(record);
		eventMessenger.send("http://demo.rasc.ch/spring4ws/schdemo#serverDoUpdate", record,
				Collections.singleton(message.getSessionId()));
	}

	@WampPublishListener("http://demo.rasc.ch/spring4ws/schdemo#clientDoAdd")
	public void clientDoAdd(PublishMessage message, List<Map<String, Object>> records) {
		List<Object> updatedRecords = new ArrayList<>();
		List<ImmutableMap<String, ?>> ids = new ArrayList<>();

		for (Map<String, Object> r : records) {
			Map<String, Object> record = (Map<String, Object>) r.get("data");
			String internalId = (String) r.get("internalId");

			CustomEvent event = mapper.convertValue(record, CustomEvent.class);
			CustomEventDb.create(event);
			updatedRecords.add(event);

			ids.add(ImmutableMap.of("internalId", internalId, "record", event));
		}

		eventMessenger.send("http://demo.rasc.ch/spring4ws/schdemo#serverDoAdd",
				ImmutableMap.of("records", updatedRecords), Collections.singleton(message.getSessionId()));
		eventMessenger.send("http://demo.rasc.ch/spring4ws/schdemo#serverSyncId", ImmutableMap.of("records", ids));
	}

	@WampPublishListener("http://demo.rasc.ch/spring4ws/schdemo#clientDoRemove")
	public void clientDoRemove(PublishMessage message, List<Integer> ids) {
		CustomEventDb.delete(ids);

		eventMessenger.send("http://demo.rasc.ch/spring4ws/schdemo#serverDoRemove", ImmutableMap.of("ids", ids),
				Collections.singleton(message.getSessionId()));
	}

}
