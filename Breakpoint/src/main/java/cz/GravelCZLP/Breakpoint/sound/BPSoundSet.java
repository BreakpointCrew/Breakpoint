package cz.GravelCZLP.Breakpoint.sound;

public class BPSoundSet {
	private final BPSound[] sounds;

	public BPSoundSet(BPSound... sounds) {
		this.sounds = sounds;
	}

	public BPSound[] getSounds() {
		return this.sounds;
	}
}
