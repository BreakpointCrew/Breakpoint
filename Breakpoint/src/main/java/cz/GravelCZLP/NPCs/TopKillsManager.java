/*
* Class By GravelCZLP at 11. 11. 2016
*/

package cz.GravelCZLP.NPCs;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import cz.GravelCZLP.Breakpoint.Configuration;
import cz.GravelCZLP.PacketWrapper.v1_10.WrapperPlayServerEntityDestroy;
import cz.GravelCZLP.PacketWrapper.v1_10.WrapperPlayServerNamedEntitySpawn;

public class TopKillsManager {

	protected static final FieldAccessor ENTITY_ID = Accessors.getFieldAccessor( MinecraftReflection.getEntityClass(), "entityCount", true );
	
	public Location signLoc;
	public Location NPCLoc;
	
	private String name;
	
	private UUID uuid;
	
	private int entityId;

	public TopKillsManager(Configuration yamlConfig) {
		entityId = (Integer) ENTITY_ID.get(null);
		ENTITY_ID.set( null, entityId + 1 );
		uuid = UUID.randomUUID();
		
		signLoc = yamlConfig.getTopPlayerSignLocation();
		NPCLoc = yamlConfig.getTopPlayerNPCLocation();
	}

	private WrappedDataWatcher getWatcher() {
        WrappedDataWatcher watcher = new WrappedDataWatcher();

        watcher.setObject( 1, WrappedDataWatcher.Registry.get( Integer.class ), 300 );
        watcher.setObject( 3, WrappedDataWatcher.Registry.get( String.class ), name );
        watcher.setObject( 7, WrappedDataWatcher.Registry.get( Float.class ), (float) 20 );
        return watcher;
	}
	
	public void spawnNPC(String name) {
		this.name = name;
        WrapperPlayServerNamedEntitySpawn spawned = new WrapperPlayServerNamedEntitySpawn();
        spawned.setEntityID( entityId );
        spawned.setPosition( NPCLoc.toVector() );
        spawned.setPlayerUUID( uuid );
        spawned.setYaw( NPCLoc.getYaw() );
        spawned.setPitch( NPCLoc.getPitch() );
        spawned.setMetadata( getWatcher() );
        for (Player p : Bukkit.getOnlinePlayers()) {
        	spawned.sendPacket(p);
        }
	}
	
	public void despawnNPC() {
		WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
		packet.setEntityIds(new int[] { entityId });
		for (Player p : Bukkit.getOnlinePlayers()) {
			packet.sendPacket(p);
		}
	}
	
	public void update(UpdateData updateData) {
		Block b = signLoc.getWorld().getBlockAt(signLoc);
		if (b.getState() instanceof Sign) {
			update((Sign) b.getState(), updateData);
		} else {
			b.setType(Material.WALL_SIGN);
			update((Sign) b.getState(), updateData);
		}
	}
	
	private void update(Sign s, UpdateData updateData) {
		if (!this.name.equals(updateData.getPlayerName())) {
			despawnNPC();
			spawnNPC(updateData.getPlayerName());
		}
		s.setLine(0, String.valueOf(updateData.getNewKills()));
		s.setLine(1, String.valueOf(updateData.getNewDeaths()));
		s.setLine(2, String.valueOf(updateData.getNewMoney()));
		s.setLine(3, String.valueOf(updateData.getKillDeaths()));
		s.update(true, false);
	}
	
	public String getName() {
		return name;
	}

	public UUID getUUID() {
		return uuid;
	}

	public int getEntityId() {
		return entityId;
	}
}
