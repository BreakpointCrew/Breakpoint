package cz.GravelCZLP.Breakpoint.managers;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.inventivetalent.nicknamer.api.NickNamerAPI;

import cz.GravelCZLP.Breakpoint.players.BPPlayer;

public class NickNamerManager
{
	private static boolean loaded;
	
	public static boolean setLoaded()
	{
		Plugin plugin = Bukkit.getPluginManager().getPlugin("NickNamer");
		
		if(plugin == null)
			return false;
		
		return loaded = plugin.isEnabled();
	}
	
	public static boolean isLoaded()
	{
		return loaded;
	}
	
	public static void updateNametag(BPPlayer bpPlayer)
	{
		if(!loaded)
			return;
		
		UUID uuid = bpPlayer.getPlayer().getUniqueId();
		String playerName = bpPlayer.getPlayer().getName();
		String prefix = bpPlayer.getTagPrefix(true);
		String suffix = bpPlayer.getTagSuffix();
		
		NickNamerAPI.getNickManager().setNick(uuid, prefix + playerName + suffix);
	}
}
