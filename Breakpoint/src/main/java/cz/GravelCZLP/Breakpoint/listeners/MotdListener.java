
package cz.GravelCZLP.Breakpoint.listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.util.CachedServerIcon;

import cz.GravelCZLP.Breakpoint.Breakpoint;
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
		
		String motd = Breakpoint.getBreakpointConfig().getMotdMessage();
		motd = motd.replaceAll("!!RED!!", String.valueOf(pointsRed));
		motd = motd.replaceAll("!!BLUE!!", String.valueOf(pointsBlue));
		motd = ChatColor.translateAlternateColorCodes('&', motd);
		
		reply.setMOTD(motd);
		
		List<String> news = new ArrayList<>();
		
		for (String s : Breakpoint.getBreakpointConfig().getMotdNews()) {
			s = ChatColor.translateAlternateColorCodes('&', s);
			news.add(s);
		}
		
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