package cz.GravelCZLP.DiscordChatBot.Listeners;

import java.util.Collection;

import org.bukkit.entity.Player;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.Status;

public class ListenersDiscord {

	@EventSubscriber
	public void onChat(MessageReceivedEvent e) {
		String author = e.getMessage().getAuthor().getName();
		String msg = e.getMessage().getContent();

		String formattedMessage = "§8(Discord) " + author + ": " + msg;

		Collection<? extends Player> players = Breakpoint.getInstance().getServer().getOnlinePlayers();

		for (Player p : players) {
			BPPlayer bpPlayer = BPPlayer.get(p);
			if (!bpPlayer.isInGame()) {
				if (bpPlayer.getSettings().hasDiscordMessages()) {
					p.sendMessage(formattedMessage);
				}
			}
		}
	}

	@EventSubscriber
	public void onReady(final ReadyEvent e) {
		e.getClient().changeStatus(Status.game("Majkraft"));
	}
	
}
