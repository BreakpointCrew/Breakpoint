/*
* Class by GravelCZLP
* 
* Copyright 2016 GravelCZLP
*
* All Rights Reserved
*/

package cz.GravelCZLP.Breakpoint.players;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import me.leoko.advancedban.manager.TimeManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;

public class FrozebanTimer implements Runnable {

	public static HashMap<String, Integer> timers;
	
	public FrozebanTimer() {
		timers = new HashMap<String, Integer>();
	}
	
	@Override
	public void run() {
		for (Entry<String, Integer> entry : timers.entrySet()) {
			if (entry.getValue() > 0) {
				entry.setValue(entry.getValue() - 1);
			}
			if (entry.getValue() <= 0) {
				performBan(entry.getKey());
				timers.remove(entry.getKey());
			}
		}
	}

	private void performBan(String name) {
		Player p = Bukkit.getPlayer(name);
		if (p == null) {
			throw new NullPointerException("Player is null");
		}
		
		String uuid = p.getUniqueId().toString();
	 
		PunishmentType type = PunishmentType.TEMP_BAN;
	 
		long start = TimeManager.getTime(); 
		long end = TimeManager.getTime() + 86400000L * 2;
	 
		new Punishment(name, uuid, "Odpojení při prohledávání", "Breakpoint",type, start, end, "", -1).create();
	}
	
	public void startLoops() {
		Bukkit.getScheduler().runTaskTimer(Breakpoint.getInstance(), this, 0L, 20L);
	}
}
