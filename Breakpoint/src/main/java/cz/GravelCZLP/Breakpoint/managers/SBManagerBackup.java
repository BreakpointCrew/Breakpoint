package cz.GravelCZLP.Breakpoint.managers;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.game.Game;
import cz.GravelCZLP.Breakpoint.language.MessageType;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;

@SuppressWarnings("deprecation")
public class SBManagerBackup {
	private final BPPlayer bpPlayer;
	private final Scoreboard sb;
	protected Objective lobbyObj, voteObj, rankObj, progressObj;

	public SBManagerBackup(BPPlayer bpPlayer) {
		this.bpPlayer = bpPlayer;
		this.sb = Bukkit.getScoreboardManager().getNewScoreboard();
		init();
		bpPlayer.getPlayer().setScoreboard(this.sb);
		updateSidebarObjective();
	}

	private void init() {
		initLobbyObj();
		initVoteObj();
		initRankObj();
	}

	private void initLobbyObj() {
		this.lobbyObj = this.sb.registerNewObjective("LOBBY", "dummy");
		this.lobbyObj.setDisplayName(MessageType.SCOREBOARD_LOBBY_HEADER.getTranslation().getValue());
	}

	private void initRankObj() {
		this.rankObj = this.sb.registerNewObjective("RANK", "dummy");
		this.rankObj.setDisplaySlot(DisplaySlot.PLAYER_LIST);
	}

	private void initVoteObj() {
		this.voteObj = this.sb.registerNewObjective("VOTE", "dummy");
		this.voteObj.setDisplayName(MessageType.MAP_VOTING_HEADER.getTranslation().getValue());
	}

	public void initProgressObj() {
		this.progressObj = this.sb.registerNewObjective("PROGRESS", "dummy");
		this.progressObj.setDisplayName("Undefined");
	}

	public void unregister() {
		for (Objective obj : this.sb.getObjectives()) {
			obj.unregister();
		}

		this.lobbyObj = this.rankObj = this.voteObj = this.progressObj = null;

		this.sb.resetScores(getPlayer().getOfflinePlayer());
		;
	}

	public static void updateLobbyObjectives() {
		for (BPPlayer bpPlayer : BPPlayer.onlinePlayers) {
			if (bpPlayer.isInLobby()) {
				bpPlayer.getScoreboardManager().updateLobbyObjective();
			}
		}
	}

	public void updateLobbyObjective() {
		for (Game game : GameManager.getGames()) {
			String name = game.getName();
			int players = game.getPlayers().size();
			OfflinePlayer fakePlayer = Bukkit.getOfflinePlayer(ChatColor.YELLOW + name);
			Score score = this.lobbyObj.getScore(fakePlayer);

			score.setScore(players);
		}
	}

	public void updateOnlinePlayerRanks() {
		try {
			synchronized (BPPlayer.onlinePlayers) {
				for (BPPlayer bpPlayer : BPPlayer.onlinePlayers) {
					updatePlayerRank(bpPlayer);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Breakpoint.warn("Error when updating player ranks in tab: " + e.getClass());
		}
	}

	public void updatePlayerRank(BPPlayer bpPlayer) {
		Player player = bpPlayer.getPlayer();
		String playerName = player.getName();
		int rank = StatisticsManager.isUpdating() ? 0 : StatisticsManager.getRank(playerName);
		String tag = bpPlayer.getTag();

		setNameRank(tag, rank);
	}

	private void setNameRank(String playerName, int rank) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
		Score score = this.rankObj.getScore(player);
		score.setScore(rank);
	}

	public void updateVoteOptions(Map<String, Integer> votes) {
		for (Entry<String, Integer> entry : votes.entrySet()) {
			String name = entry.getKey();
			int voted = entry.getValue();
			if (name != null) {
				Score score = this.voteObj.getScore(Bukkit.getOfflinePlayer(ChatColor.AQUA + name));
				score.setScore(voted);
			}
		}
	}

	public void restartVoteObj() {
		this.voteObj.unregister();
		initVoteObj();
	}

	public void updateSidebarObjective() {
		Game game = this.bpPlayer.getGame();

		if (game != null) {
			if (!game.votingInProgress()) {
				this.progressObj.setDisplaySlot(DisplaySlot.SIDEBAR);
			} else {
				this.voteObj.setDisplaySlot(DisplaySlot.SIDEBAR);
			}
		} else {
			this.lobbyObj.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
	}

	public static String formatTime(int timeLeft) {
		if (timeLeft <= 0) {
			return "0:00";
		}

		int seconds = timeLeft;
		int minutes = 0;

		while (seconds >= 60) {
			seconds -= 60;
			minutes++;
		}

		String sMinutes, sSeconds;

		if (minutes < 10) {
			sMinutes = "0" + minutes;
		} else {
			sMinutes = Integer.toString(minutes);
		}

		if (seconds < 10) {
			sSeconds = "0" + seconds;
		} else {
			sSeconds = Integer.toString(seconds);
		}

		return sMinutes + ":" + sSeconds;
	}

	public Scoreboard getScoreboard() {
		return this.sb;
	}

	public BPPlayer getPlayer() {
		return this.bpPlayer;
	}

	public Objective getProgressObj() {
		return this.progressObj;
	}

	public void setProgressObj(Objective progressObj) {
		this.progressObj = progressObj;
	}
}
