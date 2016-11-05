package cz.GravelCZLP.DiscordChatBot.Listeners;

import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import cz.GravelCZLP.DiscordChatBot.DiscordChat;
import net.md_5.bungee.api.ChatColor;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class ListenersMinecraft implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		BPPlayer bpPlayer = BPPlayer.get(e.getPlayer());
		Player p = e.getPlayer();
		
		String msg = e.getMessage();
		
		String clanName = bpPlayer.getClan() != null ? bpPlayer.getClan().getName() : "";
		String pozice = bpPlayer.getChatPrefix();
		
		String game = bpPlayer.isInGame() ? bpPlayer.getGame().getType().name() : "";
		
		String finalMessage = clanName + " " 
			+ WordUtils.capitalize(game) + " " +  
			pozice + " " + 
			p.getName() + " " 
			+ ": " + msg;
		finalMessage = ChatColor.stripColor(finalMessage);
		
		
		List<IChannel> channels = DiscordChat.getDiscordClient().getChannels();
		for (IChannel c : channels) {
			if (c.getName().equalsIgnoreCase("minecraftchat")) {
				try {
					c.sendMessage(finalMessage);
				} catch (MissingPermissionsException | RateLimitException | DiscordException e1) {
					e1.printStackTrace();
				}
				return;
			}
		}
	}

}
