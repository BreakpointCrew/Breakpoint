/*
* Class By GravelCZLP at 25. 4. 2017
*/

package cz.GravelCZLP.Breakpoint.hooks;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import fr.onecraft.clientstats.ClientStats;
import fr.onecraft.clientstats.ClientStatsAPI;

public class ClientStatsHooks {

	private static ClientStatsAPI csapi = null;
	private boolean isHooked = false;
	
	public static ClientStatsHooks hook() {
		Plugin protocolSupport = Bukkit.getPluginManager().getPlugin("ProtocolSupport");
		Plugin clientStats = Bukkit.getPluginManager().getPlugin("ClientStats");
		
		if (protocolSupport != null && clientStats == null) {
			Breakpoint.getInstance().getLogger().log(Level.SEVERE, "Protocol Support was found, but Client Stats were not, unable to hook to Client Stats.");
			return new ClientStatsHooks();
		} else if (protocolSupport == null && clientStats != null) {
			Breakpoint.getInstance().getLogger().log(Level.SEVERE, "Client Stats were found, but Protocol Support was not, there is no point in hooking to Client Stats.");
			return new ClientStatsHooks();
		} else if (protocolSupport == null && clientStats == null){
			Breakpoint.getInstance().getLogger().log(Level.SEVERE, "Neither Prtocol Support nor Client Stats were found, unable to hook to Client Stats");
			return new ClientStatsHooks();
		}
		ClientStatsHooks csh = new ClientStatsHooks();
		csh.tryHookToAPI();
		return csh;
	}
	
	private void tryHookToAPI() {
		csapi = ClientStats.getApi();
		isHooked = true;
	}
	
	public ClientStatsAPI getHookedAPI() {
		if (!isHooked()) {
			return null;
		}
		return csapi;
	}
	
	public boolean isHooked() {
		return isHooked;
	}
	
	public HashMap<String, Object> checkOldVersion(Player p) {
		if (!isHooked()) {
			return null;
		}
		if (csapi != null && csapi.isVersionDetectionEnabled()) {
			int protocolVersion = csapi.getProtocol(p.getUniqueId());
			String versionName = csapi.getVersionName(protocolVersion);
			HashMap<String, Object> ret = new HashMap<>();
			ret.put("version", protocolVersion);
			ret.put("name", versionName);
			return ret;
		} else if (csapi == null) {
			tryHookToAPI();
		}
		return null; 
	}
	
}
