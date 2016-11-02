/*
* Class By GravelCZLP at 1. 11. 2016
*/

package cz.GravelCZLP.BreakpointInfo.Packets;

public class Packet {

	
	public NetworkSide site;
	
	public Packet(NetworkSide site) {
		this.site = site;
	}
	
	public static enum NetworkSide {
		CLIENT(0), SERVER(1), BOTH(2);
		
		public int id;
		
		private NetworkSide(int id) {
			this.id = id;
		}
	}
}
