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

public class Configuration
{
	private StorageType storageType;
	private String mySQLHost, mySQLDatabase, mySQLUsername, mySQLPassword, mySQLTablePlayers, languageFileName, cwChallengeGame;
	private Location lobbyLocation, shopLocation, vipInfoLocation, moneyInfoLocation, NPCTopKillLoc1, NPCTopKillLoc2, NPCTopKillLoc3, TopKillSignLoc1, TopKillSignLoc2, TopKillSignLoc3;
	private int mySQLPort, cwBeginHour, cwEndHour, cwWinLimit, cwEmeraldsForTotalWin;
	private RandomShop randomShop;
	private List<String> lobbyMessages;
	private String[] vipFeatures;
	
	public Configuration(StorageType storageType, String mySQLHost, int mySQLPort, String mySQLDatabase, String mySQLUsername, String mySQLPassword, String mySQLTablePlayers, String languageFileName, String cwChallengeGame, Location lobbyLocation, Location shopLocation, Location vipInfoLocation, Location moneyInfoLocation, RandomShop randomShop, int cwBeginHour, int cwEndHour, int cwWinLimit, int cwEmeraldsForTotalWin, List<String> lobbyMessages, String[] vipFeatures, Location npc1, Location npc2, Location npc3, Location sign1, Location sign2, Location sign3)
	{
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
		this.NPCTopKillLoc1 = npc1;
		this.NPCTopKillLoc2 = npc2;
		this.NPCTopKillLoc3 = npc3;
		this.TopKillSignLoc1 = sign1;
		this.TopKillSignLoc2 = sign2;
		this.TopKillSignLoc3 = sign3;
	}
	
	public static Configuration load()
	{
		File file = getFile();
		YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(file);
		
		String rawStorageType = yamlConfig.getString("storageType", "YAML");
		StorageType storageType;
		
		try
		{
			storageType = StorageType.valueOf(rawStorageType.toUpperCase());
		}
		catch(Exception e)
		{
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

		Location npc1loc = deserializeLocation(yamlConfig.getString("locations.npc.topkill1", "world,0,64,8,0,0"));
		Location npc2loc = deserializeLocation(yamlConfig.getString("locations.npc.topkill2", "world,0,64,8,0,0"));;
		Location npc3loc = deserializeLocation(yamlConfig.getString("locations.npc.topkill3", "world,0,64,8,0,0"));;
		
		Location npcSignLoc1 = deserializeLocation(yamlConfig.getString("locations.npc.signs.top1", "world,0,64,8,0,0"));
		Location npcSignLoc2 = deserializeLocation(yamlConfig.getString("locations.npc.signs.top2", "world,0,64,8,0,0"));
		Location npcSignLoc3 = deserializeLocation(yamlConfig.getString("locations.npc.signs.top3", "world,0,64,8,0,0"));
		
		int cwBeginHour = yamlConfig.getInt("cwBeginHour", 18);
		int cwEndHour = yamlConfig.getInt("cwEndHour", 21);
		int cwWinLimit = yamlConfig.getInt("cwWinLimit", 3);
		int cwEmeraldsForTotalWin = yamlConfig.getInt("cwEmeraldsForTotalWin", 25);
		
		List<String> lobbyMessages = yamlConfig.getStringList("lobbyMessages");
		
		if(lobbyMessages == null)
			lobbyMessages = new LinkedList<String>();
		
		String[] vipFeatures = null;
		List<String> list = yamlConfig.getStringList("vipFeatures");
		int size = list.size();
		
		if(list == null || size <= 0)
			vipFeatures = null;
		else
			vipFeatures = list.toArray(new String[size]);
		
		RandomShop randomShop = null;
		String[] rawRSLoc = ((String) yamlConfig.get("randomshoploc", "world,0,0,0,0,0")).split(",");
		Location rsLoc = new Location(Bukkit.getWorld(rawRSLoc[0]), Integer.parseInt(rawRSLoc[1]), Integer.parseInt(rawRSLoc[2]), Integer.parseInt(rawRSLoc[3]));
		int rsDir = Integer.parseInt(rawRSLoc[4]);
		randomShop = new RandomShop(rsLoc, rsDir);
		
		return new Configuration(
				storageType,
				mySQLHost,
				mySQLPort,
				mySQLDatabase,
				mySQLUsername,
				mySQLPassword,
				mySQLTablePlayers,
				languageFileName,
				challengeGameName,
				lobbyLocation,
				shopLocation,
				vipInfoLocation,
				moneyInfoLocation,
				randomShop,
				cwBeginHour,
				cwEndHour,
				cwWinLimit,
				cwEmeraldsForTotalWin,
				lobbyMessages,
				vipFeatures,
				npc1loc,
				npc2loc,
				npc3loc,
				npcSignLoc1,
				npcSignLoc2,
				npcSignLoc3
				);
	}
	
	public void save() throws IOException
	{
		File file = getFile();
		YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(file);
		
		yamlConfig.set("storageType", storageType.name());
		
		yamlConfig.set("mySQL.host", mySQLHost);
		yamlConfig.set("mySQL.port", mySQLPort);
		yamlConfig.set("mySQL.database", mySQLDatabase);
		yamlConfig.set("mySQL.username", mySQLUsername);
		yamlConfig.set("mySQL.password", mySQLPassword);
		yamlConfig.set("mySQL.table.players", mySQLTablePlayers);
		
		yamlConfig.set("locations.lobby", serialize(lobbyLocation));
		yamlConfig.set("locations.shop", serialize(shopLocation));
		yamlConfig.set("locations.vipInfo", serialize(vipInfoLocation));
		yamlConfig.set("locations.moneyInfo", serialize(moneyInfoLocation));
		yamlConfig.set("lobbyMessages", lobbyMessages);
		yamlConfig.set("lang", languageFileName);
		yamlConfig.set("vipFeatures", vipFeatures != null ? Arrays.asList(vipFeatures) : null);
		
		yamlConfig.set("cwChallengeGame", cwChallengeGame);
		yamlConfig.set("cwBeginHour", cwBeginHour);
		yamlConfig.set("cwEndHour", cwEndHour);
		yamlConfig.set("cwWinLimit", cwWinLimit);
		yamlConfig.set("cwEmeraldsForTotalWin", cwEmeraldsForTotalWin);
		
		yamlConfig.set("locations.npc.topkill1", serialize(NPCTopKillLoc1));
		yamlConfig.set("locations.npc.topkill2", serialize(NPCTopKillLoc2));
		yamlConfig.set("locations.npc.topkill3", serialize(NPCTopKillLoc3));
		
		yamlConfig.set("locations.npc.signs.top1", serialize(TopKillSignLoc1));
		yamlConfig.set("locations.npc.signs.top2", serialize(TopKillSignLoc2));
		yamlConfig.set("locations.npc.signs.top3", serialize(TopKillSignLoc3));
		
		Location rsLoc = randomShop.getLocation();
		int rsDir = randomShop.getDirection();
		
		yamlConfig.set("randomshoploc", rsLoc.getWorld().getName() + "," + rsLoc.getBlockX() + "," + rsLoc.getBlockY() + "," + rsLoc.getBlockZ() + "," + rsDir);
		yamlConfig.save(file);
	}
	
	public FruitSQL connectToMySQL()
	{
		return new FruitSQL(mySQLHost, mySQLPort, mySQLDatabase, mySQLUsername, mySQLPassword);
	}
	
	private static String serialize(Location loc)
	{
		return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
	}
	
	private static Location deserializeLocation(String raw)
	{
		String[] rawSplit = raw.split(",");
		return new Location(Bukkit.getServer().getWorld(rawSplit[0]), Double.parseDouble(rawSplit[1]), Double.parseDouble(rawSplit[2]), Double.parseDouble(rawSplit[3]), Float.parseFloat(rawSplit[4]), rawSplit.length >= 6 ? Float.parseFloat(rawSplit[5]) : 0F);
	}
	
	public static File getFile()
	{
		return new File("plugins/Breakpoint/config.yml");
	}

	public String getLanguageFileName()
	{
		return languageFileName;
	}
	
	public void setLanguageFileName(String languageFileName)
	{
		this.languageFileName = languageFileName;
	}

	public Location getLobbyLocation()
	{
		return lobbyLocation;
	}

	public void setLobbyLocation(Location lobbyLocation)
	{
		this.lobbyLocation = lobbyLocation;
	}

	public Location getShopLocation()
	{
		return shopLocation;
	}

	public void setShopLocation(Location shopLocation)
	{
		this.shopLocation = shopLocation;
	}

	public Location getVipInfoLocation()
	{
		return vipInfoLocation;
	}

	public void setVipInfoLocation(Location vipInfoLocation)
	{
		this.vipInfoLocation = vipInfoLocation;
	}

	public Location getMoneyInfoLocation()
	{
		return moneyInfoLocation;
	}

	public void setMoneyInfoLocation(Location moneyInfoLocation)
	{
		this.moneyInfoLocation = moneyInfoLocation;
	}

	public RandomShop getRandomShop()
	{
		return randomShop;
	}

	public void setRandomShop(RandomShop randomShop)
	{
		this.randomShop = randomShop;
	}

	public List<String> getLobbyMessages()
	{
		return lobbyMessages;
	}

	public void setLobbyMessages(List<String> lobbyMessages)
	{
		this.lobbyMessages = lobbyMessages;
	}

	public String[] getVIPFeatures()
	{
		return vipFeatures;
	}

	public void setVIPFeatures(String[] vipFeatures)
	{
		this.vipFeatures = vipFeatures;
	}

	public String getCWChallengeGame()
	{
		return cwChallengeGame;
	}

	public void setCWChallengeGame(String cwChallengeGame)
	{
		this.cwChallengeGame = cwChallengeGame;
	}

	public int getCWBeginHour()
	{
		return cwBeginHour;
	}

	public void setCWBeginHour(int cwBeginHour)
	{
		this.cwBeginHour = cwBeginHour;
	}

	public int getCWEndHour()
	{
		return cwEndHour;
	}

	public void setCWEndHour(int cwEndHour)
	{
		this.cwEndHour = cwEndHour;
	}

	public int getCWWinLimit()
	{
		return cwWinLimit;
	}

	public void setCWWinLimit(int cwWinLimit)
	{
		this.cwWinLimit = cwWinLimit;
	}

	public int getCWEmeraldsForTotalWin()
	{
		return cwEmeraldsForTotalWin;
	}

	public void setCWEmeraldsForTotalWin(int cwEmeraldsForTotalWin)
	{
		this.cwEmeraldsForTotalWin = cwEmeraldsForTotalWin;
	}

	public StorageType getStorageType()
	{
		return storageType;
	}

	public void setStorageType(StorageType storageType)
	{
		this.storageType = storageType;
	}

	public String getMySQLHost()
	{
		return mySQLHost;
	}

	public void setMySQLHost(String mySQLHost)
	{
		this.mySQLHost = mySQLHost;
	}

	public String getMySQLDatabase()
	{
		return mySQLDatabase;
	}

	public void setMySQLDatabase(String mySQLDatabase)
	{
		this.mySQLDatabase = mySQLDatabase;
	}

	public String getMySQLUsername()
	{
		return mySQLUsername;
	}

	public void setMySQLUsername(String mySQLUsername)
	{
		this.mySQLUsername = mySQLUsername;
	}

	public String getMySQLPassword()
	{
		return mySQLPassword;
	}

	public void setMySQLPassword(String mySQLPassword)
	{
		this.mySQLPassword = mySQLPassword;
	}

	public int getMySQLPort()
	{
		return mySQLPort;
	}

	public void setMySQLPort(int mySQLPort)
	{
		this.mySQLPort = mySQLPort;
	}

	public String getMySQLTablePlayers()
	{
		return mySQLTablePlayers;
	}

	public void setMySQLTablePlayers(String mySQLTablePlayers)
	{
		this.mySQLTablePlayers = mySQLTablePlayers;
	}
	public Location[] getNPCsLocations() {
		return new Location[] { NPCTopKillLoc1, NPCTopKillLoc2, NPCTopKillLoc3};
	}
	public Location[] getNPCsSignLocations() {
		return new Location[] {TopKillSignLoc1, TopKillSignLoc2, TopKillSignLoc3};
	}
	
	public void setNPCsSignLocations(Location loc, int i) {
		switch (i) {
		case 1:
			TopKillSignLoc1 = loc;
			break;
		case 2:
			TopKillSignLoc2 = loc;
			break;
		case 3:
			TopKillSignLoc3 = loc;
			break;
		default:
			throw new IllegalArgumentException("Number " + i + " is not a valid number");
		
		
		}
	}
	public void setNPCsLocations(Location loc, int i) {
		switch (i) {
		case 1:
			NPCTopKillLoc1 = loc;
			break;
		case 2:
			NPCTopKillLoc2 = loc;
			break;
		case 3:
			NPCTopKillLoc3 = loc;
			break;
		default:
			throw new IllegalArgumentException("Number " + i + " is not a valid argument");
		}
	}
}
