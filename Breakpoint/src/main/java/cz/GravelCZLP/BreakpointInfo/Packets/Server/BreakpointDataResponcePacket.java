/*
* Class By GravelCZLP at 1. 11. 2016
*/

package cz.GravelCZLP.BreakpointInfo.Packets.Server;

import cz.GravelCZLP.BreakpointInfo.Packets.Packet;
import cz.GravelCZLP.BreakpointInfo.Utils.BreakpointInfo;

public class BreakpointDataResponcePacket extends Packet {

	private BreakpointInfo info;
	
	public BreakpointDataResponcePacket(BreakpointInfo info) {
		super(NetworkSide.SERVER);
		this.info = info;
	}
	
	public BreakpointInfo getInfo() {
		return info;
	}
}
