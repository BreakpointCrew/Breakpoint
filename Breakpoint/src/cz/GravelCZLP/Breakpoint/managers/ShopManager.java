package cz.GravelCZLP.Breakpoint.managers;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import cz.GravelCZLP.Breakpoint.achievements.Achievement;
import cz.GravelCZLP.Breakpoint.equipment.ArmorMerchandiseType;
import cz.GravelCZLP.Breakpoint.equipment.BPArmor;
import cz.GravelCZLP.Breakpoint.equipment.BPEquipment;
import cz.GravelCZLP.Breakpoint.equipment.BPSkull;
import cz.GravelCZLP.Breakpoint.equipment.BPSkull.SkullType;
import cz.GravelCZLP.Breakpoint.language.MessageType;
import cz.GravelCZLP.Breakpoint.perks.Perk;
import cz.GravelCZLP.Breakpoint.perks.PerkType;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import cz.GravelCZLP.Breakpoint.players.LobbyInventory;

public class ShopManager
{
	public static void buyItem(BPPlayer bpPlayer, Sign sign, String[] lines)
	{
		if(isArmorShop(lines))
			buyArmor(bpPlayer, sign, lines);
		else if(isSkullShop(lines))
			buySkull(bpPlayer, sign, lines);
		else if(isPerkShop(lines))
			buyPerk(bpPlayer, sign, lines);
	}

	public static void buySkull(BPPlayer bpPlayer, Sign sign, String[] lines)
	{
		Player player = bpPlayer.getPlayer();
		String typeName = ChatColor.stripColor(lines[1]);
		SkullType skullType = SkullType.parse(typeName);
		boolean vip = skullType == null || skullType.isVip();
		boolean canUse = bpPlayer.isVIP() || bpPlayer.isStaff() || bpPlayer.isSponsor();
		
		if(!vip && canUse)
		{
			Location bLoc = sign.getLocation();
			String nameColored = lines[0];
			int cost = Integer.valueOf(lines[3]);
			int exHours = 1;
			if(bLoc.equals(bpPlayer.getShopItemLocation()))
			{
				int money = bpPlayer.getMoney();
				
				if (money >= cost)
				{
					bpPlayer.addMoney(-cost, false, false);
					BPSkull bpSkull = new BPSkull(skullType != null ? skullType.name() : typeName, exHours * 60);
					InventoryMenuManager.saveLobbyMenu(bpPlayer);
					processBoughtItem(bpPlayer, bpSkull, canUse);
					InventoryMenuManager.showLobbyMenu(bpPlayer);
					bpPlayer.getStatistics().increaseBought();
					Achievement.checkBought(bpPlayer);
					player.sendMessage(MessageType.SHOP_PURCHASE_ARMOR_SUCCESS.getTranslation().getValue(nameColored, cost));
				}
				else
					player.sendMessage(MessageType.SHOP_PURCHASE_NOTENOUGHEMERALDS.getTranslation().getValue());
				
				bpPlayer.setShopItemLocation(null);
			}
			else
			{
				if(!bpPlayer.hasSpaceInLobbyInventory())
				{
					player.sendMessage(MessageType.SHOP_PURCHASE_NOINVENTORYSPACE.getTranslation().getValue());
					return;
				}
				
				bpPlayer.setShopItemLocation(bLoc);
				player.sendMessage(MessageType.SHOP_PURCHASE_ARMOR_QUESTION.getTranslation().getValue(lines[0], cost, exHours));
			}
		}
		else
			player.sendMessage(MessageType.SHOP_PURCHASE_VIPSONLY.getTranslation().getValue());
	}

	public static void buyArmor(BPPlayer bpPlayer, Sign sign, String[] lines)
	{
		Player player = bpPlayer.getPlayer();
		String typeName = ChatColor.stripColor(lines[0]);
		ArmorMerchandiseType amt = ArmorMerchandiseType.parse(typeName);
		boolean canUse = bpPlayer.isVIP() || bpPlayer.isStaff() || bpPlayer.isSponsor();
		
		if (amt != null)
		{
			Location bLoc = sign.getLocation();
			String nameColored = lines[1];
			
			if (bLoc.equals(bpPlayer.getShopItemLocation()))
			{
				int colorId = Integer.decode("0x" + sign.getLine(2));
				String[] rawData = lines[3].split(" : ");
				int cost = Integer.parseInt(rawData[0]);
				int exMins = Integer.parseInt(rawData[1]);
				int money = bpPlayer.getMoney();
				
				if (money >= cost)
				{
					int typeId = amt.ordinal();
					BPArmor psArmor = new BPArmor(typeId, colorId, nameColored, exMins);
					
					bpPlayer.addMoney(-cost, false, false);
					InventoryMenuManager.saveLobbyMenu(bpPlayer);
					processBoughtItem(bpPlayer, psArmor, canUse);
					InventoryMenuManager.showLobbyMenu(bpPlayer);
					bpPlayer.getStatistics().increaseBought();
					Achievement.checkBought(bpPlayer);
					player.sendMessage(MessageType.SHOP_PURCHASE_ARMOR_SUCCESS.getTranslation().getValue(nameColored, cost));
				}
				else
					player.sendMessage(MessageType.SHOP_PURCHASE_NOTENOUGHEMERALDS.getTranslation().getValue());
				
				bpPlayer.setShopItemLocation(null);
			}
			else
			{
				String name = ChatColor.stripColor(nameColored);
				if (name.startsWith("VIP") && !canUse)
				{
					player.sendMessage(MessageType.SHOP_PURCHASE_VIPSONLY.getTranslation().getValue());
					return;
				}
				if (!bpPlayer.hasSpaceInLobbyInventory())
				{
					player.sendMessage(MessageType.SHOP_PURCHASE_NOINVENTORYSPACE.getTranslation().getValue());
					return;
				}
				String[] rawData = lines[3].split(" : ");
				int cost = Integer.parseInt(rawData[0]);
				int exHours = Integer.parseInt(rawData[1]) / 60;
				player.sendMessage(MessageType.SHOP_PURCHASE_ARMOR_QUESTION.getTranslation().getValue(lines[0], cost, exHours));
				bpPlayer.setShopItemLocation(bLoc);
			}
		}
	}

	public static void buyPerk(BPPlayer bpPlayer, Sign sign, String[] lines)
	{
		Player player = bpPlayer.getPlayer();
		String typeName = ChatColor.stripColor(lines[1]);
		PerkType type = PerkType.parse(typeName);
		
		if(type != null)
		{
			Location bLoc = sign.getLocation();
			
			if (bLoc.equals(bpPlayer.getShopItemLocation()))
			{
				int cost = Integer.parseInt(lines[3]);
				int money = bpPlayer.getMoney();
				
				if (money >= cost)
				{
					Perk perk = bpPlayer.getOrAddPerk(type);
					int lives = Integer.parseInt(lines[2]);
					
					perk.increaseLivesLeft(lives);
					bpPlayer.addMoney(-cost, false, false);
					bpPlayer.getStatistics().increaseBought();
					Achievement.checkBought(bpPlayer);
					player.sendMessage(MessageType.SHOP_PURCHASE_PERK_SUCCESS.getTranslation().getValue(typeName, cost));
				}
				else
					player.sendMessage(MessageType.SHOP_PURCHASE_NOTENOUGHEMERALDS.getTranslation().getValue());
				
				bpPlayer.setShopItemLocation(null);
			}
			else
			{
				int cost = Integer.parseInt(lines[3]);
				int lives = Integer.parseInt(lines[2]);
				player.sendMessage(MessageType.SHOP_PURCHASE_PERK_QUESTION.getTranslation().getValue(lines[1], cost, lives));
				bpPlayer.setShopItemLocation(bLoc);
			}
		}
	}

	public static void processBoughtItem(BPPlayer bpPlayer, BPEquipment item, boolean isVIP)
	{
		LobbyInventory inv = bpPlayer.getLobbyInventory();
		BPEquipment[] contents = inv.getContents();
		int size = isVIP ? 24 : 12;
		for (int i = 0; i < size; i++)
			if (contents[4 + i] == null)
			{
				contents[4 + i] = item;
				break;
			}
		
		inv.setContents(contents);
	}

	@SuppressWarnings("deprecation")
	public static void buildArmorShop(Location loc, int facing, String color, int[] cost, int[] time, String name)
	{
		int signData = getSignData(facing);
		int[] direction = getDirection(facing);
		ArmorMerchandiseType[] values = ArmorMerchandiseType.values();
		for (int typeId = 0; typeId < 4; typeId++)
		{
			ArmorMerchandiseType amt = values[3 - typeId];
			
			for (int timeId = 0; timeId < time.length; timeId++)
				try
				{
					World world = loc.getWorld();
					int x = loc.getBlockX() + direction[0] * typeId;
					int y = loc.getBlockY() + time.length - timeId - 1;
					int z = loc.getBlockZ() + direction[1] * typeId;
					Block block = world.getBlockAt(x, y, z);
					block.setTypeIdAndData(68, (byte) signData, true);
					Sign sign = (Sign) block.getState();
					sign.setLine(0, ChatColor.DARK_GRAY + "" + ChatColor.BOLD + amt.getTranslated());
					sign.setLine(1, ChatColor.translateAlternateColorCodes('&', name));
					sign.setLine(2, color);
					sign.setLine(3, (cost[timeId] * amt.getMaterialAmount()) + " : " + time[timeId]);
					sign.update(true);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
		}
	}

	@SuppressWarnings("deprecation")
	public static void buildSkullShop(Location loc, int facing, String name, SkullType type, BlockFace faceing) {
		int signData = getSignData(facing);
		try {
			World w = loc.getWorld();
			Block b = w.getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			b.setTypeIdAndData(68, (byte) signData, true);
			Sign s = (Sign) b.getState();
			if (name == null || name == "") {
				s.setLine(0, WordUtils.capitalize(type.name()));
			} else {
				s.setLine(0, ChatColor.translateAlternateColorCodes('&', name));	
			}
			s.setLine(1, type.name().toUpperCase());
			s.setLine(2, MessageType.SHOP_ITEM_SKULL_LABEL.getTranslation().getValue());
			s.setLine(3, String.valueOf(type.getCost()));	
			
			ItemFrame itemFrame = (ItemFrame) w.spawn(loc, ItemFrame.class);
			BPSkull skull = new BPSkull(s.getLine(0), 1 * 60);
			itemFrame.setItem(skull.getItemStack());
			itemFrame.setFacingDirection(faceing);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static int getSignData(int facing)
	{
		if (facing == 0)
			return 2;
		else
			if (facing == 1)
				return 5;
			else
				if (facing == 2)
					return 3;
				else
					if (facing == 3)
						return 4;
		return -1;
	}

	private static int[] getDirection(int facing)
	{
		switch(facing)
		{
			case 0: return new int[] { -1, 0 };
			case 1: return new int[] { 0, -1 };
			case 2: return new int[] { 1, 0 };
			case 3: return new int[] { 0, 1 };
			default: return new int[2];
		}
	}

	public static boolean isShop(String[] lines)
	{
		return isArmorShop(lines) || isSkullShop(lines) || isPerkShop(lines);
	}

	public static boolean isArmorShop(String[] lines)
	{
		String withoutColors = ChatColor.stripColor(lines[0]);
		return ArmorMerchandiseType.parse(withoutColors) != null;
	}

	public static boolean isSkullShop(String[] lines)
	{
		return ChatColor.stripColor(lines[2]).equals(MessageType.SHOP_ITEM_SKULL_LABEL.getTranslation().getValue());
	}

	public static boolean isPerkShop(String[] lines)
	{
		String label = ChatColor.stripColor(lines[0]);
		
		if(!label.equals(MessageType.SHOP_ITEM_PERK_LABEL.getTranslation().getValue()))
			return false;
		
		PerkType type = PerkType.parse(ChatColor.stripColor(lines[1]));
		
		if(type == null)
			return false;
		
		return true;
	}
}
