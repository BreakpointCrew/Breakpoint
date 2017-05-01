/*
* Class By GravelCZLP at 25. 4. 2017
*/

package cz.GravelCZLP.Breakpoint.hooks;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import net.milkbowl.vault.economy.Economy;

public class VaultHooks {

	private boolean isHooked = false;
	
	public static VaultHooks hook() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			return new VaultHooks();
		}
		
		VaultHooks hook = new VaultHooks();
		Breakpoint.getInstance().getLogger().log(Level.INFO, "Vault found, trying to hook into it.");
		hook.setupEconomy();
		
		return hook;
	}
	
	private Economy economy;
	
	public Economy getEconomy() {
		if (!isHooked()) {
			return null;
		}
		return economy;
	}
	
	public boolean isHooked() {
		return isHooked;
	}
	
	public void setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = 
				Bukkit.getServicesManager().getRegistration(Economy.class);
		
		if (economyProvider != null) {
			Breakpoint.getInstance().getLogger().log(Level.INFO, "Sucessfully hooked into Vault.");
			economy = economyProvider.getProvider();
			isHooked = true;
		} else {
			Breakpoint.getInstance().getLogger().log(Level.WARNING, "Vault was found, but Breakpoint was unable to hoot into it.");
			Breakpoint.warn("Vault was found, but Breakpoint was unable to hoot into it.");
		}
	}
}
