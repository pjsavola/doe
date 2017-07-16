import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class PlayArea extends JPanel {
	private static final long serialVersionUID = 1L;

	private final int WIDTH = 1200;
    private final int HEIGHT = 800;

    private final int W2 = WIDTH / 2;
    private final int H2 = HEIGHT / 2;
    
    private double radius = 40;
    
	final Map<Point, Tile> grid = new HashMap<>();;
	final Set<TownSite> townSites = new HashSet<>();
	final List<Player> players = new ArrayList<>();
	HarvestAction action;
	
	public PlayArea(int playerCount) {
		new Tile(-1, 0, grid, townSites);
		new Tile(-2, 0, grid, townSites);
		new Tile(0, 0, grid, townSites);
		new Tile(1, 0, grid, townSites);
		new Tile(2, 0, grid, townSites);
		new Tile(-2, 1, grid, townSites);
		new Tile(-1, 1, grid, townSites); 
		new Tile(0, 1, grid, townSites);
		new Tile(1, 1, grid, townSites);
		new Tile(-1, -1, grid, townSites); 
		new Tile(0, -1, grid, townSites);
		new Tile(1, -1, grid, townSites);
		new Tile(2, -1, grid, townSites);
		
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		while (playerCount-- > 0) {
			players.add(new Player());
		}
	}
	
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g.fillRect(0, 0, WIDTH, HEIGHT);
        drawCircle(g2d, W2, H2, 660, true, true, 0x4488FF, 0);
        for (final Tile tile : grid.values()) {
        	tile.draw(g2d, W2, H2, radius);
        }
        for (final TownSite townSite : townSites) {
        	townSite.draw(g2d, W2, H2, radius);
        }
        action.draw(g);
    }
    
    public void drawCircle(Graphics2D g, int x, int y, int diameter,
            boolean centered, boolean filled, int colorValue, int lineThickness) {
        drawOval(g, x, y, diameter, diameter, centered, filled, colorValue, lineThickness);
    }

    public void drawOval(Graphics2D g, int x, int y, int width, int height,
            boolean centered, boolean filled, int colorValue, int lineThickness) {
        // Store before changing.
        Stroke tmpS = g.getStroke();
        Color tmpC = g.getColor();

        g.setColor(new Color(colorValue));
        g.setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        int x2 = centered ? x - (width / 2) : x;
        int y2 = centered ? y - (height / 2) : y;

        if (filled)
            g.fillOval(x2, y2, width, height);
        else
            g.drawOval(x2, y2, width, height);

        // Set values to previous when done.
        g.setColor(tmpC);
        g.setStroke(tmpS);
    }
    
    public Tile getClosestTile(final int x, final int y) {
    	double minDistance = Double.POSITIVE_INFINITY;
    	Tile closestTile = null;
    	final Point clickPoint = new Point(x, y);
		for (final Tile tile : grid.values()) {
			final Point tilePoint = new Point(tile.getX(W2, radius), tile.getY(H2, radius));
			final double distance = tilePoint.distance(clickPoint);
			if (distance < minDistance && distance < radius) {
				minDistance = distance;
				closestTile = tile;
			}
		}
		return closestTile;
    }
    
    public void startAction() {
    	action = new HarvestAction(this, players.get(0));
    	townSites.iterator().next().found(players.get(0));
    	townSites.iterator().next().increaseSize();
    }
	
    public static void main(String[] args) {
        JFrame f = new JFrame();
        PlayArea p = new PlayArea(2);
        f.setContentPane(p);
        p.startAction();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
