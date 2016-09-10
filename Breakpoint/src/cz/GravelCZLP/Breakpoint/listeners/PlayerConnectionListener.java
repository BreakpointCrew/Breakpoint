package cz.GravelCZLP.Breakpoint.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.map.MinecraftFont;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.exceptions.NotStaffException;
import cz.GravelCZLP.Breakpoint.game.Game;
import cz.GravelCZLP.Breakpoint.game.GameType;
import cz.GravelCZLP.Breakpoint.game.ctf.CTFGame;
import cz.GravelCZLP.Breakpoint.game.ctf.Team;
import cz.GravelCZLP.Breakpoint.managers.GameManager;
import cz.GravelCZLP.Breakpoint.managers.InventoryMenuManager;
import cz.GravelCZLP.Breakpoint.managers.SBManager;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import me.leoko.advancedban.manager.TimeManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;

public class PlayerConnectionListener implements Listener
{
	Breakpoint plugin;
	public static final int spaceWidth = 5;

	public PlayerConnectionListener(Breakpoint p)
	{
		plugin = p;
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		Player player = event.getPlayer();
		
		System.out.println("Logged in: " + player + "IP: " + 
		event.getPlayer().getAddress().getAddress().toString());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		event.setJoinMessage(null);
		
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Breakpoint.getInstance(), new Runnable()
//		{
//			@Override
//			public void run()
//			{
				Player player = event.getPlayer();
				String playerName = player.getName();
				BPPlayer bpPlayer;
				
				try
				{					
					bpPlayer = BPPlayer.get(playerName, true);
				}
				catch(Exception e)
				{
					player.kickPlayer(ChatColor.RED + "Breakpoint Error: " + e.getMessage());
					return;
				}
				
				try {
					bpPlayer.setColorStaff();
				} catch (NotStaffException e) {
					e.printStackTrace();
				}
				
				bpPlayer.clearAfkSecondsToKick();
				bpPlayer.reset();
				//plugin.fim.loadPlayerData(player);
				
				if (player.isDead())
					return;
				
				SBManager sbm = bpPlayer.getScoreboardManager();
				
				bpPlayer.updatePermissions();
				
				player.setGameMode(GameMode.ADVENTURE);
				bpPlayer.spawn();
				sbm.updateLobbyObjective();
				bpPlayer.setPlayerListName();
				bpPlayer.setTimeJoined(System.currentTimeMillis());
				player.setHealthScaled(true);
//			}
//		});
	}

/*	public void onPlayerDisconnect(Player player)
	{
		BPPlayer bpPlayer = BPPlayer.get(player);
		
		if (!bpPlayer.isInGame())
			InventoryMenuManager.saveLobbyMenu(player, bpPlayer);
		else
			bpPlayer.updateArmorMinutesLeft();
		
		bpPlayer.saveData();
		//plugin.fim.savePlayerData(player);
		bpPlayer.reset(plugin);
		BPPlayer.removePlayer(bpPlayer);
		bpPlayer.tryFinalize();
		plugin.gm.updateTeamMapViews();
	}*/

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		event.setQuitMessage(null);
		
		Player player = event.getPlayer();
		BPPlayer bpPlayer = BPPlayer.get(player);
		
		if(bpPlayer == null)
			return;
		
		if (!bpPlayer.isInGame())
			InventoryMenuManager.saveLobbyMenu(bpPlayer);
		else
		{
			Game game = bpPlayer.getGame();
			
			bpPlayer.updateArmorMinutesLeft();
			game.onPlayerLeaveGame(bpPlayer);
		}
		
		if (bpPlayer.isBeingControled()) {
			
			String name = bpPlayer.getPlayer().getName(); 
			String uuid = bpPlayer.getPlayer().getUniqueId().toString();
			
			PunishmentType type = PunishmentType.TEMP_BAN;
			
			long start = TimeManager.getTime();
			long end = TimeManager.getTime() + (86400000L * 2);
			
			new Punishment(name, uuid, "Odpojení při prohledávání", "CONSOLE", type, start, end, "", -1)
			.create();
		}
		
		bpPlayer.trySave();
		bpPlayer.reset();
		BPPlayer.removePlayer(bpPlayer);
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		bpPlayer.getScoreboardManager().unregister();
	
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event)
	{
		event.setLeaveMessage(null);
	}

	@EventHandler
	public void onServerPing(ServerListPingEvent event)
	{
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
		event.setMotd("§c[--------------[§dBREAKPOINT§c]--------------]\n §cC§dT§3F: §3Blue:" + bodyBlue + " §cRed:" + bodyRed);
	}
	 
	public int getWidth(String string)
	{
		String noColors = ChatColor.stripColor(string);
		int width = 0;
		String noSpaces = noColors.replace(" ", "");
		int spaces = 0;
		for (int i = 0; i < noColors.length(); i++)
			if (string.charAt(i) == ' ')
				spaces += spaceWidth;
		width += MinecraftFont.Font.getWidth(noSpaces);
		width += spaces;
		return width;
	}
}
