package cz.GravelCZLP.Breakpoint.managers;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.Configuration;
import cz.GravelCZLP.Breakpoint.statistics.PlayerStatistics;
import cz.GravelCZLP.NPC.NPC;

public class TopKillsManager {

	private boolean isNpcLoopRunning;
	private boolean isSignLoopRunning;
	
	private NPC npc1;
	
	private Configuration config;
	
	public TopKillsManager(Configuration config) {
		isSignLoopRunning = false;
		isNpcLoopRunning = false;
		this.config = config;
	}
	
	public void spawn() {
		spawnNPC();
		spawnSign();
		
	}
	
	public void spawnNPC() {
		Location loc = config.getTopNPCLocation();
		
		PlayerStatistics stat = null;
		try {
			stat = StatisticsManager.playersRankedByKills.get(0);
		} catch (NullPointerException e) {
			Breakpoint.warn("Error when loading data for NPC: " + e.getMessage());
		}
		if (stat == null) {
			return;
		}
		NPC npc = new NPC(stat.getName(), loc);
		npc.setGameMode(GameMode.CREATIVE);
		npc.setSkin(stat.getName());
		npc.spawn();
		npc1 = npc;
	}
	public void spawnSign() {
		Block signBlock = config.getTopSignLocation().getBlock();
		if (!signBlock.getChunk().isLoaded()) {
			signBlock.getChunk().load();
		}
		PlayerStatistics stat = null;
		try {
			stat = StatisticsManager.playersRankedByKills.get(0);
		} catch (NullPointerException e) {
			Breakpoint.warn("Error when spawing NPC: " + e.getMessage());
		}
		if (stat == null) {
			return;
		}
		if (signBlock.getState() instanceof Sign) {
			Sign sign = (Sign) signBlock.getState();
			sign.setLine(0, "§4§l" + stat.getName());
			sign.setLine(1, "§aK: " + stat.getKills());
			sign.setLine(2, "§cDeaths: " + stat.getDeaths());
			sign.setLine(3, "§8" + Double.toString(((double) stat.getKills()) / ((double) stat.getDeaths())));
		} else {
			signBlock.setType(Material.WALL_SIGN);
			
			Sign sign = (Sign) signBlock.getState();
			sign.setLine(0, "§4§l" + stat.getName());
			sign.setLine(1, "§aK: " + stat.getKills());
			sign.setLine(2, "§cDeaths: " + stat.getDeaths());
			sign.setLine(3, "§8" + Double.toString(((double) stat.getKills()) / ((double) stat.getDeaths())));
		}
	}
	public void startLoops() {
		startSignUpdateLoop();
		startNpcUpdateLoop();
	}
	
	public void startSignUpdateLoop() {
		if (isSignLoopRunning) {
			return;
		}
		isSignLoopRunning = true;
		Bukkit.getScheduler().runTaskTimer(Breakpoint.getInstance(), new Runnable() {
			@Override
			public void run() {
				Block signBlock = Bukkit.getWorlds().get(0).getBlockAt(config.getTopSignLocation());
				if (!(signBlock.getState() instanceof Sign)) {
					throw new NullPointerException("Block at 'Top sign' Locaton is not equal to Sign");
				}
				PlayerStatistics stat = null;
				try {
					stat = StatisticsManager.playersRankedByKills.get(0);
				} catch (NullPointerException e) {
					Breakpoint.warn("Error when spawing NPC: " + e.getMessage());
				}
				if (stat == null) {
					return;
				}
				Sign sign = (Sign) signBlock.getState();
				
				sign.setLine(0, "§4§l" + stat.getName());
				sign.setLine(1, "§aK: " + stat.getKills());
				sign.setLine(2, "§cDeaths: " + stat.getDeaths());
				sign.setLine(3, "§8" + Double.toString(((double) stat.getKills()) / ((double) stat.getDeaths())));
			}
		}, 10L, 2000L);
	}
	public void startNpcUpdateLoop() {
		if (isNpcLoopRunning) {
			return;
		}
		isNpcLoopRunning = true;
		Bukkit.getScheduler().runTaskTimer(Breakpoint.getInstance(), new Runnable() {
			@Override
			public void run() {
				PlayerStatistics stat = null;
				try {
					stat = StatisticsManager.playersRankedByKills.get(0);
				} catch (NullPointerException e) {
					Breakpoint.warn("Error when spawing NPC: " + e.getMessage());
				}
				if (stat == null) {
					return;
				}
				npc1.setCustomName(stat.getName());
				npc1.setSkin(stat.getName());
			}
		}, 10L, 2000L);
	}
	public void DeleteAndDespawn() {
		npc1.destroy();
	}
}
