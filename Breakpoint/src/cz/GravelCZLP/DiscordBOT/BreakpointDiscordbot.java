package cz.GravelCZLP.DiscordBOT;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class BreakpointDiscordbot {

	private static IDiscordClient client;
	
	public BreakpointDiscordbot() {}
	
	public void start() throws DiscordException, RateLimitException {
		ClientBuilder clientBuilder = new ClientBuilder();
		clientBuilder.withToken(Breakpoint.getBreakpointConfig().getBotToken());
		client = clientBuilder.login();
		try {
			new MessageBuilder(client).appendContent("Minecraft DiscordBot By gravelCZLP byl Zapnut").build();
		} catch (MissingPermissionsException e) {
			e.printStackTrace();
		}
	}
	
}
