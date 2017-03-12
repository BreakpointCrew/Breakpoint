package cz.GravelCZLP.Breakpoint.listeners;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.map.MinecraftFont;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.game.Game;
import cz.GravelCZLP.Breakpoint.language.MessageType;
import cz.GravelCZLP.Breakpoint.managers.InventoryMenuManager;
import cz.GravelCZLP.Breakpoint.managers.SBManager;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import cz.GravelCZLP.PingAPI.PingAPI;

public class PlayerConnectionListener implements Listener {
	Breakpoint plugin;
	public static final int spaceWidth = 5;

	public PlayerConnectionListener(Breakpoint p) {
		this.plugin = p;

		setupPings();
	}
	
	@EventHandler
	public void onLogin(AsyncPlayerPreLoginEvent e) {
		String ip = e.getAddress().toString();
		Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
		
		for (Player p : players) {
			String playerip = p.getAddress().getAddress().toString();
			if (playerip.equals(ip)) {
				e.setLoginResult(Result.KICK_OTHER);
				e.disallow(Result.KICK_OTHER, MessageType.ONLY_ONE_IP.getTranslation().getValue());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		event.setJoinMessage("§8[§r+§8] §r" + event.getPlayer().getName());

		Player player = event.getPlayer();
		String playerName = player.getName();
		BPPlayer bpPlayer;

		try {
			bpPlayer = BPPlayer.get(playerName, true);
		} catch (Exception e) {
			player.kickPlayer(ChatColor.RED + "Breakpoint Error: " + e.getMessage());
			return;
		}

		bpPlayer.clearAfkSecondsToKick();
		bpPlayer.reset();
		bpPlayer.setScoreboardManager(new SBManager(bpPlayer));
		
		if (player.isDead()) {
			return;
		}

		SBManager sbm = bpPlayer.getScoreboardManager();

		player.setGameMode(GameMode.ADVENTURE);
		bpPlayer.spawn();
		sbm.updateLobbyObjective();
		bpPlayer.setPlayerListName();
		bpPlayer.setTimeJoined(System.currentTimeMillis());
		player.setHealthScaled(true);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);

		Player player = event.getPlayer();
		BPPlayer bpPlayer = BPPlayer.get(player);

		if (bpPlayer == null) {
			return;
		}

		if (!bpPlayer.isInGame()) {
			InventoryMenuManager.saveLobbyMenu(bpPlayer);
		} else {
			Game game = bpPlayer.getGame();

			bpPlayer.updateArmorMinutesLeft();
			game.onPlayerLeaveGame(bpPlayer);
		}
		

		bpPlayer.trySave();
		bpPlayer.reset();
		BPPlayer.removePlayer(bpPlayer);
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		bpPlayer.getScoreboardManager().unregister();
 
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		event.setLeaveMessage(null);
	}

	public void setupPings() {
		PingAPI.registerListener(new MotdListener());
	}

	public int getWidth(String string) {
		String noColors = ChatColor.stripColor(string);
		int width = 0;
		String noSpaces = noColors.replace(" ", "");
		int spaces = 0;
		for (int i = 0; i < noColors.length(); i++) {
			if (string.charAt(i) == ' ') {
				spaces += spaceWidth;
			}
		}
		width += MinecraftFont.Font.getWidth(noSpaces);
		width += spaces;
		return width;
	}
}
