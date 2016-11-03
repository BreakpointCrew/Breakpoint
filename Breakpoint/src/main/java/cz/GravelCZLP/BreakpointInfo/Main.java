
package cz.GravelCZLP.BreakpointInfo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.BreakpointInfo.Connection.ConnectionListener;
import cz.GravelCZLP.BreakpointInfo.Packets.Common.ExceptionPacket;
import cz.GravelCZLP.BreakpointInfo.Utils.LogFormat;
import cz.GravelCZLP.BreakpointInfo.Utils.Network;

public class Main {
	
	public Breakpoint pl = null;;
	
	public Logger logger = null;;
	
	public Server server = null;;
	
	public boolean isDisabled = false;
	
	public int port;
	
	private static Main instance;
	
	public static Main getInstance() {
		return instance;
	}
	
	public Main(Breakpoint bp) {
		pl = bp;
	}
	
	public void start() throws IOException {
		logger = Logger.getLogger("Breakpoint Data");
		
		FileHandler dateLog;
		FileHandler latestLog;
		
		File dataFolder = new File(pl.getDataFolder() + "/BreakpointData");
		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}
		
		File logFolder = new File(dataFolder + "/logs");
		if (!logFolder.exists()) {
			logFolder.mkdir();
		}
		
		try {
			SimpleDateFormat format = new SimpleDateFormat("HH:mm dd_MM_yyyy");
			File f1 = new File(pl.getDataFolder() + "/BreakpointData/logs/log_" + format.format(Calendar.getInstance().getTime()) + ".log");
			f1.createNewFile();
			dateLog = new FileHandler(pl.getDataFolder() + "/BreakpointData/logs/log_" + format.format(Calendar.getInstance().getTime()) + ".log");
			logger.addHandler(dateLog);
			dateLog.setFormatter(new LogFormat());
			
			File f = new File(pl.getDataFolder() + "/BreakpointData/logs/latest.log");
			if (f.exists()) {
				f.delete();
			}
			
			latestLog = new FileHandler(pl.getDataFolder() + "/BreakpointData/logs/latest.log");
			logger.addHandler(latestLog);
			latestLog.setFormatter(new LogFormat());
			
			logger.info("File Logger was been inited..");
		} catch (Exception e) { e.printStackTrace(); }
		
		logger.info("Loading Server..");
		
		logger.info("Loading config");
		loadConfig();
		logger.info("Config Loaded..");
		
		logger.info("Loading Bans");
		loadBans();
		logger.info("Bans Loaded");
		
		logger.info("Initializing Server");
		server = new Server();
		logger.info("Starting Server");
		server.start();
		logger.info("Binding server to port: " + port);
		server.bind(port);
		logger.info("Server Initialized and Started");
		
		logger.info("Setting up Packets to Kryo");
		Network.setupPackets(server.getKryo());
		logger.info("Packets injected");
		
		server.addListener(new ConnectionListener(this));
		logger.info("Listener added");
		instance = this;
	}
	
	public void loadConfig() {
		File configFile = new File(pl.getDataFolder() + "/BreakpointData/config.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		config.addDefault("connection.portTCP", 25589);
		port = config.getInt("connection.portTCP");
	}
	
	public void loadBans() {
		File banFile = new File(pl.getDataFolder() + "/BreakpointData/BanList.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(banFile);
		permaBans = config.getStringList("banlist");
	}
	
	public void stop() {
		logger.info("Server stoping..");
		File banFile = new File(pl.getDataFolder() + "/BreakpointData/BanList.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(banFile);
		logger.info("Saveing bans..");
		config.set("banlist", permaBans);
		logger.info("Bans Saved..");
		logger.info("Disconnecting all connections and sending packets about server is being shuted down..");
		for (Connection conn : server.getConnections()) {
			Exception exc = new Exception("Server shuting down");
			conn.sendTCP(new ExceptionPacket(exc));
			conn.close();
		}
		logger.info("Success...");
		logger.info("Closeing server port and all new connections");
		server.close();
		logger.info("Success..");
		logger.info("Stoping server..");
		server.stop();
		logger.info("Server Stoped!!");
	}

	private HashMap<String, Integer> lastRequests = new HashMap<>();
	private HashMap<String, Integer> lastConnections = new HashMap<>(); 
	
	private HashMap<String, Integer> tempBans = new HashMap<>();
	
	private List<String> permaBans = new ArrayList<String>();
	
	public boolean canRequest(Connection connection) {
		String ip = connection.getRemoteAddressTCP().getAddress().getHostAddress();
		int amoutOfRequests = lastRequests.get(ip).intValue();
		if (isDisabled) {
			logger.warning("API is disabled, IP: " + ip + " did not get responce.");
			return false;
		}
		if (amoutOfRequests >= 100) {
			logger.warning("IP: " + ip + " was permanently banned for too many requests per minute.");
			permaBan(ip);
			connection.close();
			return false;
		}
		if (amoutOfRequests >= 60) {
			logger.warning("IP: " + ip + " was temporarly banned for too many requests per minute.");
			tempBan(ip);
			connection.close();
			return false;
		}
		if (amoutOfRequests >= 10) {
			logger.warning("IP:" + ip + " is over request limit, it did not get responce.");
			return false;
		}
		return true;
	}
	
	public boolean canConnect(Connection conn) {
		String ip = conn.getRemoteAddressTCP().getAddress().getHostAddress();
		int amoutOfConnections = lastConnections.get(ip).intValue();
		if (isDisabled) {
			logger.warning("API is disabled, IP: " + ip + " was disconnected.");
			conn.close();
			return false;
		}
		if (amoutOfConnections >= 20) {
			permaBan(ip);
			conn.close();
			logger.warning("IP: " + ip + " was pernamently banned for too many connections per minute.");
			return false;
		} else if (amoutOfConnections >= 10) {
			conn.close();
			logger.warning("IP: " + ip + " was temporarly banned for too many connections per minute.");
			tempBan(ip);
			return false;
		}
		return true;
	}
	
	public void tempBan(String ip) {
		tempBans.put(ip, 10);
	}
	
	public void permaBan(String ip) {
		permaBans.add(ip);
	}
}