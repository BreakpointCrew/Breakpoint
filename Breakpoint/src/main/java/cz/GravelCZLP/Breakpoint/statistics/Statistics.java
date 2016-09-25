package cz.GravelCZLP.Breakpoint.statistics;

public class Statistics {
	private String name;
	private int kills, assists, deaths, money, bought, flagTakes, flagCaptures;

	public Statistics(String name, int kills, int assists, int deaths, int money, int bought, int flagTakes,
			int flagCaptures) {
		this.name = name;
		this.kills = kills;
		this.assists = assists;
		this.deaths = deaths;
		this.money = money;
		this.bought = bought;
		this.flagTakes = flagTakes;
		this.flagCaptures = flagCaptures;
	}

	public void increaseScore(int kills, int deaths) {
		increaseKills(kills);
		increaseDeaths(deaths);
	}

	public void increaseBought(int by) {
		this.bought += by;
	}

	public void increaseBought() {
		increaseBought(1);
	}

	public void increaseAssists(int by) {
		this.assists += by;
	}

	public void increaseAssists() {
		increaseAssists(1);
	}

	public void increaseKills(int by) {
		this.kills += by;
	}

	public void increaseKills() {
		increaseKills(1);
	}

	public void increaseDeaths(int by) {
		this.deaths += by;
	}

	public void increaseDeaths() {
		increaseDeaths(1);
	}

	public void increaseFlagCaptures(int by) {
		this.flagCaptures += by;
	}

	public void increaseFlagCaptures() {
		increaseFlagCaptures(1);
	}

	public void increaseFlagTakes(int by) {
		this.flagTakes += by;
	}

	public void increaseFlagTakes() {
		increaseFlagTakes(1);
	}

	public void increaseMoney(int by) {
		this.money += by;
	}

	public void increaseMoney() {
		increaseMoney(1);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getKills() {
		return this.kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getDeaths() {
		return this.deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public int getMoney() {
		return this.money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getBought() {
		return this.bought;
	}

	public void setBought(int bought) {
		this.bought = bought;
	}

	public int getFlagTakes() {
		return this.flagTakes;
	}

	public void setFlagTakes(int flagTakes) {
		this.flagTakes = flagTakes;
	}

	public int getFlagCaptures() {
		return this.flagCaptures;
	}

	public void setFlagCaptures(int flagCaptures) {
		this.flagCaptures = flagCaptures;
	}

	public int getAssists() {
		return this.assists;
	}

	public void setAssists(int assists) {
		this.assists = assists;
	}
}
