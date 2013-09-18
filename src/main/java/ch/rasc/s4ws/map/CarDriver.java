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

		LatLng latLng = Route.routeBlue.get(blueRoute);
		blueRoute++;
		if (blueRoute >= Route.routeBlue.size()) {
			blueRoute = 0;
		}

		messagingTemplate.convertAndSend("/queue/blueCar", latLng);
	}

	@Scheduled(initialDelay = 2000, fixedDelay = 1200)
	public void driveRedCar() {

		LatLng latLng = Route.routeRed.get(redRoute);
		redRoute++;
		if (redRoute >= Route.routeRed.size()) {
			redRoute = 0;
		}

		messagingTemplate.convertAndSend("/queue/redCar", latLng);
	}

}
