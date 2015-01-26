package ch.rasc.s4ws.hwsexp;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ch.rasc.wampspring.EventMessenger;

@Service
public class RandomDataService {

	private final static Random random = new Random();

	@Autowired
	private EventMessenger eventMessenger;

	@Scheduled(initialDelay = 2000, fixedRate = 1000)
	public void sendRandomData() {
		int[] randomNumbers = new int[100];
		for (int i = 0; i < randomNumbers.length; i++) {
			randomNumbers[i] = random.nextInt(101);
		}
		this.eventMessenger.sendToAll("data", randomNumbers);
	}

}