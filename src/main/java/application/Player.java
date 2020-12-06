package application;

import javafx.beans.property.*;

public class Player {
	private SimpleStringProperty playerName;
	private SimpleStringProperty playerDate;
	private SimpleIntegerProperty playerLevel;
	
	public Player(String playerName, String playerDate, int playerLevel) {
		this.playerName = new SimpleStringProperty(playerName);
		this.playerDate = new SimpleStringProperty(playerDate);
		this.playerLevel = new SimpleIntegerProperty(playerLevel);
	}

	public String getPlayerName() {
		return playerName.get();
	}

	public void setPlayerName(String playerName) {
		this.playerName = new SimpleStringProperty(playerName);
	}

	public String getPlayerDate() {
		return playerDate.get();
	}

	public void setPlayerDate(String playerDate) {
		this.playerDate = new SimpleStringProperty(playerDate);
	}

	public int getPlayerLevel() {
		return playerLevel.get();
	}

	public void setPlayerLevel(int playerLevel) {
		this.playerLevel = new SimpleIntegerProperty(playerLevel);
	}
	
	
	
	
}
