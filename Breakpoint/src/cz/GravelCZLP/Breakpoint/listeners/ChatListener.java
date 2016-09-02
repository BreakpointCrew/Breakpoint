package cz.GravelCZLP.Breakpoint.listeners;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.game.Game;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import cz.GravelCZLP.Breakpoint.players.clans.Clan;

public class ChatListener implements Listener
{
	
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		BPPlayer bpPlayer = BPPlayer.get(player);
		String message = event.getMessage();
		String lastMsg = bpPlayer.getLastMessage();
		
		if (ChatUtils.isAd(message) && !bpPlayer.isStaff()) {
			player.kickPlayer("§4Reklama: web/server");
			event.setCancelled(true);
			return;
		}
		
		/*List<String> mentioned = new LinkedList<String>();
		
		String[] split = event.getMessage().split(" ");
		
		for (int i = 0; i < split.length; i++) {
			if (split[i].startsWith("@")) {
				mentioned.add(split[i].replaceAll("@", ""));
				split[i] = "§c" + split[i];
			}
		}
		
		StringBuilder b = new StringBuilder();
		for (String s : split) {
			b.append(s);
		}
		
		event.setMessage(b.toString());*/
		
		if(message.equals(lastMsg))
		{
			event.setCancelled(true);
			return;
		}
		else
			bpPlayer.setLastMessage(message);
		
		Game game = bpPlayer.getGame();
		boolean cont = game != null ? game.getListener().onPlayerChat(event, bpPlayer) : true;
		
		if(!cont)
			return;
		
		if (bpPlayer.isStaff()) {
			message = ChatColor.translateAlternateColorCodes('&', message);
		}
		
		if(message.charAt(0) == '#')
		{
			if(bpPlayer.isStaff())
			{
				message = message.substring(1);
				event.setCancelled(true);
				String playerName = player.getName();
				bpPlayer.sendStaffMessage(message);
				Breakpoint.info("Staff chat: " + playerName + ": " + message);
				return;
			}
		}
		else if(message.charAt(0) == '&')
		{
			String playerName = player.getName();
			Clan clan = Clan.getByPlayer(playerName);
			if(clan != null)
			{
				event.setCancelled(true);
				bpPlayer.sendClanMessage(message);
				Breakpoint.info("Clan [" + clan.getName() + "] chat: " + playerName + ": " + message);
				return;
			}
		}
		String chatPrefix = bpPlayer.getChatPrefix();
		event.setFormat(chatPrefix + "%1$s" + ChatColor.GRAY + ": " + "%2$s");
	}

	public static class ChatUtils {
		private static String web = "<\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]>";
		private static String IPV4 = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
		private static String IPV6 = "([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}";
		
		private static Pattern webPattern = Pattern.compile(web);
		private static Pattern ipv4Pattern = Pattern.compile(IPV4);
		private static Pattern ipv6pattern = Pattern.compile(IPV6);
		
		public static boolean isAd(String input) {
			
			Matcher m = webPattern.matcher(input);
			Matcher m1 = ipv4Pattern.matcher(input);
			Matcher m2 = ipv6pattern.matcher(input);
			if (m.matches() || m2.matches() || m1.matches()) {
				return true;
			}
			
			return false;
		}
	}
	
}
