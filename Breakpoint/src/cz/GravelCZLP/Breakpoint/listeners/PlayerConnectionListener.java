package cz.GravelCZLP.Breakpoint.listeners;

import java.util.ArrayList;
import java.util.List;

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
import org.bukkit.map.MinecraftFont;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.game.Game;
import cz.GravelCZLP.Breakpoint.game.GameType;
import cz.GravelCZLP.Breakpoint.game.ctf.CTFGame;
import cz.GravelCZLP.Breakpoint.game.ctf.Team;
import cz.GravelCZLP.Breakpoint.managers.GameManager;
import cz.GravelCZLP.Breakpoint.managers.InventoryMenuManager;
import cz.GravelCZLP.Breakpoint.managers.SBManager;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import cz.GravelCZLP.PingAPI.PingAPI;
import cz.GravelCZLP.PingAPI.PingEvent;
import cz.GravelCZLP.PingAPI.PingListener;
import cz.GravelCZLP.PingAPI.PingReply;
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
		
		setupPings();
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		Player player = event.getPlayer();
		
		System.out.println("Logged in: " + player + " From IP: " + player.getAddress().getHostString());
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
				
				bpPlayer.clearAfkSecondsToKick();
				bpPlayer.reset();
				//plugin.fim.loadPlayerData(player);
				
				if (player.isDead())
					return;
				
				SBManager sbm = bpPlayer.getScoreboardManager();
				
				player.setGameMode(GameMode.ADVENTURE);
				bpPlayer.spawn();
				sbm.updateLobbyObjective();
				bpPlayer.setPlayerListName();
				bpPlayer.setTimeJoined(System.currentTimeMillis());
				player.setHealthScaled(true);
				
				/*EntityPlayer handle = ((CraftPlayer) player).getHandle();
				
				handle.playerConnection.sendPacket(getTabListPakcet());*/
//			}
//		});
	}

	/*private PacketPlayOutPlayerListHeaderFooter getTabListPakcet() {
		try {
			Object header = net.minecraft.server.v1_10_R1.IChatBaseComponent.class.getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + MessageType.CHAT_BREAKPOINT.getTranslation().getValue() + "\"}");
			Object footer = net.minecraft.server.v1_10_R1.IChatBaseComponent.class.getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + "\n we <3 u" + "\"}");
			Constructor<?> cons = PacketPlayOutPlayerListHeaderFooter.class.getConstructor(net.minecraft.server.v1_10_R1.IChatBaseComponent.class);
			PacketPlayOutPlayerListHeaderFooter o = (PacketPlayOutPlayerListHeaderFooter) cons.newInstance(header); 
			Field field = o.getClass().getDeclaredField("b");
			field.setAccessible(true);
			field.set(cons, footer);
			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new PacketPlayOutPlayerListHeaderFooter();
	}*/
	
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
	
	public void setupPings() {
		PingAPI.registerListener(new PingListener() {
			@Override
			public void onPing(PingEvent e) {
				CTFGame ctfGame = null;
				for (Game game : GameManager.getGames())
					if (game.getType() == GameType.CTF)
						ctfGame = (CTFGame) game;
				
				int blueId = Team.getId(Team.BLUE);
				int redId = Team.getId(Team.RED);
				int bodyBlue = ctfGame.getFlagManager().getScore()[blueId];
				int bodyRed = ctfGame.getFlagManager().getScore()[redId];
				
				PingReply reply = e.getReply();
				
				reply.setMOTD("§c[------------§8[§d§lBREAKPOINT§r§8]§c-------§8[§6AAC§8]§c------]\n"
						+ "§cC§dT§9F: §9Blue§8: " + bodyBlue + " §cRed§8: " + bodyRed);
				
				List<String> news = new ArrayList<String>();
				
				news.add("   §d§lBreakpoint");
				news.add("--------------");
				news.add("§aKity Za Emeraldy.");
				news.add("§4Nové Mapy.");
				news.add("§4Odebrány Perky.");
				news.add("§62 typy VIP");
				news.add("§4Anti-Cheat !");
				news.add("§1A mnoho dalšího!");
				
				reply.setProtocolVersion(210);
				reply.setProtocolName("§aNovinky...");
				reply.setPlayerSample(news);
			}
		});
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
