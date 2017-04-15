package cz.GravelCZLP.Breakpoint.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.Configuration;
import cz.GravelCZLP.Breakpoint.achievements.Achievement;
import cz.GravelCZLP.Breakpoint.equipment.BPArmor;
import cz.GravelCZLP.Breakpoint.equipment.BPEquipment;
import cz.GravelCZLP.Breakpoint.game.CharacterType;
import cz.GravelCZLP.Breakpoint.language.MessageType;
import cz.GravelCZLP.Breakpoint.managers.InventoryMenuManager;
import cz.GravelCZLP.Breakpoint.perks.Perk;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import cz.GravelCZLP.Breakpoint.players.ServerPosition;
import cz.GravelCZLP.Breakpoint.players.Settings;
import net.minecraft.server.v1_11_R1.NBTTagCompound;

public class PlayerInventoryListener implements Listener {
	Breakpoint plugin;

	public PlayerInventoryListener(Breakpoint p) {
		this.plugin = p;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory inv = event.getInventory();
		InventoryType it = inv.getType();
		if (it == InventoryType.CRAFTING) {
			Player player = (Player) event.getWhoClicked();
			BPPlayer bpPlayer = BPPlayer.get(player);
			SlotType st = event.getSlotType();
			int slotId = event.getRawSlot();
			if (bpPlayer.isInGame()) {
				if (st == SlotType.ARMOR) {
					event.setCancelled(true);
				} else if (slotId >= 9 && slotId <= 35) {
					onIngameMenuClick(event, bpPlayer);
				}
			} else if (bpPlayer.isInLobby()) {
				if (slotId >= 36 && slotId <= 44 || InventoryMenuManager.isLobbyBorder(slotId)) {
					event.setCancelled(true);

					if (slotId == 43) {
						bpPlayer.setAchievementViewTarget(bpPlayer);
						bpPlayer.setAchievementViewPage(0);
						Achievement.showAchievementMenu(bpPlayer);
						InventoryMenuManager.updateInventoryDelayed(player);
						return;
					} else if (slotId == 42) {
						Settings.showSettingsMenu(bpPlayer);
						/*
						 * else if(slotId == 41) Perk.showPerkMenu(bpPlayer);
						 */
					}

					InventoryMenuManager.updateInventoryDelayed(player);
				} else if (slotId == 22) {
					ItemStack cursor = player.getItemOnCursor();
					if (cursor != null) {
						Material mat = cursor.getType();
						if (mat != Material.AIR) {
							String name = mat.name().replaceAll("_", " ").toLowerCase();
							if (cursor.hasItemMeta()) {
								ItemMeta im = cursor.getItemMeta();
								if (im.hasDisplayName()) {
									name = im.getDisplayName();
								}
							}
							player.setItemOnCursor(null);
							InventoryMenuManager.saveLobbyMenu(bpPlayer);
							player.sendMessage(MessageType.MENU_TRASH_USE.getTranslation().getValue(name));
						}
					}
					event.setCancelled(true);
					InventoryMenuManager.updateInventoryDelayed(player);
				} else {
					ServerPosition pos = bpPlayer.getServerPosition();
					boolean b = pos.isSponsor() || pos.isStaff() || pos.isVIP() || pos.isVIPPlus() || pos.isYoutube();

					if (!b) {
						if (InventoryMenuManager.isVipSlot(slotId)) {
							event.setCancelled(true);
							InventoryMenuManager.updateInventoryDelayed(player);
							return;
						}
					}
					if (event.isShiftClick()) {
						if (st != SlotType.ARMOR) {
							PlayerInventory pi = player.getInventory();
							ItemStack is = event.getCurrentItem();
							BPEquipment equipment = BPEquipment.parse(is);
							if (equipment == null) {
								return;
							}
							ItemStack[] armor = pi.getArmorContents();
							if (equipment instanceof BPArmor) {
								event.setCancelled(true);
							} else if (armor[3].getTypeId() != 0) {
								event.setCancelled(true);
							} else {
								event.setCancelled(true);
								armor[3] = is.clone();
								event.setCurrentItem(null);
								pi.setArmorContents(armor);
							}
						} else if (!bpPlayer.hasSpaceInLobbyInventory()) {
							event.setCancelled(true);
							return;
						}
					} else if (st == SlotType.ARMOR) {
						int slot = event.getRawSlot();
						if (slot == 5) {
							ItemStack cursor = event.getCursor();
							if (cursor != null) {
								ItemStack clicked = event.getCurrentItem();
								event.setCursor(clicked);
								event.setCurrentItem(cursor);
								event.setCancelled(true);
							}
						}
					}
				}
			} else {
				event.setCancelled(true);
			}
		} else if (it == InventoryType.CHEST) {
			Player player = (Player) event.getWhoClicked();
			BPPlayer bpPlayer = BPPlayer.get(player);

			onChestMenuClick(event, bpPlayer);
		}
	}

	public void onChestMenuClick(InventoryClickEvent event, BPPlayer bpPlayer) {
		Inventory inv = event.getInventory();

		if (inv == null) {
			return;
		}

		String title = inv.getTitle();

		if (title.equals(Settings.MENU_TITLE)) {
			Settings.onMenuClick(event, bpPlayer);
		} else if (title.equals(Achievement.MENU_TITLE)) {
			Achievement.onMenuClick(event, bpPlayer);
		} else if (title.equals(Perk.MENU_TITLE)) {
			Perk.onMenuClick(event, bpPlayer);
		}
	}

	@SuppressWarnings("deprecation")
	public void onIngameMenuClick(InventoryClickEvent event, BPPlayer bpPlayer) {
		event.setCancelled(true);
		Inventory inv = event.getInventory();
		InventoryHolder holder = inv.getHolder();
		if (!(holder instanceof Player)) {
			return;
		}
		Player player = (Player) holder;
		ItemStack item = event.getCurrentItem();
		Material mat = item.getType();
		if (mat == Material.WOOD_DOOR) {
			bpPlayer.setSingleTeleportLocation(null);
			bpPlayer.setLeaveAfterDeath(true);
			player.sendMessage(MessageType.MENU_LOBBY_USE.getTranslation().getValue());
		} else if (mat == Material.ITEM_FRAME) {
			Configuration config = Breakpoint.getBreakpointConfig();

			bpPlayer.setSingleTeleportLocation(config.getShopLocation().clone());
			bpPlayer.setLeaveAfterDeath(true);
			player.sendMessage(MessageType.MENU_STORE_USE.getTranslation().getValue());
		} else if (mat == Material.SKULL_ITEM) {
			player.setHealth(0.0);
		} else if (mat == Material.NETHER_STAR) {
			Configuration config = Breakpoint.getBreakpointConfig();

			bpPlayer.setSingleTeleportLocation(config.getVipInfoLocation().clone());
			bpPlayer.setLeaveAfterDeath(true);
			player.sendMessage(MessageType.MENU_VIPINFO_USE.getTranslation().getValue());
		} else if (mat == Material.EMERALD) {
			Configuration config = Breakpoint.getBreakpointConfig();

			bpPlayer.setSingleTeleportLocation(config.getMoneyInfoLocation().clone());
			bpPlayer.setLeaveAfterDeath(true);
			player.sendMessage(MessageType.MENU_EMERALDS_USE.getTranslation().getValue());
		} else if (mat == Material.MONSTER_EGG) {
			CharacterType ct = null;

			net.minecraft.server.v1_11_R1.ItemStack nmsIs = CraftItemStack.asNMSCopy(item);

			NBTTagCompound tag = nmsIs.getTag();

			NBTTagCompound idTag = (NBTTagCompound) tag.get("EntityTag");

			EntityType e = null;

			String entity = idTag.getString("id");

			entity = entity.toUpperCase();

			if (entity.contains("LAVASLIME")) {
				entity = "MAGMA_CUBE";
			}
			
			entity = entity.replaceAll("MINECRAFT:", "");
			
			entity = entity.toUpperCase();

			e = EntityType.valueOf(entity);

			ct = CharacterType.getByMonsterEggId(e.getTypeId());
			if (ct != null) {
				String name = ct.getProperName();

				ServerPosition pos = bpPlayer.getServerPosition();
				boolean b = pos.isSponsor() || pos.isStaff() || pos.isVIP() || pos.isVIPPlus() || pos.isYoutube();

				if (ct.requiresVIP() && !b) {
					player.sendMessage(ChatColor.DARK_GRAY + "---");
					player.sendMessage(MessageType.LOBBY_CHARACTER_VIPSONLY.getTranslation().getValue(name));
					player.sendMessage(ChatColor.DARK_GRAY + "---");
					return;
				}
				bpPlayer.setQueueCharacter(ct);
				player.sendMessage(MessageType.LOBBY_CHARACTER_SELECTED.getTranslation().getValue(name));
				player.sendMessage(MessageType.OTHER_CHARACTERRESPAWNINFO.getTranslation().getValue());
			}
		} else if (mat == Material.REDSTONE_COMPARATOR) {
			Settings.showSettingsMenu(bpPlayer);
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		BPPlayer bpPlayer = BPPlayer.get(player);
		if (bpPlayer.isInGame() && event.getItemDrop().getItemStack().getType() == Material.GLASS_BOTTLE) {
			event.getItemDrop().remove();
			return;
		}
		if (!(bpPlayer.getServerPosition().isStaff() && player.getGameMode() == GameMode.CREATIVE)) {
			event.setCancelled(true);
			return;
		}
	}
}
