package cz.GravelCZLP.Breakpoint.players;

import java.util.Arrays;
import java.util.List;

import cz.GravelCZLP.Breakpoint.Breakpoint;
import me.limeth.storageAPI.Column;
import me.limeth.storageAPI.ColumnType;
import me.limeth.storageAPI.Storage;

public class ServerPosition {

	public enum ServerPositionEnum {
		ADMIN, DEVELOPER, MODERATOR, HELPER, YOUTUBE ,SPONSOR, VIPPLUS, VIP, NORMAL;
	}
	
	public static ServerPosition load(Storage storage) throws Exception {
		
		Enum<?> type = storage.get(Enum.class, "vipType");
		
		return new ServerPosition(type);
	}
	
	private static ServerPositionEnum type;
	
	public void save(Storage storage) {
		
		storage.put("vipType", type.name());
	}
	
	public static List<Column> getRequiredMySQLColumns()
	{
		return Arrays.asList(
				new Column("serverPos", ColumnType.ENUM, type.name())
				);
	}
	
	@SuppressWarnings("static-access")
	public ServerPosition(Enum<?> type) {
		try {
			ServerPositionEnum e = ServerPositionEnum.valueOf(type.name());
			this.type = e;
		} catch (IllegalArgumentException e) {
			Breakpoint.warn("Server Position: " + type + " is not Inplemented yet.");
			this.type = ServerPositionEnum.NORMAL;
		}
	}
	public boolean isVIP() {
		return type == ServerPositionEnum.VIP;
	}
	
	public boolean isVIPPlus() {
		return type == ServerPositionEnum.VIPPLUS;
	}
	
	public ServerPositionEnum getPositon() {
		return type;
	}
	
	public boolean isStaff() {
		return (type != ServerPositionEnum.NORMAL) 
				|| (type != ServerPositionEnum.VIP) 
				|| (type != ServerPositionEnum.VIPPLUS);
	}
	
	public void setPosition(ServerPositionEnum n) {
		type = n;
	}
	
	public boolean isNormalPlayer() {
		return type == ServerPositionEnum.NORMAL;
	}
}
