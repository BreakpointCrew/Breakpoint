package cz.GravelCZLP.BreakpointInfo.threads;

import cz.GravelCZLP.BreakpointInfo.DataListenerMain;

public class MinuteLimiterListener implements Runnable {

	DataListenerMain main;

	public MinuteLimiterListener(DataListenerMain d) {
		this.main = d;
	}

	@Override
	public void run() {
		this.main.connectionsPerMinute.clear();
		this.main.requestsPerMin.clear();
	}

}
