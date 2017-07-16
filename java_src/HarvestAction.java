import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class HarvestAction {
	private final static Font font = new Font("Arial", Font.BOLD, 12);
	private final Player player;
	private final PlayArea playArea;
	private final Map<Tile, Cube> selection = new LinkedHashMap<>();
	private final Map<Tile, Cube> used = new HashMap<>();
	private int foodCollected = 0;
	private int productionCollected = 0;
	private int wealthCollected = 0;
	private BufferedImage customCursor;
	private Point customCursorLocation;
	
	private final MouseListener mouseListener = new MouseListener() {
    	@Override
		public void mouseReleased(MouseEvent e) {
		}
		@Override
		public void mousePressed(MouseEvent e) {
		}
		@Override
		public void mouseExited(MouseEvent e) {
		}
		@Override
		public void mouseEntered(MouseEvent e) {
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			click(e.getX(), e.getY());
		}
	};
	
	private final MouseMotionListener mouseMotionListener = new MouseMotionListener() {
		@Override
		public void mouseMoved(MouseEvent e) {
			customCursorLocation = new Point(e.getX(), e.getY());
			playArea.repaint();
		}
		@Override
		public void mouseDragged(MouseEvent e) {
		}
	};
	
	private final KeyListener keyListener = new KeyListener() {
		@Override
		public void keyTyped(KeyEvent e) {
			switch (e.getKeyChar()) {
			case 'f':
				foodCollected += player.convertToFood(selection.values());
				useSelection();
				break;
			case 'p':
				productionCollected += player.convertToProduction(selection.values());
				useSelection();
				break;
			case 'w':
				wealthCollected += player.convertToWealth(selection.values());
				useSelection();
				break;
			case 'q':
				foodCollected = 0;
				productionCollected = 0;
				wealthCollected = 0;
				resetSelection();
				break;
			case 'c':
				player.modifyFood(foodCollected);
				player.modifyProduction(productionCollected);
				player.modifyWealth(wealthCollected);
				resetSelection();
				confirm();
				break;
			default:
				break;
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {
		}
		@Override
		public void keyPressed(KeyEvent e) {
		}
	};
	
	public HarvestAction(final PlayArea playArea, final Player player) {
		this.player = player;
		this.playArea = playArea;
		
        playArea.addMouseListener(mouseListener);
        
        playArea.addMouseMotionListener(mouseMotionListener);

        // The JFrame does not seem to be the direct parent.
        playArea.getParent().getParent().getParent().addKeyListener(keyListener);
	}
	
	public void confirm() {
		// Drop all related listeners
        playArea.removeMouseListener(mouseListener);
        playArea.removeMouseMotionListener(mouseMotionListener);
        playArea.getParent().getParent().getParent().removeKeyListener(keyListener);
	}
	
	public void draw(final Graphics g) {
		if (customCursor != null) {
			g.drawImage(customCursor, customCursorLocation.x, customCursorLocation.y, null);
		}
		final String text = "(f)ood: " + foodCollected +
			"   (p)roduction: " + productionCollected +
			"   (w)ealth: " + wealthCollected + 
			"   (c)onfirm" +
			"   (q)uit";
		g.setFont(font);
        final FontMetrics metrics = g.getFontMetrics();
        int w = metrics.stringWidth(text);
        int h = metrics.getHeight();
        g.drawString(text, 0 + w/2, 0 + h);
	}
	
	private void resetSelection() {
		for (final Map.Entry<Tile, Cube> entry : used.entrySet()) {
			entry.getKey().setCube(entry.getValue());
		}
		for (final Map.Entry<Tile, Cube> entry : selection.entrySet()) {
			entry.getKey().setCube(entry.getValue());
		}
		used.clear();
		selection.clear();
		updateCursor();
		playArea.repaint();
	}

	private void useSelection() {
		used.putAll(selection);
		selection.clear();
		updateCursor();
		playArea.repaint();
	}
	
	private void click(final Tile tile) {
		final Player owner = tile.getOwner();
		if (owner != null && owner != player) {
			// Tile owned by another player, cannot select.
			System.err.println("Tile owned by another player");
			return;
		}
		final Map<Player, Integer> totalSizeMap = new HashMap<>();
		for (final TownSite townSite : tile.getTownSites()) {
			final Player townSiteOwner = townSite.getOwner();
			if (townSiteOwner != null) {
				totalSizeMap.putIfAbsent(townSiteOwner, 0);
				totalSizeMap.put(townSiteOwner, totalSizeMap.get(townSiteOwner) + 1);
			}
		}
		final Integer playerTotalSize = totalSizeMap.get(player);
		if (playerTotalSize == null) {
			// Player does not have towns next to the tile, cannot select.
			System.err.println("No towns next to the tile");
			return;
		}
		if (owner == null && playerTotalSize < totalSizeMap.values().stream().max(Integer::max).get()) {
			// Someone owns greater total town size next to the tile, cannot select.
			System.err.println("Too small total town size next to the tile");
			return;
		}
		if (used.containsKey(tile)) {
			// The cube is consumed already in this harvest.
			System.err.println("Cube consumed already");
			return;
		}
		if (selection.containsKey(tile)) {
			tile.setCube(selection.remove(tile));
		} else if (selection.size() < 7) {
			final Set<Tile> allTiles = new HashSet<>();
			allTiles.addAll(selection.keySet());
			allTiles.addAll(used.keySet());
			int totalSize = 0;
			for (final TownSite townSite : player.getTownSites()) {
				totalSize += townSite.size();
			}
			if (allTiles.size() > totalSize - player.getUnhappiness()) {
				System.err.println("Too many cubes selected");
				return;
			}
			selection.put(tile, tile.getCube());
			if (HarvestAlgorithm.isValid(allTiles, player.getTownSites())) {
				tile.setCube(null);
			} else {
				System.err.println("Invalid selection");
				selection.remove(tile);
			}
		}
	}
	
	private void click(final int x, final int y) {
		final Tile closestTile = playArea.getClosestTile(x, y);
		if (closestTile != null) {
			click(closestTile);
			playArea.repaint();
			updateCursor();
		}
    }
	
	private void updateCursor() {
		if (selection.isEmpty()) {
			playArea.setCursor(Cursor.getDefaultCursor());
			customCursor = null;
		} else {
			final int width = selection.size() * 15;
			final int height = 15;
			final Toolkit kit = Toolkit.getDefaultToolkit();
			final BufferedImage image = GraphicsEnvironment
			    .getLocalGraphicsEnvironment()
			    .getDefaultScreenDevice()
			    .getDefaultConfiguration()
			    .createCompatibleImage(width, height, Transparency.TRANSLUCENT);
			final Graphics2D g = image.createGraphics();
			int px = 0;
			for (final Map.Entry<Tile, Cube> entry : selection.entrySet()) {
				entry.getValue().draw(g, px, 0);
				px += 15;
			}
			g.dispose();
			final Cursor blankCursor = kit.createCustomCursor(
				new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), 
				new Point(0, 0), 
				"Blank");
			playArea.setCursor(blankCursor);
			customCursor = image;
		}		
	}
}
