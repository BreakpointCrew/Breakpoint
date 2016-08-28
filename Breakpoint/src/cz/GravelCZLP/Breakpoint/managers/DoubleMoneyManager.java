package cz.GravelCZLP.Breakpoint.managers;

import java.util.Calendar;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.game.ctf.CTFMap;

public class DoubleMoneyManager
{
	private static boolean doubleXP;
	
	private static Random r = new Random();
	
	public static void update()
	{
		updateDoubleXP();
	}
	
	private static void updateDoubleXP()
	{
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		
		doubleXP = day == Calendar.SUNDAY;
	}
	
	public static boolean isDoubleXP()
	{
		return doubleXP;
	}
	
	public static void startBoostLoop()
	{
		if (!isDoubleXP()) {
			return;
		}
		Bukkit.getScheduler().runTaskTimer(Breakpoint.getInstance(), new Runnable() {
			@Override
			public void run() {
			
				CTFMap map = (CTFMap) GameManager.getGame("ctf").getCurrentMap();
				int i = r.nextInt(map.getMelounBoostsLocations().length);
				
				Location loc = map.getMelounBoostsLocations()[i];
				
				ItemStack boost = new ItemStack(Material.MELON);
				ItemMeta bm = boost.getItemMeta();
				bm.setDisplayName("melounBoost");
				boost.setItemMeta(bm);
				Location loc0 = loc.clone();
				loc0.setYaw(90);
				loc0.setPitch(90);
				loc.getWorld().dropItem(loc0, boost);
			}
		}, 0L, Breakpoint.getBreakpointConfig().getTimeForSpeedMeloun());
	}
}
