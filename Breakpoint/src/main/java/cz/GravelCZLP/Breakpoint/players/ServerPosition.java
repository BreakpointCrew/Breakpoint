package cz.GravelCZLP.Breakpoint.players;

import java.util.ArrayList;
import java.util.List;

import me.limeth.storageAPI.Column;
import me.limeth.storageAPI.ColumnType;
import me.limeth.storageAPI.Storage;

public class ServerPosition {

	public static enum ServerPositionEnum {
		ADMIN(8), DEVELOPER(7), MODERATOR(6), HELPER(5), YOUTUBE(4), SPONSOR(3), VIPPLUS(2), VIP(1), NORMAL(0);
		
		private int id;
		
		private ServerPositionEnum(int id) {
			this.id = id;
		}
		
		public static ServerPositionEnum fromId(int id) {
			for (ServerPositionEnum e : values()) {
				if (e.getId() == id) {
					return e;
				}
			}
			return NORMAL;
		}
		
		public int getId() {
			return id;
		}
	}

	private static ServerPositionEnum position;

	public static List<Column> getRequiredMySQLColumns() {
		List<Column> col = new ArrayList<>();
		
		col.add(new Column("position", ColumnType.INT));
		
		return col;
	}

	public static final ServerPosition load(Storage storage) throws Exception {

		ServerPositionEnum pos = ServerPositionEnum.fromId(storage.get(int.class, "position", 0));

		return new ServerPosition(pos);
	}

	public void save(Storage storage) {
		storage.put("position", position.getId());
	}

	public ServerPosition(ServerPositionEnum position) {
		ServerPosition.position = position;
	}

	public ServerPositionEnum getPosition() {
		return position;
	}

	public void setPosition(ServerPositionEnum newPos) {
		position = newPos;
	}

	public boolean isYoutube() {
		return position == ServerPositionEnum.YOUTUBE;
	}

	public boolean isStaff() {
		return position == ServerPositionEnum.ADMIN || position == ServerPositionEnum.DEVELOPER
				|| position == ServerPositionEnum.HELPER || position == ServerPositionEnum.MODERATOR;
	}

	public boolean isSponsor() {
		return position == ServerPositionEnum.SPONSOR;
	}

	public boolean isVIP() {
		return position == ServerPositionEnum.VIP;
	}

	public boolean isVIPPlus() {
		return position == ServerPositionEnum.VIPPLUS;
	}
}
