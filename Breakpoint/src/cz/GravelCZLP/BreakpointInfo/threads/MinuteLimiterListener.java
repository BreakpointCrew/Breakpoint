package cz.GravelCZLP.BreakpointInfo.threads;

import java.util.TimerTask;

import cz.GravelCZLP.BreakpointInfo.DataListenerMain;

public class MinuteLimiterListener extends TimerTask {

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
