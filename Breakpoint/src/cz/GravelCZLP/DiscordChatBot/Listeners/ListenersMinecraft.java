package cz.GravelCZLP.DiscordChatBot.Listeners;

import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import cz.GravelCZLP.Breakpoint.game.GameType;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import cz.GravelCZLP.Breakpoint.players.clans.Clan;
import cz.GravelCZLP.DiscordChatBot.MainMCChat;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class ListenersMinecraft implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if (e.isCancelled()) {
			return;
		}
		Player player = e.getPlayer();
		BPPlayer bpPlayer = BPPlayer.get(player);

		String author = player.getName();
		String msg = e.getMessage();

		GameType game = null;

		Clan c = null;

		String pozice = bpPlayer.getChatName();

		boolean isInGame = bpPlayer.isInGame();

		if (isInGame) {
			game = bpPlayer.getGame().getType();
		}
		if (bpPlayer.getClan() != null) {
			c = bpPlayer.getClan();
		}

		String formatedMessage = (isInGame ? WordUtils.capitalize(game.name()) : "") + pozice + " " + c.getName() + " "
				+ author + ": " + msg;
		formatedMessage = formatedMessage.replaceAll("§", "");

		for (IChannel channel : MainMCChat.getDiscordClient().getChannels(false)) {
			if (channel.getName().equalsIgnoreCase("minecraftchat")) {
				try {
					channel.sendMessage(formatedMessage);
				} catch (RateLimitException | MissingPermissionsException | DiscordException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

}
