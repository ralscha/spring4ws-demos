package ch.rasc.s4ws.tennis;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class MatchService {

	private Random random;

	private final Map<String, TennisMatch> matches = new ConcurrentHashMap<>();

	private final static Map<String, Map<String, String>> matchClientBets = new ConcurrentHashMap<>();

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	@MessageMapping("/tennis/bet/{clientId}/{matchId}")
	public void bet(String winner, @DestinationVariable("clientId") String clientId,
			@DestinationVariable("matchId") String matchId) {
		Map<String, String> clientBets = matchClientBets.get(matchId);
		clientBets.put(clientId, winner);

		System.out.println(clientId);
		System.out.println(winner);
		System.out.println(matchId);
	}

	@PostConstruct
	public void init() {
		this.random = new Random();
		this.matches.put("1234", new TennisMatch("1234", "US OPEN - QUARTER FINALS",
				new Player("Ferrer D.", "es"), new Player("Almagro N.", "es")));
		this.matches.put("1235", new TennisMatch("1235", "US OPEN - QUARTER FINALS",
				new Player("Djokovic N.", "rs"), new Player("Berdych T.", "cz")));
		this.matches.put("1236", new TennisMatch("1236", "US OPEN - QUARTER FINALS",
				new Player("Murray A.", "gb"), new Player("Chardy J.", "fr")));
		this.matches.put("1237", new TennisMatch("1237", "US OPEN - QUARTER FINALS",
				new Player("Federer R.", "ch"), new Player("Tsonga J.W.", "fr")));

		for (String matchId : this.matches.keySet()) {
			matchClientBets.put(matchId, new ConcurrentHashMap<String, String>());
		}
	}

	@Scheduled(initialDelay = 2000, fixedRate = 3000)
	public void play() {
		for (Map.Entry<String, TennisMatch> match : this.matches.entrySet()) {
			TennisMatch m = match.getValue();
			if (m.isFinished()) {
				m.reset();
			}
			// Handle point
			if (this.random.nextInt(2) == 1) {
				m.playerOneScores();
			}
			else {
				m.playerTwoScores();
			}

			this.messagingTemplate.convertAndSend("/topic/tennis/match/" + m.getKey(), m);

			if (m.isFinished()) {
				Map<String, String> clientBets = matchClientBets.get(m.getKey());
				for (String clientId : clientBets.keySet()) {
					String betWinner = clientBets.get(clientId);
					String result;
					if (betWinner.equals(m.playerWithHighestSets())) {
						result = "OK";
					}
					else {
						result = "NOK";
					}
					this.messagingTemplate.convertAndSend(
							"/queue/tennis/bet/" + clientId + "/" + m.getKey(), result);
				}

			}
		}
	}

	public Map<String, TennisMatch> getMatches() {
		return this.matches;
	}
}
