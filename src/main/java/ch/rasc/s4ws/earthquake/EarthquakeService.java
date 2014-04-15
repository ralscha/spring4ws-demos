package ch.rasc.s4ws.earthquake;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class EarthquakeService {

	private final static String pastHour = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";

	private final static Log logger = LogFactory.getLog(EarthquakeService.class);

	private final static ObjectMapper objectMapper = new ObjectMapper();

	private GeoJson lastGeoJson = null;

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	@SubscribeMapping("/topic/quakes.all")
	public GeoJson subscribe() {
		return lastGeoJson;
	}

	@Scheduled(initialDelay = 2000, fixedDelay = 60000)
	public void pollData() {
		try {
			lastGeoJson = objectMapper.readValue(new URL(pastHour), GeoJson.class);
			messagingTemplate.convertAndSend("/topic/quakes.all", lastGeoJson);
		} catch (IOException e) {
			logger.error("poll data", e);
		}
	}

}
