package cz.GravelCZLP.Breakpoint.players;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.GravelCZLP.Breakpoint.equipment.BPEquipment;
import me.limeth.storageAPI.Column;
import me.limeth.storageAPI.ColumnType;
import me.limeth.storageAPI.Storage;

public class LobbyInventory {
	private static int SLOT_AMOUNT = 28;
	private static int MAX_SERIALIZED_SIZE = 128;
	private BPEquipment[] contents;

	public LobbyInventory(BPEquipment... contents) {
		this.contents = contents;
	}

	public static final LobbyInventory load(Storage storage) throws Exception {
		String[] rawContents = storage.tryGetArray("lobbyInventory", String.class, SLOT_AMOUNT);
		BPEquipment[] contents = new BPEquipment[SLOT_AMOUNT];

		for (int i = 0; i < SLOT_AMOUNT; i++) {
			if (rawContents[i] != null) {
				String[] raw = rawContents[i].split(",");
				contents[i] = BPEquipment.deserialize(raw);
			} else {
				contents[i] = null;
			}
		}

		return new LobbyInventory(contents);
	}

	public void save(Storage storage) {
		List<String> serialized = new ArrayList<>();

		for (int i = 0; i < SLOT_AMOUNT; i++) {
			BPEquipment bpEquipment = this.contents[i];

			if (bpEquipment != null) {
				String value = bpEquipment.serialize();
				serialized.add(value);
				continue;
			} else {
				serialized.add(null);
			}
		}

		storage.put("lobbyInventory", serialized);
	}

	public static List<Column> getRequiredMySQLColumns() {// (size + divider) *
															// slots + bracket
		return Arrays
				.asList(new Column("lobbyInventory", ColumnType.VARCHAR, (MAX_SERIALIZED_SIZE + 1) * SLOT_AMOUNT + 1));
	}

	public boolean isEmpty() {
		for (BPEquipment content : this.contents) {
			if (content != null) {
				return false;
			}
		}

		return true;
	}

	public BPEquipment[] getContents() {
		return this.contents;
	}

	public void setContents(BPEquipment[] contents) {
		this.contents = contents;
	}
}
