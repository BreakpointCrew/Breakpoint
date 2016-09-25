package cz.GravelCZLP.Breakpoint.maps;

import java.awt.image.BufferedImage;

public class Display {
	private static final int mapSize = 128;
	private final int tileWidth, tileHeight;
	private final short topLeftMapId;
	private final byte[][] surface;

	public Display(int tileWidth, int tileHeight) {
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.surface = new byte[mapSize * tileWidth][mapSize * tileHeight];
		this.topLeftMapId = MapManager.getNextFreeId(getTileAmount());
	}

	public byte[][] getSurface() {
		return this.surface;
	}

	public int getTileAmount() {
		return this.tileWidth * this.tileHeight;
	}

	public byte getColor(int x, int y) {
		return this.surface[x][y];
	}

	public void setColor(int x, int y, byte color) {
		this.surface[x][y] = color;
	}

	public int getTileWidth() {
		return this.tileWidth;
	}

	public int getTileHeight() {
		return this.tileHeight;
	}

	public short getTopLeftMapId() {
		return this.topLeftMapId;
	}

	public static void initializeWithImage(String path) {
		BufferedImage img = BPMapRenderer.getImage(path);
		int surfaceWidth = img.getWidth();
		int surfaceHeight = img.getHeight();
		byte[][] bytes = BPMapRenderer.toBytes(img);
		int tileWidth = surfaceWidth / mapSize + (surfaceWidth % mapSize > 0 ? 1 : 0);
		int tileHeight = surfaceHeight / mapSize + (surfaceHeight % mapSize > 0 ? 1 : 0);
		Display display = new Display(tileWidth, tileHeight);
		int startX = (tileWidth * mapSize - surfaceWidth) / 2;
		int startY = (tileHeight * mapSize - surfaceWidth) / 2;

		for (int x = 0; x < surfaceWidth; x++) {
			for (int y = 0; y < surfaceHeight; y++) {
				display.setColor(startX + x, startY + y, bytes[x][y]);
			}
		}
	}
}
