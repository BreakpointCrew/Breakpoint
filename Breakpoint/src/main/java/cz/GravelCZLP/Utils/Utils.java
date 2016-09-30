/*
* Class by GravelCZLP
* 
* Copyright 2016 GravelCZLP
*
* All Rights Reserved
*/

package cz.GravelCZLP.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import com.esotericsoftware.jsonbeans.Json;

public class Utils {

	@SuppressWarnings("static-access")
	public static boolean hasProxy(String ip) throws IOException {
		URL url = new URL("proxycheck.io/v1/" + ip);
		
		InputStreamReader input = new InputStreamReader(url.openStream());
		
		BufferedReader reader = new BufferedReader(input);
		
		StringBuffer buffer = new StringBuffer();
		
		char[] chars = new char[1024];
		int read = 0;
		
		while ((read = reader.read(chars)) != -1) {
			buffer.append(chars, 0, read);
		}
		
		String output = buffer.toString();
		
		if (reader != null)
			reader.close();
		
		Json json = new Json();
		ProxyJson proxyJson = json.fromJson(ProxyJson.class, output);
		if (proxyJson.proxy.equalsIgnoreCase("yes"))
			return true;
		else
			return false;
	}
	
	public static class ProxyJson {
		public static String ip = "";
		public static String proxy = "";
	}
}
