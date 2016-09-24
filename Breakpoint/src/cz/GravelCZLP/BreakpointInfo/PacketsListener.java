package cz.GravelCZLP.BreakpointInfo;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import cz.GravelCZLP.BreakpointInfo.Packets.DataRequestPacket;
import cz.GravelCZLP.BreakpointInfo.Packets.DataResponcePacket;

public class PacketsListener extends Listener {

	private DataListenerMain main;

	public PacketsListener(DataListenerMain a) {
		this.main = a;
	}

	@Override
	public void connected(Connection conn) {
		if (this.main.canConnect(conn)) {
			int i = this.main.connectionsPerMinute.get(conn.getRemoteAddressTCP().getAddress().toString()).intValue();
			this.main.connectionsPerMinute.put(conn.getRemoteAddressTCP().getAddress().toString(), i + 1);
		} else {
			conn.close();
		}
	}

	@Override
	public void received(Connection conn, Object o) {
		if (o == null) {
			return;
		}
		boolean canRequest = this.main.canReqquest(conn);
		if (o instanceof DataRequestPacket) {
			if (canRequest == true) {
				DataResponcePacket responce = new DataResponcePacket(this.main.getBPInfo());
				conn.sendTCP(responce);
			} else {
				DataResponcePacket responce = new DataResponcePacket(null);
				conn.sendTCP(responce);
			}
		}
	}

}
