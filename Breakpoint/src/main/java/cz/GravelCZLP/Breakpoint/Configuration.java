package cz.GravelCZLP.Breakpoint;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import me.limeth.storageAPI.StorageType;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import com.fijistudios.jordan.FruitSQL;

public class Configuration {
	private StorageType storageType;
	private String mySQLHost, mySQLDatabase, mySQLUsername, mySQLPassword, mySQLTablePlayers, languageFileName,
			cwChallengeGame, token;
	private Location lobbyLocation, shopLocation, vipInfoLocation, moneyInfoLocation, staffListLocation;
	private int mySQLPort, cwBeginHour, cwEndHour, cwWinLimit, cwEmeraldsForTotalWin;
	private RandomShop randomShop;
	private List<String> lobbyMessages;
	private String[] vipFeatures;
	private long BoostMelounTime;
	private TS3Config ts3;
	
	public Configuration(StorageType storageType, String mySQLHost, int mySQLPort, String mySQLDatabase,
			String mySQLUsername, String mySQLPassword, String mySQLTablePlayers, String languageFileName,
			String cwChallengeGame, Location lobbyLocation, Location shopLocation, Location vipInfoLocation,
			Location moneyInfoLocation, RandomShop randomShop, int cwBeginHour, int cwEndHour, int cwWinLimit,
			int cwEmeraldsForTotalWin, List<String> lobbyMessages, String[] vipFeatures, Location staffListLocation, long BoostMelounTime, String token, TS3Config ts3) {
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
		this.token = token;
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

		String token = yamlConfig.getString("tokens.discord", "token here m8");

		Location lobbyLocation = deserializeLocation(yamlConfig.getString("locations.lobby", "world,0,64,0,0,0"));
		Location shopLocation = deserializeLocation(yamlConfig.getString("locations.shop", "world,0,64,0,0,0"));
		Location vipInfoLocation = deserializeLocation(yamlConfig.getString("locations.vipInfo", "world,0,64,8,0,0"));
		Location moneyInfoLocation = deserializeLocation(
				yamlConfig.getString("locations.moneyInfo", "world,0,64,8,0,0"));
		Location staffListLocation = deserializeLocation(
				yamlConfig.getString("locations.stafflist", "world,0,64,8,0,0"));

		int cwBeginHour = yamlConfig.getInt("cwBeginHour", 18);
		int cwEndHour = yamlConfig.getInt("cwEndHour", 21);
		int cwWinLimit = yamlConfig.getInt("cwWinLimit", 3);
		int cwEmeraldsForTotalWin = yamlConfig.getInt("cwEmeraldsForTotalWin", 25);

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

		int ts3port = yamlConfig.getInt("tokens.ts3.port", 10011);
		String ts3address = yamlConfig.getString("tokens.ts3.ip", "192.168.1.100");
		int ts3sid = yamlConfig.getInt("tokens.ts3.sid", 1);
		
		String ts3name = yamlConfig.getString("tokens.ts3.name", "admin");
		String ts3pass = yamlConfig.getString("tokens.ts3.password", "pass");
		
		TS3Config ts3 = new TS3Config(ts3address, ts3port, ts3name, ts3pass, ts3sid);
		
		RandomShop randomShop = null;
		String[] rawRSLoc = ((String) yamlConfig.get("randomshoploc", "world,0,0,0,0,0")).split(",");
		Location rsLoc = new Location(Bukkit.getWorld(rawRSLoc[0]), Integer.parseInt(rawRSLoc[1]),
				Integer.parseInt(rawRSLoc[2]), Integer.parseInt(rawRSLoc[3]));
		int rsDir = Integer.parseInt(rawRSLoc[4]);
		randomShop = new RandomShop(rsLoc, rsDir);

		return new Configuration(storageType, mySQLHost, mySQLPort, mySQLDatabase, mySQLUsername, mySQLPassword,
				mySQLTablePlayers, languageFileName, challengeGameName, lobbyLocation, shopLocation, vipInfoLocation,
				moneyInfoLocation, randomShop, cwBeginHour, cwEndHour, cwWinLimit, cwEmeraldsForTotalWin, lobbyMessages,
				vipFeatures, staffListLocation, BoostMelounTime, token, ts3);
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
		yamlConfig.set("lobbyMessages", this.lobbyMessages);
		yamlConfig.set("lang", this.languageFileName);
		yamlConfig.set("vipFeatures", this.vipFeatures != null ? Arrays.asList(this.vipFeatures) : null);

		yamlConfig.set("cwChallengeGame", this.cwChallengeGame);
		yamlConfig.set("cwBeginHour", this.cwBeginHour);
		yamlConfig.set("cwEndHour", this.cwEndHour);
		yamlConfig.set("cwWinLimit", this.cwWinLimit);
		yamlConfig.set("cwEmeraldsForTotalWin", this.cwEmeraldsForTotalWin);

		yamlConfig.set("TimeForBoostMelounToSpawn", 200L);

		yamlConfig.set("tokens.discord", "token");
		
		yamlConfig.set("tokens.ts3.name", String.valueOf(ts3.queryUserName));
		yamlConfig.set("tokens.ts3.password", String.valueOf(ts3.queryPassword));
		
		yamlConfig.set("tokens.ts3.ip", String.valueOf(ts3.address));
		yamlConfig.set("tokens.ts3.port", String.valueOf(ts3.port));
		yamlConfig.set("tokens.ts3.sid", String.valueOf(ts3.ts3id));
		
		Location rsLoc = this.randomShop.getLocation();
		int rsDir = this.randomShop.getDirection();

		yamlConfig.set("randomshoploc", rsLoc.getWorld().getName() + "," + rsLoc.getBlockX() + "," + rsLoc.getBlockY()
				+ "," + rsLoc.getBlockZ() + "," + rsDir);
		yamlConfig.save(file);
	}
	
	public FruitSQL connectToMySQL() {
		return new FruitSQL(this.mySQLHost, this.mySQLPort, this.mySQLDatabase, this.mySQLUsername, this.mySQLPassword);
	}

	private static String serialize(Location loc) {
		if (loc == null) {
			return "null";
		}
		return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw()
				+ "," + loc.getPitch();
	}

	public static Location deserializeLocation(String raw) {
		if (raw == "null" || raw == "" || raw == null) {
			return null;
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

	public String getBotToken() {
		return this.token;
	}
	
	public TS3Config getTS3Things() {
		return ts3;
	}
	
	public static class TS3Config {
		
		public String address, queryUserName, queryPassword;
		public int port, ts3id;
		
		public TS3Config(String address, int port, String queryUsername, String querypassword, int id) {
			this.address       = address;
			this.queryUserName = queryUsername;
			this.queryPassword = querypassword;
			this.port          = port;
			ts3id = id;
		}
	}
	
}
