package cz.GravelCZLP.Breakpoint.managers.events.advent;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.equipment.BPBlock;
import cz.GravelCZLP.Breakpoint.equipment.BPEquipment;
import cz.GravelCZLP.Breakpoint.language.MessageType;
import cz.GravelCZLP.Breakpoint.managers.InventoryMenuManager;
import cz.GravelCZLP.Breakpoint.managers.ShopManager;
import cz.GravelCZLP.Breakpoint.managers.events.EventManager;
import cz.GravelCZLP.Breakpoint.maps.MapManager;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;

public class AdventManager implements EventManager {
	// {{STATIC
	public static final int GIFT_MINUTES = 60 * 2;
	public static final int LAST_DAY = 24;

	public static AdventManager load(int year) {
		File file = getFile(year);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		ArrayList<AdventGift> gifts = new ArrayList<>();
		ConfigurationSection blocksSection = config.getConfigurationSection("blocks");
		Set<String> rawBlocks = blocksSection != null ? blocksSection.getKeys(false) : new HashSet<>();
		int i = 0;

		for (String rawBlock : rawBlocks) {
			try {
				if (i >= LAST_DAY) {
					break;
				}

				BPBlock block = (BPBlock) BPEquipment.deserialize(rawBlock.split(","));
				List<String> giftedTo = config.getStringList("blocks." + rawBlock);
				AdventGift gift = new AdventGift(block, giftedTo);

				gifts.add(gift);
				i++;
			} catch (Exception e) {
				Breakpoint.warn("Error when loading Advent block: " + rawBlock + " - " + e.getMessage());
				e.printStackTrace();
			}
		}

		while (i < LAST_DAY) {
			gifts.add(null);
			i++;
		}

		return new AdventManager(year, gifts);
	}

	public static File getFile(int year) {
		return new File("plugins/Breakpoint/events/advent_" + year + ".yml");
	}

	public static File getConfigFile() {
		return new File("plugins/Breakpoint/events/advent_config.yml");
	}
	// }}STATIC

	private final int year;
	private final ArrayList<AdventGift> gifts;
	private final short mapId;
	private int dayOfMonth;
	private AdventGift gift;

	public AdventManager(int year, ArrayList<AdventGift> gifts) {
		if (gifts.size() != LAST_DAY) {
			throw new IllegalArgumentException("gifts.size() != " + LAST_DAY + "; gifts.size() == " + gifts.size());
		}

		this.year = year;
		this.gifts = gifts;
		this.mapId = MapManager.getNextFreeId();

		setDayOfMonth();
		fillList();
		setGift();

		@SuppressWarnings("deprecation")
		MapView mapView = Bukkit.getMap(this.mapId);

		if (mapView == null) {
			throw new IllegalArgumentException("Bukkit.getMap(" + this.mapId + ") == null");
		}

		new AdventMapRenderer(this, this.dayOfMonth).set(mapView);
	}

	@Override
	public void save() {
		File file = getFile(this.year);
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

		for (AdventGift gift : this.gifts) {
			BPBlock block = gift.getBlock();
			String rawBlock = block.serialize();
			List<String> giftedTo = gift.getGiftedTo();

			yml.set("blocks." + rawBlock, giftedTo);
		}

		try {
			yml.save(file);
		} catch (Exception e) {
			Breakpoint.warn("Error when saving AdventManager! " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void fillList() {
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(getConfigFile());
		List<String> rawIds = yml.getStringList("blocks");

		for (int i = 0; i < this.gifts.size(); i++) {
			AdventGift gift = this.gifts.get(i);

			if (gift != null) {
				continue;
			}

			Random rnd = new Random(this.year * (i + 1));
			String rawId = rawIds.get(rnd.nextInt(rawIds.size()));
			String[] splitId = rawId.split("\\:");

			try {
				int id = Integer.parseInt(splitId[0]);
				byte data = splitId.length > 1 ? Byte.parseByte(splitId[1]) : 0;
				@SuppressWarnings("deprecation")
				String name = MessageType.EVENT_ADVENT_BLOCKNAME.getTranslation().getValue(this.year, i + 1,
						Material.getMaterial(id).name());
				name = MessageType.EQUIPMENT_BLOCKNAME.getTranslation().getValue(name);
				BPBlock block = new BPBlock(name, GIFT_MINUTES, id, data);

				gift = new AdventGift(block);
				this.gifts.set(i, gift);
			} catch (Exception e) {
				i--;
				Breakpoint.warn("Error when filling advent gift list: " + rawId);
				e.printStackTrace();
			}
		}
	}

	@Override
	public void showLobbyMenu(BPPlayer bpPlayer) {
		Player player = bpPlayer.getPlayer();
		PlayerInventory pi = player.getInventory();
		pi.setItem(2, getCalendarMap(player));
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		BPPlayer bpPlayer = BPPlayer.get(player);

		if (!bpPlayer.isInLobby()) {
			return;
		}

		Action action = event.getAction();

		if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
			return;
		}

		PlayerInventory inv = player.getInventory();
		int slot = inv.getHeldItemSlot();

		if (slot == 2) {
			AdventGift gift = getGift();
			String playerName = player.getName();

			if (!gift.hasEarned(playerName)) {
				if (!bpPlayer.hasSpaceInLobbyInventory()) {
					player.sendMessage(MessageType.EVENT_ADVENT_NOSPACE.getTranslation().getValue());
					return;
				}

				BPBlock block = gift.getBlock();

				boolean b = player.hasPermission("Breakpoint.vipSlots");

				InventoryMenuManager.saveLobbyMenu(bpPlayer);
				ShopManager.processBoughtItem(bpPlayer, block, b);
				InventoryMenuManager.showLobbyMenu(bpPlayer);
				gift.addGiftedTo(playerName);
				player.sendMessage(MessageType.EVENT_ADVENT_EARN.getTranslation().getValue());
			} else {
				player.sendMessage(MessageType.EVENT_ADVENT_ALREADYEARNED.getTranslation().getValue());
			}
		}
	}

	public ItemStack getCalendarMap(Player player) {
		ItemStack is = MapManager.getMap(player, this.mapId);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(MessageType.EVENT_ADVENT_MAP_NAME.getTranslation().getValue());
		is.setItemMeta(im);
		return is;
	}

	public void setGift() {
		this.gift = this.gifts.get(this.dayOfMonth - 1);
	}

	public AdventGift getGift() {
		return this.gift;
	}

	public void setDayOfMonth() {
		this.dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	public ArrayList<AdventGift> getGifts() {
		return this.gifts;
	}

	public int getYear() {
		return this.year;
	}

	public short getMapId() {
		return this.mapId;
	}
}
