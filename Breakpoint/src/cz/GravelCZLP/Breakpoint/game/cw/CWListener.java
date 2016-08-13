package cz.GravelCZLP.Breakpoint.game.cw;

import cz.GravelCZLP.Breakpoint.game.Game;
import cz.GravelCZLP.Breakpoint.game.ctf.CTFListener;

public class CWListener extends CTFListener
{
	public CWListener(Game game)
	{
		super(game, CWGame.class);
	}
	
	@Override
	public CWGame getGame()
	{
		return (CWGame) super.getGame();
	}
}
