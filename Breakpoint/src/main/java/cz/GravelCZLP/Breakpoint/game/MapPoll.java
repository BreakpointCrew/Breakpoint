package cz.GravelCZLP.Breakpoint.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.game.ctf.CTFGame;
import cz.GravelCZLP.Breakpoint.language.MessageType;
import cz.GravelCZLP.Breakpoint.managers.PlayerManager;
import cz.GravelCZLP.Breakpoint.managers.SBManager;
import cz.GravelCZLP.Breakpoint.maps.VoteRenderer;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;

@SuppressWarnings("deprecation")
public class MapPoll {
	private final Game game;
	private Map<String, Integer> votes;
	public String[] maps;
	private final List<String> haveVoted;
	private boolean voting;
	private int result;
	private final short mapViewId;

	public MapPoll(Game game) {
		this.game = game;
		this.votes = new HashMap<>();
		this.haveVoted = new ArrayList<>();
		this.voting = false;
		this.mapViewId = game.getVotingMapId();
		getMapsInOrder(game.getMaps(), game.getPlayers().size());
		setMapImages();
		startCountdown();
	}

	public void getMapsInOrder(List<? extends BPMap> availableMaps, int players) {
		List<BPMap> allowedMaps = new ArrayList<>();
		Map<String, Integer> result = new HashMap<>();
		this.maps = new String[5];
		for (BPMap map : availableMaps) {
			if (map.isPlayable()) {
				if (map.isPlayableWith(players)) {
					allowedMaps.add(map);
				}
			}
		}

		for (int i = 0; i < 5; i++) {
			BPMap topMap = null;
			long topTime = 1000000000000000000L;
			for (BPMap map : allowedMaps) {
				long lastTimePlayed = map.getLastTimePlayed();
				if (lastTimePlayed < topTime) {
					topMap = map;
					topTime = lastTimePlayed;
				}
			}
			if (topMap != null) {
				allowedMaps.remove(topMap);
				result.put(topMap.getName(), 0);
				this.maps[i] = topMap.getName();
			} else {
				break;
			}
		}
		this.votes = result;
	}

	public int getBestMap() {
		int nejHlas = 0;
		ArrayList<Integer> nejMapy = new ArrayList<>();
		for (int mapNo = 0; mapNo < this.maps.length; mapNo++) {
			if (this.maps[mapNo] != null) {
				String stringMap = this.maps[mapNo];
				int hlasy = this.votes.get(stringMap);
				if (hlasy >= nejHlas) {
					if (hlasy > nejHlas) {
						nejMapy.clear();
					}
					nejMapy.add(mapNo);
					nejHlas = hlasy;
				}
			} else {
				break;
			}
		}
		if (nejMapy.size() > 1) {
			for (int i = 0; i < nejMapy.size(); i++) {
				int bestMapIndex = nejMapy.get(i);
				String bestMapName = this.maps[bestMapIndex];
				BPMap bestMap = this.game.getMapByName(bestMapName);
				if (this.game.isMapActive(bestMap)) {
					nejMapy.remove(i);
					break;
				}
			}
		}
		if (nejMapy.size() > 1) {
			Random rand = new Random();
			return nejMapy.get(rand.nextInt(nejMapy.size()));
		} else {
			if (nejMapy.isEmpty()) {
				return -1;
			} else {
				return nejMapy.get(0);
			}
		}
	}

	public void setMapImages() {
		for (int i = 0; i < this.maps.length; i++) {
			if (this.maps[i] != null) {
				setMapImage(i, this.maps[i]);
			}
		}
	}

	public void setMapImage(int mapId, String mapName) {
		VoteRenderer vr = new VoteRenderer(this.game.getMapByName(mapName));
		MapView mv = Bukkit.getMap((short) (this.mapViewId + mapId));
		vr.set(mv);
	}

	public void showOptions() {
		for (BPPlayer bpPlayer : this.game.getPlayers()) {
			if (bpPlayer.isInGame()) {
				showOptions(bpPlayer);
			}
		}
	}

	public void showOptions(BPPlayer bpPlayer) {
		Player player = bpPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		PlayerManager.clearHotBar(inv);
		for (int i = 0; i < this.maps.length; i++) {
			if (this.maps[i] != null) {
				inv.setItem(i, getMapItem(i));
			}
		}
		updateOptions(player);
	}

	public void updateOptions(Player player) {
		for (int i = 0; i < this.maps.length; i++) {
			if (this.maps[i] != null) {
				player.sendMap(Bukkit.getMap((short) (this.mapViewId + i)));
			}
		}
	}

	public ItemStack getMapItem(int i) {
		ItemStack is = new ItemStack(Material.MAP, 1, (short) (this.mapViewId + i));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("" + ChatColor.YELLOW + ChatColor.GOLD + this.maps[i]);
		is.setItemMeta(im);
		return is;
	}

	public boolean isColored(int amount) {
		if (amount <= 0) {
			return false;
		}
		for (int i : this.votes.values()) {
			if (i > amount) {
				return false;
			}
		}
		return true;
	}

	public int getMapPercentage(double curVotes) {
		double allVotes = 0;
		for (int i : this.votes.values()) {
			allVotes += i;
		}
		return (int) (curVotes / allVotes * 100);
	}

	public boolean hasVoted(String playerName) {
		return this.haveVoted.contains(playerName);
	}

	public boolean isIdCorrect(int id) {
		int size = 0;
		for (String map : this.maps) {
			if (map != null) {
				size++;
			} else {
				break;
			}
		}
		return id >= 0 && id <= size;
	}

	public int vote(String playerName, int mapId, int strength) {
		String mapName = this.maps[mapId];
		int curVotes = this.votes.get(mapName) + strength;
		this.votes.put(mapName, curVotes);
		this.haveVoted.add(playerName);

		for (BPPlayer bpPlayer : this.game.getPlayers()) {
			bpPlayer.getScoreboardManager().updateVoteOptions(this.votes);
		}

		return curVotes;
	}

	public void endVoting() {
		this.voting = false;
		int mapId = getBestMap();
		if (mapId == -1) {
			if (this.game.getPlayers() != null) {
				for (BPPlayer bpPlayer : this.game.getPlayers()) {
					bpPlayer.setSingleTeleportLocation(null);
					bpPlayer.leaveGame();
					bpPlayer.getPlayer().setHealth(0.0);
					bpPlayer.getPlayer()
							.sendMessage(MessageType.NOT_ENOUGH_PLAYERS_STARTGAME.getTranslation().getValue());
					this.result = -1;
					this.game.noPlayers = true;
					return;
				}
			}
			return;
		}
		String mapName = this.maps[mapId];
		int score = this.votes.get(mapName);
		this.result = this.game.getMaps().indexOf(this.game.getMapByName(mapName));
		int perc = getMapPercentage(score);

		PlayerManager.clearHotBars();

		for (BPPlayer bpPlayer : this.game.getPlayers()) {
			bpPlayer.getScoreboardManager().updateSidebarObjective();
		}

		this.game.broadcast(MessageType.VOTING_END.getTranslation().getValue(mapName, score, perc), true);
	}

	public void endPoll() {
		if (this.result == -1) {
			List<BPMap> maps = new ArrayList<>();
			for (BPMap bpMap : this.game.getMaps()) {
				if (bpMap.isPlayable()) {
					maps.add(bpMap);
				}
			}
			BPMap toChange = maps.get(new Random().nextInt(maps.size()));
			this.game.changeMap(this.game.getMaps().indexOf(this.game.getMapByName(toChange.getName())));
			this.game.setMapPoll(null);
			if (game.getType() == GameType.CTF) {
				CTFGame ctfGame = (CTFGame) game;
				ctfGame.getFlagManager().removeFlags();
				ctfGame.getFlagManager().removeHolders();
			}
			return;
		}
		this.game.changeMap(this.result);
		this.game.setMapPoll(null);
	}

	public void startCountdown() {
		this.voting = true;
		Breakpoint plugin = Breakpoint.getInstance();

		for (BPPlayer bpPlayer : this.game.getPlayers()) {
			SBManager sbm = bpPlayer.getScoreboardManager();

			showOptions();
			sbm.restartVoteObj();
			sbm.updateVoteOptions(this.votes);
			sbm.updateSidebarObjective();
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				endVoting();
			}
		}, 20L * 30);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				endPoll();
			}
		}, 20L * 40);
	}

	public boolean getVoting() {
		return this.voting;
	}

	public int getNumOfMaps() {
		return this.maps.length;
	}
}
