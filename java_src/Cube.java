import java.awt.Color;
import java.awt.Graphics2D;

public class Cube {

	final Type type;
	
	public Cube(final Type type) {
		this.type = type;
	}
	
	public void draw(final Graphics2D g, final int x, final int y) {
		final Color oldColor = g.getColor();
		g.setColor(type.getColor());
		g.fillRect(x, y, 13, 13);
		g.setColor(Tile.black);
		g.drawRect(x, y, 13, 13);
		g.setColor(oldColor);
	}
	
	public Type getType() {
		return type;
	}
}
