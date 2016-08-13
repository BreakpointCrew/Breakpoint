package cz.GravelCZLP.Breakpoint.managers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Licence {

	public static boolean isAllowed() {
		String b = "false";
		
		try {
			b = getResponce();
		} catch (IOException e) {
			
		}
		
		return Boolean.valueOf(b);
	}
	
	private static String getResponce() throws IOException {
		
		URL o = new URL("https://raw.githubusercontent.com/GravelCrew/Breakpoint/master/l");
		HttpURLConnection con = (HttpURLConnection) o.openConnection();
		con.setDoOutput(true);
		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line;
		StringBuffer buffer = new StringBuffer();
		while ( (line = reader.readLine()) != null ){
			buffer.append(line);
		}
		
		reader.close();
		con.disconnect();
		
		String msg = buffer.toString();
		
		return msg;
	}
}
