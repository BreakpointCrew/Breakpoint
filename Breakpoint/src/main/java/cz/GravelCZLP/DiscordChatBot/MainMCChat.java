package cz.GravelCZLP.DiscordChatBot;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.DiscordChatBot.Listeners.ListenersDiscord;
import cz.GravelCZLP.DiscordChatBot.Listeners.ListenersMinecraft;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

public class MainMCChat {

	Breakpoint bp;

	private static IDiscordClient client;

	public MainMCChat(Breakpoint bp) {
		this.bp = bp;
	}

	public void start() throws DiscordException {
		ClientBuilder cb = new ClientBuilder();
		cb.withToken(Breakpoint.getBreakpointConfig().getBotToken());
		client = cb.login();

		client.getDispatcher().registerListener(new ListenersDiscord());

		this.bp.getServer().getPluginManager().registerEvents(new ListenersMinecraft(), this.bp);
	}

	public void stop() throws RateLimitException, DiscordException {
		client.logout();
	}

	public static IDiscordClient getDiscordClient() {
		return client;
	}

}
