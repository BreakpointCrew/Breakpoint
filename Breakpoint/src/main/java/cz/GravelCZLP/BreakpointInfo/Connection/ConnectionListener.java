/*
* Class By GravelCZLP at 1. 11. 2016
*/

package cz.GravelCZLP.BreakpointInfo.Connection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import cz.GravelCZLP.Breakpoint.players.BPPlayer;
import cz.GravelCZLP.BreakpointInfo.Main;
import cz.GravelCZLP.BreakpointInfo.Packets.Packet;
import cz.GravelCZLP.BreakpointInfo.Packets.Packet.NetworkSide;
import cz.GravelCZLP.BreakpointInfo.Packets.Client.BreakpointDataRequestPacket;
import cz.GravelCZLP.BreakpointInfo.Packets.Client.PlayerDataRequestPacket;
import cz.GravelCZLP.BreakpointInfo.Packets.Common.ExceptionPacket;
import cz.GravelCZLP.BreakpointInfo.Packets.Server.BreakpointDataResponcePacket;
import cz.GravelCZLP.BreakpointInfo.Packets.Server.PlayerDataResponcePacket;
import cz.GravelCZLP.BreakpointInfo.Utils.BPPlayerInfo;
import cz.GravelCZLP.BreakpointInfo.Utils.BreakpointInfo;

public class ConnectionListener extends Listener {
	
	private Main main;
	
	public ConnectionListener(Main m) {
		main = m;
	}
	
	@Override
	public void connected(Connection connection) {
		super.connected(connection);
		if (!main.canConnect(connection)) {
			connection.sendTCP(new ExceptionPacket(new Exception("Too many connections")));
			connection.close();
		}
	}
	
	@Override
	public void received(Connection connection, Object object) {
		super.received(connection, object);
		
		if (!(object instanceof Packet)) { return; }
		Packet packet = (Packet) object;
		if (packet.site != NetworkSide.CLIENT) { return; }
		
		String ip = connection.getRemoteAddressTCP().getAddress().getHostAddress();
		
		if (packet instanceof BreakpointDataRequestPacket) {
			BreakpointDataRequestPacket pack = (BreakpointDataRequestPacket) packet;
			main.logger.info("IP: " + ip + " (" + pack.getName() + ") send a request of Breakpoint data");
			if (main.canRequest(connection)) {
				BreakpointDataResponcePacket packetToSend = new BreakpointDataResponcePacket(BreakpointInfo.getActualInfo());
				connection.sendTCP(packetToSend);
			} else {
				Exception exc = new Exception("You are over request limit");
				ExceptionPacket packetToSend = new ExceptionPacket(exc);
				connection.sendTCP(packetToSend);
			}
		} else if (packet instanceof PlayerDataRequestPacket) {
			PlayerDataRequestPacket pack = (PlayerDataRequestPacket) packet;
			main.logger.info("IP:" + ip + " send a request of data of Player:" + pack.playerName);
			if (main.canRequest(connection)) {
				Player p = Bukkit.getPlayer(pack.getPlayerName());
				if (p == null) {
					PlayerDataResponcePacket packetToSend = new PlayerDataResponcePacket(null);
					connection.sendTCP(packetToSend);
					return;
				}
				if (!p.isOnline()) {
					PlayerDataResponcePacket packetToSend = new PlayerDataResponcePacket(null);
					connection.sendTCP(packetToSend);
					return;
				}
				BPPlayer bpPlayer = BPPlayer.get(p);
				PlayerDataResponcePacket packetToSend = new PlayerDataResponcePacket(BPPlayerInfo.getInfoAboutPlayer(bpPlayer));
				connection.sendTCP(packetToSend);
			} else {
				Exception exc = new Exception("You are over request limit");
				ExceptionPacket packetToSend = new ExceptionPacket(exc);
				connection.sendTCP(packetToSend);
			}
		} else {
			return;
		}
	}
}
