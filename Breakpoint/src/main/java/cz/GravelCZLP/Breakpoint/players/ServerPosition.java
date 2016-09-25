package cz.GravelCZLP.Breakpoint.players;

import java.util.Arrays;
import java.util.List;

import me.limeth.storageAPI.Column;
import me.limeth.storageAPI.ColumnType;
import me.limeth.storageAPI.Storage;

public class ServerPosition {

	public static enum ServerPositionEnum {
		ADMIN, DEVELOPER, MODERATOR, HELPER, YOUTUBE, SPONSOR, VIPPLUS, VIP, NORMAL;
	}

	private static ServerPositionEnum position;

	public static List<Column> getRequiredMySQLColumns() {
		return Arrays.asList(new Column("position", ColumnType.ENUM, position.name()));
	}

	public static final ServerPosition load(Storage s) throws Exception {

		ServerPositionEnum pos = (ServerPositionEnum) s.get(Enum.class, "position", ServerPositionEnum.NORMAL);

		return new ServerPosition(pos);
	}

	public void save(Storage storage) {
		storage.put("position", position.name());
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
