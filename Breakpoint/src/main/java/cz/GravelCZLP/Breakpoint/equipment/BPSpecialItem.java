/*
* Class By GravelCZLP at 7. 5. 2017
*/

package cz.GravelCZLP.Breakpoint.equipment;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BPSpecialItem extends BPEquipment {

	private SpecialItemType type;
	
	public BPSpecialItem(String name, int minutesLeft, SpecialItemType type) {
		super(name, minutesLeft);
		this.type = type;
	}

	@Override
	public String getEquipmentLabel() {
		return "specialitem";
	}
	
	@Override
	protected ItemStack getItemStackRaw() {
		String name = getName();
		ItemStack is = new ItemStack(type.getMaterial());
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		is.setItemMeta(im);
		return is;
	}

	@Override
	protected String serializeRaw() {
		return getMinutesLeft() + "," + type.name();
	}
	
	
	public static enum SpecialItemType {
		
		BLAZE_ROD(Material.BLAZE_ROD),
		STICK(Material.STICK),
		FEATHER(Material.FEATHER),
		MELOUN(Material.SPECKLED_MELON);
		
		private Material matType;
		
		private SpecialItemType(Material mat) {
			this.matType = mat;
		}
		public Material getMaterial() {
			return matType;
		}
	}
}
