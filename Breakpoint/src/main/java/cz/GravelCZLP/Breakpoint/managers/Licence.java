package cz.GravelCZLP.Breakpoint.managers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import net.md_5.bungee.api.ChatColor;

public class Licence {

	public static boolean isAllowed() {
		String b = "false";
		try {
			b = getResponce();
			ConsoleCommandSender console = Bukkit.getConsoleSender();
			console.sendMessage(ChatColor.GOLD + "[Breakpoint Loader] Responce: " + getResponce());
		} 
		catch (IOException e) {
			e.printStackTrace();
			ConsoleCommandSender console = Bukkit.getConsoleSender();
			console.sendMessage(ChatColor.RED + "Failed to check Licence, Breakpoint will not enable!");
			return false;
		}
		return Boolean.valueOf(b);
	}

	private static String getResponce() throws IOException {

		URL o = new URL(
				"https://gist.githubusercontent.com/GravelCZLP/399cb004a98b5d46789cbc3d7b713332/raw/e48677918e17b8f04c788a41f25ad9f6d06cd233/licence.txt");
		HttpURLConnection con = (HttpURLConnection) o.openConnection();
		con.setDoOutput(true);
		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line;
		StringBuffer buffer = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}

		reader.close();
		con.disconnect();

		String msg = buffer.toString();
		return msg;
	}
}
