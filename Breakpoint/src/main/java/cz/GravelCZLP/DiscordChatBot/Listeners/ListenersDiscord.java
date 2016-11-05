package cz.GravelCZLP.DiscordChatBot.Listeners;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.Status;

public class ListenersDiscord {

	@EventSubscriber
	public void onReady(final ReadyEvent e) {
		e.getClient().changeStatus(Status.game("Majkrap"));
	}
	
}
