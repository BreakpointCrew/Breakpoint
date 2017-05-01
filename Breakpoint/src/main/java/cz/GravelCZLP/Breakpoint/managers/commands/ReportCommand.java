/*
* Class By GravelCZLP at 30. 4. 2017
*/

package cz.GravelCZLP.Breakpoint.managers.commands;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ReportCommand implements CommandExecutor, Listener {

	private AtomicInteger reports = new AtomicInteger();
	private HashMap<Integer, String> reportedby = new HashMap<>(); 
	private HashMap<Integer, String> reported = new HashMap<>();
	private HashMap<Integer, String> reason = new HashMap<>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			//report 0 1 2 3 4 5 6 7 8 9 etc....
			sender.sendMessage(ChatColor.RED + "/report <Player> <reason>");
			sender.sendMessage(ChatColor.RED + "/report list");
			sender.sendMessage(ChatColor.RED + "/report remove id");
		}
		Player p = Bukkit.getPlayer(args[0]);
		if (p != null) {
			StringBuilder builder = new StringBuilder();
			for (int i = 1; i < args.length; i++) {
				builder.append(args[i]);
				builder.append(' ');
			}
			int i = reports.incrementAndGet();
			reportedby.put(i, sender.getName());
			reported.put(i, p.getName());
			reason.put(i, builder.toString());
			sender.sendMessage(ChatColor.GREEN + "Nahlásil si " + p.getName() + " za: " + builder.toString());
			for (Player players : Bukkit.getOnlinePlayers()) {
				if (players.hasPermission("Breakpoint.reports.list")) {
					players.sendMessage(ChatColor.RED +"Hrac " + sender.getName() + " Nahlasil " + p.getName() + " za:" + builder.toString());
				}
			}
			return false;
		}
		if (args[0].equalsIgnoreCase("list") && sender.hasPermission("Breakpoint.reports.list")) {
			sender.sendMessage(ChatColor.GOLD + "Report ID | Reporter | Reported | Reason");
			sender.sendMessage(ChatColor.RED + "________________________________________");
			for (int i = 0; i < reports.get(); i++) {
				sender.sendMessage(ChatColor.RED + "| " + i + " | " + reportedby.get(i) + 
						" | " + reported.get(i) + " | " + reason.get(i) + " |");
				
			}
			sender.sendMessage(ChatColor.RED + "--------------------------------------------");
			return false;
		} else if (args[0].equalsIgnoreCase("remove") && sender.hasPermission("Breakpoint.reports.remove")) {
			int i = 0;
			try {
				i = Integer.parseInt(args[1]);
			} catch (Exception e) {
				sender.sendMessage("That is not an valid ID");
				return false;
			}
			if (i > reports.get() || i < 0) {
				sender.sendMessage("ID is out of range");
				return false;
			}
			reports.decrementAndGet();
			reportedby.remove(i);
			reported.remove(i);
			reason.remove(i);
			sender.sendMessage("Report " + i + " was been removed");
		}
		return false;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent e) {
		if (e.getPlayer().hasPermission("Breakpoint.reports.list")) {
			e.getPlayer().sendMessage(ChatColor.GRAY + "Use /report list - to see all the reports.");
		}
	}
	
}
