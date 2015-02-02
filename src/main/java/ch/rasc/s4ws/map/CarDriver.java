package ch.rasc.s4ws.map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CarDriver {

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	private int blueRoute = 0;

	private int redRoute = 0;

	@Scheduled(initialDelay = 1000, fixedDelay = 1000)
	public void driveBlueCar() {

		LatLng latLng = Route.routeBlue.get(this.blueRoute);
		this.blueRoute++;
		if (this.blueRoute >= Route.routeBlue.size()) {
			this.blueRoute = 0;
		}

		this.messagingTemplate.convertAndSend("/topic/map/blueCar", latLng);
	}

	@Scheduled(initialDelay = 2000, fixedDelay = 1200)
	public void driveRedCar() {

		LatLng latLng = Route.routeRed.get(this.redRoute);
		this.redRoute++;
		if (this.redRoute >= Route.routeRed.size()) {
			this.redRoute = 0;
		}

		this.messagingTemplate.convertAndSend("/topic/map/redCar", latLng);
	}

}
