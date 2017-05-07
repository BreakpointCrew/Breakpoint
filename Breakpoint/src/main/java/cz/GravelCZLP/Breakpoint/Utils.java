/*
* Class By GravelCZLP at 26. 11. 2016
*/

package cz.GravelCZLP.Breakpoint;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;

import cz.GravelCZLP.Breakpoint.game.CharacterType;
import cz.GravelCZLP.Breakpoint.managers.StatisticsManager;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import cz.GravelCZLP.Breakpoint.statistics.TotalPlayerStatistics;
import cz.GravelCZLP.BreakpointInfo.Utils.BreakpointInfo;
import net.minecraft.server.v1_11_R1.MinecraftServer;

public class Utils {

	public static List<Location> getCircle(Location center, double radius, int amount) {
		World w = center.getWorld();
		double inc = 6.283185307179586 / (double) amount;
		List<Location> locs = new ArrayList<>();
		int i = 0;
		while (i < amount) {
			double angle = (double) i * inc;
			double x = center.getX() + radius * Math.cos(angle);
			double z = center.getZ() + radius * Math.sin(angle);
			Location loc = new Location(w, x, center.getY(), z);
			locs.add(loc);
			i++;
		}
		return locs;
	}
	
	public static class Runnables {
		
		public static Runnable getPyroSpetialEffect(BPPlayer bpPlayer) {
			if (!bpPlayer.isInGame()) {
				throw new IllegalStateException("Players needs to be in game");
			}
			if (!(bpPlayer.getGameProperties().getCharacterType() == CharacterType.PYRO)) {
				throw new IllegalStateException("Player needs to have Pyro class");
			}
			
			final Location center = bpPlayer.getPlayer().getLocation();
			
			Runnable r = new Runnable() {
				double cir1radius = 0.0;
				double cir2radius = 0.0;
				double increseAmount = 0.15;
				
				@Override
				public void run() {
					if (cir1radius <= 1.75) {
						cir1radius =+ increseAmount;	
					}
					if (cir2radius <= 4.75) {
						cir2radius =+ increseAmount;
					}
					List<Location> cir1 = Utils.getCircle(center, cir1radius, 15);
					List<Location> cir2 = Utils.getCircle(center, cir2radius, 30);
					
					for (Location loc : cir1) {
						loc.getWorld().spawnParticle(Particle.FLAME, loc, 5);	
					}
					for (Location loc : cir2) {
						loc.getWorld().spawnParticle(Particle.FLAME, loc, 5);
					}
				}
				
			};
			return r;
		}
	}
	public static class PastebinReport {
		private static final String DATE = new SimpleDateFormat("dd-MM-yyyy kk:mm Z").format(new Date());
		private static final String API_DEV_KEY = "c5696fc96651b00456d31454d970d53d";
		
		private StringBuilder report = new StringBuilder();
		private String url = "";
		
	    private void append(String s) {
	        report.append(s + '\n');
	    }
		
	    private String getNMSVersion() {
	    	MinecraftServer ms = ((CraftServer) Bukkit.getServer()).getServer();
	    	return ms.getVersion();
	    }
	    
	    public String getReport() {
	    	createReport();
	    	postReport();
	    	return url;
	    }
	    
	    private void appendSystemInfo() {
	        Runtime runtime = Runtime.getRuntime();
	        append("AntiCheat Version: " + Breakpoint.getVersion());
	        append("Server Version: " + Bukkit.getVersion());
	        append("Server Implementation: " + Bukkit.getName());
	        append("Server ID: " + Bukkit.getServerId());
	        append("NMS Version: " + getNMSVersion());
	        append("Java Version: " + System.getProperty("java.vendor") + " " + System.getProperty("java.version"));
	        append("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
	        append("Free Memory: " + runtime.freeMemory() / 1024 / 1024 + "MB");
	        append("Max Memory: " + runtime.maxMemory() / 1024 / 1024 + "MB");
	        append("Total Memory: " + runtime.totalMemory() / 1024 / 1024 + "MB");
	        append("Online Mode: " + Bukkit.getOnlineMode());
	        append("Players: " + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
	        append("Plugin Count: " + Bukkit.getPluginManager().getPlugins().length);
	        append("Plugin Uptime: " + ((System.currentTimeMillis() - Breakpoint.getInstance().timeBoot) / 1000) / 60 + " minutes");
	    }
	    
	    private void createReport() {
	    	append("------------ Breakpoint Report - " + DATE + " ------------");
	    	appendSystemInfo();
	    	TotalPlayerStatistics totplsts = StatisticsManager.getTotalStats();
	    	append("Total kills: " + totplsts.getKills());
	    	append("Average kills: " + totplsts.getAverageKills());
	    	append("Average deaths: " + totplsts.getAverageBought());
	    	append("Average assists: " + totplsts.getAverageAssists());
	    	append("Average flag captures: " + totplsts.getAverageFlagCaptures());
	    	append("Average flag takes: " + totplsts.getAverageFlagTakes());
	    	append("Average money: " + totplsts.getAverageMoney());
	    	append("Average bought: " + totplsts.getAverageBought());
	    	for (CharacterType ct : CharacterType.values()) {
	    		String c = WordUtils.capitalize(ct.getProperName());
	    		append("Kills " + c + ": " + totplsts.getKills(ct));
	    	}
	    	BreakpointInfo info = BreakpointInfo.getActualInfo();
	    	append("Current Players in Lobby" + info.getPlayerIsLobby());
	    	append("Current Players in Game: " + info.getPlayerInGame());
	    	append("Current Players in CTF: " + info.getPlayersInCTF());
	    	append("Current Players in DM: " + info.getPlayersInDM());
	    	append("Best Player in DM: " + info.getBestPlayerInDM());
	    	append("Blue Crystals in CTF: " + info.getBlueCrystals());
	    	append("Red Crystals in CTF: " + info.getRedCrystals());
	    }
	    public void postReport() {
	    	try {
	    		URL pastebinAPI = new URL("http://pastebin.com/api/api_post.php");
	    		HttpsURLConnection conn = (HttpsURLConnection) pastebinAPI.openConnection();
	    		conn.setConnectTimeout(5000);
	    		conn.setReadTimeout(5000);
	    		conn.setRequestMethod("POST");
	    		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	    		conn.setInstanceFollowRedirects(false);
	    		conn.setDoOutput(true);
	    		OutputStream out = conn.getOutputStream();
	    		
	    		StringBuilder post = new StringBuilder();
	    		post.append("api_option=paste");
	    		post.append("&api_dev_key=" + e(API_DEV_KEY));
	    		post.append("&api_post_code=" + e(report.toString()));
	    		post.append("&api_paste_private=" + e("1"));
	    		post.append("&api_paste_name=" + e("Breakpoint Report"));
	    		post.append("&api_paste_expire_date=" + e("1M"));
	    		post.append("&api_paste_format=" + e("text"));
	    		post.append("&api_user_key=" + e(""));
	    		
	    		out.write(post.toString().getBytes());
	    		out.flush();
	    		out.close();
	    		
	    		if (conn.getResponseCode() == 200) {
	    			InputStream is = conn.getInputStream();
	    			BufferedReader br = new BufferedReader(new InputStreamReader(is));
	    			String line;
	    			StringBuffer response = new StringBuffer();
	    			while ((line = br.readLine()) != null) {
	    				response.append(line);
	    				response.append("\n\r");
	    			}
	    			br.close();
	    			
	    			String result = response.toString().trim();
	    			if (!result.contains("https://") || !result.contains("http://")) {
	    				throw new BPReportPostException(result);
	    			} else {
	    				url = result.trim();
	    			}
	    		} else {
	    			throw new BPReportPostException("Responce code was other then 200: " + conn.getResponseCode());
	    		}
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	    private String e(String e) throws UnsupportedEncodingException {
	    	return URLEncoder.encode(e, "utf-8");
	    }
	}
	public static class BPReportPostException extends Exception {

		private static final long serialVersionUID = -1427692136833410347L;

		public BPReportPostException(String s) {
			super(s);
		}
		
	}
}
