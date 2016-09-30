package cz.GravelCZLP.Breakpoint.game.ctf;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import cz.GravelCZLP.Breakpoint.game.BPMap;
import cz.GravelCZLP.Breakpoint.game.GameType;

public class CTFMap extends BPMap {

	// {{STATIC
	public static final CTFMap load(YamlConfiguration yml, String path, String name) {
		String fullPath = path + "." + name;

		String[] rawRedSpawn = yml.getString(fullPath + ".redSpawn").split(",");
		Location redSpawn = new Location(Bukkit.getWorld(rawRedSpawn[0]), Double.parseDouble(rawRedSpawn[1]),
				Double.parseDouble(rawRedSpawn[2]), Double.parseDouble(rawRedSpawn[3]),
				Float.parseFloat(rawRedSpawn[4]), 1.0F);
		String[] rawBlueSpawn = yml.getString(fullPath + ".blueSpawn").split(",");
		Location blueSpawn = new Location(Bukkit.getWorld(rawBlueSpawn[0]), Double.parseDouble(rawBlueSpawn[1]),
				Double.parseDouble(rawBlueSpawn[2]), Double.parseDouble(rawBlueSpawn[3]),
				Float.parseFloat(rawBlueSpawn[4]), 1.0F);
		String[] rawRedFlag = yml.getString(fullPath + ".redFlag").split(",");
		Location redFlag = new Location(Bukkit.getWorld(rawRedFlag[0]), Integer.parseInt(rawRedFlag[1]),
				Integer.parseInt(rawRedFlag[2]), Integer.parseInt(rawRedFlag[3]));
		String[] rawBlueFlag = yml.getString(fullPath + ".blueFlag").split(",");
		Location blueFlag = new Location(Bukkit.getWorld(rawBlueFlag[0]), Integer.parseInt(rawBlueFlag[1]),
				Integer.parseInt(rawBlueFlag[2]), Integer.parseInt(rawBlueFlag[3]));

		List<String> rawMelounBoostList = yml.getStringList(fullPath + ".melounBoosts");

		List<Location> boosts = new ArrayList<>();

		for (String rawSpawn : rawMelounBoostList) {
			String[] split = rawSpawn.split(",");
			Location loc = new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]),
					Double.parseDouble(split[2]), Double.parseDouble(split[3]));

			boosts.add(loc);
		}

		int minPlayers = yml.getInt(fullPath + ".min");
		int maxPlayers = yml.getInt(fullPath + ".max");
		double fallDamageMultiplier = yml.getDouble(fullPath + ".fallDamageMultiplier", 1.0);

		return new CTFMap(name, redSpawn, blueSpawn, redFlag, blueFlag, minPlayers, maxPlayers, fallDamageMultiplier,
				boosts);
	}
	// }}STATIC

	private final Location[] teamSpawn = new Location[2];
	private final Location[] teamFlags = new Location[2];
	private List<Location> melounBoosts = new ArrayList<>();

	public CTFMap(String name, Location redSpawn, Location blueSpawn, Location redFlag, Location blueFlag,
			int minPlayers, int maxPlayers, double fallDamageMultiplier, List<Location> boosts) {
		super(name, GameType.CTF, minPlayers, maxPlayers, fallDamageMultiplier);
		this.teamSpawn[0] = redSpawn;
		this.teamSpawn[1] = blueSpawn;
		this.teamFlags[0] = redFlag;
		this.teamFlags[1] = blueFlag;
		this.melounBoosts = boosts;
	}

	public CTFMap(String name, int minPlayers, int maxPlayers) {
		this(name, null, null, null, null, minPlayers, maxPlayers, 1.0, null);
	}

	@Override
	public void saveExtra(YamlConfiguration yml, String path) {
		String mapPath = path + "." + getName();

		yml.set(mapPath + ".redSpawn", this.teamSpawn[0].getWorld().getName() + "," + this.teamSpawn[0].getX() + ","
				+ this.teamSpawn[0].getY() + "," + this.teamSpawn[0].getZ() + "," + this.teamSpawn[0].getYaw());
		yml.set(mapPath + ".blueSpawn", this.teamSpawn[1].getWorld().getName() + "," + this.teamSpawn[1].getX() + ","
				+ this.teamSpawn[1].getY() + "," + this.teamSpawn[1].getZ() + "," + this.teamSpawn[1].getYaw());
		yml.set(mapPath + ".redFlag", this.teamFlags[0].getWorld().getName() + "," + this.teamFlags[0].getBlockX() + ","
				+ this.teamFlags[0].getBlockY() + "," + this.teamFlags[0].getBlockZ());
		yml.set(mapPath + ".blueFlag", this.teamFlags[1].getWorld().getName() + "," + this.teamFlags[1].getBlockX()
				+ "," + this.teamFlags[1].getBlockY() + "," + this.teamFlags[1].getBlockZ());
		yml.set(mapPath + ".melounBoosts", boostsToStringList());
	}

	@Override
	public boolean isPlayable() {
		return getName() != null && this.teamSpawn != null && this.teamSpawn[0] != null && this.teamSpawn[1] != null
				&& this.teamFlags != null && this.teamFlags[0] != null && this.teamFlags[1] != null;
	}

	public Location[] getTeamSpawn() {
		return this.teamSpawn;
	}

	public Location[] getTeamFlags() {
		return this.teamFlags;
	}

	public List<Location> getMelounBoostsLocations() {
		return this.melounBoosts;
	}

	public void addMelounBoostLocation(Location loc) {
		if (this.melounBoosts == null) {
			this.melounBoosts = new ArrayList<>();
		}

		this.melounBoosts.add(loc);
	}

	public List<String> boostsToStringList() {
		List<String> list = new ArrayList<>();

		for (Location loc : this.melounBoosts) {

			String v = loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();

			list.add(v);
		}

		return list;
	}
}
