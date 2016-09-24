package cz.GravelCZLP.BreakpointInfo.Packets;

import cz.GravelCZLP.BreakpointInfo.BPInfo;

public class DataResponcePacket {

	private BPInfo bpInfo;

	public DataResponcePacket(BPInfo info) {
		this.bpInfo = info;
	}

	public BPInfo getBreakpointInfo() {
		return this.bpInfo;
	}

}
