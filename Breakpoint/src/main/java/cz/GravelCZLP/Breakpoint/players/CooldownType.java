package cz.GravelCZLP.Breakpoint.players;

public enum CooldownType {
	HEAL("Breakpoint.heal"), 
	POTION_RAW("Breakpoint.chemik.potion.", true), 
	BOW_PYRO("Breakpoint.pyroman.bow"), 
	BOW_SNIPER("Breakpoint.odstrelovac.bow"), 
	BLAZE_ROD_MAGE("Breakpoint.cernokneznik.blaze_rod"), 
	STICK_MAGE("Breakpoint.cernokneznik.stick"), 
	FEATHER_MAGE("Breakpoint.cernokneznik.feather"),
	
	PYRO_EFFECT("Breakpoint.pyro.effects.flame");

	private final String path;
	private final boolean raw;

	private CooldownType(String path) {
		this(path, false);
	}

	private CooldownType(String path, boolean raw) {
		this.path = path;
		this.raw = raw;
	}

	public String getPath() {
		return this.path;
	}

	public boolean isRaw() {
		return this.raw;
	}
}
