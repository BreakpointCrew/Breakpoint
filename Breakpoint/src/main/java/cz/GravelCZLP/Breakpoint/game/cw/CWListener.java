package cz.GravelCZLP.Breakpoint.game.cw;

import org.bukkit.event.player.PlayerToggleSprintEvent;

import cz.GravelCZLP.Breakpoint.game.Game;
import cz.GravelCZLP.Breakpoint.game.GameType;
import cz.GravelCZLP.Breakpoint.game.ctf.CTFListener;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;

public class CWListener extends CTFListener {
	public CWListener(Game game) {
		super(game, CWGame.class);
	}

	@Override
	public CWGame getGame() {
		return (CWGame) super.getGame();
	}
	
	// We want fast game CTF doesnt matter, imo
	@Override
	public void onPlayerToggleSprint(BPPlayer bpPlayer, PlayerToggleSprintEvent e) {
		if (bpPlayer.isInGame()) {
			if (bpPlayer.getGame().getType() == GameType.CW) {
				bpPlayer.getPlayer().setSprinting(e.isSprinting());
			}
		}
	}
}
