package cz.GravelCZLP.Breakpoint.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.Configuration;
import cz.GravelCZLP.Breakpoint.statistics.PlayerStatistics;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public class TopKillsManager {

	private boolean isNpcLoopRunning;
	private boolean isSignLoopRunning;
	
	private int npcid;
	
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
		NPCRegistry api = CitizensAPI.getNPCRegistry();
		PlayerStatistics stat = StatisticsManager.playersRankedByKills.get(0);
		if (stat == null) {
			return;
		}
		NPC npc = api.createNPC(EntityType.PLAYER, stat.getName());
		npc.setProtected(true);
		npc.setFlyable(true);
		npcid = npc.getId();
		npc.spawn(loc);
		
	}
	public void spawnSign() {
		Block signBlock = Bukkit.getWorlds().get(0).getBlockAt(config.getTopSignLocation());
		PlayerStatistics stat = StatisticsManager.playersRankedByKills.get(0);
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
				PlayerStatistics stat = StatisticsManager.playersRankedByKills.get(0);
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
				NPC npc = CitizensAPI.getNPCRegistry().getById(npcid);
				npc.despawn(DespawnReason.PLUGIN);
				PlayerStatistics stat = StatisticsManager.playersRankedByKills.get(0);
				if (stat == null) {
					return;
				}
				npc.setName(stat.getName());
				npc.spawn(config.getTopNPCLocation());
			}
		}, 10L, 2000L);
	}
}
