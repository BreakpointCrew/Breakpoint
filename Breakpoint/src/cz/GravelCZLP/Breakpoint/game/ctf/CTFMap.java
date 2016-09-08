package cz.GravelCZLP.Breakpoint.game.ctf;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import cz.GravelCZLP.Breakpoint.game.BPMap;
import cz.GravelCZLP.Breakpoint.game.GameType;

public class CTFMap extends BPMap
{
	
	//{{STATIC
	public static final CTFMap load(YamlConfiguration yml, String path, String name)
	{
		String fullPath = path + "." + name;
		
		String[] rawRedSpawn = yml.getString(fullPath + ".redSpawn").split(",");
		Location redSpawn = new Location(Bukkit.getWorld(rawRedSpawn[0]), Double.parseDouble(rawRedSpawn[1]), Double.parseDouble(rawRedSpawn[2]), Double.parseDouble(rawRedSpawn[3]), Float.parseFloat(rawRedSpawn[4]), 1.0F);
		String[] rawBlueSpawn = yml.getString(fullPath + ".blueSpawn").split(",");
		Location blueSpawn = new Location(Bukkit.getWorld(rawBlueSpawn[0]), Double.parseDouble(rawBlueSpawn[1]), Double.parseDouble(rawBlueSpawn[2]), Double.parseDouble(rawBlueSpawn[3]), Float.parseFloat(rawBlueSpawn[4]), 1.0F);
		String[] rawRedFlag = yml.getString(fullPath + ".redFlag").split(",");
		Location redFlag = new Location(Bukkit.getWorld(rawRedFlag[0]), Integer.parseInt(rawRedFlag[1]), Integer.parseInt(rawRedFlag[2]), Integer.parseInt(rawRedFlag[3]));
		String[] rawBlueFlag = yml.getString(fullPath + ".blueFlag").split(",");
		Location blueFlag = new Location(Bukkit.getWorld(rawBlueFlag[0]), Integer.parseInt(rawBlueFlag[1]), Integer.parseInt(rawBlueFlag[2]), Integer.parseInt(rawBlueFlag[3]));
		
		List<String> rawMelounBoostList = yml.getStringList(fullPath + ".melounBoosts");
		
		List<Location> boosts = new ArrayList<Location>();
		
		
		for(String rawSpawn : rawMelounBoostList)
		{
			String[] split = rawSpawn.split(",");
			Location loc = new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
			
			boosts.add(loc);
		}
		
		int minPlayers = yml.getInt(fullPath + ".min");
		int maxPlayers =  yml.getInt(fullPath + ".max");
		double fallDamageMultiplier = yml.getDouble(fullPath + ".fallDamageMultiplier", 1.0);
		
		return new CTFMap(name, redSpawn, blueSpawn, redFlag, blueFlag, minPlayers, maxPlayers, fallDamageMultiplier, boosts);
	}
	//}}STATIC
	
	private final Location[] teamSpawn = new Location[2];
	private final Location[] teamFlags = new Location[2];
	private List<Location> melounBoosts = new ArrayList<Location>();
	
	public CTFMap(String name, Location redSpawn, Location blueSpawn, Location redFlag, Location blueFlag, int minPlayers, int maxPlayers, double fallDamageMultiplier, List<Location> boosts)
	{
		super(name, GameType.CTF, minPlayers, maxPlayers, fallDamageMultiplier);
		teamSpawn[0] = redSpawn;
		teamSpawn[1] = blueSpawn;
		teamFlags[0] = redFlag;
		teamFlags[1] = blueFlag;
		melounBoosts = boosts;
	}
	
	public CTFMap(String name, int minPlayers, int maxPlayers)
	{
		this(name, null, null, null, null, minPlayers, maxPlayers, 1.0, null);
	}
	
	@Override
	public void saveExtra(YamlConfiguration yml, String path)
	{
		String mapPath = path + "." + getName();
		
		yml.set(mapPath + ".redSpawn", teamSpawn[0].getWorld().getName() + "," + teamSpawn[0].getX() + "," + teamSpawn[0].getY() + "," + teamSpawn[0].getZ() + "," + teamSpawn[0].getYaw());
		yml.set(mapPath + ".blueSpawn", teamSpawn[1].getWorld().getName() + "," + teamSpawn[1].getX() + "," + teamSpawn[1].getY() + "," + teamSpawn[1].getZ() + "," + teamSpawn[1].getYaw());
		yml.set(mapPath + ".redFlag", teamFlags[0].getWorld().getName() + "," + teamFlags[0].getBlockX() + "," + teamFlags[0].getBlockY() + "," + teamFlags[0].getBlockZ());
		yml.set(mapPath + ".blueFlag", teamFlags[1].getWorld().getName() + "," + teamFlags[1].getBlockX() + "," + teamFlags[1].getBlockY() + "," + teamFlags[1].getBlockZ());
		yml.set(mapPath + ".melounBoosts", boostsToStringList());
	}
	
	@Override
	public boolean isPlayable()
	{
		return getName() != null && teamSpawn != null && teamSpawn[0] != null && teamSpawn[1] != null && teamFlags != null && teamFlags[0] != null && teamFlags[1] != null;
	}

	public Location[] getTeamSpawn()
	{
		return teamSpawn;
	}

	public Location[] getTeamFlags()
	{
		return teamFlags;
	}
	
	public List<Location> getMelounBoostsLocations()
	{
		return melounBoosts;
	}
	
	public void addMelounBoostLocation(Location loc)
	{	
		if (melounBoosts == null) {
			melounBoosts = new ArrayList<Location>();
		}
		
		melounBoosts.add(loc);
	}
	public List<String> boostsToStringList()
	{
		List<String> list = new ArrayList<String>();
		
		for (Location loc : melounBoosts) {
			
			String v = loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
			
			list.add(v);
		}
		
		return list;
	}
}
