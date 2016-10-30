
package cz.GravelCZLP.TS3Manager;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.TS3Query.FloodRate;

import cz.GravelCZLP.Breakpoint.Configuration;

public class TS3ManagerMain {

	
	public void setupTS3manager(Configuration cfg) {
		String serverAddress = cfg.getTS3Things().address;
		int serverPort = cfg.getTS3Things().port;
		int sid = cfg.getTS3Things().ts3id;
		
		String password = cfg.getTS3Things().queryPassword;
		String name = cfg.getTS3Things().queryUserName;
		
		TS3Config ts3cfg = new TS3Config();
		ts3cfg.setCommandTimeout(1000);
		ts3cfg.setFloodRate(FloodRate.DEFAULT);
		ts3cfg.setHost(serverAddress);
		ts3cfg.setQueryPort(serverPort);
		
		TS3Query query = new TS3Query(ts3cfg);
		query.connect();
		
		TS3ApiAsync api = query.getAsyncApi();
		api.login(name, password);
		
		api.selectVirtualServerById(sid);
		
		api.setNickname("Breakpoint bot");
		
		api.sendServerMessage("o shit waddup !");
	}
	
	
}