package cz.GravelCZLP.BreakpointInfo.threads;

import java.util.Map.Entry;

import cz.GravelCZLP.BreakpointInfo.DataListenerMain;

public class UnBanThread implements Runnable {

	DataListenerMain main;

	public UnBanThread(DataListenerMain data) {
		this.main = data;
	}

	@Override
	public void run() {
		for (Entry<String, Integer> entry : main.banned.entrySet()) {
			String ip = entry.getKey();
			int remaing = entry.getValue();
			entry.setValue(remaing--);
			remaing = entry.getValue();
			if (remaing == 0) {
				main.banned.remove(ip);
			}
		}
	}

}
