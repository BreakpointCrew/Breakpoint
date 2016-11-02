/*
* Class By GravelCZLP at 2. 11. 2016
*/

package cz.GravelCZLP.BreakpointInfo.Packets.Client;

import cz.GravelCZLP.BreakpointInfo.Packets.Packet;

public class PlayerDataRequestPacket extends Packet {

	public String playerName;
	
	public PlayerDataRequestPacket(String playername) {
		super(NetworkSide.CLIENT);
		playerName = playername;
	}

	public String getPlayerName() {
		return playerName;
	}
}
