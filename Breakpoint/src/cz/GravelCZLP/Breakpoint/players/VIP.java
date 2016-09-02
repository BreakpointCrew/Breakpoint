package cz.GravelCZLP.Breakpoint.players;

import java.util.Arrays;
import java.util.List;

import me.limeth.storageAPI.Column;
import me.limeth.storageAPI.ColumnType;
import me.limeth.storageAPI.Storage;

public class VIP {

	public enum VIPEnum {
		NORMAL, VIP, VIPPlus, VIPPlusPlus;
	}
	
	public static VIP load(Storage storage) throws Exception {
		
		String vip = storage.get(String.class, "vipType");
		
		return new VIP(vip);
		
	}
	
	private VIPEnum vip;
	
	public void save(Storage storage) {
		
		storage.put("vipType", vip.toString());
		
	}
	
	public static List<Column> getRequiredMySQLColumns()
	{
		return Arrays.asList(
				new Column("vipType", ColumnType.ENUM)
				);
	}
	
	public VIP(String vip) {
		try {
			this.vip = VIPEnum.valueOf(vip);	
		} catch (NullPointerException ignore) {
			this.vip = VIPEnum.NORMAL;
		}
	}
	
	public boolean hasNoVIP() {
		return vip == VIPEnum.NORMAL;
	}
	
	public VIPEnum getVIPType() {
		return vip;
	}
}
