
package cz.GravelCZLP.Breakpoint.listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.util.CachedServerIcon;

import cz.GravelCZLP.Breakpoint.game.Game;
import cz.GravelCZLP.Breakpoint.game.GameType;
import cz.GravelCZLP.Breakpoint.game.ctf.CTFGame;
import cz.GravelCZLP.Breakpoint.game.ctf.Team;
import cz.GravelCZLP.Breakpoint.managers.GameManager;
import cz.GravelCZLP.PingAPI.PingAPI;
import cz.GravelCZLP.PingAPI.PingEvent;
import cz.GravelCZLP.PingAPI.PingReply;

/*
 * Author: GravelCZLP
 */

public class MotdListener implements cz.GravelCZLP.PingAPI.PingListener {

	@Override
	public void onPing(PingEvent event) {
		CTFGame ctfGame = null;
		for (Game game : GameManager.getGames())
			if (game.getType() == GameType.CTF)
				ctfGame = (CTFGame) game;
		
		int blueId = Team.getId(Team.BLUE);
		int redId = Team.getId(Team.RED);
		int pointsBlue = ctfGame.getFlagManager().getScore()[blueId];
		int pointsRed = ctfGame.getFlagManager().getScore()[redId];
		
		PingReply reply = event.getReply();
		
		reply.setMOTD("§c[------------§8[§d§lBREAKPOINT§r§8]§c------------]\n"
				+ "§cC§dT§9F: §9Blue§8: " + pointsBlue + " §cRed§8: " + pointsRed);
		
		List<String> news = new ArrayList<>();
		
		news.add("§d§lBreakpoint");
		news.add("----------");
		news.add("§4Nové Mapy.");
		news.add("§4Odebrány Perky.");
		news.add("§62 typy VIP");
		news.add("§4Anti-Cheat !");
		news.add("§1A mnoho dalšího!");
		
		CachedServerIcon icon = null;
		try {
			icon = Bukkit.loadServerIcon(new File("plugins/Breakpoint/images/logo.png"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		reply.setProtocolVersion(316);
		reply.setProtocolName("§aNovinky...");
		reply.setIcon(icon);
		reply.setPlayerSample(news);
	}

	@Override
	public void onPingAPIEnable(PingAPI api) {
		
	}

	@Override
	public void onPingAPIDisable(PingAPI api) {
		
	}

}