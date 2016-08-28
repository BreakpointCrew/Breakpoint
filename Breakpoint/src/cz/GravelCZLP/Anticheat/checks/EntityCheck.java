package cz.GravelCZLP.Anticheat.checks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class EntityCheck {

	private Map<String, Long> lastAttack = new HashMap<String, Long>();
	private Map<String, Long> bowWindUp = new HashMap<String, Long>();
	
	public void logAttack(String p) {
		lastAttack.put(p, System.currentTimeMillis());
	}
	
	public void logBowWindUp(String p) {
		bowWindUp.put(p, System.currentTimeMillis());
	}
	
	public boolean checkFastBow(Player p, float force) {
		
		int ticks = (int) ((((System.currentTimeMillis() - bowWindUp.get(p.getName())) * 20) / 1000 ) +3);
		bowWindUp.remove(p.getName());
		float f = (float) ticks / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;
		f = f > 1.0F ? 1.0F : f;
		if (Math.abs(force - f) > 0.25) {
			return true;
		}
		return false;
	}
	
	public boolean checkLongReachDamage(Player player, double x, double y, double z) {
        double i = x >= 4.25 ? x : y > 4.25 ? y : z > 4.25 ? z : -1;
        if (i != -1) {
            return true;
        } else {
            return false;
        }
	}
	
}
