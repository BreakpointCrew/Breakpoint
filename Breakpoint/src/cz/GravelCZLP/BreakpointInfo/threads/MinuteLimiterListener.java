package cz.GravelCZLP.BreakpointInfo.threads;

import java.util.TimerTask;

import cz.GravelCZLP.BreakpointInfo.DataListenerMain;

public class MinuteLimiterListener extends TimerTask {

	DataListenerMain main;
	
	public MinuteLimiterListener(DataListenerMain d) {
		main = d;
	}

	@Override
	public void run() {
		main.connectionsPerMinute.clear();
		main.requestsPerMin.clear();
	}

}
