
package cz.GravelCZLP.BreakpointInfo.Packets;

public class ExceptionPacket {

	private String s;
	
	public ExceptionPacket(String msg) {
		s = msg;
	}
	
	public String getMessage() {
		return s;
	}
}
