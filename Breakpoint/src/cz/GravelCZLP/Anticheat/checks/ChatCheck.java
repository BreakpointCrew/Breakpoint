package cz.GravelCZLP.Anticheat.checks;

import org.bukkit.entity.Player;

public class ChatCheck {

	public boolean checkChatImposible(Player p) {
		
		if (p.isBlocking() || p.isDead() || p.isSneaking() || p.isSprinting()) {
			return true;
		}
		
		return false;
	}
	
}
