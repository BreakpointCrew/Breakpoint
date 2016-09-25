package cz.GravelCZLP.Breakpoint.statistics;

import cz.GravelCZLP.Breakpoint.players.clans.Clan;

public class CWMatchResult {
	private long timestamp;
	private String opponent;
	private int[] points;

	public CWMatchResult(long timestamp, String opponent, int[] points) {
		setTimestamp(timestamp);
		setOpponent(opponent);
		setPoints(points);
	}

	public boolean hasWon() {
		return this.points[0] > this.points[1];
	}

	public boolean hasLost() {
		return this.points[1] > this.points[0];
	}

	public boolean wasDraw() {
		return this.points[0] == this.points[1];
	}

	public CWMatchResult(Clan opponent, int[] points) {
		this(System.currentTimeMillis(), opponent.getColoredName(), points);
	}

	public String serialize() {
		return this.timestamp + "," + this.opponent + "," + this.points[0] + "," + this.points[1];
	}

	public static CWMatchResult unserialize(String string) {
		String[] values = string.split(",");

		return new CWMatchResult(Long.parseLong(values[0]), values[1],
				new int[] { Integer.parseInt(values[2]), Integer.parseInt(values[3]) });
	}

	public String getOpponent() {
		return this.opponent;
	}

	public final void setOpponent(String opponent) {
		if (opponent == null) {
			throw new IllegalArgumentException("opponent == null");
		}

		this.opponent = opponent;
	}

	public int[] getPoints() {
		return this.points;
	}

	public final void setPoints(int[] points) {
		if (points == null) {
			throw new IllegalArgumentException("points == null");
		} else if (points.length != 2) {
			throw new IllegalArgumentException("points.length != 2");
		}

		this.points = points;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
