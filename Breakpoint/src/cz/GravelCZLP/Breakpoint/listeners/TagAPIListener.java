package cz.GravelCZLP.Breakpoint.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
//import org.kitteh.tag.PlayerReceiveNameTagEvent;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;

public class TagAPIListener implements Listener
{
	Breakpoint plugin;
	//public static final boolean hideOpponents = false;

	public TagAPIListener(Breakpoint p)
	{
		plugin = p;
	}

	/*@EventHandler
	public void onNameTag(PlayerReceiveNameTagEvent event)
	{
		Player nPlayer = event.getNamedPlayer();
		BPPlayer bpNPlayer = BPPlayer.get(nPlayer);
		String tag = bpNPlayer.getTag();
		event.setTag(tag);
	}*/
}
