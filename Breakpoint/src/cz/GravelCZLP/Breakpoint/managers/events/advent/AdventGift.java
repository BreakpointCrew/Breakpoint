package cz.GravelCZLP.Breakpoint.managers.events.advent;

import java.util.LinkedList;
import java.util.List;

import cz.GravelCZLP.Breakpoint.equipment.BPBlock;

public class AdventGift {
	private final BPBlock block;
	private final List<String> giftedTo;

	public AdventGift(BPBlock block, List<String> giftedTo) {
		if (block == null) {
			throw new IllegalArgumentException("block == null");
		}

		this.block = block;
		this.giftedTo = giftedTo != null ? giftedTo : new LinkedList<>();
	}

	public AdventGift(BPBlock block) {
		this(block, null);
	}

	public void addGiftedTo(String playerName) {
		this.giftedTo.add(playerName);
	}

	public boolean hasEarned(String playerName) {
		return this.giftedTo.contains(playerName);
	}

	public List<String> getGiftedTo() {
		return this.giftedTo;
	}

	public BPBlock getBlock() {
		return this.block;
	}
}
