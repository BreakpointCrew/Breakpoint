package cz.GravelCZLP.Breakpoint;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
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

import cz.GravelCZLP.Breakpoint.hooks.ClientStatsHooks;
import cz.GravelCZLP.Breakpoint.hooks.NametagAPIHooks;
import cz.GravelCZLP.Breakpoint.hooks.VaultHooks;
import cz.GravelCZLP.Breakpoint.language.Language;
import cz.GravelCZLP.Breakpoint.language.MessageType;
import cz.GravelCZLP.Breakpoint.listeners.BanListener;
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
import cz.GravelCZLP.Breakpoint.managers.VIPManager;
import cz.GravelCZLP.Breakpoint.managers.commands.AchievementsCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.commands.BPCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.commands.BreakpointCommand;
import cz.GravelCZLP.Breakpoint.managers.commands.CWCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.commands.ClanCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.commands.FlyCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.commands.GMCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.commands.HelpOPCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.commands.RankCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.commands.ReportCommand;
import cz.GravelCZLP.Breakpoint.managers.commands.ShopCommand;
import cz.GravelCZLP.Breakpoint.managers.commands.SkullCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.commands.TopClansCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.commands.TopCommandExecutor;
import cz.GravelCZLP.Breakpoint.managers.events.EventManager;
import cz.GravelCZLP.Breakpoint.managers.events.advent.AdventManager;
import cz.GravelCZLP.Breakpoint.maps.MapManager;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import cz.GravelCZLP.Breakpoint.players.Settings;
import cz.GravelCZLP.Breakpoint.players.clans.Clan;
import cz.GravelCZLP.BreakpointInfo.Main;
import me.limeth.storageAPI.StorageType;
import net.minecraft.server.v1_11_R1.MinecraftServer;

public class Breakpoint extends JavaPlugin {
	private static Breakpoint instance;
	private static Configuration config;
	private static FruitSQL mySQL;
	public static final String PLUGIN_NAME = "Breakpoint";

	public AbilityManager am = new AbilityManager(this);
	public AfkManager afkm = new AfkManager(this);
	public MapManager mapm;
	public static BreakpointCommand externalExceturorsHandler;
	public ProtocolManager prm; // BPPlayer-520, this-181 a 80,
								// PlayerManager-355
	public EventManager evtm;
	public boolean successfullyEnabled;

	private Main data = null;
	
	public VaultHooks vaultHook;
	public ClientStatsHooks clientStatsHook;
	public NametagAPIHooks nameTagAPIHook;
	
	public long timeBoot;
	
	private boolean canEnable = false;
	
	@Override
	public void onEnable() {
		canEnable = Licence.isAllowed();
		if (canEnable) {
			instance = this;
			this.prm = ProtocolLibrary.getProtocolManager();
			mapm = new MapManager();
			mapm.setup();
			Clan.loadClans();

			if (Configuration.getFile().exists()) {
				config = Configuration.load();
			} else {
				File f = Configuration.getFile();
				if (f.isDirectory()) {
					f.delete();
				}
				if (!f.exists()) {
					try {
						f.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				config = Configuration.load();
			}
			if (config.getStorageType() == StorageType.MYSQL) {
				mySQL = config.connectToMySQL();
			}
			externalExceturorsHandler = new BreakpointCommand();
			
			BPPlayer.updateTable(mySQL);
			Language.loadLanguage(PLUGIN_NAME, config.getLanguageFileName());
			config.getRandomShop().build();
			ChatManager.loadStrings();
			InventoryMenuManager.initialize();
			GameManager.loadGames();
			GameManager.startPlayableGames();
			redirectCommands();
			StatisticsManager.startLoop();
			registerListeners();
			this.afkm.startLoop();
			VIPManager.startLoops();
			LobbyInfoManager.startLoop();
			setEventManager();
			DoubleMoneyManager.update();
			DoubleMoneyManager.startBoostLoop();
			StatisticsManager.updateStatistics();
			
			vaultHook = VaultHooks.hook();
			clientStatsHook = ClientStatsHooks.hook();
			nameTagAPIHook = NametagAPIHooks.hook();
			
			cheatSlots(100);
			
			this.data = new Main(this);

			try {
				this.data.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for (World w : Bukkit.getWorlds()) {
				List<Entity> entites = w.getEntities();
				for (Entity e : entites) {
					if (e instanceof Item) {
						Item i = (Item) e;
						if (i.getItemStack().getType() == Material.SPECKLED_MELON) {
							e.remove();
						}
					}
				}
			}
			
			getServer().clearRecipes();
			World world = config.getLobbyLocation().getWorld();
			world.setStorm(false);
			world.setThundering(false);
			world.setWeatherDuration(1000000000);
			
			this.successfullyEnabled = true;
			
			this.timeBoot = System.currentTimeMillis();
			
			return;
		} else {
			this.successfullyEnabled = false;
			ConsoleCommandSender sender = Bukkit.getConsoleSender(); 
			sender.sendMessage(ChatColor.RED + "  #-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#");
			sender.sendMessage(ChatColor.RED + " # Není licence na spuštění Breakpointu #");
			sender.sendMessage(ChatColor.RED + "#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#");
			getServer().getPluginManager().registerEvents(new BanListener(), this);
		}
	}
	
	public void reload() {
		if (!this.successfullyEnabled) {
			return;
		}
		
		trySave();
		
		this.data.stop();
		
		if (this.evtm != null) {
			this.evtm.save();
		}
		
		getServer().getScheduler().cancelTasks(this);
		
		instance = null;
		config = null;
		onEnable();
	}
	
	@Override
	public void onDisable() {
		if (!this.successfullyEnabled) {
			return;
		}

		trySave();
		kickPlayers();

		this.data.stop();

		if (this.evtm != null) {
			this.evtm.save();
		}

		getServer().getScheduler().cancelTasks(this);
		instance = null;
		config = null;
	}
	
	public void save() throws IOException {
		BPPlayer.saveOnlinePlayersData();
		Clan.saveClans();
		config.save();
		GameManager.saveGames();
	}

	public void trySave() {
		try {
			save();
		} catch (IOException e) {
			warn("Error when saving Breakpoint data: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void redirectCommands() {
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
		server.getPluginCommand("shop").setExecutor(new ShopCommand());
	}
	
	public void registerListeners() {
		PluginManager pm = getServer().getPluginManager();

		pm.registerEvents(new PlayerInteractListener(this), this);
		pm.registerEvents(new PlayerConnectionListener(this), this);
		pm.registerEvents(new PVPListener(this), this);
		pm.registerEvents(new ChatListener(), this);
		pm.registerEvents(new PlayerInventoryListener(this), this);

		ReportCommand rc = new ReportCommand();
		pm.registerEvents(rc, this);
		
		getServer().getPluginCommand("report").setExecutor(rc);
		
		// if(NametagEditManager.isLoaded())
		// pm.registerEvents(new TagAPIListener(this), this);

		// Disable enchantments
		this.prm./* getAsynchronousManager().registerAsyncHandler */addPacketListener(
				new PacketAdapter(this, PacketType.Play.Server.ENTITY_EQUIPMENT) {
					@Override
					public void onPacketSending(PacketEvent event) {
						Player player = event.getPlayer();

						if (player == null) {
							return;
						}

						World world = player.getWorld();
						PacketContainer packet = event.getPacket();
						Entity entity = packet.getEntityModifier(world).read(0);

						if (entity instanceof Player) {
							Player viewed = (Player) entity;
							String viewedName = viewed.getName();
							String playerName = player.getName();
							Clan viewedClan = Clan.getByPlayer(viewedName);
							Clan playerClan = Clan.getByPlayer(playerName);
							if (viewedClan != null && playerClan != null) {
								if (viewedClan.equals(playerClan)) {
									return;
								}
							}
						}

						ItemStack stack = packet.getItemModifier().read(0);

						if (stack != null) {
							Set<Enchantment> encs = stack.getEnchantments().keySet();
							for (Enchantment enc : encs) {
								stack.removeEnchantment(enc);
							}
						}
					}
				});

		this.prm./* getAsynchronousManager().registerAsyncHandler */addPacketListener(
				new PacketAdapter(this, PacketType.Play.Server.WINDOW_ITEMS) {
					@Override
					public void onPacketSending(PacketEvent event) {
						Player player = event.getPlayer();
						BPPlayer bpPlayer = BPPlayer.get(player);

						if (bpPlayer == null || !bpPlayer.isInGame()) {
							return;
						}

						Settings settings = bpPlayer.getSettings();

						if (settings.hasShowEnchantments()) {
							return;
						}

						PacketContainer packet = event.getPacket();
						ItemStack[] stacks = packet.getItemArrayModifier().read(0);

						if (stacks != null) {
							for (ItemStack stack : stacks) {
								if (stack != null) {
									removeEnchantments(stack);
								}
							}
						}
					}
				});

		this.prm./* getAsynchronousManager().registerAsyncHandler */addPacketListener(
				new PacketAdapter(this, PacketType.Play.Server.SET_SLOT) {
					@Override
					public void onPacketSending(PacketEvent event) {
						Player player = event.getPlayer();
						BPPlayer bpPlayer = BPPlayer.get(player);

						if (bpPlayer == null || !bpPlayer.isInGame()) {
							return;
						}

						Settings settings = bpPlayer.getSettings();

						if (settings.hasShowEnchantments()) {
							return;
						}

						PacketContainer packet = event.getPacket();
						ItemStack stack = packet.getItemModifier().read(0);

						if (stack != null) {
							removeEnchantments(stack);
						}
					}
				});
	}

	private static void removeEnchantments(ItemStack stack) {
		Map<Enchantment, Integer> entries = stack.getEnchantments();

		if (entries == null || entries.size() <= 0) {
			return;
		}

		ItemMeta im = stack.getItemMeta();
		List<String> lore = im.hasLore() ? im.getLore() : new LinkedList<>();

		for (Entry<Enchantment, Integer> entry : entries.entrySet()) {
			Enchantment type = entry.getKey();
			Integer level = entry.getValue();

			im.removeEnchant(type);
			lore.add(ChatColor.GRAY + type.getName() + " " + level);
		}

		im.setLore(lore);
		stack.setItemMeta(im);
	}

	public static void info(String string) {
		Bukkit.getConsoleSender().sendMessage("[Breakpoint] " + string);
	}

	public static void warn(String string) {
		Bukkit.getConsoleSender().sendMessage("[Breakpoint] [Warning] " + string);
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.hasPermission("Breakpoint.receiveWarnings")) {
				player.sendMessage(MessageType.CHAT_BREAKPOINT.getTranslation().getValue() + ChatColor.RED
						+ " [Warning] " + string);
			}
		}
	}

	public static void broadcast(String string, boolean prefix) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage((prefix ? MessageType.CHAT_BREAKPOINT.getTranslation().getValue() : ChatColor.YELLOW)
					+ " " + string);
		}

		Bukkit.getConsoleSender().sendMessage("[Breakpoint] [Broadcast] " + string);
	}

	public static void broadcast(String string) {
		broadcast(string, false);
	}

	public static void clearChat() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			for (int i = 0; i < 10; i++) {
				player.sendMessage("");
			}
		}
	}

	public void clearChat(Player player) {
		for (int i = 0; i < 10; i++) {
			player.sendMessage("");
		}
	}

	public void kickPlayers() {
		String msg = MessageType.CHAT_BREAKPOINT.getTranslation().getValue() + " "
				+ MessageType.OTHER_RESTART.getTranslation().getValue();

		for (Player player : Bukkit.getOnlinePlayers()) {
			player.kickPlayer(msg);
		}
	}

	public void setEventManager() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

		if (month == Calendar.DECEMBER && dayOfMonth <= AdventManager.LAST_DAY) {
			this.evtm = AdventManager.load(year);
		}
	}

	// only for debug
	public void setEventManager(EventManager e) {
		this.evtm = e;
	}
	
	public boolean hasEvent() {
		return this.evtm != null;
	}

	public EventManager getEventManager() {
		return this.evtm;
	}

	public MapManager getMapManager() {
		return mapm;
	}
	
	public static Breakpoint getInstance() {
		return instance;
	}

	public static Configuration getBreakpointConfig() {
		return config;
	}

	public static FruitSQL getMySQL() {
		return mySQL;
	}

	public static boolean hasMySQL() {
		return mySQL != null;
	}
	public static BreakpointCommand getExcternalBPCommandExecutor() {
		return externalExceturorsHandler;
	}
	public Main getAPI() {
		return data;
	}
	public ClientStatsHooks getClientStatsHook() {
		return clientStatsHook;
	}
	public VaultHooks getVaultHooks() {
		return vaultHook;
	}
	public NametagAPIHooks getNametagAPIHook() {
		return nameTagAPIHook;
	}
	
	private void cheatSlots(int amount) {
		MinecraftServer ms = ((CraftServer) getServer()).getServer();
		try {
			Field playerListField = ms.getClass().getField("v");
			playerListField.setAccessible(true);
			Field playerCount = playerListField.getClass().getField("maxPlayers");
			playerCount.setAccessible(true);
			playerCount.set(playerCount, amount);
		} catch (Exception e ) {
			getLogger().log(Level.WARNING, "Failed to cheat Minecraft server slots", e);
			e.printStackTrace();
		}
	}
	
	public static String getVersion() {
		return "5.1.0";
	}
}
