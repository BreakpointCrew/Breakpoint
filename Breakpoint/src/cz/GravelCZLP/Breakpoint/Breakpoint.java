package cz.GravelCZLP.Breakpoint;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.fijistudios.jordan.FruitSQL;

import cz.GravelCZLP.Breakpoint.language.Language;
import cz.GravelCZLP.Breakpoint.language.MessageType;
import cz.GravelCZLP.Breakpoint.listeners.ChatListener;
import cz.GravelCZLP.Breakpoint.listeners.PVPListener;
import cz.GravelCZLP.Breakpoint.listeners.PlayerConnectionListener;
import cz.GravelCZLP.Breakpoint.listeners.PlayerInteractListener;
import cz.GravelCZLP.Breakpoint.listeners.PlayerInventoryListener;
import cz.GravelCZLP.Breakpoint.managers.AbilityManager;
import cz.GravelCZLP.Breakpoint.managers.AfkManager;
import cz.GravelCZLP.Breakpoint.managers.ChatManager;
import cz.GravelCZLP.Breakpoint.managers.DoubleMoneyManager;
import cz.GravelCZLP.Breakpoint.managers.GameManager;
import cz.GravelCZLP.Breakpoint.managers.InventoryMenuManager;
import cz.GravelCZLP.Breakpoint.managers.Licence;
import cz.GravelCZLP.Breakpoint.managers.LobbyInfoManager;
import cz.GravelCZLP.Breakpoint.managers.StatisticsManager;
import cz.GravelCZLP.Breakpoint.managers.TopKillsManager;
import cz.GravelCZLP.Breakpoint.managers.VIPManager;
import cz.GravelCZLP.Breakpoint.managers.commands.AchievementsCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.commands.BPCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.commands.CWCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.commands.ClanCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.commands.FlyCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.commands.GMCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.commands.HelpOPCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.commands.RankCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.commands.SkullCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.commands.TopClansCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.commands.TopCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.events.EventManager;
import cz.GravelCZLP.Breakpoint.managers.events.advent.AdventManager;
import cz.GravelCZLP.Breakpoint.maps.MapManager;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import cz.GravelCZLP.Breakpoint.players.Settings;
import cz.GravelCZLP.Breakpoint.players.clans.Clan;
import me.limeth.storageAPI.StorageType;

public class Breakpoint extends JavaPlugin
{
	private static Breakpoint instance;
	private static Configuration config;
	private static FruitSQL mySQL;
	public static final String PLUGIN_NAME = "Breakpoint";
	
	public AbilityManager am = new AbilityManager(this);
	public AfkManager afkm = new AfkManager(this);
	public MapManager mapm;
	public ProtocolManager prm; //BPPlayer-520, this-181 a 80, PlayerManager-355
	public EventManager evtm;
	public boolean successfullyEnabled;
	public TopKillsManager topKill;
	
	@Override
	public void onEnable()
	{
		if(Licence.isAllowed()) {
			instance = this;
			prm = ProtocolLibrary.getProtocolManager();
			MapManager.setup();
			Clan.loadClans();
			config = Configuration.load();
			
			if(config.getStorageType() == StorageType.MYSQL)
				mySQL = config.connectToMySQL();
				
			if (getConfig() != null) {
				saveDefaultConfig();
			}
				
			BPPlayer.updateTable(mySQL);
			Language.loadLanguage(PLUGIN_NAME, config.getLanguageFileName());
			config.getRandomShop().build();
			ChatManager.loadStrings();
			InventoryMenuManager.initialize();
			GameManager.loadGames();
			GameManager.startPlayableGames();
			redirectCommands();
			StatisticsManager.startLoop();
			//TODO : topKill = new TopKillsManager(config);
			registerListeners();
			afkm.startLoop();
			VIPManager.startLoops();
			LobbyInfoManager.startLoop();
			setEventManager();
			DoubleMoneyManager.update();
			DoubleMoneyManager.startBoostLoop();
			getServer().clearRecipes();
			World world = config.getLobbyLocation().getWorld();
			world.setStorm(false);
			world.setThundering(false);
			world.setWeatherDuration(1000000000);
			successfullyEnabled = true;
			return;
		} else {
			successfullyEnabled = false;
			System.out.println("#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#");
			System.out.println("Není licence na spuštění Breakpointu ");
			System.out.println("#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#");
		}
		//TODO: getServer().getPluginManager().registerEvents(new BanListener(), this);
	}

	@Override
	public void onDisable()
	{
		if (!successfullyEnabled)
			return;
		
		trySave();
		kickPlayers();
		
		//topKill.DeleteAndDespawn();
		
		if(evtm != null)
			evtm.save();
		
		getServer().getScheduler().cancelTasks(this);
		instance = null;
		config = null;
	}
	
	public void save() throws IOException
	{
		BPPlayer.saveOnlinePlayersData();
		Clan.saveClans();
		config.save();
		GameManager.saveGames();
	}
	
	public void trySave()
	{
		try
		{
			save();
		}
		catch(IOException e)
		{
			warn("Error when saving Breakpoint data: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void redirectCommands()
	{
		Server server = getServer();
		
		server.getPluginCommand("bp").setExecutor(new BPCommandExecutor(this));
		server.getPluginCommand("helpop").setExecutor(new HelpOPCommandExecutor());
		server.getPluginCommand("clan").setExecutor(new ClanCommandExecutor());
		server.getPluginCommand("achievements").setExecutor(new AchievementsCommandExecutor());
		server.getPluginCommand("top").setExecutor(new TopCommandExecutor());
		server.getPluginCommand("topclans").setExecutor(new TopClansCommandExecutor());
		server.getPluginCommand("rank").setExecutor(new RankCommandExecutor());
		server.getPluginCommand("gm").setExecutor(new GMCommandExecutor());
		server.getPluginCommand("skull").setExecutor(new SkullCommandExecutor());
		server.getPluginCommand("fly").setExecutor(new FlyCommandExecutor());
		server.getPluginCommand("cw").setExecutor(new CWCommandExecutor());
	}

	public void registerListeners()
	{
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(new PlayerInteractListener(this), this);
		pm.registerEvents(new PlayerConnectionListener(this), this);
		pm.registerEvents(new PVPListener(this), this);
		pm.registerEvents(new ChatListener(), this);
		pm.registerEvents(new PlayerInventoryListener(this), this);
		
	//	if(NametagEditManager.isLoaded())
	//		pm.registerEvents(new TagAPIListener(this), this);
		
		// Disable enchantments
		prm./*getAsynchronousManager().registerAsyncHandler*/addPacketListener(new PacketAdapter(this, PacketType.Play.Server.ENTITY_EQUIPMENT) {
			@Override
			public void onPacketSending(PacketEvent event)
			{
				Player player = event.getPlayer();
				
				if(player == null)
					return;
				
				World world = player.getWorld();
				PacketContainer packet = event.getPacket();
				Entity entity = packet.getEntityModifier(world).read(0);
				
				if (entity instanceof Player)
				{
					Player viewed = (Player) entity;
					String viewedName = viewed.getName();
					String playerName = player.getName();
					Clan viewedClan = Clan.getByPlayer(viewedName);
					Clan playerClan = Clan.getByPlayer(playerName);
					if (viewedClan != null && playerClan != null)
						if (viewedClan.equals(playerClan))
							return;
				}
				
				ItemStack stack = packet.getItemModifier().read(0);
				
				if (stack != null)
				{
					Set<Enchantment> encs = stack.getEnchantments().keySet();
					for (Enchantment enc : encs)
						stack.removeEnchantment(enc);
				}
			}
		});
		
		prm./*getAsynchronousManager().registerAsyncHandler*/addPacketListener(new PacketAdapter(this, PacketType.Play.Server.WINDOW_ITEMS) {
			@Override
			public void onPacketSending(PacketEvent event)
			{
				Player player = event.getPlayer();
				BPPlayer bpPlayer = BPPlayer.get(player);
				
				if(bpPlayer == null || !bpPlayer.isInGame())
					return;
				
				Settings settings = bpPlayer.getSettings();
				
				if(settings.hasShowEnchantments())
					return;
				
				PacketContainer packet = event.getPacket();
				ItemStack[] stacks = packet.getItemArrayModifier().read(0);
				
				if(stacks != null)
					for(ItemStack stack : stacks)
						if(stack != null)
							removeEnchantments(stack);
			}
		});
		
		prm./*getAsynchronousManager().registerAsyncHandler*/addPacketListener(new PacketAdapter(this, PacketType.Play.Server.SET_SLOT) {
			@Override
			public void onPacketSending(PacketEvent event)
			{
				Player player = event.getPlayer();
				BPPlayer bpPlayer = BPPlayer.get(player);
				
				if(bpPlayer == null || !bpPlayer.isInGame())
					return;
				
				Settings settings = bpPlayer.getSettings();
				
				if(settings.hasShowEnchantments())
					return;
				
				PacketContainer packet = event.getPacket();
				ItemStack stack = packet.getItemModifier().read(0);
				
				if(stack != null)
					removeEnchantments(stack);
			}
		});
	}
	
	private static void removeEnchantments(ItemStack stack)
	{
		Map<Enchantment, Integer> entries = stack.getEnchantments();
		
		if(entries == null || entries.size() <= 0)
			return;
		
		ItemMeta im = stack.getItemMeta();
		List<String> lore = im.hasLore() ? im.getLore() : new LinkedList<String>();
		
		for(Entry<Enchantment, Integer> entry : entries.entrySet())
		{
			Enchantment type = entry.getKey();
			Integer level = entry.getValue();
			
			im.removeEnchant(type);
			lore.add(ChatColor.GRAY + type.getName() + " " + level);
		}
		
		im.setLore(lore);
		stack.setItemMeta(im);
	}

	public static void info(String string)
	{
		Bukkit.getConsoleSender().sendMessage("[Breakpoint] " + string);
	}

	public static void warn(String string)
	{
		Bukkit.getConsoleSender().sendMessage("[Breakpoint] [Warning] " + string);
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.hasPermission("Breakpoint.receiveWarnings"))
				player.sendMessage(MessageType.CHAT_BREAKPOINT.getTranslation().getValue() + ChatColor.RED + " [Warning] " + string);
	}

	public static void broadcast(String string, boolean prefix)
	{
		for (Player player : Bukkit.getOnlinePlayers())
			player.sendMessage((prefix ? MessageType.CHAT_BREAKPOINT.getTranslation().getValue() : ChatColor.YELLOW) + " " + string);
		
		Bukkit.getConsoleSender().sendMessage("[Breakpoint] [Broadcast] " + string);
	}

	public static void broadcast(String string)
	{
		broadcast(string, false);
	}
	
	public static void clearChat()
	{
		for (Player player : Bukkit.getOnlinePlayers())
			for (int i = 0; i < 10; i++)
				player.sendMessage("");
	}

	public void clearChat(Player player)
	{
		for (int i = 0; i < 10; i++)
			player.sendMessage("");
	}
	
	public void kickPlayers()
	{
		String msg = MessageType.CHAT_BREAKPOINT.getTranslation().getValue() + " " + MessageType.OTHER_RESTART.getTranslation().getValue();
		
		for (Player player : Bukkit.getOnlinePlayers())
			player.kickPlayer(msg);
	}
	
	public void setEventManager()
	{
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		
		if(month == Calendar.DECEMBER && dayOfMonth <= AdventManager.LAST_DAY)
			evtm = AdventManager.load(year);
	}
	
	public boolean hasEvent()
	{
		return evtm != null;
	}
	
	public EventManager getEventManager()
	{
		return evtm;
	}

	public static Breakpoint getInstance()
	{
		return instance;
	}

	public static Configuration getBreakpointConfig()
	{
		return config;
	}

	public static FruitSQL getMySQL()
	{
		return mySQL;
	}

	public static boolean hasMySQL()
	{
		return mySQL != null;
	}
}
