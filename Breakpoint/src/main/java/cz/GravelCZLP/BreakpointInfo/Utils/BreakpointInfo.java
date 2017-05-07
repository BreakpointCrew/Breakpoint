/*
* Class By GravelCZLP at 1. 11. 2016
*/

package cz.GravelCZLP.BreakpointInfo.Utils;

import org.bukkit.entity.Player;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.game.Game;
import cz.GravelCZLP.Breakpoint.game.GameType;
import cz.GravelCZLP.Breakpoint.game.ctf.CTFGame;
import cz.GravelCZLP.Breakpoint.game.ctf.Team;
import cz.GravelCZLP.Breakpoint.game.dm.DMGame;
import cz.GravelCZLP.Breakpoint.managers.GameManager;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import cz.GravelCZLP.BreakpointInfo.Main;

public class BreakpointInfo {

	@SuppressWarnings("static-access")
	public static BreakpointInfo getActualInfo() {
		if (Main.getInstance() == null) {
			return null;
		}
		CTFGame ctfGame = null;
		DMGame dmGame = null;
		Breakpoint bp = Main.getInstance().pl;
		
		for (Game g : GameManager.getGames()) {
			if (g.getType() == GameType.CTF) {
				ctfGame = (CTFGame) g;
			} 
			if (g.getType() == GameType.DM) {
				dmGame = (DMGame) g;
			}
		}
		
		int playersInLobby = 0;
		int playersInGame = 0;
		int playersInCTF = 0;
		int playersInDM = 0;
		
		for (Player p : bp.getServer().getOnlinePlayers()) {
			BPPlayer bpPlayer = BPPlayer.get(p);
			if (!bpPlayer.isInGame()) {
				playersInLobby++;
			} else {
				playersInGame++;
				if (bpPlayer.getGame().getType() == GameType.CTF) {
					playersInCTF++;
				} else if (bpPlayer.getGame().getType() == GameType.DM) {
					playersInDM++;
				}
			}
		}
		
		int blueId = Team.getId(Team.BLUE);
		int redId = Team.getId(Team.RED);
		int pointsBlue = ctfGame.getFlagManager().getScore()[blueId];
		int pointsRed = ctfGame.getFlagManager().getScore()[redId];
		
		String CWChallangeGame = bp.getBreakpointConfig().getCWChallengeGame();
		String bestPlayerInDM = dmGame.getCurrentBestPlayer();
		
		
		return new BreakpointInfo(playersInLobby, playersInGame, pointsRed, pointsBlue, bestPlayerInDM, CWChallangeGame, playersInCTF, playersInDM);
	}
	
	//LMFAO the names :D
	private int pil;
	private int pig;
	private int rc;
	private int bc;
	private String bestPlayerDm;
	private String Cwg;
	private int pictf;
	private int pidm;
	
	public BreakpointInfo(int playersInLobby, int playersInGame, int redCrystals, int blueCrystals, String bestPlayerInDM,
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
