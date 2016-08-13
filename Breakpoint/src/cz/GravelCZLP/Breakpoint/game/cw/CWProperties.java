package cz.GravelCZLP.Breakpoint.game.cw;

import org.bukkit.ChatColor;

import cz.GravelCZLP.Breakpoint.game.CharacterType;
import cz.GravelCZLP.Breakpoint.game.ctf.CTFGame;
import cz.GravelCZLP.Breakpoint.game.ctf.CTFProperties;
import cz.GravelCZLP.Breakpoint.game.ctf.Team;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;

public class CWProperties extends CTFProperties
{
	public CWProperties(CTFGame game, BPPlayer bpPlayer, Team team, CharacterType characterType)
	{
		super(game, bpPlayer, team, characterType);
	}
	
	public CWProperties(CTFGame game, BPPlayer bpPlayer)
	{
		super(game, bpPlayer);
	}
	
	@Override
	public String getChatPrefix()
	{
		Team team = getTeam();
		ChatColor nameColor = Team.getChatColor(team);
		
		return ChatColor.WHITE + "»" + nameColor;
	}
}
