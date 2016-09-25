package cz.GravelCZLP.Breakpoint.maps;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;

import cz.GravelCZLP.Breakpoint.game.BPMap;
import cz.GravelCZLP.Breakpoint.game.ctf.CTFMap;

public class CurrentMapRenderer extends BPMapRenderer {
	private byte[][] image;

	public CurrentMapRenderer(CTFMap map) {
		setCurrentMap(map);
	}

	public CurrentMapRenderer() {
		this(null);
	}

	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		if (this.image != null) {
			drawImage(canvas);
		}
	}

	public void setCurrentMap(BPMap map) {
		this.image = toBytes(map != null ? map.getImage() : null);
	}

	public void drawImage(MapCanvas canvas) {
		for (int x = 0; x < this.image.length; x++) {
			for (int y = 0; y < this.image[0].length; y++) {
				canvas.setPixel(x, y, this.image[x][y]);
			}
		}
	}
}
