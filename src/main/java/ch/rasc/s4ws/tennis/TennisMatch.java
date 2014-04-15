package ch.rasc.s4ws.tennis;

import java.util.Calendar;

public class TennisMatch {

	private final Player player1;

	private final Player player2;

	private int player1Points;

	private int player2Points;

	private final String key;

	private String title;

	private boolean isSet1Finished = false;

	private boolean isSet2Finished = false;

	private boolean isSet3Finished = false;

	private String serve;

	private boolean isFinished = false;

	private String liveComments;

	public Player getPlayer1() {
		return player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public TennisMatch(String key, String title, Player player1, Player player2) {
		this.key = key;
		this.title = title;
		this.player1 = player1;
		this.player2 = player2;
		this.serve = player1.getName();
		liveComments = "Welcome to this match between " + player1.getName() + " and " + player2.getName() + ".";
	}

	public String getKey() {
		return key;
	}

	public synchronized void reset() {
		player1.reset();
		player2.reset();
		isSet1Finished = isSet2Finished = isSet3Finished = isFinished = false;

		liveComments = "WELCOME to this match between " + player1.getName() + " and " + player2.getName() + ".";

	}

	public String getPlayer1Score() {
		if (hasAdvantage() && player1Points > player2Points) {
			addLiveComments("Advantage " + playerWithHighestScore());
			return "AD";
		}
		if (isDeuce()) {
			addLiveComments("Deuce");
			return "40";
		}
		return translateScore(player1Points);
	}

	public String getPlayer2Score() {
		if (hasAdvantage() && player2Points > player1Points) {
			addLiveComments("Advantage " + playerWithHighestScore());
			return "AD";
		}
		if (isDeuce()) {
			return "40";
		}
		return translateScore(player2Points);
	}

	private boolean isDeuce() {
		return player1Points >= 3 && player2Points == player1Points;
	}

	private String playerWithHighestScore() {
		if (player1Points > player2Points) {
			return player1.getName();
		}
		return player2.getName();
	}

	private String playerWithHighestGames() {
		if (player1.getGamesInCurrentSet() > player2.getGamesInCurrentSet()) {
			return player1.getName();
		}
		return player2.getName();
	}

	public String playerWithHighestSets() {
		if (player1.getSets() > player2.getSets()) {
			return player1.getName();
		}
		return player2.getName();
	}

	public boolean hasMatchWinner() {
		if (isSet1Finished && isSet2Finished && (isSet3Finished || player1.getSets() != player2.getSets())) {
			return true;
		}
		return false;
	}

	public boolean hasGameWinner() {
		boolean hasGameWinner = false;
		if (player2Points >= 4 && player2Points >= player1Points + 2) {
			player2.incGamesInCurrentSet();
			hasGameWinner = true;
		}
		if (player1Points >= 4 && player1Points >= player2Points + 2) {
			player1.incGamesInCurrentSet();
			hasGameWinner = true;
		}
		if (hasGameWinner) {
			addLiveComments("Game " + playerWithHighestScore());
			player2Points = 0;
			player1Points = 0;
			if (player1.getName().equals(serve)) {
				serve = player2.getName();
			} else {
				serve = player1.getName();
			}
		}
		return hasGameWinner;
	}

	public boolean hasSetWinner() {
		if (player1.getGamesInCurrentSet() >= 6
				&& (player1.getGamesInCurrentSet() >= player2.getGamesInCurrentSet() + 2 || player1
						.getGamesInCurrentSet() + player2.getGamesInCurrentSet() == 13)
				|| player2.getGamesInCurrentSet() >= 6
				&& (player2.getGamesInCurrentSet() >= player1.getGamesInCurrentSet() + 2 || player1
						.getGamesInCurrentSet() + player2.getGamesInCurrentSet() == 13)) {
			if (!isSet1Finished) {
				isSet1Finished = true;
				player1.setSet1(player1.getGamesInCurrentSet());
				player2.setSet1(player2.getGamesInCurrentSet());
			} else if (!isSet2Finished) {
				isSet2Finished = true;
				player1.setSet2(player1.getGamesInCurrentSet());
				player2.setSet2(player2.getGamesInCurrentSet());
			} else {
				isSet3Finished = true;
				player1.setSet3(player1.getGamesInCurrentSet());
				player2.setSet3(player2.getGamesInCurrentSet());
			}

			addLiveComments(playerWithHighestGames() + " wins this set !!");
			if (player1.getGamesInCurrentSet() > player2.getGamesInCurrentSet()) {
				player1.incSets();
			} else {
				player2.incSets();
			}

			player1.setGamesInCurrentSet(0);
			player2.setGamesInCurrentSet(0);

			// check if match is finished
			if (hasMatchWinner()) {
				isFinished = true;
				addLiveComments(playerWithHighestGames() + " WINS the match !!");
			}

			return true;
		}
		return false;
	}

	private boolean hasAdvantage() {
		if (player2Points >= 4 && player2Points == player1Points + 1) {
			return true;
		}
		if (player1Points >= 4 && player1Points == player2Points + 1) {
			return true;
		}

		return false;

	}

	public void playerOneScores() {
		liveComments = "";
		player1Points++;
		if (hasGameWinner()) {
			hasSetWinner();
		}
	}

	public void playerTwoScores() {
		liveComments = "";
		player2Points++;
		if (hasGameWinner()) {
			hasSetWinner();
		}
	}

	private static String translateScore(int score) {
		switch (score) {
		case 3:
			return "40";
		case 2:
			return "30";
		case 1:
			return "15";
		case 0:
			return "0";
		default:
			return "40";
		}
	}

	public boolean isSet1Finished() {
		return isSet1Finished;
	}

	public boolean isSet2Finished() {
		return isSet2Finished;
	}

	public boolean isSet3Finished() {
		return isSet3Finished;
	}

	public String getLiveComments() {
		return liveComments;
	}

	public void addLiveComments(String comments) {
		Calendar cal = Calendar.getInstance();
		int H = cal.get(Calendar.HOUR);
		int m = cal.get(Calendar.MINUTE);
		int s = cal.get(Calendar.SECOND);
		liveComments = "\n" + H + ":" + m + ":" + s + " - " + comments;
	}

	public String getServe() {
		return serve;
	}

	public void setServe(String serve) {
		this.serve = serve;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}
}