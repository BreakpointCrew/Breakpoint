/*
* Class By GravelCZLP at 26. 11. 2016
*/

package cz.GravelCZLP.Breakpoint;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import cz.GravelCZLP.Breakpoint.game.CharacterType;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;

public class Utils {

	public static List<Location> getCircle(Location center, double radius, int amount) {
		World w = center.getWorld();
		double inc = 6.283185307179586 / (double) amount;
		List<Location> locs = new ArrayList<>();
		int i = 0;
		while (i < amount) {
			double angle = (double) i * inc;
			double x = center.getX() + radius * Math.cos(angle);
			double z = center.getZ() + radius * Math.sin(angle);
			Location loc = new Location(w, x, center.getY(), z);
			locs.add(loc);
			i++;
		}
		return locs;
	}
	
	public static class Runnables {
		
		public static Runnable getPyroSpetialEffect(BPPlayer bpPlayer) {
			if (!bpPlayer.isInGame()) {
				throw new IllegalStateException("Players needs to be in game");
			}
			if (!(bpPlayer.getGameProperties().getCharacterType() == CharacterType.PYRO)) {
				throw new IllegalStateException("Player needs to have Pyro class");
			}
			
			final Location center = bpPlayer.getPlayer().getLocation();
			
			Runnable r = new Runnable() {
				double cir1radius = 0.0;
				double cir2radius = 0.0;
				double increseAmount = 0.15;
				
				@Override
				public void run() {
					if (cir1radius <= 1.75) {
						cir1radius =+ increseAmount;	
					}
					if (cir2radius <= 4.75) {
						cir2radius =+ increseAmount;
					}
					List<Location> cir1 = Utils.getCircle(center, cir1radius, 15);
					List<Location> cir2 = Utils.getCircle(center, cir2radius, 30);
					
					for (Location loc : cir1) {
						loc.getWorld().spawnParticle(Particle.FLAME, loc, 5);	
					}
					for (Location loc : cir2) {
						loc.getWorld().spawnParticle(Particle.FLAME, loc, 5);
					}
				}
				
			};
			return r;
		}
		
		
	}
}
