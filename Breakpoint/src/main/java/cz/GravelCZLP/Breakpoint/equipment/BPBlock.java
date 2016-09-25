package cz.GravelCZLP.Breakpoint.equipment;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BPBlock extends BPEquipment {
	private int id;
	private byte data;

	public BPBlock(String name, int minutesLeft, int id, byte data) {
		super(name, minutesLeft);

		this.id = id;
		this.data = data;
	}

	@Override
	protected ItemStack getItemStackRaw() {
		String name = getName();
		@SuppressWarnings("deprecation")
		ItemStack is = new ItemStack(this.id, 1, this.data);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		is.setItemMeta(im);
		return is;
	}

	@Override
	protected String serializeRaw() {
		return getName() + "," + getMinutesLeft() + "," + this.id + "," + this.data;
	}

	@Override
	public String getEquipmentLabel() {
		return "block";
	}

	@Override
	public BPBlock clone() {
		return new BPBlock(getName(), getMinutesLeft(), getId(), getData());
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte getData() {
		return this.data;
	}

	public void setData(byte data) {
		this.data = data;
	}
}
