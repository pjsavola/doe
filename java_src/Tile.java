import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Tile {
	
	static Random r = new Random();
	public static Color black = new Color(0x000000);
	private final Type type;
	
	private final static Font font = new Font("Arial", Font.BOLD, 12);
	final Point coords;
	private final List<Tile> neighbors = new ArrayList<>();
	private final List<TownSite> townSites = new ArrayList<>();
	private Player owner;
	private Cube cube;
	
	public Tile(final int x, final int y, final Map<Point, Tile> tiles, final Set<TownSite> townSites) {
		coords = new Point(x, y);
		Tile current = tiles.get(new Point(x - 1, y));
		current = markAdjacent(tiles.get(new Point(x - 1, y + 1)), current, townSites);
		current = markAdjacent(tiles.get(new Point(x, y + 1)), current, townSites);
		current = markAdjacent(tiles.get(new Point(x + 1, y)), current, townSites);
		current = markAdjacent(tiles.get(new Point(x + 1, y - 1)), current, townSites);
		current = markAdjacent(tiles.get(new Point(x, y - 1)), current, townSites);
		current = markAdjacent(tiles.get(new Point(x - 1, y)), current, townSites);
		tiles.put(coords, this);
		type = Type.values()[r.nextInt(Type.values().length)];
		cube = new Cube(type);
	}
	
	public List<TownSite> getTownSites() {
		return townSites;
	}
	
	public List<Tile> getNeighbors() {
		return neighbors;
	}
	
	public Cube getCube() {
		return cube;
	}
	
	public Player getOwner() {
		return owner;
	}

	public void setCube(final Cube cube) {
		this.cube = cube;
	}
	
	public int getX(final int x, final double radius) {
		return (int) (x + coords.y * Math.sqrt(3) / 2 * radius + coords.x * Math.sqrt(3) * radius);
	}
	
	public int getY(final int y, final double radius) {
		return (int) (y - coords.y * 1.5 * radius);
	}
	
	private Tile markAdjacent(final Tile tile, final Tile prev, final Set<TownSite> townSites) {
		if (tile != null) {
			neighbors.add(tile);
			tile.neighbors.add(this);
			if (prev != null) {
				final TownSite townSite = new TownSite(tile, prev, this);
				this.townSites.add(townSite);
				tile.townSites.add(townSite);
				prev.townSites.add(townSite);
				townSites.add(townSite);
			}
		}
		return tile;
	}
	
    private double findAngle(double fraction) {
        return fraction * Math.PI * 2 + Math.toRadians((90 + 180) % 360);
    }
    
    private Point findPoint(final int x, final int y, final double radius, double angle) {
        int px = (int) (x + Math.cos(angle) * radius);
        int py = (int) (y + Math.sin(angle) * radius);
        return new Point(px, py);
    }
	
	public void draw(final Graphics2D g, final int x, final int y, final double radius) {
		final int px = getX(x, radius);
		final int py = getY(y, radius);
        final int[] xpoints = new int[6];
		final int[] ypoints = new int[6];
        for (int p = 0; p < 6; p++) {
            final double angle = findAngle((double) p / 6);
            final Point point = findPoint(px, py, radius, angle);
            xpoints[p] = point.x;
            ypoints[p] = point.y;
        }
        
        // Store before changing.
        Stroke tmpS = g.getStroke();
        Color tmpC = g.getColor();
        
        g.setColor(type.getColor());
        g.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

		g.fillPolygon(xpoints, ypoints, 6);
		g.setColor(black);
		g.drawPolygon(xpoints, ypoints, 6);

		if (cube != null) {
			cube.draw(g, px - 6, py - 20);	
        }
		
		final String text = coords.x + "," + coords.y;
		g.setFont(font);
        final FontMetrics metrics = g.getFontMetrics();
        int w = metrics.stringWidth(text);
        int h = metrics.getHeight();
        g.drawString(text, px - w/2, py + h/2); 
		
        // Set values to previous when done.
        g.setColor(tmpC);
        g.setStroke(tmpS);
	}
}
