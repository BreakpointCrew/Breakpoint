package cz.GravelCZLP.Breakpoint.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.Configuration;
import cz.GravelCZLP.Breakpoint.achievements.Achievement;
import cz.GravelCZLP.Breakpoint.game.Game;
import cz.GravelCZLP.Breakpoint.game.GameType;
import cz.GravelCZLP.Breakpoint.game.MapPoll;
import cz.GravelCZLP.Breakpoint.game.ctf.CTFProperties;
import cz.GravelCZLP.Breakpoint.language.MessageType;
import cz.GravelCZLP.Breakpoint.managers.AbilityManager;
import cz.GravelCZLP.Breakpoint.managers.GameManager;
import cz.GravelCZLP.Breakpoint.managers.PlayerManager;
import cz.GravelCZLP.Breakpoint.managers.ShopManager;
import cz.GravelCZLP.Breakpoint.perks.Perk;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import cz.GravelCZLP.Breakpoint.players.CooldownType;
import cz.GravelCZLP.Breakpoint.players.Settings;

public class PlayerInteractListener implements Listener
{
	Breakpoint plugin;

	public PlayerInteractListener(Breakpoint p)
	{
		plugin = p;
	}
	
	@SuppressWarnings("deprecation")
	public void onPlayerUseItem(PlayerInteractEvent event)
	{
		Action action = event.getAction();
		if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
		{
			Player player = event.getPlayer();
			BPPlayer bpPlayer = BPPlayer.get(player);
			ItemStack item = player.getItemInHand();
			Material mat = item.getType();
			short durability = item.getDurability();
			
			if(bpPlayer.isInGame())
			{
				Game game = bpPlayer.getGame();
				
				if(game.votingInProgress())
					voting(event, bpPlayer);
				
				if(mat == Material.INK_SACK && durability == 1)
				{
					if(player.getHealth() < 20)
						if(!bpPlayer.hasCooldown(CooldownType.HEAL.getPath(), 0.5, true))
						{
							Location loc = player.getLocation();
							World world = loc.getWorld();
							
							player.setItemInHand(PlayerManager.decreaseItem(item));
							
							if(player.hasPotionEffect(PotionEffectType.HEAL))
								player.removePotionEffect(PotionEffectType.HEAL);
							
							player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 1), true);
							
							world.playSound(loc, Sound.ENTITY_GENERIC_EAT, 1F, 1F);
						}
				}
				else if(mat == Material.POTION && durability >= 16000)
				{
					if(!bpPlayer.hasCooldown(CooldownType.POTION_RAW.getPath() + durability, 2, true))
					{
						item.setAmount(item.getAmount() + 1);
						player.setItemInHand(item);
					}
					else
					{
						event.setCancelled(true);
						player.updateInventory();
					}
				}
				else
					game.getListener().onPlayerRightClickItem(event, bpPlayer, item);
			}
			else if(bpPlayer.isInLobby())
				if(mat == Material.NAME_TAG)
				{
					GameMode gamemode = player.getGameMode();
					if(gamemode != GameMode.CREATIVE)
					{
						event.setCancelled(true);
						bpPlayer.setAchievementViewTarget(bpPlayer);
						bpPlayer.setAchievementViewPage(0);
						Achievement.showAchievementMenu(bpPlayer);
					}
				}
				else if(mat == Material.REDSTONE_COMPARATOR)
				{
					GameMode gamemode = player.getGameMode();
					if(gamemode != GameMode.CREATIVE)
					{
						event.setCancelled(true);
						Settings.showSettingsMenu(bpPlayer);
					}
				}
				else if(mat == Material.EXP_BOTTLE)
				{
					GameMode gamemode = player.getGameMode();
					if(gamemode != GameMode.CREATIVE)
					{
						event.setCancelled(true);
						Perk.showPerkMenu(bpPlayer);
						bpPlayer.getPlayer().updateInventory();
					}
				}
		}
		else if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)
		{
			Player player = event.getPlayer();
			BPPlayer bpPlayer = BPPlayer.get(player);
			ItemStack item = player.getItemInHand();
			Material mat = item.getType();
			if(mat == Material.BLAZE_ROD)
			{
				if(!bpPlayer.hasCooldown(CooldownType.BLAZE_ROD_MAGE.getPath(), 3, true))
				{
					Location eyeLoc = player.getEyeLocation();
					AbilityManager.launchFireball(player, eyeLoc, AbilityManager.getDirection(player));
				}
			}
			else if(mat == Material.STICK)
			{
				if(!bpPlayer.hasCooldown(CooldownType.STICK_MAGE.getPath(), 3, true))
				{
					Location eyeLoc = player.getEyeLocation();
					AbilityManager.launchSmallFireball(player, eyeLoc, AbilityManager.getDirection(player));
				}
			}
			else if(mat == Material.FEATHER)
			{
				if(!bpPlayer.hasCooldown(CooldownType.FEATHER_MAGE.getPath(), 4, true))
					AbilityManager.launchPlayer(player);
			}
			else
			{
				Game game = bpPlayer.getGame();
				
				if(game != null)
					game.getListener().onPlayerLeftClickItem(event, bpPlayer, item);
			}
		}
	}

	public void voting(PlayerInteractEvent event, BPPlayer bpPlayer)
	{
		Game game = bpPlayer.getGame();
		Player player = bpPlayer.getPlayer();
		MapPoll mapPoll = game.getMapPoll();
		PlayerInventory inv = player.getInventory();
		int mapId = inv.getHeldItemSlot();
		if(mapPoll.isIdCorrect(mapId))
		{
			String playerName = player.getName();
			if(!mapPoll.hasVoted(playerName))
			{
				int strength = bpPlayer.isVIP() ? 2 : 1;
				mapPoll.vote(playerName, mapId, strength);
				PlayerManager.clearHotBar(inv);
				player.updateInventory();
				String mapName = mapPoll.maps[mapId];
				player.sendMessage(MessageType.VOTING_VOTE.getTranslation().getValue(mapName));
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
	{
		Entity entity = event.getRightClicked();
		if(entity instanceof ItemFrame)
		{
			Player player = event.getPlayer();
			if(!player.hasPermission("Breakpoint.admin"))
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if(plugin.hasEvent())
			plugin.evtm.onPlayerInteract(event);
		
		boolean cont = onPlayerBlockInteract(event);
		
		if(!cont)
			return;
		
		onPlayerUseItem(event);
	}
	
	public boolean onPlayerBlockInteract(PlayerInteractEvent event)
	{
		Action action = event.getAction();
		
		if(action == Action.RIGHT_CLICK_BLOCK)
		{
			Player player = event.getPlayer();
			BPPlayer bpPlayer = BPPlayer.get(player);
			
			if(bpPlayer.isInGame())
			{
				Game game = bpPlayer.getGame();
				
				game.getListener().onPlayerRightClickBlock(event, bpPlayer);
			}
			else
			{
				Block block = event.getClickedBlock();
				Material type = block.getType();
				
				if(type == Material.WALL_SIGN || type == Material.SIGN_POST)
				{
					Sign sign = (Sign) block.getState();
					Location loc = block.getLocation();
					Game clickedGame = GameManager.getGame(loc);
					
					if(clickedGame != null)
					{
						if(clickedGame.isPlayable())
							try
							{
								clickedGame.join(bpPlayer);
							}
							catch(Exception e)
							{
								player.sendMessage(e.getMessage());
							}
						else
							player.sendMessage(MessageType.LOBBY_GAME_NOTREADY.getTranslation().getValue(clickedGame.getName()));
					}
					else
					{
						String[] lines = sign.getLines();
						
						if(ShopManager.isShop(lines))
							ShopManager.buyItem(bpPlayer, sign, lines);
					}
					
					event.setCancelled(true);
					return false;
				}
				else if(type == Material.SKULL)
				{
					Block signBlock = block.getRelative(BlockFace.UP);
					Material signMat = signBlock.getType();
					
					if(signMat == Material.SIGN_POST || signMat == Material.WALL_SIGN)
					{
						Sign sign = (Sign) signBlock.getState();
						String[] lines = sign.getLines();
						
						if(ShopManager.isShop(lines))
							ShopManager.buyItem(bpPlayer, sign, lines);
					}
				}
			}
		}
		else if(action == Action.PHYSICAL)
		{
			Block block = event.getClickedBlock();
			Material mat = block.getType();
			if(mat == Material.STONE_PLATE)
			{
				Block below = block.getRelative(BlockFace.DOWN);
				Material belowMat = below.getType();
				Player player = event.getPlayer();
				BPPlayer bpPlayer = BPPlayer.get(player);
				
				if(!bpPlayer.isInGame())
				{
					Configuration config = Breakpoint.getBreakpointConfig();
					
					if(belowMat == Material.EMERALD_BLOCK)
						bpPlayer.teleport(config.getShopLocation(), false);
					else if(belowMat == Material.QUARTZ_BLOCK)
						bpPlayer.teleport(config.getLobbyLocation(), false);
					else if (belowMat == Material.REDSTONE_BLOCK) 
						bpPlayer.teleport(config.getStaffListLocation(), false);
				}
				else
				{
					Game game = bpPlayer.getGame();
					
					game.getListener().onPlayerPhysicallyInteractWithBlock(event, bpPlayer, below);
				}
			}
		}
		
		return true;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		if(!(player.hasPermission("Breakpoint.build") && player.getGameMode() == GameMode.CREATIVE))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		if(!(player.hasPermission("Breakpoint.build") && player.getGameMode() == GameMode.CREATIVE))
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event)
	{
		event.setCancelled(true);
		Location loc = event.getLocation();
		World world = loc.getWorld();
		world.createExplosion(loc.getX(), loc.getY(), loc.getZ(), event.getYield(), false, false);
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent e)
	{
		BPPlayer bpPlayer = BPPlayer.get(e.getPlayer());
		if (bpPlayer.isInGame()) {
			if (bpPlayer.getGame().getType() == GameType.CTF) {
				ItemStack item = (ItemStack) e.getItem();
				if (item.getType() == Material.MELON) {
					if (item.getItemMeta().getDisplayName() == "melounBoost") {
						e.setCancelled(true);
						e.getItem().remove();
						Color color = Color.WHITE;
						CTFProperties props = (CTFProperties) bpPlayer.getGameProperties();
						color = props.getTeam().getColor();
						/*if (e.getPlayer().hasPotionEffect(PotionEffectType.SPEED)) {
							for (PotionEffect effect : e.getPlayer().getActivePotionEffects()) {
								if (effect.getAmplifier() == 1) {
									e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 3, false, true, color));
								} else if (effect.getAmplifier() == 2) {
									
								} else {
									
								}
							}
						} else {
							
						}*/
						e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 3, false, true, color));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onHangingBreak(HangingBreakEvent event)
	{
		if(event.isCancelled())
			return;
		if(event.getCause() == RemoveCause.ENTITY)
			event.setCancelled(true);
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event)
	{
		Inventory inv = event.getInventory();
		String name = ChatColor.stripColor(inv.getName());
		
		if(name.startsWith("BREAKPOINT"))
			return;
		
		if(inv.getType() != InventoryType.PLAYER && !event.getPlayer().hasPermission("breakpoint.admin"))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent event)
	{
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event)
	{
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockSpread(BlockSpreadEvent event)
	{
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event)
	{
		IgniteCause ic = event.getCause();
		if(ic != IgniteCause.FLINT_AND_STEEL)
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{	
		Player player = event.getPlayer();
		
		BPPlayer bpPlayer = BPPlayer.get(player);
		Game game = bpPlayer.getGame();
		
		if(game != null)
			game.getListener().onPlayerTeleport(event, bpPlayer);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		event.setCancelled(true);
	}
}
