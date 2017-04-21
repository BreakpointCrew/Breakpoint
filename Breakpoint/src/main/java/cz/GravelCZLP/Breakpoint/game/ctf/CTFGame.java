package cz.GravelCZLP.Breakpoint.game.ctf;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.game.BPMap;
import cz.GravelCZLP.Breakpoint.game.CharacterType;
import cz.GravelCZLP.Breakpoint.game.Game;
import cz.GravelCZLP.Breakpoint.game.GameType;
import cz.GravelCZLP.Breakpoint.language.MessageType;
import cz.GravelCZLP.Breakpoint.language.Translation;
import cz.GravelCZLP.Breakpoint.managers.PlayerManager;
import cz.GravelCZLP.Breakpoint.managers.SBManager;
import cz.GravelCZLP.Breakpoint.managers.SoundManager;
import cz.GravelCZLP.Breakpoint.maps.BPMapPalette;
import cz.GravelCZLP.Breakpoint.maps.MapManager;
import cz.GravelCZLP.Breakpoint.maps.SizeRenderer;
import cz.GravelCZLP.Breakpoint.perks.Perk;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import cz.GravelCZLP.Breakpoint.sound.BPSound;

@SuppressWarnings("deprecation")
public class CTFGame extends Game {
	private final FlagManager flm;
	private final TeamBalanceManager tbm;
	public static final int emeraldsForWin = 50;
	public static final int emeraldsForCapture = 10;
	public static int spawnProtectionSeconds = 10;
	public Location teamSelectionLocation, characterSelectionLocation;
	public final SizeRenderer[] teamSizeRenderers = new SizeRenderer[2];
	public final short teamSizeRenderersMapId;
	private final Translation scoreHeaderTranslation = MessageType.SCOREBOARD_PROGRESS_CTF_HEADER.getTranslation();
	private final String[] scoreHeaderNames = {
			MessageType.SCOREBOARD_PROGRESS_CTF_TEAM_RED.getTranslation().getValue(),
			MessageType.SCOREBOARD_PROGRESS_CTF_TEAM_BLUE.getTranslation().getValue() };

	public CTFGame(GameType gt, String name, Location signLoc, Location teamSelectionLocation,
			Location characterSelectionLocation, LinkedList<CTFMap> maps, boolean balanceTeams) {
		super(gt, name, signLoc, maps);
		this.flm = new FlagManager(this);
		this.tbm = new TeamBalanceManager(this);

		if (balanceTeams) {
			getTeamBalanceManager().startLoop();
		}

		this.teamSelectionLocation = teamSelectionLocation;
		this.characterSelectionLocation = characterSelectionLocation;
		// Maps
		this.teamSizeRenderersMapId = MapManager.getNextFreeId(2);
		this.teamSizeRenderers[0] = new SizeRenderer(BPMapPalette.getColor(BPMapPalette.RED, 2),
				BPMapPalette.getColor(BPMapPalette.RED, 0), getPlayersInTeam(Team.RED).size());
		MapView rtsmv = Bukkit.getMap(this.teamSizeRenderersMapId);
		this.teamSizeRenderers[0].set(rtsmv);
		this.teamSizeRenderers[1] = new SizeRenderer(BPMapPalette.getColor(BPMapPalette.DARK_BLUE, 2),
				BPMapPalette.getColor(BPMapPalette.DARK_BLUE, 0), getPlayersInTeam(Team.BLUE).size());
		MapView btsmv = Bukkit.getMap((short) (this.teamSizeRenderersMapId + 1));
		System.out.print(teamSizeRenderersMapId);
		this.teamSizeRenderers[1].set(btsmv);
		if (players.size() < 2) {
			flm.removeFlags();
		}
	}

	public CTFGame(String name, Location signLoc, Location teamSelectionLocation, Location characterSelectionLocation,
			LinkedList<CTFMap> maps) {
		this(GameType.CTF, name, signLoc, teamSelectionLocation, characterSelectionLocation, maps, true);
	}

	public CTFGame(String name, Location signLoc) {
		this(name, signLoc, null, null, new LinkedList<CTFMap>());
	}

	@Override
	public void startExtra() {
		for (BPMap map : getPlayableMaps()) {
			if (map.getGameType() == GameType.CTF) {
				CTFMap ctfMap = (CTFMap) map;
				if (ctfMap.isPlayable()) {
					for (int i = 0; i < ctfMap.getTeamFlags().length; i++) {
						Chunk chunk = ctfMap.getTeamFlags()[i].getWorld().getChunkAt(ctfMap.getTeamFlags()[i]);
						if (!chunk.isLoaded()) {
							chunk.load();
						}
					}
				}
			}
		}
		this.flm.startLoops();
	}

	@Override
	public void showInGameMenu(BPPlayer bpPlayer) {
		Player player = bpPlayer.getPlayer();
		PlayerInventory pi = player.getInventory();

		FlagManager.giveCompass(player, pi);
	}

	@Override
	public void onPlayerLeaveGame(BPPlayer bpPlayer) {
		if (this.flm.isHoldingFlag(bpPlayer)) {
			this.flm.dropFlag(bpPlayer);
		}
		if (players.size() < 1) {
			flm.removeFlags();
		}
		super.onPlayerLeaveGame(bpPlayer);
		updateTeamMapViews();
	}

	@Override
	public void join(BPPlayer bpPlayer) throws Exception {
		bpPlayer.setGameProperties(new CTFProperties(this, bpPlayer));
		super.join(bpPlayer);
		bpPlayer.spawn();
		
		updateTeamSizeRenderers();
		
		if (players.size() >= 2) {
			flm.spawnFlags();
		} else {
			flm.removeFlags();
		}
	}

	public void superJoin(BPPlayer bpPlayer) throws Exception {
		super.join(bpPlayer);
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if (args.length <= 0) {
			sender.sendMessage("info, start, map, teamSelLoc, charSelLoc, spawnFlags, removeFlags, giveNeededMaps");
		} else if (args[0].equalsIgnoreCase("info")) {
			sender.sendMessage("Name: " + getName());
			sender.sendMessage("Active: " + isActive());
			sender.sendMessage("Playable: " + isPlayable(true));
			sender.sendMessage("MapSecondsLeft: " + getMapSecondsLeft());
			sender.sendMessage("#Maps: " + getMaps().size());
			sender.sendMessage("Current map: " + getCurrentMap().getName());
			sender.sendMessage("#Players: " + getPlayers().size());
		} else if (args[0].equalsIgnoreCase("start")) {
			if (isPlayable(true)) {
				start();
			} else {
				sender.sendMessage(ChatColor.RED + "The game is not playable!");
			}
		} else if (args[0].equalsIgnoreCase("teamSelLoc")) {
			if (!(sender instanceof Player)) {
				return;
			}
			Player player = (Player) sender;
			Location loc = player.getLocation();
			this.teamSelectionLocation = loc;
			sender.sendMessage(ChatColor.GREEN + "Team selection location successfully set!");
		} else if (args[0].equalsIgnoreCase("charSelLoc")) {
			if (!(sender instanceof Player)) {
				return;
			}
			Player player = (Player) sender;
			Location loc = player.getLocation();
			this.characterSelectionLocation = loc;
			sender.sendMessage(ChatColor.GREEN + "Character selection location successfully set!");
		} else if (args[0].equalsIgnoreCase("spawnFlags")) {
			this.flm.spawnFlags();
			sender.sendMessage("Flags have been spawned.");
		} else if (args[0].equalsIgnoreCase("removeFlags")) {
			this.flm.removeFlags();
			sender.sendMessage("Flags have been removed.");
		} else if (args[0].equalsIgnoreCase("map")) {
			if (args.length <= 1) {
				sender.sendMessage("list");
				sender.sendMessage("add [MinPlayers] [MaxPlayers] [Name]");
				sender.sendMessage("remove [Name]");
				sender.sendMessage("setCapacity [Min] [Max] [Name]");
				sender.sendMessage("teamSpawn [Team] [Name]");
				sender.sendMessage("teamFlag [Team] [Name]");
				sender.sendMessage("addMelounBoost [Name]");
			} else if (args[1].equalsIgnoreCase("list")) {
				List<? extends BPMap> maps = getMaps();
				for (int i = 0; i < maps.size(); i++) {
					BPMap map = maps.get(i);
					String name = map.getName();
					boolean playable = map.isPlayable();
					boolean capPlayable = map.isPlayableWith(this.players.size());
					int min = map.getMinimumPlayers();
					int max = map.getMaximumPlayers();
					ChatColor color = !playable ? ChatColor.RED : !capPlayable ? ChatColor.YELLOW : ChatColor.GREEN;
					sender.sendMessage(color + name + ChatColor.GRAY + " <" + min + "; " + max + ">");
				}
			} else if (args[1].equalsIgnoreCase("add")) {
				if (args.length <= 4) {
					sender.sendMessage("add [MinPlayers] [MaxPlayers] [Name]");
					return;
				}
				try {
					int min = Integer.parseInt(args[2]);
					int max = Integer.parseInt(args[3]);
					String name = args[4];
					for (int i = 5; i < args.length; i++) {
						name += " " + args[i];
					}
					CTFMap map = new CTFMap(name, min, max);
					getMaps().add(map);
					sender.sendMessage(
							ChatColor.GREEN + "Map '" + name + "' <" + min + "; " + max + "> successfully added.");
				} catch (Exception e) {
					e.printStackTrace();
					sender.sendMessage(ChatColor.RED + "An error occured.");
				}
			} else if (args[1].equalsIgnoreCase("remove")) {
				if (args.length <= 2) {
					sender.sendMessage("remove [Name]");
					return;
				}
				String name = args[2];
				for (int i = 3; i < args.length; i++) {
					name += " " + args[i];
				}
				BPMap map = getMapByName(name);
				getMaps().remove(map);
				sender.sendMessage(ChatColor.GREEN + "Map '" + name + "' successfully removed.");
			} else if (args[1].equalsIgnoreCase("setCapacity")) {
				if (args.length <= 4) {
					sender.sendMessage("setCapacity [MinPlayers] [MaxPlayers] [Name]");
					return;
				}
				String name = args[4];
				int minPlayers, maxPlayers;
				for (int i = 5; i < args.length; i++) {
					name += " " + args[i];
				}
				CTFMap map = getMapByName(name);
				if (map == null) {
					sender.sendMessage(ChatColor.RED + "Map '" + name + "' not found.");
					return;
				}
				try {
					minPlayers = Integer.parseInt(args[2]);
					maxPlayers = Integer.parseInt(args[3]);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "An error occured.");
					return;
				}
				map.setMinimumPlayers(minPlayers);
				map.setMaximumPlayers(maxPlayers);
				sender.sendMessage(ChatColor.GREEN + "Capacity for map '" + name + "' successfully set. <" + minPlayers
						+ "; " + maxPlayers + ">");
			} else if (args[1].equalsIgnoreCase("teamSpawn")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "Only for players!");
					return;
				}
				Player player = (Player) sender;
				if (args.length < 4) {
					return;
				}
				String mapName = args[3];
				for (int i = 4; i < args.length; i++) {
					mapName += " " + args[i];
				}
				CTFMap map = getMapByName(mapName);
				if (map == null) {
					sender.sendMessage(ChatColor.RED + "Map not found!");
					return;
				}
				Team team;
				try {
					team = Team.valueOf(args[2].toUpperCase());
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Incorrect team");
					return;
				}
				int teamId = Team.getId(team);
				Location[] spawnPoints = map.getTeamSpawn();
				spawnPoints[teamId] = player.getLocation();
				sender.sendMessage(ChatColor.GREEN + "Spawn location on map '" + mapName + "' for team " + teamId
						+ " successfully set.");
			} else if (args[1].equalsIgnoreCase("teamFlag")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "Only for players!");
					return;
				}

				Player player = (Player) sender;

				if (args.length < 4) {
					return;
				}

				String mapName = args[3];

				for (int i = 4; i < args.length; i++) {
					mapName += " " + args[i];
				}

				CTFMap map = getMapByName(mapName);

				if (map == null) {
					sender.sendMessage(ChatColor.RED + "Map not found!");
					return;
				}

				Team team;

				try {
					team = Team.valueOf(args[2].toUpperCase());
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Incorrect team");
					return;
				}

				int teamId = Team.getId(team);
				Location[] spawnFlags = map.getTeamFlags();
				spawnFlags[teamId] = player.getLocation().getBlock().getLocation();
				sender.sendMessage(ChatColor.GREEN + "Flag location on map '" + mapName + "' for team " + teamId
						+ " successfully set.");
			} else if (args[1].equalsIgnoreCase("addMelounBoost")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "Only for players!");
					return;
				}
				CTFMap map = getMapByName(args[2]);
				if (map == null) {
					sender.sendMessage(ChatColor.RED + "Map not found!");
					return;
				}
				CTFMap ctfMap;
				if (!(map instanceof CTFMap)) {
					return;
				}
				ctfMap = map;
				Player p = (Player) sender;
				ctfMap.addMelounBoostLocation(p.getLocation());
				sender.sendMessage(ChatColor.GREEN + "Location succesfully added !");
			} else if (args[1].equalsIgnoreCase("giveNeededMaps")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("Only for Players");
					return;
				}
				List<Short> maps = new ArrayList<Short>();
				for (int i = 0; i < 1; i++) {
					maps.add((short) (teamSizeRenderersMapId + i));
				}
				maps.add(playerAmountRendererMapId);
				maps.add(currentMapMapId);
				for (int i = 0; i < maps.size(); i++) {
					ItemStack map = new ItemStack(Material.MAP, 1, maps.get(i));
					((Player) sender).getInventory().addItem(map);
				}
			}
		}
	}

	@Override
	public void updateLobbyMaps(BPPlayer bpPlayer) {
		super.updateLobbyMaps(bpPlayer);

		Player player = bpPlayer.getPlayer();

		for (int i = 0; i < 1; i++) {
			MapView v = Bukkit.getMap((short) (teamSizeRenderersMapId + i));
			if (v == null) {
				return;
			}
			player.sendMap(v);
		}
	}

	@Override
	public void updateProgressObjective(BPPlayer bpPlayer) {
		updateProgressObjective(bpPlayer.getScoreboardManager().getProgressObj());
	}

	public void updateProgressObjective(Objective progressObj) {
		updateProgressObjectiveHeader(progressObj);
		updateProgressObjectiveScores(progressObj);
	}

	public void updateProgressObjectiveScores(BPPlayer bpPlayer) {
		updateProgressObjectiveScores(bpPlayer.getScoreboardManager().getProgressObj());
	}

	public void updateProgressObjectiveScores(Objective progressObj) {
		for (int i = 0; i < 2; i++) {
			Score score = progressObj.getScore(Bukkit.getOfflinePlayer(this.scoreHeaderNames[i]));
			score.setScore(this.flm.getScore()[i]);
		}
	}

	@Override
	public void updateProgressObjectiveHeader(BPPlayer bpPlayer) {
		updateProgressObjectiveHeader(bpPlayer.getScoreboardManager().getProgressObj());
	}

	public void updateProgressObjectiveHeader(Objective progressObj) {
		progressObj.setDisplayName(this.scoreHeaderTranslation.getValue(SBManager.formatTime(getMapSecondsLeft())));
	}

	public void updateTeamSizeRenderers() {
		for (int i = 0; i < 2; i++) {
			Team team = Team.getById(i);
			this.teamSizeRenderers[i].setSize(getPlayersInTeam(team).size());
			MapManager.updateMapForNotPlayingPlayers((short) (this.teamSizeRenderersMapId + i));
		}
	}

	public void updateProgressObjectiveScore() {
		int[] flagScores = getFlagManager().getScore();
		OfflinePlayer[] fakePlayers = new OfflinePlayer[] {
				Bukkit.getOfflinePlayer(MessageType.SCOREBOARD_PROGRESS_CTF_TEAM_RED.getTranslation().getValue()),
				Bukkit.getOfflinePlayer(MessageType.SCOREBOARD_PROGRESS_CTF_TEAM_BLUE.getTranslation().getValue()) };

		for (BPPlayer bpPlayer : this.players) {
			SBManager sbm = bpPlayer.getScoreboardManager();
			Objective progressObj = sbm.getProgressObj();

			for (int i = 0; i < fakePlayers.length; i++) {
				Score score = progressObj.getScore(fakePlayers[i]);
				score.setScore(flagScores[i]);
			}
		}
	}

	@Override
	public boolean isPlayable() {
		if (!super.isPlayable()) {
			return false;
		}
		return this.flm != null && this.teamSelectionLocation != null && this.characterSelectionLocation != null;
	}

	protected boolean isSuperPlayable() {
		return super.isPlayable();
	}

	@Override
	protected void saveExtra(YamlConfiguration yml) {
		yml.set(getName() + ".teamSelLoc",
				this.teamSelectionLocation.getWorld().getName() + "," + this.teamSelectionLocation.getX() + ","
						+ this.teamSelectionLocation.getY() + "," + this.teamSelectionLocation.getZ() + ","
						+ this.teamSelectionLocation.getYaw() + "," + this.teamSelectionLocation.getPitch());
		yml.set(getName() + ".charSelLoc",
				this.characterSelectionLocation.getWorld().getName() + "," + this.characterSelectionLocation.getX()
						+ "," + this.characterSelectionLocation.getY() + "," + this.characterSelectionLocation.getZ()
						+ "," + this.characterSelectionLocation.getYaw() + ","
						+ this.characterSelectionLocation.getPitch());
	}

	@Override
	protected void endRoundExtra() {
		this.flm.removeFlags();
		this.flm.removeHolders();
		int[] score = this.flm.getScore();
		broadcast(ChatColor.GRAY + "---------------------------------");
		if (score[0] == score[1]) {
			broadcast(MessageType.RESULT_CTF_DRAW.getTranslation().getValue());
			SoundManager.playTeamSound(this, Sound.ENTITY_ARROW_HIT, 16F, 0.5F, Team.RED);
			SoundManager.playTeamSound(this, Sound.ENTITY_ARROW_HIT, 16F, 0.5F, Team.BLUE);
		} else if (score[0] > score[1]) {
			broadcast(MessageType.RESULT_CTF_WIN_RED.getTranslation().getValue());
			SoundManager.playTeamSound(this, Sound.ENTITY_ENDERDRAGON_DEATH, 16F, 0.5F, Team.BLUE);
			SoundManager.playTeamSound(this, Sound.ENTITY_PLAYER_LEVELUP, 16F, 4F, Team.RED);
			awardPlayersInTeam(Team.RED, emeraldsForWin);
			spawnFireworks(Team.RED);
			if (score[1] <= 0) {
				SoundManager.playTeamSound(BPSound.FLAWLESS_VICTORY, this, Team.RED);
				SoundManager.playTeamSound(BPSound.HUMILIATING_DEFEAT, this, Team.BLUE);
			}
		} else if (score[1] > score[0]) {
			broadcast(MessageType.RESULT_CTF_WIN_BLUE.getTranslation().getValue());
			SoundManager.playTeamSound(this, Sound.ENTITY_ENDERDRAGON_DEATH, 16F, 0.5F, Team.RED);
			SoundManager.playTeamSound(this, Sound.ENTITY_PLAYER_LEVELUP, 16F, 4F, Team.BLUE);
			awardPlayersInTeam(Team.BLUE, emeraldsForWin);
			spawnFireworks(Team.BLUE);
			if (score[0] <= 0) {
				SoundManager.playTeamSound(BPSound.FLAWLESS_VICTORY, this, Team.BLUE);
				SoundManager.playTeamSound(BPSound.HUMILIATING_DEFEAT, this, Team.RED);
			}
		}
		broadcast(ChatColor.GRAY + "---------------------------------");
		for (BPPlayer bpPlayer : players) {
			bpPlayer.getScoreboardManager().updateSidebarObjective();
		}
	}

	public void spawnFireworks(final Team team) {
		BukkitScheduler bc = Bukkit.getScheduler();
		long delay = 10L;
		int amount = (int) (20L / delay * 30);
		for (int i = 0; i < amount; i++) {
			bc.scheduleSyncDelayedTask(Breakpoint.getInstance(), new Runnable() {
				@Override
				public void run() {
					Firework fw = PlayerManager.spawnFirework(getSpawnLocation(team));
					FireworkMeta fm = fw.getFireworkMeta();
					Random rnd = new Random();
					Color[] colors = new Color[3];
					fm.setPower(1 + rnd.nextInt(3));
					if (team == Team.RED) {
						colors[0] = Color.RED;
						colors[1] = Color.ORANGE;
						colors[2] = Color.YELLOW;
					} else if (team == Team.BLUE) {
						colors[0] = Color.BLUE;
						colors[1] = Color.AQUA;
						colors[2] = Color.NAVY;
					}
					for (int i = 0; i < 1 + rnd.nextInt(5); i++) {
						Builder fe = FireworkEffect.builder();
						fe = fe.flicker(rnd.nextBoolean());
						fe = fe.trail(rnd.nextBoolean());
						fe = fe.with(FireworkEffect.Type.values()[rnd.nextInt(5)]);
						List<Color> color = new ArrayList<>();
						List<Color> fade = new ArrayList<>();
						for (int j = 0; j < 1 + rnd.nextInt(3); j++) {
							color.add(colors[rnd.nextInt(3)]);
						}
						for (int j = 0; j < 1 + rnd.nextInt(3); j++) {
							fade.add(colors[rnd.nextInt(3)]);
						}
						fe = fe.withColor(color);
						fe = fe.withFade(fade);
						fm.addEffect(fe.build());
					}
					fw.setFireworkMeta(fm);
				}
			}, delay * i);
		}
	}

	@Override
	public CTFMap getCurrentMap() {
		return (CTFMap) super.getCurrentMap();
	}

	public void awardPlayersInTeam(Team team, int emeralds) {
		for (BPPlayer bpWinner : getPlayersInTeam(team)) {
			bpWinner.addMoney(emeralds, true, true);
		}
	}

	public List<BPPlayer> getPlayersInTeam(Team team) {
		List<BPPlayer> list = new ArrayList<>();
		for (BPPlayer bpPlayer : getPlayers()) {
			CTFProperties props = (CTFProperties) bpPlayer.getGameProperties();
			Team curTeam = props.getTeam();
			if (curTeam == team) {
				list.add(bpPlayer);
			}
		}
		return list;
	}

	public void updateTeamMapViews() {
		int[] teamSizes = getTeamSizes();
		for (int i = 0; i < 2; i++) {
			this.teamSizeRenderers[i].setSize(teamSizes[i]);
			MapView map = Bukkit.getMap((short) (teamSizeRenderersMapId + i));
			for (BPPlayer bpPlayer : BPPlayer.onlinePlayers) {
				Player player = bpPlayer.getPlayer();
				if (bpPlayer.isInLobby() && map != null) {
					player.sendMap(map);
				}
			}
		}
	}

	public Location getSpawnLocation(Team team) {
		int i = getActiveMapId();
		if (i == -1) {
			i = 0;
		}
		return getMaps().get(i).getTeamSpawn()[Team.getId(team)];
	}

	public int[] getTeamSizes() {
		int[] players = new int[] { 0, 0 };
		for (BPPlayer bpPlayer : getPlayers()) {
			Team team = ((CTFProperties) bpPlayer.getGameProperties()).getTeam();
			if (team == null) {
				continue;
			}
			int teamId = Team.getId(team);
			players[teamId]++;
		}
		return players;
	}

	@Override
	public void spawn(BPPlayer bpPlayer) {
		Player player = bpPlayer.getPlayer();
		if (player.isDead()) {
			return;
		}
		CTFProperties props = (CTFProperties) bpPlayer.getGameProperties();
		Team team = props.getTeam();
		bpPlayer.setSpawnTime(System.currentTimeMillis());
		bpPlayer.purify();
		props.equip();
		Perk.onSpawn(bpPlayer);
		bpPlayer.getScoreboardManager().updateSidebarObjective();
		updateProgressObjective(bpPlayer);
		if (team == null) {
			bpPlayer.teleport(this.teamSelectionLocation, false);
			return;
		}
		CharacterType ct = props.getCharacterType();
		if (ct == null) {
			bpPlayer.teleport(this.characterSelectionLocation, false);
			return;
		}
		Location spawnLoc = getSpawnLocation(team);
		bpPlayer.teleport(spawnLoc, true);
	}

	@Override
	public void reset(BPPlayer bpPlayer) {
		if (this.flm.isHoldingFlag(bpPlayer)) {
			this.flm.dropFlag(bpPlayer);
		}
	}

	@Override
	public void broadcastDeathMessage(String victim) {
		if (votingInProgress()) {
			return;
		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			BPPlayer bpPlayer = BPPlayer.get(player);
			if (bpPlayer.isInGame()) {
				if (bpPlayer.getSettings().hasDeathMessages()) {
					player.sendMessage(MessageType.PVP_KILLINFO_DIED.getTranslation().getValue(victim));
				}
			}
		}
	}

	public void sendTeamMessage(Player player, String message) {
		BPPlayer bpPlayer = BPPlayer.get(player);
		CTFProperties props = (CTFProperties) bpPlayer.getGameProperties();
		String playerName = player.getName();
		Team team = props.getTeam();
		sendTeamMessage(playerName, message, team);
	}

	public void sendTeamMessage(String playerName, String message, Team team) {
		for (BPPlayer bpTarget : getPlayers()) {
			Player target = bpTarget.getPlayer();
			CTFProperties props = (CTFProperties) bpTarget.getGameProperties();
			Team targetTeam = props.getTeam();

			if (targetTeam == team) {
				target.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + playerName + ": " + message);
			}
		}
	}

	public boolean canJoinTeam(Team team) {
		int[] players = getTeamSizes();
		if (team == Team.RED) {
			players[0]++;
			return players[0] <= players[1] + 1;
		} else if (team == Team.BLUE) {
			players[1]++;
			return players[1] <= players[0] + 1;
		} else {
			return false;
		}
	}

	public FlagManager getFlagManager() {
		return this.flm;
	}

	@Override
	public CTFMap getMapByName(String name) {
		return (CTFMap) super.getMapByName(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public LinkedList<CTFMap> getMaps() {
		LinkedList<? extends BPMap> maps = super.getMaps();
		return (LinkedList<CTFMap>) maps;
	}

	@Override
	protected void changeMapExtra() {
		this.flm.reset();
		this.flm.spawnFlags();
	}

	public TeamBalanceManager getTeamBalanceManager() {
		return this.tbm;
	}
}
