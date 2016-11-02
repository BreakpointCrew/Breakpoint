/*
* Class By GravelCZLP at 2. 11. 2016
*/

package cz.GravelCZLP.BreakpointInfo.Packets.Server;

import cz.GravelCZLP.BreakpointInfo.Packets.Packet;
import cz.GravelCZLP.BreakpointInfo.Utils.BPPlayerInfo;

public class PlayerDataResponcePacket extends Packet {

	private BPPlayerInfo info;
	
	public PlayerDataResponcePacket(BPPlayerInfo Info) {
		super(NetworkSide.SERVER);
		info = Info;
	}

	public BPPlayerInfo getInfo() {
		return info;
	}
}
