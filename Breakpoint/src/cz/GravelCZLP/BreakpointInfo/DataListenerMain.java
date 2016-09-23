package cz.GravelCZLP.BreakpointInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
import cz.GravelCZLP.BreakpointInfo.Packets.DataRequestPacket;
import cz.GravelCZLP.BreakpointInfo.Packets.DataResponcePacket;
import cz.GravelCZLP.BreakpointInfo.threads.MinuteLimiterListener;
import cz.GravelCZLP.BreakpointInfo.threads.UnBanThread;

public class DataListenerMain {

	Breakpoint bp;
	
	public HashMap<String, Integer> requestsPerMin; // 5 per 1 minute
	public List<String> banned; // if more then 20 per minute; unban every 10 mins
	public HashMap<String, Integer> connectionsPerMinute; // max 20 conn. per minute
	
	private Timer timer = null;
	
	private static Server server;
	
	public DataListenerMain(Breakpoint bp) {
		banned = new ArrayList<String>();
		requestsPerMin = new HashMap<String, Integer>();
		connectionsPerMinute = new HashMap<String, Integer>();
		this.bp = bp;
	}
	
	public void start() throws IOException {
		server = new Server();
		server.start();
		
		Kryo kryo = server.getKryo();
		kryo.register(DataRequestPacket.class);
		kryo.register(DataResponcePacket.class);
		kryo.register(BPInfo.class);
		
		server.bind(33698, 33697);
		
		server.addListener(new PacketsListener(this));
		
		timer = new Timer();
		timer.schedule(new UnBanThread(this), (1000 * 60 * 10));
		timer.schedule(new MinuteLimiterListener(this), (1000 * 60));
	}

	public void stop() {
		for (Connection conn : server.getConnections()) {
			conn.close();
		}
		timer.cancel();
		server.stop();
	}
	
	public boolean canReqquest(Connection conn) {
		String ip = conn.getRemoteAddressTCP().getAddress().toString();
		if (banned.contains(conn.getRemoteAddressTCP().getAddress().toString())) {
			return false;
		}
		if (!requestsPerMin.containsKey(ip)) {
			requestsPerMin.put(ip, 1);
		}
		int i = requestsPerMin.get(conn.getRemoteAddressTCP().getAddress().toString());
		if (i < 5) {
			return true;
		} else if (i > 20) {
			banned.add(ip);
			return false;
		} else {
			return false;
		}
	}

	public boolean canConnect(Connection conn) {
		String ip = conn.getRemoteAddressTCP().getAddress().toString();
		if (banned.contains(ip)) {
			return false;
		}
		if (!connectionsPerMinute.containsKey(ip)) {
			connectionsPerMinute.put(ip, 1);
		}
		if (connectionsPerMinute.get(ip) > 20) {
			banned.add(ip);
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("static-access")
	public BPInfo getBPInfo() {
		BPInfo info = null;
		
		int playersInLobby = 0;
		int playersInGame = 0;
		int playersInCTF = 0;
		int playersInDM = 0;
		for (Player p : Bukkit.getOnlinePlayers()) {
			BPPlayer bpPlayer = BPPlayer.get(p);
			if (!bpPlayer.isInGame())
				playersInLobby++;
			if (bpPlayer.isInGame())
				playersInGame++;
				if (bpPlayer.getGame().getType() == GameType.CTF) {
					playersInCTF++;
				} else if (bpPlayer.getGame().getType() == GameType.DM) {
					playersInDM++;
				}
		}
		CTFGame ctfGame = null;
		for (Game game : GameManager.getGames())
			if (game.getType() == GameType.CTF)
				ctfGame = (CTFGame) game;
		
		int blueId = Team.getId(Team.BLUE);
		int redId = Team.getId(Team.RED);
		int bodyBlue = ctfGame.getFlagManager().getScore()[blueId];
		int bodyRed = ctfGame.getFlagManager().getScore()[redId];
		
		String CWGame = bp.getBreakpointConfig().getCWChallengeGame();
		
		DMGame dmGame = null;
		for (Game game : GameManager.getGames())
			if (game.getType() == GameType.DM)
				dmGame = (DMGame) game;
		
		String bestPlayerInDM = dmGame.getCurrentBestPlayer();
		
		info = new BPInfo(playersInLobby, playersInGame, bodyRed, bodyBlue, bestPlayerInDM, CWGame, playersInCTF, playersInDM);
		
		return info;
	}
}
