package cz.GravelCZLP.DiscordChatBot;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

public class MainMCChat {

	private static IDiscordClient client;
	
	public void start() throws DiscordException {
		ClientBuilder cb = new ClientBuilder();
		cb.withToken(Breakpoint.getBreakpointConfig().getBotToken());
		client = cb.login();
		
	}

	public void stop() throws RateLimitException, DiscordException {
		client.logout();
	}

	public static IDiscordClient getDiscordClient() {
		return client;
	}
	
}
