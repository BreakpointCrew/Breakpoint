/*
* Class By GravelCZLP at 25. 4. 2017
*/

package cz.GravelCZLP.Breakpoint.hooks;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nametagedit.plugin.NametagEdit;
import com.nametagedit.plugin.api.INametagApi;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import cz.GravelCZLP.Breakpoint.players.BPPlayer;

public class NametagAPIHooks {

	private boolean isHooked = false;
	private INametagApi nametagAPI;
	
	public static NametagAPIHooks hook() {
		Plugin nametagEdit = Bukkit.getPluginManager().getPlugin("NametagEdit");
		if (nametagEdit == null) {
			Breakpoint.getInstance().getLogger().log(Level.SEVERE, "NametagEdit was not found, there will not be any colored names in Games, this can confuse Players.");
			return new NametagAPIHooks();
		}
		NametagAPIHooks hook = new NametagAPIHooks();
		hook.tryHook();
		return hook;
	}
	
	public void tryHook() {
		nametagAPI = NametagEdit.getApi();
		isHooked = true;
	}
	
	public boolean isHooked() {
		return isHooked;
	}
	
	public INametagApi getAPI() {
		return nametagAPI;
	}
	
	public void setPrefix(Player p, String prefix) {
		if (!isHooked()) {
			return;
		}
		getAPI().setPrefix(p, prefix);
	}
	
	public void setSuffix(Player p, String suffix) {
		if (!isHooked()) {
			return;
		}
		getAPI().setSuffix(p, suffix);
	}
	public void updateNametag(BPPlayer bpPlayer) {
		if (!isHooked()) {
			return;
		}
		String prefix = BPPlayer.brackets(bpPlayer.getNameTagPrefix()) + " " + ChatColor.RESET;
		String suffix = bpPlayer.getTagSuffix();
		setPrefix(bpPlayer.getPlayer(), prefix);
		setSuffix(bpPlayer.getPlayer(), suffix);
 	}
}
