package cz.GravelCZLP.Breakpoint.statistics;

import java.util.HashMap;

import cz.GravelCZLP.Breakpoint.game.CharacterType;

public class TotalPlayerStatistics extends PlayerStatistics {
	private int playerAmount = 0;

	public TotalPlayerStatistics() {
		super(null, 0, 0, 0, 0, 0, 0, 0, null);

		HashMap<CharacterType, Integer> ctKills = new HashMap<>();

		for (CharacterType ct : CharacterType.values()) {
			ctKills.put(ct, 0);
		}

		setCharacterKills(ctKills);
	}

	public void add(PlayerStatistics stat) {
		setPlayerAmount(getPlayerAmount() + 1);

		this.increaseKills(stat.getKills());
		this.increaseAssists(stat.getAssists());
		this.increaseDeaths(stat.getDeaths());
		this.increaseFlagTakes(stat.getFlagTakes());
		this.increaseFlagCaptures(stat.getFlagCaptures());
		this.increaseBought(stat.getBought());
		this.increaseMoney(stat.getMoney());

		for (CharacterType ct : CharacterType.values()) {
			this.increaseKills(stat.getKills(ct), ct);
		}
	}

	public double getAverageKills() {
		return (double) getKills() / (double) this.playerAmount;
	}

	public double getAverageAssists() {
		return (double) getAssists() / (double) this.playerAmount;
	}

	public double getAverageDeaths() {
		return (double) getDeaths() / (double) this.playerAmount;
	}

	public double getAverageFlagTakes() {
		return (double) getFlagTakes() / (double) this.playerAmount;
	}

	public double getAverageFlagCaptures() {
		return (double) getFlagCaptures() / (double) this.playerAmount;
	}

	public double getAverageBought() {
		return (double) getBought() / (double) this.playerAmount;
	}

	public double getAverageMoney() {
		return (double) getMoney() / (double) this.playerAmount;
	}

	public double getAverageKills(CharacterType ct) {
		return (double) getKills(ct) / (double) this.playerAmount;
	}

	public int getPlayerAmount() {
		return this.playerAmount;
	}

	public void setPlayerAmount(int playerAmount) {
		this.playerAmount = playerAmount;
	}
}
