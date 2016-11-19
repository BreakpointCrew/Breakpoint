/*
* Class By GravelCZLP at 7. 11. 2016
*/

package cz.GravelCZLP.Breakpoint.managers.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

public class BreakpointCommand {

	List<BreakpointCommandExternalExcetuor> executors = new ArrayList<>(); 
	
	public void addExecutor(BreakpointCommandExternalExcetuor exe) {
		executors.add(exe);
	}
	
	public void callAllExecutors(String[] args, CommandSender sender) {
		for (BreakpointCommandExternalExcetuor exe : executors) {
			exe.onCommand(args, sender);
		}
	}
	
	public static interface BreakpointCommandExternalExcetuor {
		
		public void onCommand(String[] args, CommandSender sender);
		
	}
	
}
