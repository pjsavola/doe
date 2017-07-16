import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class HarvestAlgorithm {
	
    public static boolean isValid(final Set<Tile> selectedTiles, final Set<TownSite> townSites) {
    	final Map<TownSite, List<Tile>> matching = new HashMap<>();
    	for (final TownSite townSite : townSites) {
    		matching.put(townSite, new ArrayList<>());
    	}
    	for (final Tile tile : selectedTiles) {
    		final Set<TownSite> visited = new HashSet<>();
    		if (!bpm(tile, visited, matching)) {
    			return false;
    		}
    	}
    	return true;
    }

    private static boolean bpm(final Tile tile, final Set<TownSite> visited, final Map<TownSite, List<Tile>> matching) {
    	for (final TownSite neighbor : tile.getTownSites()) {
    		if (visited.add(neighbor)) {
    			final List<Tile> assigned = matching.get(neighbor);
    			if (assigned == null) {
    				// Town site does not belong to the player
    				continue;
    			}
    			if (assigned.size() < neighbor.size()) {
    				assigned.add(tile);
    				return true;
    			}
    			// Attempt re-assigning any of the already assigned sites
    			for (final Tile assignedTile : assigned) {
    				if (bpm(assignedTile, visited, matching)) {
    					assigned.remove(assignedTile);
    					assigned.add(tile);
    					return true;
    				}
    			}
    		}
    	}
    	return false;
    }
}
