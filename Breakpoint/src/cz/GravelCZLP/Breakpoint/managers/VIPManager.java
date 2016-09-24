package cz.GravelCZLP.Breakpoint.managers;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.Configuration;
import cz.GravelCZLP.Breakpoint.language.MessageType;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import cz.GravelCZLP.Breakpoint.players.ServerPosition;

public class VIPManager {
	public static void startLoops() {
		startReminderLoop();
		startLobbyFlyLoop();
	}

	public static void startReminderLoop() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Breakpoint.getInstance(), new Runnable() {
			@Override
			public void run() {
				remindPlayers();
			}
		}, 20L * 60, 20L * 60 * 5);
	}

	public static void startLobbyFlyLoop() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Breakpoint.getInstance(), new Runnable() {
			@Override
			public void run() {
				checkFlyingPlayersInLobby();
			}
		}, 20L * 5, 20L * 5);
	}

	public static void checkFlyingPlayersInLobby() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.isFlying()) {
				BPPlayer bpPlayer = BPPlayer.get(player);
				if (!bpPlayer.getServerPosition().isStaff()) {
					if (isFarFromSpawnToUseFly(player)) {
						player.sendMessage(MessageType.COMMAND_FLY_TOOFAR.getTranslation().getValue());
						player.setAllowFlight(false);
						player.setFlying(false);
					}
				}
			}
		}
	}

	public static boolean isFarFromSpawnToUseFly(Player player) {
		Configuration config = Breakpoint.getBreakpointConfig();
		Location loc = player.getLocation();
		Location lobbyLoc = config.getLobbyLocation();
		double distance = loc.distance(lobbyLoc);
		return distance > 96;
	}

	public static void remindPlayers() {
		String feature = getRandomFeature();

		if (feature == null) {
			return;
		}

		feature = ChatColor.translateAlternateColorCodes('&', feature);

		for (Player player : Bukkit.getOnlinePlayers()) {
			BPPlayer bpPlayer = BPPlayer.get(player);

			ServerPosition pos = bpPlayer.getServerPosition();
			boolean b = pos.isSponsor() || pos.isStaff() || pos.isVIP() || pos.isVIPPlus() || pos.isYoutube();

			if (!b) {
				remindPlayer(player, feature);
			}
		}
	}

	public static void remindPlayer(Player player, String feature) {
		player.sendMessage(ChatColor.DARK_GRAY + "#############################");
		player.sendMessage(MessageType.OTHER_VIPFEATURE.getTranslation().getValue(feature));
		player.sendMessage(ChatColor.DARK_GRAY + "#############################");
	}

	public static String getRandomFeature() {
		Configuration config = Breakpoint.getBreakpointConfig();
		String[] features = config.getVIPFeatures();

		if (features == null) {
			return null;
		}

		int rnd = new Random().nextInt(features.length);
		return features[rnd];
	}
}
