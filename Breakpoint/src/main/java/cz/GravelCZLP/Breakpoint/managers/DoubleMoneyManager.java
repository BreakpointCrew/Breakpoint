package cz.GravelCZLP.Breakpoint.managers;

import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.game.Game;
import cz.GravelCZLP.Breakpoint.game.GameType;
import cz.GravelCZLP.Breakpoint.game.ctf.CTFGame;
import cz.GravelCZLP.Breakpoint.game.ctf.CTFMap;

public class DoubleMoneyManager {
	private static boolean doubleXP;

	public static void update() {
		updateDoubleXP();
	}

	private static void updateDoubleXP() {
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);

		doubleXP = day == Calendar.SUNDAY;
	}

	public static boolean isDoubleXP() {
		return doubleXP;
	}

	public static void startBoostLoop() {
		if (!isDoubleXP()) {
			return;
		}
		Bukkit.getScheduler().runTaskTimer(Breakpoint.getInstance(), new Runnable() {
			@Override
			public void run() {

				CTFMap map = null;
				for (Game g : GameManager.getGames()) {
					if (g.getType() == GameType.CTF) {
						CTFGame ctfGame = (CTFGame) g;
						map = ctfGame.getCurrentMap();
					}
				}

				for (Location loc : map.getMelounBoostsLocations()) {
					for (Entity ent : loc.getWorld().getNearbyEntities(loc, 2, 2, 2)) {
						if (ent instanceof Item) {
							ent.remove();
						}
					}
					ItemStack boost = new ItemStack(Material.SPECKLED_MELON);
					ItemMeta bm = boost.getItemMeta();
					bm.setDisplayName("melounBoost");
					boost.setItemMeta(bm);
					Location loc0 = loc.clone();
					loc0.setYaw(90);
					loc0.setPitch(90);
					loc.getWorld().dropItem(loc0, boost);
				}
			}
		}, 0L, Breakpoint.getBreakpointConfig().getTimeForSpeedMeloun());
	}
}
