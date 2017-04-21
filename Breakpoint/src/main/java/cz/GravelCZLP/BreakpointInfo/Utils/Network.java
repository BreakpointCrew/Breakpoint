
package cz.GravelCZLP.BreakpointInfo.Utils;

import java.util.List;

import com.esotericsoftware.kryo.Kryo;

import cz.GravelCZLP.Breakpoint.achievements.Achievement;
import cz.GravelCZLP.Breakpoint.game.Game;
import cz.GravelCZLP.Breakpoint.game.GameProperties;
import cz.GravelCZLP.Breakpoint.perks.Perk;
import cz.GravelCZLP.Breakpoint.players.clans.Clan;
import cz.GravelCZLP.BreakpointInfo.Packets.Packet;
import cz.GravelCZLP.BreakpointInfo.Packets.Client.BreakpointDataRequestPacket;
import cz.GravelCZLP.BreakpointInfo.Packets.Common.ExceptionPacket;
import cz.GravelCZLP.BreakpointInfo.Packets.Server.BreakpointDataResponcePacket;

public class Network {

	public static void setupPackets(Kryo kryo) {
		kryo.register(Packet.class);
		kryo.register(Packet.NetworkSide.class);
		
		kryo.register(BreakpointDataRequestPacket.class);
		kryo.register(ExceptionPacket.class);
		kryo.register(BreakpointDataResponcePacket.class);
		kryo.register(Exception.class);
		kryo.register(BreakpointInfo.class);
		
		kryo.register(BPPlayerInfo.class);
		kryo.register(List.class);
		kryo.register(Achievement.class);
		kryo.register(Perk.class);
		kryo.register(Clan.class);
		
		kryo.register(Game.class);
		kryo.register(GameProperties.class);
	}

}