
package cz.GravelCZLP.BreakpointInfo.Packets;

import cz.GravelCZLP.BreakpointInfo.Util.EnumAction;

public class ActionPacket {

	private EnumAction action;
	
	public ActionPacket(EnumAction act) {
		action = act;
	}
	
	public EnumAction getAction() {
		return action;
	}
}