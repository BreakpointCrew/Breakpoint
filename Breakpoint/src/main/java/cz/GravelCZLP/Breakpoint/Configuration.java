package cz.GravelCZLP.Breakpoint;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import com.fijistudios.jordan.FruitSQL;

import me.limeth.storageAPI.StorageType;

public class Configuration {
	private StorageType storageType;
	private String mySQLHost, mySQLDatabase, mySQLUsername, mySQLPassword, mySQLTablePlayers, languageFileName,
			cwChallengeGame;
	private Location lobbyLocation, shopLocation, vipInfoLocation, moneyInfoLocation, staffListLocation, NPCLocation, NPCSign;
	private int mySQLPort, cwBeginHour, cwEndHour, cwWinLimit, cwEmeraldsForTotalWin;
	private RandomShop randomShop;
	private List<String> lobbyMessages;
	private String[] vipFeatures;
	private long BoostMelounTime;
	
	public Configuration(StorageType storageType, String mySQLHost, int mySQLPort, String mySQLDatabase,
			String mySQLUsername, String mySQLPassword, String mySQLTablePlayers, String languageFileName,
			String cwChallengeGame, Location lobbyLocation, Location shopLocation, Location vipInfoLocation,
			Location moneyInfoLocation, RandomShop randomShop, int cwBeginHour, int cwEndHour, int cwWinLimit,
			int cwEmeraldsForTotalWin, List<String> lobbyMessages, String[] vipFeatures, Location staffListLocation, long BoostMelounTime, Location npcLoc, Location npcSign) {
		this.storageType = storageType;
		this.mySQLHost = mySQLHost;
		this.mySQLPort = mySQLPort;
		this.mySQLDatabase = mySQLDatabase;
		this.mySQLUsername = mySQLUsername;
		this.mySQLPassword = mySQLPassword;
		this.mySQLTablePlayers = mySQLTablePlayers;
		this.languageFileName = languageFileName;
		this.cwChallengeGame = cwChallengeGame;
		this.lobbyLocation = lobbyLocation;
		this.shopLocation = shopLocation;
		this.vipInfoLocation = vipInfoLocation;
		this.moneyInfoLocation = moneyInfoLocation;
		this.randomShop = randomShop;
		this.cwBeginHour = cwBeginHour;
		this.cwEndHour = cwEndHour;
		this.cwWinLimit = cwWinLimit;
		this.cwEmeraldsForTotalWin = cwEmeraldsForTotalWin;
		this.lobbyMessages = lobbyMessages;
		this.vipFeatures = vipFeatures;
		this.BoostMelounTime = BoostMelounTime;
		this.staffListLocation = staffListLocation;
		this.NPCLocation = npcLoc;
		this.NPCSign = npcSign;
	}

	public static Configuration load() {
		File file = getFile();
		YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(file);

		String rawStorageType = yamlConfig.getString("storageType", "YAML");
		StorageType storageType;

		try {
			storageType = StorageType.valueOf(rawStorageType.toUpperCase());
		} catch (Exception e) {
			storageType = StorageType.YAML;
		}

		String mySQLHost = yamlConfig.getString("mySQL.host", "127.0.0.1");
		int mySQLPort = yamlConfig.getInt("mySQL.port", 3306);
		String mySQLDatabase = yamlConfig.getString("mySQL.database", "Breakpoint");
		String mySQLUsername = yamlConfig.getString("mySQL.username", "admin");
		String mySQLPassword = yamlConfig.getString("mySQL.password", "password");
		String mySQLTablePlayers = yamlConfig.getString("mySQL.table.players", "breakpoint_players");

		String languageFileName = yamlConfig.getString("lang", "en");
		String challengeGameName = yamlConfig.getString("cwChallengeGame", "CW");

		Location lobbyLocation = deserializeLocation(yamlConfig.getString("locations.lobby", "world,0,64,0,0,0"));
		Location shopLocation = deserializeLocation(yamlConfig.getString("locations.shop", "world,0,64,0,0,0"));
		Location vipInfoLocation = deserializeLocation(yamlConfig.getString("locations.vipInfo", "world,0,64,8,0,0"));
		Location moneyInfoLocation = deserializeLocation(yamlConfig.getString("locations.moneyInfo", "world,0,64,8,0,0"));
		Location staffListLocation = deserializeLocation(yamlConfig.getString("locations.stafflist", "world,0,64,8,0,0"));

		Location NPCSignLoc = deserializeLocation(yamlConfig.getString("npcs.top.sign", "world,0,64,8,0,0"));
		Location NPCLoc = deserializeLocation(yamlConfig.getString("npcs.top.loc", "world,0,64,8,0,0"));
		
		int cwBeginHour = yamlConfig.getInt("cwBeginHour", 16);
		int cwEndHour = yamlConfig.getInt("cwEndHour", 23);
		int cwWinLimit = yamlConfig.getInt("cwWinLimit", 3);
		int cwEmeraldsForTotalWin = yamlConfig.getInt("cwEmeraldsForTotalWin", 250);

		long BoostMelounTime = yamlConfig.getLong("TimeForBoostMelounToSpawn", 10000L);

		List<String> lobbyMessages = yamlConfig.getStringList("lobbyMessages");

		if (lobbyMessages == null) {
			lobbyMessages = new LinkedList<>();
		}

		String[] vipFeatures = null;
		List<String> list = yamlConfig.getStringList("vipFeatures");
		int size = list.size();

		if (list == null || size <= 0) {
			vipFeatures = null;
		} else {
			vipFeatures = list.toArray(new String[size]);
		}
		
		RandomShop randomShop = null;
		String[] rawRSLoc = ((String) yamlConfig.get("randomshoploc", "world,0,0,0,0")).split(",");
		Location rsLoc = deserializeRandomShopLocation(rawRSLoc);
		int rsDir = Integer.parseInt(rawRSLoc[4]);
		randomShop = new RandomShop(rsLoc, rsDir);

		return new Configuration(storageType, mySQLHost, mySQLPort, mySQLDatabase, mySQLUsername, mySQLPassword,
				mySQLTablePlayers, languageFileName, challengeGameName, lobbyLocation, shopLocation, vipInfoLocation,
				moneyInfoLocation, randomShop, cwBeginHour, cwEndHour, cwWinLimit, cwEmeraldsForTotalWin, lobbyMessages,
				vipFeatures, staffListLocation, BoostMelounTime, NPCLoc, NPCSignLoc);
	}

	public void save() throws IOException {
		File file = getFile();
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(file);

		yamlConfig.set("storageType", this.storageType.name());

		yamlConfig.set("mySQL.host", this.mySQLHost);
		yamlConfig.set("mySQL.port", this.mySQLPort);
		yamlConfig.set("mySQL.database", this.mySQLDatabase);
		yamlConfig.set("mySQL.username", this.mySQLUsername);
		yamlConfig.set("mySQL.password", this.mySQLPassword);
		yamlConfig.set("mySQL.table.players", this.mySQLTablePlayers);

		yamlConfig.set("locations.lobby", serialize(this.lobbyLocation));
		yamlConfig.set("locations.shop", serialize(this.shopLocation));
		yamlConfig.set("locations.vipInfo", serialize(this.vipInfoLocation));
		yamlConfig.set("locations.moneyInfo", serialize(this.moneyInfoLocation));
		yamlConfig.set("locations.stafflist", serialize(this.staffListLocation));
		yamlConfig.set("npcs.top.sign", NPCSign);
		yamlConfig.set("npcs.top.loc", NPCLocation);
		
		yamlConfig.set("lobbyMessages", this.lobbyMessages);
		yamlConfig.set("lang", this.languageFileName);
		yamlConfig.set("vipFeatures", this.vipFeatures != null ? Arrays.asList(this.vipFeatures) : null);

		yamlConfig.set("cwChallengeGame", this.cwChallengeGame);
		yamlConfig.set("cwBeginHour", this.cwBeginHour);
		yamlConfig.set("cwEndHour", this.cwEndHour);
		yamlConfig.set("cwWinLimit", this.cwWinLimit);
		yamlConfig.set("cwEmeraldsForTotalWin", this.cwEmeraldsForTotalWin);

		yamlConfig.set("TimeForBoostMelounToSpawn", 200L);
		
		Location rsLoc = this.randomShop.getLocation();
		int rsDir = this.randomShop.getDirection();

		yamlConfig.set("randomshoploc", serializeRandomShopLoc(rsLoc, rsDir));
		yamlConfig.save(file);
	}
	
	public FruitSQL connectToMySQL() {
		return new FruitSQL(this.mySQLHost, this.mySQLPort, this.mySQLDatabase, this.mySQLUsername, this.mySQLPassword);
	}

	private static String serializeRandomShopLoc(Location rsLoc, int dir) {
		if (rsLoc == null) {
			return "world,100,50,100,0";
		}
		return rsLoc.getWorld() + "," + rsLoc.getBlockX() + "," + rsLoc.getBlockY() + "," + rsLoc.getBlockZ() + "," + dir; 
	}
	
	public static Location deserializeRandomShopLocation(String[] input) {
		if (input.length == 0 || input == null) {
			return new Location(Bukkit.getWorld("world"), 100, 50, 100);
		}
		World w = Bukkit.getWorld(input[0]);
		int x = Integer.parseInt(input[1]);
		int y = Integer.parseInt(input[2]);
		int z = Integer.parseInt(input[3]);
		return new Location(w, x, y, z);
	}
	
	private static String serialize(Location loc) {
		if (loc == null) {
			return "world,100,50,100,0";
		}
		return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw()
				+ "," + loc.getPitch();
	}

	public static Location deserializeLocation(String raw) {
		if (raw == "null" || raw == "" || raw == null) {
			return new Location(Bukkit.getWorld("world"), 100, 50, 100);
		}
		String[] rawSplit = raw.split(",");
		return new Location(Bukkit.getServer().getWorld(rawSplit[0]), Double.parseDouble(rawSplit[1]),
				Double.parseDouble(rawSplit[2]), Double.parseDouble(rawSplit[3]), Float.parseFloat(rawSplit[4]),
				rawSplit.length >= 6 ? Float.parseFloat(rawSplit[5]) : 0F);
	}

	public static File getFile() {
		return new File("plugins/Breakpoint/config.yml");
	}

	public String getLanguageFileName() {
		return this.languageFileName;
	}

	public void setLanguageFileName(String languageFileName) {
		this.languageFileName = languageFileName;
	}

	public Location getLobbyLocation() {
		return this.lobbyLocation;
	}

	public void setLobbyLocation(Location lobbyLocation) {
		this.lobbyLocation = lobbyLocation;
	}

	public Location getShopLocation() {
		return this.shopLocation;
	}

	public void setShopLocation(Location shopLocation) {
		this.shopLocation = shopLocation;
	}

	public Location getVipInfoLocation() {
		return this.vipInfoLocation;
	}

	public void setVipInfoLocation(Location vipInfoLocation) {
		this.vipInfoLocation = vipInfoLocation;
	}

	public Location getMoneyInfoLocation() {
		return this.moneyInfoLocation;
	}

	public void setMoneyInfoLocation(Location moneyInfoLocation) {
		this.moneyInfoLocation = moneyInfoLocation;
	}

	public RandomShop getRandomShop() {
		return this.randomShop;
	}

	public void setRandomShop(RandomShop randomShop) {
		this.randomShop = randomShop;
	}

	public List<String> getLobbyMessages() {
		return this.lobbyMessages;
	}

	public void setLobbyMessages(List<String> lobbyMessages) {
		this.lobbyMessages = lobbyMessages;
	}

	public String[] getVIPFeatures() {
		return this.vipFeatures;
	}

	public void setVIPFeatures(String[] vipFeatures) {
		this.vipFeatures = vipFeatures;
	}

	public String getCWChallengeGame() {
		return this.cwChallengeGame;
	}

	public void setCWChallengeGame(String cwChallengeGame) {
		this.cwChallengeGame = cwChallengeGame;
	}

	public int getCWBeginHour() {
		return this.cwBeginHour;
	}

	public void setCWBeginHour(int cwBeginHour) {
		this.cwBeginHour = cwBeginHour;
	}

	public int getCWEndHour() {
		return this.cwEndHour;
	}

	public void setCWEndHour(int cwEndHour) {
		this.cwEndHour = cwEndHour;
	}

	public int getCWWinLimit() {
		return this.cwWinLimit;
	}

	public void setCWWinLimit(int cwWinLimit) {
		this.cwWinLimit = cwWinLimit;
	}

	public int getCWEmeraldsForTotalWin() {
		return this.cwEmeraldsForTotalWin;
	}

	public void setCWEmeraldsForTotalWin(int cwEmeraldsForTotalWin) {
		this.cwEmeraldsForTotalWin = cwEmeraldsForTotalWin;
	}

	public StorageType getStorageType() {
		return this.storageType;
	}

	public void setStorageType(StorageType storageType) {
		this.storageType = storageType;
	}

	public String getMySQLHost() {
		return this.mySQLHost;
	}

	public void setMySQLHost(String mySQLHost) {
		this.mySQLHost = mySQLHost;
	}

	public String getMySQLDatabase() {
		return this.mySQLDatabase;
	}

	public void setMySQLDatabase(String mySQLDatabase) {
		this.mySQLDatabase = mySQLDatabase;
	}

	public String getMySQLUsername() {
		return this.mySQLUsername;
	}

	public void setMySQLUsername(String mySQLUsername) {
		this.mySQLUsername = mySQLUsername;
	}

	public String getMySQLPassword() {
		return this.mySQLPassword;
	}

	public void setMySQLPassword(String mySQLPassword) {
		this.mySQLPassword = mySQLPassword;
	}

	public int getMySQLPort() {
		return this.mySQLPort;
	}

	public void setMySQLPort(int mySQLPort) {
		this.mySQLPort = mySQLPort;
	}

	public String getMySQLTablePlayers() {
		return this.mySQLTablePlayers;
	}

	public void setMySQLTablePlayers(String mySQLTablePlayers) {
		this.mySQLTablePlayers = mySQLTablePlayers;
	}

	public Location getStaffListLocation() {
		return this.staffListLocation;
	}

	public long getTimeForSpeedMeloun() {
		return this.BoostMelounTime;
	}

	public Location getTopPlayerSignLocation() {
		return NPCSign;
	}

	public Location getTopPlayerNPCLocation() {
		return NPCLocation;
	}

	public void setNPCLocation(Location nPCLocation) {
		NPCLocation = nPCLocation;
	}

	public void setNPCSign(Location nPCSign) {
		NPCSign = nPCSign;
	}	
}
