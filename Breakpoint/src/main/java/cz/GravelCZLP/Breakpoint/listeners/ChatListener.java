package cz.GravelCZLP.Breakpoint.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.game.Game;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import cz.GravelCZLP.Breakpoint.players.clans.Clan;

public class ChatListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		BPPlayer bpPlayer = BPPlayer.get(player);
		String message = event.getMessage();
		String lastMsg = bpPlayer.getLastMessage();

		if (message.equals(lastMsg)) {
			event.setCancelled(true);
			return;
		} else {
			bpPlayer.setLastMessage(message);
		}

		Game game = bpPlayer.getGame();
		boolean cont = game != null ? game.getListener().onPlayerChat(event, bpPlayer) : true;

		if (!cont) {
			return;
		}

		if (message.length() >= 1 && bpPlayer.getPlayer().hasPermission("Breakpoint.chat.colors") && message.charAt(1) != '&') {
			message = ChatColor.translateAlternateColorCodes('&', message);
		}

		if (message.contains("hacky") || message.contains("hacker")) {
			event.setCancelled(true);
			player.sendMessage("§cOd toho je /report :)");
			return;
		}

		if (message.length() >= 1 && message.charAt(0) == '#' && message.charAt(1) == '#') {
			if (bpPlayer.getPlayer().hasPermission("Breakpoint.chat.staffchat")) {
				message = message.substring(2);
				event.setCancelled(true);
				String playerName = player.getName();
				bpPlayer.sendStaffMessage(message);
				Breakpoint.info("Staff chat: " + playerName + ": " + message);
				return;
			}
		} else if (message.charAt(0) == '@') {
			String playerName = player.getName();
			Clan clan = Clan.getByPlayer(playerName);
			if (clan != null) {
				event.setCancelled(true);
				bpPlayer.sendClanMessage(message);
				Breakpoint.info("Clan [" + clan.getName() + "] chat: " + playerName + ": " + message);
				return;
			}
		}
		String chatPrefix = bpPlayer.getChatPrefix();
		event.setFormat(chatPrefix + "%1$s" + ChatColor.GRAY + ": " + "%2$s");
	}
}
