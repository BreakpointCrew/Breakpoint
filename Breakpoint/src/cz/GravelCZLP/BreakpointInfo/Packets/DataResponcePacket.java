package cz.GravelCZLP.BreakpointInfo.Packets;

import cz.GravelCZLP.BreakpointInfo.BPInfo;

public class DataResponcePacket {

	private BPInfo bpInfo;
	
	public DataResponcePacket(BPInfo info) {
		bpInfo = info;
	}
	
	public BPInfo getBreakpointInfo() {
		return bpInfo;
	}

}
