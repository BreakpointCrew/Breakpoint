package cz.GravelCZLP.Breakpoint.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.language.MessageType;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;

public class AfkManager {
	public static int defSTK = 60 * 3; // Defaultn� secondsToKick
	public static final String afkKickProtectionNode = "Breakpoint.afkProtection";
	Breakpoint plugin;
	int loopId;

	public AfkManager(Breakpoint p) {
		this.plugin = p;
	}

	public boolean executeAfk(BPPlayer bpPlayer) {
		Player player = bpPlayer.getPlayer();

		if (player.hasPermission(afkKickProtectionNode)) {
			return false;
		}

		Location pastLocation = bpPlayer.getAfkPastLocation();
		Location presentLocation = player.getLocation();

		if (pastLocation != null) {
			if (pastLocation.equals(presentLocation)) {
				int secondsToKick = bpPlayer.getAfkSecondsToKick();

				if (secondsToKick <= 0) {
					player.kickPlayer(MessageType.OTHER_AFKKICK.getTranslation().getValue());
					return true;
				} else {
					bpPlayer.setAfkSecondsToKick(secondsToKick - 1);
				}

				bpPlayer.setAfkPastLocation(presentLocation);
				return false;
			}
		}

		bpPlayer.clearAfkSecondsToKick();
		return false;
	}

	public void startLoop() {
		this.loopId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
			@Override
			public void run() {
				tick();
			}
		}, 0, 20L);
	}

	public void tick() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			BPPlayer bpPlayer = BPPlayer.get(player);

			if (bpPlayer == null) {
				continue;
			}

			executeAfk(bpPlayer);
		}

		/*
		 * Iterator<BPPlayer> iterator = BPPlayer.onlinePlayers.iterator();
		 * 
		 * while(iterator.hasNext()) { BPPlayer bpPlayer = iterator.next();
		 * boolean kicked = executeAfk(bpPlayer);
		 * 
		 * if(kicked) iterator.remove(); }
		 */
	}
}
