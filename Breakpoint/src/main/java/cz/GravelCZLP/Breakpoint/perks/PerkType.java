package cz.GravelCZLP.Breakpoint.perks;

import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.comphenix.example.Attributes;
import com.comphenix.example.Attributes.Attribute;
import com.comphenix.example.Attributes.AttributeType;
import com.comphenix.example.Attributes.Operation;

import cz.GravelCZLP.Breakpoint.game.ctf.CTFProperties;
import cz.GravelCZLP.Breakpoint.language.MessageType;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;

public enum PerkType {
	// {{STATIC
	@SuppressWarnings("deprecation")
	AGILITY(MessageType.PERK_AGILITY_NAME, MessageType.PERK_AGILITY_DESC,
			new MaterialData(Material.POTION, (byte) 8194), new Attribute[] { 
					getAttribute(AttributeType.GENERIC_MOVEMENT_SPEED, Operation.MULTIPLY_PERCENTAGE, 0.1) }), STABILITY(MessageType.PERK_STABILITY_NAME, MessageType.PERK_STABILITY_DESC, new MaterialData(Material.CHAINMAIL_CHESTPLATE), new Attribute[] { getAttribute(AttributeType.GENERIC_KNOCKBACK_RESISTANCE, Operation.MULTIPLY_PERCENTAGE, 0.1) }), STRENGTH(MessageType.PERK_STRENGTH_NAME, MessageType.PERK_STRENGTH_DESC, new MaterialData(Material.IRON_SWORD), new Attribute[] { getAttribute(AttributeType.GENERIC_ATTACK_DAMAGE, Operation.MULTIPLY_PERCENTAGE, 0.1) }), 
	@Deprecated
	VITALITY(MessageType.PERK_VITALITY_NAME, MessageType.PERK_VITALITY_DESC, new MaterialData(Material.BOW), new Attribute[] { getAttribute(AttributeType.GENERIC_MAX_HEALTH, Operation.ADD_NUMBER, 20) }), POWER(MessageType.PERK_POWER_NAME, MessageType.PERK_POWER_DESC, new MaterialData(Material.BOW)) {
		public final double MULTIPLIER = 1.1;

		@Override
		public void onDamageDealtByProjectile(EntityDamageByEntityEvent event) {
			event.setDamage(event.getDamage() * this.MULTIPLIER);
		}
	},
	FIRESPREADER(MessageType.PERK_FIRESPREADER_NAME, MessageType.PERK_FIRESPREADER_DESC,
			new MaterialData(Material.BLAZE_POWDER)) {
		public final double CHANCE = 0.1, DURATION = 5;

		@Override
		public void onDamageDealtByEntity(EntityDamageByEntityEvent event) {
			Random rnd = new Random();

			if (rnd.nextDouble() < this.CHANCE) {
				if (event.getEntity() instanceof Player) {
					Player damager = (Player) event.getEntity();
					List<Entity> nearbyEntites = damager.getNearbyEntities(10, 10, 10);
					for (int i = 0; i < nearbyEntites.size(); i++) {
						if (!(nearbyEntites.get(i) instanceof Player)) {
							nearbyEntites.remove(i);
						}
					}
					
					for (int i = 0; i < nearbyEntites.size(); i++) {
						BPPlayer bpPlayer = BPPlayer.get((Player)nearbyEntites.get(i));
						if (!bpPlayer.isInGame()) {
							nearbyEntites.remove(i);
						}
					}
					
					BPPlayer bpPlayer = BPPlayer.get(damager);
					switch (bpPlayer.getGameProperties().getGameType()) {
					case CTF:
						CTFProperties props = (CTFProperties) bpPlayer.getGameProperties();
						
						break;
					case CW:
						break;
					case DM:
						break;
					default:
						break;
					
					}
				}
				
			}
		}
	},
	SPLITTER(MessageType.PERK_SPLITTER_NAME, MessageType.PERK_SPLITTER_DESC, new MaterialData(Material.FIREBALL)) {
		public final double MULTIPLIER = 1.15;

		@Override
		public void onDamageDealtByPlayer(EntityDamageByEntityEvent event) {
			Entity damager = event.getDamager();

			if (damager.isOnGround()) {
				return;
			}

			Vector velocity = damager.getVelocity();
			double y = velocity.getY();

			if (y >= 0) {
				return;
			}

			event.setDamage(event.getDamage() * this.MULTIPLIER);
		}
	},
	AIRBORN(MessageType.PERK_AIRBORN_NAME, MessageType.PERK_AIRBORN_DESC, new MaterialData(Material.FEATHER)) {
		@Override
		public void onSpawn(BPPlayer bpPlayer) {
			Player player = bpPlayer.getPlayer();

			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0, true), true);
		}
	},
	@Deprecated
	SUICIDE(MessageType.PERK_SUICIDE_NAME, MessageType.PERK_SUICIDE_DESC, new MaterialData(Material.SKULL_ITEM)) {
		public final double MULTIPLIER = 1.25;

		@Override
		public void onDamageDealtByPlayer(EntityDamageByEntityEvent event) {
			event.setDamage(event.getDamage() * this.MULTIPLIER);
		}

		@Override
		public void onDamageTakenFromPlayer(EntityDamageByEntityEvent event) {
			event.setDamage(event.getDamage() * this.MULTIPLIER);
		}
	};

	private static Attribute getAttribute(AttributeType type, Operation operation, double amount) {
		return Attribute.newBuilder().type(type).operation(operation).amount(amount).name("Breakpoint Perk Attribute")
				.build();
	}

	public static PerkType parse(String translatedName, boolean ignoreCase) {
		if (translatedName == null) {
			return null;
		}

		if (ignoreCase) {
			for (PerkType perk : values()) {
				if (translatedName.equalsIgnoreCase(perk.getName())) {
					return perk;
				}
			}
		} else {
			for (PerkType perk : values()) {
				if (translatedName.equals(perk.getName())) {
					return perk;
				}
			}
		}

		return null;
	}

	public static PerkType parse(String name) {
		return parse(name, false);
	}
	// }}STATIC

	private final MessageType name, description;
	private final MaterialData materialData;
	private final Attribute[] attributes;

	private PerkType(MessageType name, MessageType description, MaterialData materialData, Attribute[] attributes) {
		this.name = name;
		this.description = description;
		this.materialData = materialData;
		this.attributes = attributes;
	}

	private PerkType(MessageType name, MessageType description, MaterialData materialData) {
		this(name, description, materialData, null);
	}

	public void onSpawn(BPPlayer bpPlayer) {
	}

	public void onDamageDealtByEntity(EntityDamageByEntityEvent event) {
	}

	public void onDamageDealtByProjectile(EntityDamageByEntityEvent event) {
	}

	public void onDamageDealtByPlayer(EntityDamageByEntityEvent event) {
	}

	public void onDamageTakenFromEntity(EntityDamageByEntityEvent event) {
	}

	public void onDamageTakenFromProjectile(EntityDamageByEntityEvent event) {
	}

	public void onDamageTakenFromPlayer(EntityDamageByEntityEvent event) {
	}

	public ItemStack applyToClonedItemStack(ItemStack is) {
		ItemStack is2 = is.clone();

		applyToItemStack(is2);

		return is2;
	}

	public ItemStack applyToItemStack(ItemStack is) {
		if (this.attributes == null) {
			return is;
		}

		Attributes attributes = new Attributes(is);

		for (Attribute attribute : this.attributes) {
			attributes.add(attribute);
		}

		return attributes.getStack();
	}

	public String getName() {
		return this.name.getTranslation().getValue();
	}

	public List<String> getDescription() {
		return this.description.getTranslation().getValues();
	}

	public MessageType getNameMessageType() {
		return this.name;
	}

	public MessageType getDescriptionMessageType() {
		return this.description;
	}

	public Attribute[] getAttributes() {
		return this.attributes;
	}

	public MaterialData getMaterialData() {
		return this.materialData;
	}
}
