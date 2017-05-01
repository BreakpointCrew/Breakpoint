package cz.GravelCZLP.Breakpoint.managers;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.Configuration;
import cz.GravelCZLP.Breakpoint.language.MessageType;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;

public class LobbyInfoManager {
	public static void startLoop() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Breakpoint.getInstance(), new Runnable() {

			@Override
			public void run() {
				sendMessage();
			}

		}, 20L * 60, 20L * 60);
	}

	private static void sendMessage() {
		Configuration config = Breakpoint.getBreakpointConfig();
		List<String> messages = config.getLobbyMessages();

		if (messages.isEmpty()) {
			return;
		}

		String message = MessageType.CHAT_BREAKPOINT.getTranslation().getValue() + " "
				+ messages.get(new Random().nextInt(messages.size()));

		for (BPPlayer bpPlayer : BPPlayer.onlinePlayers) {
			if (BPPlayer.onlinePlayers.isEmpty() || BPPlayer.onlinePlayers == null) {
				return;
			}
			if (bpPlayer.isInLobby()) {
				Player player = bpPlayer.getPlayer();

				player.sendMessage(message);
			}
		}
	}
}
