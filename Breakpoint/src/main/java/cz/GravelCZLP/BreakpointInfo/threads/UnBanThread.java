package cz.GravelCZLP.BreakpointInfo.threads;

import java.util.TimerTask;

import cz.GravelCZLP.BreakpointInfo.DataListenerMain;

public class UnBanThread extends TimerTask {

	DataListenerMain main;

	public UnBanThread(DataListenerMain data) {
		this.main = data;
	}

	@Override
	public void run() {
		this.main.banned.clear();
	}

}
