package cz.GravelCZLP.BreakpointInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.game.Game;
import cz.GravelCZLP.Breakpoint.game.GameType;
import cz.GravelCZLP.Breakpoint.game.ctf.CTFGame;
import cz.GravelCZLP.Breakpoint.game.ctf.Team;
import cz.GravelCZLP.Breakpoint.game.dm.DMGame;
import cz.GravelCZLP.Breakpoint.managers.GameManager;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import cz.GravelCZLP.BreakpointInfo.Packets.ActionPacket;
import cz.GravelCZLP.BreakpointInfo.Packets.DataRequestPacket;
import cz.GravelCZLP.BreakpointInfo.Packets.DataResponcePacket;
import cz.GravelCZLP.BreakpointInfo.Packets.ExceptionPacket;
import cz.GravelCZLP.BreakpointInfo.Util.EnumAction;
import cz.GravelCZLP.BreakpointInfo.threads.MinuteLimiterListener;
import cz.GravelCZLP.BreakpointInfo.threads.UnBanThread;

public class DataListenerMain {

	Breakpoint bp;

	public HashMap<String, Integer> requestsPerMin; // 5 per 1 minute
	public HashMap<String, Integer> banned;// if more then 20 per minute; unban every 10 mins
	public HashMap<String, Integer> connectionsPerMinute; // max 20 conn. per
	// minute

	public HashMap<String, Integer> connectionsPerIP;
	
	public ArrayList<String> banList;
	
	private FileWriter logger = null;

	private ScheduledExecutorService executor;
	
	private static Server server;

	private static int TIME_BAN = (10 * 60);
	
	public DataListenerMain(Breakpoint bp) {
		this.banned = new HashMap<>();
		this.requestsPerMin = new HashMap<>();
		this.connectionsPerMinute = new HashMap<>();
		this.connectionsPerIP = new HashMap<>();
		this.bp = bp;
	}
	
	public void start() throws IOException {
		server = new Server();
		server.start();

		Kryo kryo = server.getKryo();
		kryo.register(DataRequestPacket.class);
		kryo.register(DataResponcePacket.class);
		kryo.register(ActionPacket.class);
		kryo.register(ExceptionPacket.class);
		kryo.register(EnumAction.class);
		kryo.register(BPInfo.class);

		server.bind(33698, 33697);

		server.addListener(new PacketsListener(this));

		File banList = new File(bp.getDataFolder() + "/banlist.json");

		if (!banList.exists()) {
			banList.createNewFile();
		}
		
		loadBans(banList);

		initLogWriter();
		
		executor = Executors.newScheduledThreadPool(2);
		executor.scheduleAtFixedRate(new UnBanThread(this), 0, 1, TimeUnit.SECONDS);
		executor.scheduleAtFixedRate(new MinuteLimiterListener(this), 0, 1, TimeUnit.SECONDS);
		
	}

	public void stop() {
		for (Connection conn : server.getConnections()) {
			conn.close();
		}
		File banList = new File(bp.getDataFolder() + "/banlist.json");
		
		try {
			saveBans(banList);
			stopLogger();
		} catch (IOException e) {
			e.printStackTrace();
		}
		executor.shutdown();
		server.stop();
	}

	public boolean canReqquest(Connection conn) {
		String ip = conn.getRemoteAddressTCP().getAddress().toString();
		info("IP:" + ip + " called a request!");
		if (this.banned.containsKey(ip) || banList.contains(ip)) {
			info("IP: " + ip + " is banned, it did not get responce");
			return false;
		}
		if (!this.requestsPerMin.containsKey(ip)) {
			this.requestsPerMin.put(ip, 1);
		}
		Calendar cal = Calendar.getInstance();
		Date d = cal.getTime();
		@SuppressWarnings("deprecation")
		String datum = d.getYear() + "-" + d.getMonth() + "-" + d.getDay() + "-" + d.getHours() + "-" + d.getMinutes() + "-" + d.getSeconds();
		
		int i = this.requestsPerMin.get(conn.getRemoteAddressTCP().getAddress().toString());
		if (i < 5) {
			return true;
		} else if (i > 20) {
			warn("IP:" + ip + " was temporary banned for too mnny requests");
			ActionPacket packet = new ActionPacket(EnumAction.TEMP_BAN);
			conn.sendTCP(packet);
			this.banned.put(ip, TIME_BAN);
			return false;
		} else if (i > 60) {
			warn("IP:" + ip + " was permanently banned for too many requests at:" + datum);
			ActionPacket packet = new ActionPacket(EnumAction.PERMA_BAN);
			conn.sendTCP(packet);
			performBan(ip);
			return false;
		}
		return false;
	}
	
	public boolean canConnect(Connection conn) {
		String ip = conn.getRemoteAddressTCP().getAddress().toString();
		info("IP: " + ip + " is trying to connect");
		if (this.banned.containsKey(ip) || banList.contains(ip)) {
			info("IP: " + ip + " is banned, it was not able to conect");
			return false;
		}
		if (!this.connectionsPerMinute.containsKey(ip)) {
			this.connectionsPerMinute.put(ip, 1);
		}
		
		Calendar cal = Calendar.getInstance();
		Date d = cal.getTime();
		@SuppressWarnings("deprecation")
		String datum = d.getYear() + "-" + d.getMonth() + "-" + d.getDay() + "-" + d.getHours() + "-" + d.getMinutes() + "-" + d.getSeconds();
		
		if (this.connectionsPerMinute.get(ip) > 20) {
			warn("IP:" + ip + " was temporary banned for too many requests");
			ActionPacket packet = new ActionPacket(EnumAction.TEMP_BAN);
			conn.sendTCP(packet);
			this.banned.put(ip, TIME_BAN);
			return false;
		}
		if (this.connectionsPerMinute.get(ip) > 60) {
			warn("IP:" + ip + " was permanently banned for too many requests at: " + datum);
			ActionPacket packet = new ActionPacket(EnumAction.PERMA_BAN);
			conn.sendTCP(packet);
			performBan(ip);
		}
		return true;
	}

	public void performBan(String ip) {
		banList.add(ip);
	}

	public void saveBans(File toWhat) throws IOException {
		Json json = new Json();
		FileWriter writer = new FileWriter(toWhat);
		writer.write(json.prettyPrint(banList));
		writer.flush();
		writer.close();
	}

	@SuppressWarnings("unchecked")
	public void loadBans(File fromWhat) throws FileNotFoundException {
		Json json = new Json();

		banList = (ArrayList<String>) json.fromJson(ArrayList.class, fromWhat);
	}

	public void initLogWriter() throws IOException {
		Calendar c = Calendar.getInstance();
		Date d = c.getTime();
		@SuppressWarnings("deprecation")
		String datum = d.getYear() + "-" + d.getMonth() + "-" + d.getDay() + "-" + d.getHours() + "-" + d.getMinutes() + "-" + d.getSeconds();

		File file = new File(bp.getDataFolder() + "/BreakpointQuery/" + "log-" + datum);
		file.createNewFile();
		logger = new FileWriter(file);
		logger.write("Starting Logger of: " + datum + "\n");
	}

	public void info(String s) {
		try {
			logger.write(s + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void warn(String s) {
		try {
			logger.write("[WARNIG] " + s + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopLogger() throws IOException {
		logger.flush();
		logger.close();
	}

	public BPInfo getBPInfo() {
		BPInfo info = null;

		int playersInLobby = 0;
		int playersInGame = 0;
		int playersInCTF = 0;
		int playersInDM = 0;
		for (Player p : Bukkit.getOnlinePlayers()) {
			BPPlayer bpPlayer = BPPlayer.get(p);
			if (!bpPlayer.isInGame()) {
				playersInLobby++;
			}
			if (bpPlayer.isInGame()) {
				playersInGame++;
				if (bpPlayer.getGame().getType() == GameType.CTF) {
					playersInCTF++;
				} else if (bpPlayer.getGame().getType() == GameType.DM) {
					playersInDM++;
				}
			}
		}
		CTFGame ctfGame = null;
		for (Game game : GameManager.getGames()) {
			if (game.getType() == GameType.CTF) {
				ctfGame = (CTFGame) game;
			}
		}

		int blueId = Team.getId(Team.BLUE);
		int redId = Team.getId(Team.RED);
		int bodyBlue = ctfGame.getFlagManager().getScore()[blueId];
		int bodyRed = ctfGame.getFlagManager().getScore()[redId];

		String CWGame = Breakpoint.getBreakpointConfig().getCWChallengeGame();

		DMGame dmGame = null;
		for (Game game : GameManager.getGames()) {
			if (game.getType() == GameType.DM) {
				dmGame = (DMGame) game;
			}
		}

		String bestPlayerInDM = dmGame.getCurrentBestPlayer();

		info = new BPInfo(playersInLobby, playersInGame, bodyRed, bodyBlue, bestPlayerInDM, CWGame, playersInCTF,
				playersInDM);

		return info;
	}
}
