/*
* Class By GravelCZLP at 11. 11. 2016
*/

package cz.GravelCZLP.NPCs;

public class UpdateData {

	private String playerName;
	
	private int newKills, newDeaths, newMoney; 
	private double killDeaths;
	
	public String getPlayerName() {
		return playerName;
	}
	
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public int getNewKills() {
		return newKills;
	}
	
	public void setNewKills(int newKills) {
		this.newKills = newKills;
	}
	
	public int getNewDeaths() {
		return newDeaths;
	}
	
	public void setNewDeaths(int newDeaths) {
		this.newDeaths = newDeaths;
	}
	
	public int getNewMoney() {
		return newMoney;
	}
	
	public void setNewMoney(int newMoney) {
		this.newMoney = newMoney;
	}
	
	public double getKillDeaths() {
		return killDeaths;
	}
	
	public void setKillDeaths(double killDeaths) {
		this.killDeaths = killDeaths;
	}
}
