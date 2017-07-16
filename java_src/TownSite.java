import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class TownSite {

	private final List<Tile> neighbors = new ArrayList<>();
	private Player owner;
	private int size;
	
	public TownSite(final Tile h1, final Tile h2, final Tile h3) {
		neighbors.add(h1);
		neighbors.add(h2);
		neighbors.add(h3);
	}
	
	public void found(final Player owner) {
		this.owner = owner;
		owner.getTownSites().add(this);
		increaseSize();
	}
	
	public void increaseSize() {
		if (owner == null) {
			throw new RuntimeException("Town site must have owner");
		}
		if (size == 3) {
			throw new RuntimeException("Size > 3 not allowed");
		}
		size++;
	}
	
	public void decreaseSize() {
		if (size == 0) {
			throw new RuntimeException("Size < 0 not allowed");
		}
		size--;
		if (size == 0) {
			owner.getTownSites().remove(this);
			owner = null;
		}
	}
	
	public void draw(Graphics2D g, final int x, final int y, final double radius) {
		if (size == 0) return;
		
		double avgX = (neighbors.get(0).coords.x + neighbors.get(1).coords.x + neighbors.get(2).coords.x) / 3.0;
		double avgY = (neighbors.get(0).coords.y + neighbors.get(1).coords.y + neighbors.get(2).coords.y) / 3.0;
		
		final int px = (int) (x + avgY * Math.sqrt(3) / 2 * radius + avgX * Math.sqrt(3) * radius);
	    final int py = (int) (y - avgY * 1.5 * radius);

	    g.setColor(owner.getColor());
	    switch (size) {
	    case 1:    
		    g.fillOval(px - 5, py - 5, 10, 10);
		    break;
	    case 2:
		    g.fillOval(px - 10, py - 5, 10, 10);
		    g.fillOval(px, py - 5, 10, 10);
		    break;
	    case 3:
		    g.fillOval(px - 10, py, 10, 10);
		    g.fillOval(px, py, 10, 10);
		    g.fillOval(px - 5, py - 8, 10, 10);
		    break;
	    }
	}
	
	public int size() {
		return size;
	}
	
	public Player getOwner() {
		return owner;
	}
}