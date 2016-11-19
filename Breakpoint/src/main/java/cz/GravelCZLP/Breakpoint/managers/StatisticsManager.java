package cz.GravelCZLP.Breakpoint.managers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.limeth.storageAPI.StorageType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.Configuration;
import cz.GravelCZLP.Breakpoint.achievements.Achievement;
import cz.GravelCZLP.Breakpoint.language.MessageType;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import cz.GravelCZLP.Breakpoint.players.clans.Clan;
import cz.GravelCZLP.Breakpoint.statistics.PlayerStatistics;
import cz.GravelCZLP.Breakpoint.statistics.Statistics;
import cz.GravelCZLP.Breakpoint.statistics.TotalPlayerStatistics;

public class StatisticsManager {
	public static final int MAX_AMOUNT = Integer.MAX_VALUE;
	private static TotalPlayerStatistics totalStats;
	public static List<PlayerStatistics> playersRankedByKills;
	private static List<Clan> clansRankedByPoints;
	private static boolean updating = false;
	private static long lastUpdate;
	
	public static void startLoop() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Breakpoint.getInstance(), new Runnable() {

			@Override
			public void run() {
				asyncUpdate();
			}

		}, 0, 20L * 60 * 10);
	}

	public static void asyncUpdate() {
		if (isUpdating()) {
			return;
		}

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				update();
				lastUpdate = System.currentTimeMillis();
			}
		}, "Async Rank Update");

		t.setPriority(Thread.NORM_PRIORITY);
		t.start();
	}

	private static synchronized void update() {
		updating = true;
		updatePlayerRanksByKills();
		updateClanRanksByPoints();
		updateStatistics();
		updateNPCStatistics();
		updating = false;
		
		long currentTime = System.currentTimeMillis();
		
		Breakpoint.warn("Statistics have been updated! it took: " + (lastUpdate - currentTime) + "ms!");
	}

	public static void updateNPCStatistics() {
		
	}
	
	public static void updateStatistics() {
		TotalPlayerStatistics stats = new TotalPlayerStatistics();

		if (playersRankedByKills == null) {
			return;
		}

		for (PlayerStatistics stat : playersRankedByKills) {
			stats.add(stat);
		}

		totalStats = stats;
	}

	public static void updateClanRanksByPoints() {
		List<Clan> unorderedClans = new ArrayList<>(Clan.clans);
		List<Clan> orderedClans = new ArrayList<>();

		for (Clan clan : unorderedClans) {
			int points = clan.getPoints();
			int i = 0;

			while (i < orderedClans.size()) {
				Clan ordered = orderedClans.get(i);
				int orderedPoints = ordered.getPoints();

				if (points > orderedPoints) {
					break;
				} else if (points == orderedPoints) {
					break;
				}

				i++;
			}

			orderedClans.add(i, clan);
		}

		clansRankedByPoints = orderedClans;
	}

	public static void updatePlayerRanksByKills() {
		Configuration config = Breakpoint.getBreakpointConfig();
		StorageType storageType = config.getStorageType();
		List<String> playerNames = BPPlayer.getPlayerNames(storageType);
		List<PlayerStatistics> unorderedList = new LinkedList<>();
		List<PlayerStatistics> orderedList = new LinkedList<>();

		if (playerNames == null) {
			return;
		}

		for (String playerName : playerNames) {
			if (isPlayer(playerName)) {
				PlayerStatistics stat = PlayerStatistics.loadPlayerStatistics(playerName);
				unorderedList.add(stat);
			}
		}

		while (unorderedList.size() > 0 && orderedList.size() < MAX_AMOUNT) {
			PlayerStatistics topStat = null;

			for (PlayerStatistics stat : unorderedList) {
				int kills = stat.getKills();

				if (topStat == null || kills > topStat.getKills()) {
					topStat = stat;
				}
			}

			if (topStat != null) {
				unorderedList.remove(topStat);
				orderedList.add(topStat);
			} else {
				break;
			}
		}

		playersRankedByKills = orderedList;
	}

	public static Integer getRank(String playerName) {
		if (playersRankedByKills == null) {
			return null;
		}

		for (int i = 0; i < playersRankedByKills.size(); i++) {
			PlayerStatistics stat = playersRankedByKills.get(i);

			if (stat.getName().equals(playerName)) {
				return i + 1;
			}
		}
		return 0;
	}

	public static Integer getRank(Clan clan) {
		if (clansRankedByPoints == null) {
			return null;
		}

		for (int i = 0; i < clansRankedByPoints.size(); i++) {
			Clan curClan = clansRankedByPoints.get(i);

			if (curClan.equals(clan)) {
				return i + 1;
			}
		}
		return 0;
	}

	public static PlayerStatistics getPlayerStatistics(String playerName) {
		if (playersRankedByKills == null) {
			return null;
		}

		for (PlayerStatistics stat : playersRankedByKills) {
			if (stat.getName().equals(playerName)) {
				return stat;
			}
		}

		return null;
	}

	private static boolean isPlayer(String playerName) {
		for (OfflinePlayer op : Bukkit.getOperators()) {
			if (op.getName().equalsIgnoreCase(playerName)) {
				return false;
			}
		}
		return true;
	}

	public static void listTopClans(CommandSender sender, int length, int page) {
		if (clansRankedByPoints == null) {
			sender.sendMessage(MessageType.RANK_TOP_UPDATING.getTranslation().getValue());
		}

		if (length * (page - 1) + 1 > clansRankedByPoints.size()) {
			sender.sendMessage(MessageType.RANK_TOP_CLANEMPTYPAGE.getTranslation().getValue());
			return;
		}
		for (int i = length * (page - 1); i < length * page; i++) {
			if (i < clansRankedByPoints.size()) {
				Clan clan = clansRankedByPoints.get(i);
				String clanName = clan.getColoredName();
				int points = clan.getPoints();
				sender.sendMessage(MessageType.RANK_TOP_CLANFORMAT.getTranslation().getValue(i + 1, clanName, points));
			} else {
				break;
			}
		}
	}

	public static void listTopPlayers(CommandSender sender, int length, int page) {
		if (playersRankedByKills == null) {
			sender.sendMessage(MessageType.RANK_TOP_UPDATING.getTranslation().getValue());
		}

		if (length * (page - 1) + 1 > playersRankedByKills.size()) {
			sender.sendMessage(MessageType.RANK_TOP_EMPTYPAGE.getTranslation().getValue());
			return;
		}
		for (int i = length * (page - 1); i < length * page; i++) {
			if (i < playersRankedByKills.size()) {
				Statistics stat = playersRankedByKills.get(i);
				String playerName = stat.getName();
				int kills = stat.getKills();
				int deaths = stat.getDeaths();
				String kdr = InventoryMenuManager.trimKdr(Double.toString((double) kills / (double) deaths));
				sender.sendMessage(
						MessageType.RANK_TOP_FORMAT.getTranslation().getValue(i + 1, playerName, kills, deaths, kdr));
			} else {
				break;
			}
		}
	}

	public static String format(String label, Object amount) {
		return ChatColor.GRAY + label + ": " + ChatColor.YELLOW + amount;
	}

	public static void showStatistics(CommandSender sender, String targetName) {
		PlayerStatistics stats = getPlayerStatistics(targetName);

		if (stats == null) {
			sender.sendMessage(MessageType.RANK_PLAYER_NOTFOUND.getTranslation().getValue(targetName));
			return;
		}

		Integer rawRank = getRank(targetName);
		String rank = rawRank != null ? Integer.toString(rawRank) : "?";
		int kills = stats.getKills();
		int deaths = stats.getDeaths();
		String kdr = Double.toString((double) kills / (double) deaths);
		kdr = InventoryMenuManager.trimKdr(kdr);
		int achievements = Achievement.getUnlockedAchievementAmount(targetName);
		int bought = stats.getBought();
		int flagTakes = stats.getFlagTakes();
		int flagCaptures = stats.getFlagCaptures();
		sender.sendMessage(MessageType.RANK_PLAYER_DESC.getTranslation().getValue(targetName, rank));
		sender.sendMessage(MessageType.RANK_PLAYER_KILLS.getTranslation().getValue(kills));
		sender.sendMessage(MessageType.RANK_PLAYER_DEATHS.getTranslation().getValue(deaths));
		sender.sendMessage(MessageType.RANK_PLAYER_KDR.getTranslation().getValue(kdr));
		sender.sendMessage(MessageType.RANK_PLAYER_ACHIEVEMENTS.getTranslation().getValue(achievements));
		sender.sendMessage(MessageType.RANK_PLAYER_MERCHANDISEPURCHASED.getTranslation().getValue(bought));
		sender.sendMessage(MessageType.RANK_PLAYER_CRYSTALSSTOLEN.getTranslation().getValue(flagTakes));
		sender.sendMessage(MessageType.RANK_PLAYER_CRYSTALSCAPTURED.getTranslation().getValue(flagCaptures));
	}

	public static boolean isUpdating() {
		return updating;
	}

	public static boolean hasTotalStats() {
		return totalStats != null;
	}

	public static TotalPlayerStatistics getTotalStats() {
		return totalStats;
	}
}