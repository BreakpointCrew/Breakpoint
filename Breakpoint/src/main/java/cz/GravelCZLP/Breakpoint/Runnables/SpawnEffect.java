
package cz.GravelCZLP.Breakpoint.Runnables;

import org.bukkit.Effect;
import org.bukkit.Location;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.Configuration;

public class SpawnEffect implements Runnable {

	@Override
	public void run() {
		Configuration config = Breakpoint.getBreakpointConfig();
		
		Location loc = config.getLobbyLocation().clone();
		int points = 5;
		for (int iteration = 0; iteration < points; iteration++) {
			double angle = 360.0 / points * iteration;
			double nextAngle = 360.0 / points * (iteration + 1); // the angle for the next point.
			angle = Math.toRadians(angle);
			nextAngle = Math.toRadians(nextAngle); // convert to radians.
			double x = Math.cos(angle);
			double z = Math.sin(angle);
			double x2 = Math.cos(nextAngle);
			double z2 = Math.sin(nextAngle);
			double deltaX = x2 - x; // get the x-difference between the points.
			double deltaZ = z2 - z; // get the z-difference between the points.
			double distance = Math.sqrt((deltaX - x) * (deltaX - x) + (deltaZ - z) * (deltaZ - z));
			for (double d = 0; d < distance - .1; d += .1) { // we subtract .1 from the distance because otherwise it would make 1 step too many.
			  loc.add(x + deltaX * d, 0, z + deltaZ * d);
			  loc.getWorld().playEffect(loc, Effect.MAGIC_CRIT, 5);
			  loc.subtract(x + deltaX * d, 0, z + deltaZ * d);
			}
		}
	}
}
