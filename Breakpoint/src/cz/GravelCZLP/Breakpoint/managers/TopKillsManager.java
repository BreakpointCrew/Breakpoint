package cz.GravelCZLP.Breakpoint.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.Configuration;
import cz.GravelCZLP.Breakpoint.statistics.PlayerStatistics;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public class TopKillsManager {

	private Configuration config;
	private List<UUID> NPCs = new ArrayList<UUID>(); 
	
	public TopKillsManager(Configuration config) {
		this.config = config;
	}
	
	
	public TopKillsManager setupFancyStats() {	
		setupNPCs();
		setSigns();
		return this;
	}
	
	public void startLoops() {
		SignsLoop();
		NPCLoop();
	}
	
	private void NPCLoop() {
		Bukkit.getScheduler().runTaskTimer(Breakpoint.getInstance(), new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < NPCs.size(); i++) {
					NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(NPCs.get(i));
					PlayerStatistics stat = null;
					try {
						stat = StatisticsManager.playersRankedByKills.get(i);
					} catch (Exception e) {
						Breakpoint.warn("Error when trying to update top kill NPCs: " + e.getMessage());
						return;
					}
					npc.setName(stat.getName());
				}
			}
		}, 1L, 2500L);
	}
	
	private void SignsLoop() {
		Bukkit.getScheduler().runTaskTimer(Breakpoint.getInstance(), new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < config.getNPCsSignLocations().length; i++) {
					Block sign = config.getNPCsSignLocations()[i].getBlock();
					if (sign.getState() instanceof Sign) {
						Sign s = (Sign) sign.getState();
						PlayerStatistics stat = null;
						try {
							stat = StatisticsManager.playersRankedByKills.get(i);	
						} catch (Exception e) {
							Breakpoint.warn("Error When trying to load signs for TOP 3 Players: " + e.getMessage());
							return;
						}
						if (stat != null) 
							s.setLine(0, stat.getName());
							s.setLine(1, "§aKills: " + String.valueOf(stat.getKills()));
							s.setLine(2, "§cDeaths: " + String.valueOf(stat.getDeaths())); 
							s.setLine(3, "§6K/D: " + Double.toString(((double) stat.getKills()) / ((double) stat.getDeaths())));
					}
				}
			}
		}, 1L, 2500L);
	}
	
	private void setupNPCs() {
		for (int i = 0; i < config.getNPCsLocations().length; i++) {
			Location loc = config.getNPCsLocations()[i];
			PlayerStatistics stat = null;
			try {
				stat = StatisticsManager.playersRankedByKills.get(i);
			} catch (Exception e) {
				Breakpoint.warn("Erron when trying to set NPCs at their Position: " + e.getMessage());
				return;
			}
			if (stat != null) {
				NPCRegistry  reg = CitizensAPI.getNPCRegistry();
				NPC npc = reg.createNPC(EntityType.PLAYER, stat.getName());
				npc.setProtected(true);
				NPCs.add(npc.getUniqueId());
				npc.spawn(loc);
			}
		}
	}
	
	private void setSigns() {
		for (int i = 0; i < config.getNPCsSignLocations().length; i++) {
			World w = config.getNPCsSignLocations()[i].getWorld();
			Block signBlock = w.getBlockAt(config.getNPCsLocations()[i]);
			if (!(signBlock.getState() instanceof Sign)) {
				signBlock.setType(Material.AIR);
				
				signBlock.setType(Material.WALL_SIGN);
				
				Sign sign = (Sign) signBlock.getState();
				PlayerStatistics stat = null;
				try {
					stat = StatisticsManager.playersRankedByKills.get(i);	
				} catch (Exception e) {
					Breakpoint.warn("Error When trying to load signs for TOP 3 Players: " + e.getMessage());
					return;
				}
				if (stat != null) 
					sign.setLine(0, stat.getName());
					sign.setLine(1, "§aKills: " + String.valueOf(stat.getKills()));
					sign.setLine(2, "§cDeaths: " + String.valueOf(stat.getDeaths())); 
					sign.setLine(3, "§6K/D: " + Double.toString(((double) stat.getKills()) / ((double) stat.getDeaths())));
				
			} else {
				Sign sign = (Sign) signBlock.getState();
				
				PlayerStatistics stat = null;
				try {
					stat = StatisticsManager.playersRankedByKills.get(i);	
				} catch (Exception e) {
					Breakpoint.warn("Error When trying to load signs for TOP 3 Players");
					return;
				}
				
				sign.setLine(0, stat.getName());
				sign.setLine(1, "§aKills: " + String.valueOf(stat.getKills()));
				sign.setLine(2, "§cDeaths: " + String.valueOf(stat.getDeaths())); 
				sign.setLine(3, "§6K/D: " + Double.toString(((double) stat.getKills()) / ((double) stat.getDeaths())));
			}
			
		}
	}
	public void despawn() {
		for (int i = 0; i < NPCs.size(); i++) {
			CitizensAPI.getNPCRegistry().deregister(CitizensAPI.getNPCRegistry().getByUniqueId(NPCs.get(i)));
		}
	}
}
