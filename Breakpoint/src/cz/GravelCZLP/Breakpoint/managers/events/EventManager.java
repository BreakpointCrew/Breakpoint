package cz.GravelCZLP.Breakpoint.managers.events;

import org.bukkit.event.player.PlayerInteractEvent;

import cz.GravelCZLP.Breakpoint.players.BPPlayer;

public interface EventManager {
	public void showLobbyMenu(BPPlayer bpPlayer);

	public void onPlayerInteract(PlayerInteractEvent event);

	public void save();
}
