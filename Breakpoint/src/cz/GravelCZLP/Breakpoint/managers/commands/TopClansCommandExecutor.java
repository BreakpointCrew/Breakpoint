package cz.GravelCZLP.Breakpoint.managers.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import cz.GravelCZLP.Breakpoint.language.MessageType;
import cz.GravelCZLP.Breakpoint.managers.StatisticsManager;

public class TopClansCommandExecutor implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length <= 0) {
			StatisticsManager.listTopClans(sender, 10, 1);
		} else {
			int page;
			try {
				page = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				sender.sendMessage(MessageType.COMMAND_TOP_EXE_INCORRECTPAGE.getTranslation().getValue());
				return true;
			}
			if (page > 0) {
				StatisticsManager.listTopClans(sender, 10, page);
			} else {
				sender.sendMessage(MessageType.COMMAND_TOP_EXE_NEGATIVEORZERO.getTranslation().getValue());
			}
		}

		return true;
	}
}
