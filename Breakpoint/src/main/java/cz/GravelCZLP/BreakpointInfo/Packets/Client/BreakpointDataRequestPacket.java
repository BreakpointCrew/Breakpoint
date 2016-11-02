/*
* Class By GravelCZLP at 1. 11. 2016
*/

package cz.GravelCZLP.BreakpointInfo.Packets.Client;

import cz.GravelCZLP.BreakpointInfo.Packets.Packet;

public class BreakpointDataRequestPacket extends Packet {

	private String name;
	
	public BreakpointDataRequestPacket(String name) {
		super(NetworkSide.CLIENT);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
