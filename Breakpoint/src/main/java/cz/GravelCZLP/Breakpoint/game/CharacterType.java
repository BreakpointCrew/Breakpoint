package cz.GravelCZLP.Breakpoint.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import cz.GravelCZLP.Breakpoint.language.MessageType;
import cz.GravelCZLP.Breakpoint.managers.PlayerManager;
import net.minecraft.server.v1_10_R1.NBTTagCompound;

public enum CharacterType {
	SWORDSMAN(false, MessageType.CHARACTER_SWORDSMAN, EntityType.COW),
	
	KNIGHT(false, MessageType.CHARACTER_KNIGHT,EntityType.BAT, new PotionEffect(PotionEffectType.ABSORPTION, 2147483647, 0, true)),
	
	ARCHER(false,MessageType.CHARACTER_ARCHER, EntityType.VILLAGER),
	
	CHEMIST(false, MessageType.CHARACTER_CHEMIST,EntityType.WITCH,new PotionEffect(PotionEffectType.REGENERATION, 2147483647, 0, true)),
	
	CULTIST(true,MessageType.CHARACTER_CULTIST, EntityType.MAGMA_CUBE, ChatColor.DARK_PURPLE,new PotionEffect(PotionEffectType.ABSORPTION, 2147483647, 4, true)),
	
	PYRO(true,MessageType.CHARACTER_PYRO, EntityType.BLAZE, ChatColor.GOLD),
	
	NINJA(true,MessageType.CHARACTER_NINJA, EntityType.ENDERMAN,ChatColor.LIGHT_PURPLE,new PotionEffect(PotionEffectType.SPEED, 2147483647, 1,true)),
	
	HEAVY(true, MessageType.CHARACTER_HEAVY,EntityType.SPIDER, ChatColor.DARK_RED,new PotionEffect(PotionEffectType.SLOW, 2147483647,0, true));
	
	private final PotionEffect[] effects;
	private final boolean requiresVIP;
	private final MessageType nameMessageType;
	private final EntityType entityType;
	private final ChatColor chatColor;

	private CharacterType(boolean requiresVIP, MessageType nameMessageType, EntityType type, ChatColor chatColor,
			PotionEffect... effects) {
		this.effects = effects;
		this.requiresVIP = requiresVIP;
		this.entityType = type;
		this.nameMessageType = nameMessageType;
		this.chatColor = chatColor;
	}

	private CharacterType(boolean requiresVIP, MessageType nameMessageType, EntityType type, PotionEffect... effects) {
		this(requiresVIP, nameMessageType, type, ChatColor.WHITE, effects);
	}

	public void applyEffects(Player player) {
		for (PotionEffect effect : getEffects()) {
			player.addPotionEffect(effect, true);
		}
	}

	@SuppressWarnings("deprecation")
	public void equipPlayer(Player player) {
		PlayerInventory pi = player.getInventory();
		switch (this) {
		case SWORDSMAN: {
			PlayerManager.enchantArmor(pi, new Object[] { Enchantment.PROTECTION_ENVIRONMENTAL, 1 });
			pi.addItem(new ItemStack(Material.DIAMOND_SWORD));
			PlayerManager.addRegenerationMatter(pi, 2);
			break;
		}
		case KNIGHT: {
			PlayerManager.enchantArmor(pi, new Object[] { Enchantment.PROTECTION_ENVIRONMENTAL, 2 });
			pi.addItem(new ItemStack(Material.STONE_SWORD));
			PlayerManager.addRegenerationMatter(pi, 2);
			break;
		}
		case ARCHER: {
			PlayerManager.enchantArmor(pi, new Object[] { Enchantment.PROTECTION_PROJECTILE, 2 });
			ItemStack infiniteBow = new ItemStack(Material.BOW);
			infiniteBow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
			pi.addItem(infiniteBow);
			pi.addItem(new ItemStack(Material.WOOD_SWORD));
			PlayerManager.addRegenerationMatter(pi, 2);
			pi.addItem(new ItemStack(Material.ARROW, 64, (short) 0));
			break;
		}
		case CHEMIST: {
			PlayerManager.enchantArmor(pi, new Object[] { Enchantment.PROTECTION_FIRE, 2 },
					new Object[] { Enchantment.OXYGEN, 1 });
			ItemStack harm = new ItemStack(373, 1, (short) 16396);
			ItemStack poison = new ItemStack(373, 1, (short) 16388);
			ItemStack blindness = new ItemStack(373, 1, (short) 16443);
			int sekundyPoison = 15;
			PotionMeta poisonPM = (PotionMeta) poison.getItemMeta();
			poisonPM.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 20 * sekundyPoison, 0), false);
			poison.setItemMeta(poisonPM);
			int sekundyBlindness = 5;
			PotionMeta blindnessPM = (PotionMeta) blindness.getItemMeta();
			blindnessPM.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * sekundyBlindness, 0), false);
			blindnessPM.addCustomEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * sekundyBlindness, 0), false);
			blindness.setItemMeta(blindnessPM);
			pi.addItem(new ItemStack(Material.WOOD_SWORD));
			pi.addItem(poison);
			pi.addItem(harm);
			pi.addItem(blindness);
			PlayerManager.addRegenerationMatter(pi, 2);
			break;
		}
		case CULTIST: {
			PlayerManager.enchantArmor(pi, new Object[] { Enchantment.PROTECTION_EXPLOSIONS, 2 });
			ItemStack stick = new ItemStack(Material.STICK);
			ItemStack blazeRod = new ItemStack(Material.BLAZE_ROD);
			ItemStack feather = new ItemStack(Material.FEATHER);
			feather.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 1);
			pi.addItem(stick);
			pi.addItem(blazeRod);
			pi.addItem(feather);
			break;
		}
		case PYRO: {
			PlayerManager.enchantArmor(pi, new Object[] { Enchantment.PROTECTION_FIRE, 3 },
					new Object[] { Enchantment.PROTECTION_ENVIRONMENTAL, 1 });
			ItemStack fireBow = new ItemStack(Material.BOW);
			fireBow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
			fireBow.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 1);
			ItemStack fireAxe = new ItemStack(Material.STONE_AXE);
			fireAxe.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
			fireAxe.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
			pi.addItem(fireAxe);
			pi.addItem(fireBow);
			PlayerManager.addRegenerationMatter(pi, 3);
			pi.addItem(new ItemStack(Material.ARROW, 64, (short) 0));
			break;
		}
		case NINJA: {
			PlayerManager.enchantArmor(pi, new Object[] { Enchantment.PROTECTION_FALL, 2 });
			ItemStack sword = new ItemStack(Material.GOLD_SWORD);
			sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
			sword.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
			pi.addItem(sword);
			PlayerManager.addRegenerationMatter(pi, 2);
			pi.addItem(new ItemStack(373, 1, (short) 8198));
			pi.addItem(new ItemStack(Material.ENDER_PEARL));
			break;
		}
		case HEAVY: {
			PlayerManager.enchantArmor(pi, new Object[] { Enchantment.PROTECTION_ENVIRONMENTAL, 3 });
			pi.addItem(new ItemStack(Material.IRON_SWORD));
			PlayerManager.addRegenerationMatter(pi, 2);
			break;
		}
		}
		player.updateInventory();
	}

	public boolean requiresVIP() {
		return this.requiresVIP;
	}

	@SuppressWarnings("deprecation")
	public static CharacterType getByMonsterEggId(short id) {
		for (CharacterType t : CharacterType.values()) {
			if (t.getEggType().getTypeId() == id) {
				return t;
			}
		}
		return null;
	}

	public String getProperName() {
		return this.nameMessageType.getTranslation().getValue();
	}

	public MessageType getNameMessageType() {
		return this.nameMessageType;
	}

	@SuppressWarnings("deprecation")
	public ItemStack getEgg() {
		ItemStack egg = new ItemStack(Material.MONSTER_EGG, 1);
		ItemMeta im = egg.getItemMeta();
		im.setDisplayName(ChatColor.ITALIC + "" + this.chatColor + getProperName());
		List<String> lore = new ArrayList<>();
		if (requiresVIP()) {
			im.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
			MessageType.MENU_EGG_VIPDESC.getTranslation().addValuesToList(lore);
		}

		MessageType.MENU_EGG_DESC.getTranslation().addValuesToList(lore);
		;
		im.setLore(lore);
		egg.setItemMeta(im);

		net.minecraft.server.v1_10_R1.ItemStack nmsIs = CraftItemStack.asNMSCopy(egg);

		NBTTagCompound idTag = new NBTTagCompound();

		idTag.setString("id", getEggType().getName());

		NBTTagCompound tag = nmsIs.hasTag() ? nmsIs.getTag() : new NBTTagCompound();

		tag.set("EntityTag", idTag);

		nmsIs.setTag(tag);

		egg = CraftItemStack.asBukkitCopy(nmsIs);

		return egg;
	}

	public EntityType getEggType() {
		return this.entityType;
	}

	public PotionEffect[] getEffects() {
		return this.effects;
	}
}
