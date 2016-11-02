/*
* Class By GravelCZLP at 1. 11. 2016
*/

package cz.GravelCZLP.BreakpointInfo.Packets.Common;

import cz.GravelCZLP.BreakpointInfo.Packets.Packet;

public class ExceptionPacket extends Packet {

	private Exception exc;
	
	public ExceptionPacket(Exception exc) {
		super(NetworkSide.BOTH);
		this.exc = exc;
	}
	
	public Exception getException() {
		return exc;
	}
}
