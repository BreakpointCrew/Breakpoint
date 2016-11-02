/*
* Class By GravelCZLP at 2. 11. 2016
*/

package cz.GravelCZLP.BreakpointInfo.Utils;

import java.util.List;

import cz.GravelCZLP.Breakpoint.achievements.Achievement;
import cz.GravelCZLP.Breakpoint.game.Game;
import cz.GravelCZLP.Breakpoint.game.GameProperties;
import cz.GravelCZLP.Breakpoint.perks.Perk;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import cz.GravelCZLP.Breakpoint.players.ServerPosition;
import cz.GravelCZLP.Breakpoint.players.clans.Clan;
import cz.GravelCZLP.Breakpoint.statistics.PlayerStatistics;

public class BPPlayerInfo {

	public static BPPlayerInfo getInfoAboutPlayer(BPPlayer bpPlayer) {
		
		PlayerStatistics stats = bpPlayer.getStatistics();
		List<Achievement> achivements = bpPlayer.getAchievements();
		List<Perk> perks = bpPlayer.getPerks(); 
		Clan c = bpPlayer.getClan();
		
		Game game = null;
		GameProperties props = null;
		if (bpPlayer.isInGame()) {
			game = bpPlayer.getGame();
			props = bpPlayer.getGameProperties();
		}
		
		ServerPosition pos = bpPlayer.getServerPosition();
		String prefix = bpPlayer.getChatPrefix();
		
		return new BPPlayerInfo(stats, achivements, perks, c, game, props, pos, bpPlayer.isInLobby(), prefix);
	}
	
	private PlayerStatistics stats;
	private List<Achievement> achivements;
	private List<Perk> perks;
	private Clan c;
	private boolean isInLobby;
	
	private Game g;
	private GameProperties gameProps;
	private ServerPosition pos;
	private String prefix;
	
	public BPPlayerInfo(PlayerStatistics stats, List<Achievement> achive, List<Perk> perks, Clan clan, Game g, GameProperties props, ServerPosition pos, boolean isInLobby, String p) {
		this.stats = stats;
		this.achivements = achive;
		this.perks = perks;
		this.c = clan;
		this.g = g;
		this.gameProps = props;
		this.pos = pos;
		this.prefix = p;
		this.isInLobby = isInLobby;
	}
	
	/**
	 * Gets Statistics of Player
	 * 
	 * @return Statistics of Player
	 */
	public PlayerStatistics getStats() {
		return stats;
	}
	
	/**
	 * Gets Achievements of Player
	 * 
	 * @return List of Achievements of Player 
	 */
	public List<Achievement> getAchievements() {
		return achivements;
	}
	
	/**
	 * Gets Perks of Player, Deprecated
	 * 
	 * @return List of Perks of Player
	 */
	@Deprecated
	public List<Perk> getPerks() {
		return perks;
	}
	
	/**
	 * Gets clan of Player
	 * 
	 * @return Player Clan, null if is not in clan
	 */
	public Clan getClan() {
		return c;
	}
	
	/**
	 * Gets if Player in lobby
	 * 
	 * @return if Player is in lobby or not
	 */
	public boolean isInLobby() {
		return isInLobby;
	}
	
	/**
	 * Returns Game that player is in
	 * 
	 * @return Game Object of Player, null if is not in game
	 */
	public Game getGame() {
		return g;
	}

	/**
	 * Returns a properties of player that is in game
	 * 
	 * @return GamePeroperties Object of Player, null if is not in game
	 */
	public GameProperties getGameProperties() {
		return gameProps;
	}
	
	/**
	 * Returns Position of player
	 * 
	 * @return ServerPosition object
	 */
	public ServerPosition getServerPosition() {
		return pos;
	}
	
	/**
	 * Gets Player Prefix
	 * 
	 * @return String of Player Prefix
	 */
	public String getPrefix() {
		return prefix;
	}
}
