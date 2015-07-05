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
		return this.player1;
	}

	public Player getPlayer2() {
		return this.player2;
	}

	public TennisMatch(String key, String title, Player player1, Player player2) {
		this.key = key;
		this.title = title;
		this.player1 = player1;
		this.player2 = player2;
		this.serve = player1.getName();
		this.liveComments = "Welcome to this match between " + player1.getName() + " and "
				+ player2.getName() + ".";
	}

	public String getKey() {
		return this.key;
	}

	public synchronized void reset() {
		this.player1.reset();
		this.player2.reset();
		this.isSet1Finished = this.isSet2Finished = this.isSet3Finished = this.isFinished = false;

		this.liveComments = "WELCOME to this match between " + this.player1.getName()
				+ " and " + this.player2.getName() + ".";

	}

	public String getPlayer1Score() {
		if (hasAdvantage() && this.player1Points > this.player2Points) {
			addLiveComments("Advantage " + playerWithHighestScore());
			return "AD";
		}
		if (isDeuce()) {
			addLiveComments("Deuce");
			return "40";
		}
		return translateScore(this.player1Points);
	}

	public String getPlayer2Score() {
		if (hasAdvantage() && this.player2Points > this.player1Points) {
			addLiveComments("Advantage " + playerWithHighestScore());
			return "AD";
		}
		if (isDeuce()) {
			return "40";
		}
		return translateScore(this.player2Points);
	}

	private boolean isDeuce() {
		return this.player1Points >= 3 && this.player2Points == this.player1Points;
	}

	private String playerWithHighestScore() {
		if (this.player1Points > this.player2Points) {
			return this.player1.getName();
		}
		return this.player2.getName();
	}

	private String playerWithHighestGames() {
		if (this.player1.getGamesInCurrentSet() > this.player2.getGamesInCurrentSet()) {
			return this.player1.getName();
		}
		return this.player2.getName();
	}

	public String playerWithHighestSets() {
		if (this.player1.getSets() > this.player2.getSets()) {
			return this.player1.getName();
		}
		return this.player2.getName();
	}

	public boolean hasMatchWinner() {
		if (this.isSet1Finished && this.isSet2Finished && (this.isSet3Finished
				|| this.player1.getSets() != this.player2.getSets())) {
			return true;
		}
		return false;
	}

	public boolean hasGameWinner() {
		boolean hasGameWinner = false;
		if (this.player2Points >= 4 && this.player2Points >= this.player1Points + 2) {
			this.player2.incGamesInCurrentSet();
			hasGameWinner = true;
		}
		if (this.player1Points >= 4 && this.player1Points >= this.player2Points + 2) {
			this.player1.incGamesInCurrentSet();
			hasGameWinner = true;
		}
		if (hasGameWinner) {
			addLiveComments("Game " + playerWithHighestScore());
			this.player2Points = 0;
			this.player1Points = 0;
			if (this.player1.getName().equals(this.serve)) {
				this.serve = this.player2.getName();
			}
			else {
				this.serve = this.player1.getName();
			}
		}
		return hasGameWinner;
	}

	public boolean hasSetWinner() {
		if (this.player1.getGamesInCurrentSet() >= 6
				&& (this.player1
						.getGamesInCurrentSet() >= this.player2.getGamesInCurrentSet() + 2
						|| this.player1.getGamesInCurrentSet()
								+ this.player2.getGamesInCurrentSet() == 13)
				|| this.player2.getGamesInCurrentSet() >= 6 && (this.player2
						.getGamesInCurrentSet() >= this.player1.getGamesInCurrentSet() + 2
						|| this.player1.getGamesInCurrentSet()
								+ this.player2.getGamesInCurrentSet() == 13)) {
			if (!this.isSet1Finished) {
				this.isSet1Finished = true;
				this.player1.setSet1(this.player1.getGamesInCurrentSet());
				this.player2.setSet1(this.player2.getGamesInCurrentSet());
			}
			else if (!this.isSet2Finished) {
				this.isSet2Finished = true;
				this.player1.setSet2(this.player1.getGamesInCurrentSet());
				this.player2.setSet2(this.player2.getGamesInCurrentSet());
			}
			else {
				this.isSet3Finished = true;
				this.player1.setSet3(this.player1.getGamesInCurrentSet());
				this.player2.setSet3(this.player2.getGamesInCurrentSet());
			}

			addLiveComments(playerWithHighestGames() + " wins this set !!");
			if (this.player1.getGamesInCurrentSet() > this.player2
					.getGamesInCurrentSet()) {
				this.player1.incSets();
			}
			else {
				this.player2.incSets();
			}

			this.player1.setGamesInCurrentSet(0);
			this.player2.setGamesInCurrentSet(0);

			// check if match is finished
			if (hasMatchWinner()) {
				this.isFinished = true;
				addLiveComments(playerWithHighestGames() + " WINS the match !!");
			}

			return true;
		}
		return false;
	}

	private boolean hasAdvantage() {
		if (this.player2Points >= 4 && this.player2Points == this.player1Points + 1) {
			return true;
		}
		if (this.player1Points >= 4 && this.player1Points == this.player2Points + 1) {
			return true;
		}

		return false;

	}

	public void playerOneScores() {
		this.liveComments = "";
		this.player1Points++;
		if (hasGameWinner()) {
			hasSetWinner();
		}
	}

	public void playerTwoScores() {
		this.liveComments = "";
		this.player2Points++;
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
		return this.isSet1Finished;
	}

	public boolean isSet2Finished() {
		return this.isSet2Finished;
	}

	public boolean isSet3Finished() {
		return this.isSet3Finished;
	}

	public String getLiveComments() {
		return this.liveComments;
	}

	public void addLiveComments(String comments) {
		Calendar cal = Calendar.getInstance();
		int H = cal.get(Calendar.HOUR);
		int m = cal.get(Calendar.MINUTE);
		int s = cal.get(Calendar.SECOND);
		this.liveComments = "\n" + H + ":" + m + ":" + s + " - " + comments;
	}

	public String getServe() {
		return this.serve;
	}

	public void setServe(String serve) {
		this.serve = serve;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isFinished() {
		return this.isFinished;
	}

	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}
}