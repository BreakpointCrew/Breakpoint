package cz.GravelCZLP.Breakpoint.game.ctf;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.language.MessageType;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;

public class TeamBalanceManager {
	private final CTFGame game;
	private int loopId;

	public TeamBalanceManager(CTFGame game) {
		this.game = game;
	}

	public void checkTeams() {
		Random random = new Random();
		List<BPPlayer> red = this.game.getPlayersInTeam(Team.RED);
		List<BPPlayer> blue = this.game.getPlayersInTeam(Team.BLUE);
		while (red.size() > blue.size() + 1) {
			movePlayerToTeam(red.get(random.nextInt(red.size())), Team.BLUE);
			red = this.game.getPlayersInTeam(Team.RED);
			blue = this.game.getPlayersInTeam(Team.BLUE);
		}
		while (blue.size() > red.size() + 1) {
			movePlayerToTeam(blue.get(random.nextInt(blue.size())), Team.RED);
			red = this.game.getPlayersInTeam(Team.RED);
			blue = this.game.getPlayersInTeam(Team.BLUE);
		}
	}

	public void movePlayerToTeam(BPPlayer bpPlayer, Team team) {
		Player player = bpPlayer.getPlayer();
		CTFProperties props = (CTFProperties) bpPlayer.getGameProperties();
		FlagManager flm = this.game.getFlagManager();

		props.setTeam(team);
		bpPlayer.spawn();

		if (flm.isHoldingFlag(bpPlayer)) {
			flm.dropFlag(bpPlayer);
		}

		bpPlayer.setPlayerListName();

		player.updateInventory();
		player.sendMessage(ChatColor.DARK_RED + "--- --- --- --- ---");

		if (team == Team.RED) {
			player.sendMessage(MessageType.BALANCE_MOVERED.getTranslation().getValue());
		} else if (team == Team.BLUE) {
			player.sendMessage(MessageType.BALANCE_MOVEBLUE.getTranslation().getValue());
		}

		player.sendMessage(ChatColor.DARK_RED + "--- --- --- --- ---");
	}

	public void startLoop() {
		this.loopId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Breakpoint.getInstance(), new Runnable() {
			@Override
			public void run() {
				checkTeams();
			}
		}, 0L, 20L * 30);
	}

	public CTFGame getGame() {
		return this.game;
	}

	public int getLoopId() {
		return this.loopId;
	}
}
