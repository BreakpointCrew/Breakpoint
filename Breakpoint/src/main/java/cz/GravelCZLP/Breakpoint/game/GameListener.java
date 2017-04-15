package cz.GravelCZLP.Breakpoint.game;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.ItemStack;

import cz.GravelCZLP.Breakpoint.players.BPPlayer;

public abstract class GameListener {
	private final Game game;

	public GameListener(Game game, Class<? extends Game> gameClass) {
		if (!game.getClass().equals(gameClass)) {
			throw new IllegalArgumentException("Given game is not applicable.");
		}

		this.game = game;
	}

	/** @return Continue? */
	public abstract boolean onPlayerChat(AsyncPlayerChatEvent event, BPPlayer bpPlayer);

	public abstract void onPlayerDeath(PlayerDeathEvent event, BPPlayer bpPlayer);

	public abstract void onPlayerRespawn(PlayerRespawnEvent event, BPPlayer bpPlayer, boolean leaveAfterDeath);

	public abstract void onEntityDamage(EntityDamageEvent event);

	public abstract void onPlayerShootBow(EntityShootBowEvent event, BPPlayer bpPlayer);

	public abstract void onPlayerSplashedByPotion(PotionSplashEvent event, BPPlayer bpShooter, BPPlayer bpVictim);

	public abstract void onPlayerRightClickBlock(PlayerInteractEvent event, BPPlayer bpPlayer);

	public abstract void onPlayerPhysicallyInteractWithBlock(PlayerInteractEvent event, BPPlayer bpPlayer,
			Block blockBelow);

	public abstract void onPlayerRightClickItem(PlayerInteractEvent event, BPPlayer bpPlayer, ItemStack item);

	public abstract void onPlayerLeftClickItem(PlayerInteractEvent event, BPPlayer bpPlayer, ItemStack item);

	public abstract void onPlayerTeleport(PlayerTeleportEvent event, BPPlayer bpPlayer);

	public Game getGame() {
		return this.game;
	}

	public abstract void onPlayerMove(BPPlayer bpPlayer, Location from, Location to, PlayerMoveEvent e);

	public abstract void onPlayerToggleSprint(BPPlayer bpPlayer, PlayerToggleSprintEvent e);
	
	
}
