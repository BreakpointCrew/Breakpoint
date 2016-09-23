package cz.GravelCZLP.BreakpointInfo;

public class BPInfo {

	private int pil;
	private int pig;
	private int rc;
	private int bc;
	private String bestPlayerDm;
	private String Cwg;
	private int pictf;
	private int pidm;
	
	public BPInfo(int playersInLobby, int playersInGame, int redCrystals, int blueCrystals, 
			String bestPlayerInDM, String CWGame, int playersInCTF, int playersInDM) {
		pil = playersInLobby;
		pig = playersInGame;
		rc = redCrystals;
		bc = blueCrystals;
		bestPlayerDm = bestPlayerInDM;
		Cwg = CWGame;
		pictf = playersInCTF;
		pidm = playersInDM;
	}
	
	public int getPlayerIsLobby() {
		return pil;
	}
	public int getPlayerInGame() {
		return pig;
	}
	public int getRedCrystals() {
		return rc;
	}
	public int getBlueCrystals() {
		return bc;
	}
	public String getBestPlayerInDM() {
		return bestPlayerDm;
	}
	public String getCWGame() {
		return Cwg;
	}
	public int getPlayersInCTF() {
		return pictf;
	}
	public int getPlayersInDM() {
		return pidm;
	}
}
