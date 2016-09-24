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

	public BPInfo(int playersInLobby, int playersInGame, int redCrystals, int blueCrystals, String bestPlayerInDM,
			String CWGame, int playersInCTF, int playersInDM) {
		this.pil = playersInLobby;
		this.pig = playersInGame;
		this.rc = redCrystals;
		this.bc = blueCrystals;
		this.bestPlayerDm = bestPlayerInDM;
		this.Cwg = CWGame;
		this.pictf = playersInCTF;
		this.pidm = playersInDM;
	}

	public int getPlayerIsLobby() {
		return this.pil;
	}

	public int getPlayerInGame() {
		return this.pig;
	}

	public int getRedCrystals() {
		return this.rc;
	}

	public int getBlueCrystals() {
		return this.bc;
	}

	public String getBestPlayerInDM() {
		return this.bestPlayerDm;
	}

	public String getCWGame() {
		return this.Cwg;
	}

	public int getPlayersInCTF() {
		return this.pictf;
	}

	public int getPlayersInDM() {
		return this.pidm;
	}
}
