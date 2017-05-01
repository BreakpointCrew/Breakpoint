/*
* Class By GravelCZLP at 23. 4. 2017
*/

package cz.GravelCZLP.Breakpoint.managers.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cz.GravelCZLP.Breakpoint.equipment.BPSkull.SkullType;
import cz.GravelCZLP.Breakpoint.managers.ShopManager;

public class ShopCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Nespravne Argumenty");
			sender.sendMessage(ChatColor.GRAY + "/shop buildShop [type (armor, skull)] [side] [#RGB] [cost,cost...] [time,time...] [name] (For armor)");
			sender.sendMessage(ChatColor.GRAY + "/shop buildShop [type (skull, armor] [side] [skulltype (list for list of values)] [name] (For skull)");
			sender.sendMessage(ChatColor.RED + "/bp buildShop [type (skull, armor)] args....");
			return false;
		}
		 if (args[0].equalsIgnoreCase("buildShop")) {
				if (!(sender instanceof Player)) {
					return true;
				}
				Player player = (Player) sender;
				if (args[1].equalsIgnoreCase("armor")) {
					if (args.length >= 7) {
						Location loc;
						int facing;
						String color;
						int[] cost;
						int[] time;
						String name;
						try {
							loc = player.getLocation();
							facing = Integer.parseInt(args[2]);
							color = args[3];
							name = "";
							for (int i = 6; i < args.length; i++) {
								name += args[i] + " ";
							}
							name = name.substring(0, name.length() - 1);
							String[] rawCost = args[4].split(",");
							String[] rawTime = args[5].split(",");
							if (rawCost.length != rawTime.length) {
								player.sendMessage(ChatColor.RED + "Pocet casu se musi rovnat poctu cen.");
								return true;
							}
							cost = new int[rawCost.length];
							time = new int[rawTime.length];
							for (int i = 0; i < cost.length; i++) {
								cost[i] = Integer.parseInt(rawCost[i]);
								time[i] = Integer.parseInt(rawTime[i]);
							}
						} catch (Exception e) {
							player.sendMessage(ChatColor.RED + "Nespravne argumenty!");
							return true;
						}
						ShopManager.buildArmorShop(loc, facing, color, cost, time, name);
						player.sendMessage(ChatColor.GREEN + "Obchod postaven!");
					} else {
						player.sendMessage(ChatColor.RED + "Nespravne argumenty!");
						player.sendMessage(
								ChatColor.GRAY + "/bp buildShop [type (armor, skull)] [side] [#RGB] [cost,cost...] [time,time...] [name] (For armor)");
						// cmd 0 1 2 3 4 5 6 7 8
					}
				} else if (args[1].equalsIgnoreCase("skull")) {
					if (args.length >= 4) {
						if (args[3].equalsIgnoreCase("list")) {
							for (SkullType s : SkullType.values()) {
								String name = s.name() + ", Formatted name: " + s.getFormattedName();
								String alias = s.getAlias();
								int cost = s.getCost();
								boolean vip = s.isVip();
								sender.sendMessage(name + " " + alias + " " + cost + " "+ vip);
							}
							return true;
						}
						Location loc = null;
						int facing = 0;
						String name = "";
						SkullType type = null;

						try {
							loc = player.getLocation();
							facing = Integer.parseInt(args[2]);
							for (int i = 4; i < args.length; i++) {
								name += args[i] + " ";
							}
							name = ChatColor.translateAlternateColorCodes('&', name);
							type = SkullType.valueOf(args[3].toUpperCase());
							ShopManager.buildSkullShop(loc, facing, name, type,
									player.getLocation().getBlock().getFace(player.getLocation().getBlock()));
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
						player.sendMessage(ChatColor.RED + "Nespravne Argumenty");
						player.sendMessage(ChatColor.GRAY + "/bp buildShop [type (skull, armor] [side] [skulltype (list for list of values)] [name] (For skull)");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "/bp buildShop [type (skull, armor)] args....");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Nespravne Argumenty");
				sender.sendMessage(ChatColor.GRAY + "/shop buildShop [type (armor, skull)] [side] [#RGB] [cost,cost...] [time,time...] [name] (For armor)");
				sender.sendMessage(ChatColor.GRAY + "/shop buildShop [type (skull, armor] [side] [skulltype (list for list of values)] [name] (For skull)");
				sender.sendMessage(ChatColor.RED + "/shop buildShop [type (skull, armor)] args....");
			}
		return false;
	}

}
