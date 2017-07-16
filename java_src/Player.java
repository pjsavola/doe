import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class Player {
	private Color color = new Color(0xFF0000);
	private Set<TownSite> townSites = new HashSet<>();
	private Map<Type, TreeSet<Converter>> foodConverters = new HashMap<>();
	private Map<Type, TreeSet<Converter>> productionConverters = new HashMap<>();
	private Map<Type, TreeSet<Converter>> wealthConverters = new HashMap<>();
	private int food;
	private int production;
	private int wealth;
	private int unhappiness;
	
	public Player() {
		// Basic tech
		foodConverters.computeIfAbsent(Type.GRASS, key -> new TreeSet<>()).add(new Converter(1, 2));
		foodConverters.computeIfAbsent(Type.PLAINS, key -> new TreeSet<>()).add(new Converter(1, 1));
		foodConverters.computeIfAbsent(Type.FOREST, key -> new TreeSet<>()).add(new Converter(1, 1));
		foodConverters.computeIfAbsent(Type.WATER, key -> new TreeSet<>()).add(new Converter(1, 1));
		foodConverters.computeIfAbsent(Type.HILLS, key -> new TreeSet<>()).add(new Converter(1, 1));
		productionConverters.computeIfAbsent(Type.FOREST, key -> new TreeSet<>()).add(new Converter(1, 2));
		productionConverters.computeIfAbsent(Type.PLAINS, key -> new TreeSet<>()).add(new Converter(1, 1));
		productionConverters.computeIfAbsent(Type.HILLS, key -> new TreeSet<>()).add(new Converter(1, 1));
		productionConverters.computeIfAbsent(Type.MOUNTAIN, key -> new TreeSet<>()).add(new Converter(1, 1));
		productionConverters.computeIfAbsent(Type.DESERT, key -> new TreeSet<>()).add(new Converter(1, 1));
		wealthConverters.computeIfAbsent(Type.WATER, key -> new TreeSet<>()).add(new Converter(1, 2));
		wealthConverters.computeIfAbsent(Type.GRASS, key -> new TreeSet<>()).add(new Converter(1, 1));
		wealthConverters.computeIfAbsent(Type.PLAINS, key -> new TreeSet<>()).add(new Converter(1, 1));
		wealthConverters.computeIfAbsent(Type.HILLS, key -> new TreeSet<>()).add(new Converter(1, 1));
		wealthConverters.computeIfAbsent(Type.DESERT, key -> new TreeSet<>()).add(new Converter(1, 1));
	}
	
	public Color getColor() {
		return color;
	}
	
	public Set<TownSite> getTownSites() {
		return townSites;
	}
	
	public int convertToFood(final Collection<Cube> cubes) {
		return convert(cubes, foodConverters);
	}
	
	public int convertToProduction(final Collection<Cube> cubes) {
		return convert(cubes, productionConverters);
	}
	
	public int convertToWealth(final Collection<Cube> cubes) {
		return convert(cubes, wealthConverters);
	}
	
	public void modifyFood(final int delta) {
		food += delta;
	}
	
	public void modifyProduction(final int delta) {
		production += delta;
	}
	
	public void modifyWealth(final int delta) {
		wealth += delta;
	}
	
	public int getUnhappiness() {
		return unhappiness;
	}
	
	private int convert(final Collection<Cube> cubes, final Map<Type, TreeSet<Converter>> converters) {
		final Map<Type, List<Cube>> typeLists = new HashMap<>();
		for (final Cube cube : cubes) {
			typeLists.computeIfAbsent(cube.getType(), key -> new ArrayList<>()).add(cube);
		}
		int result = 0;
		for (final Map.Entry<Type, List<Cube>> entry : typeLists.entrySet()) {
			final TreeSet<Converter> converterSet = converters.get(entry.getKey());
			if (converterSet == null) {
				continue;
			}
			for (final Converter converter : converterSet) {
				result += converter.convert(entry.getValue());
				if (entry.getValue().isEmpty()) {
					break;
				}
			}
		}
		return result;
	}
	
	private static class Converter implements Comparable<Converter> {
		private int input;
		private int output;
		
		public Converter(final int input, final int output) {
			this.input = input;
			this.output = output;
		}
		
		// Descending order in order to use best converters as much as possible
		@Override
		public int compareTo(final Converter other) {
			return 2 * output / input - 2 * other.output / other.input;
		}
		
		public int convert(final List<Cube> list) {
			int convertResult = 0;
			while (list.size() >= input) {
				int elemsToRemove = input;
				while (elemsToRemove-- > 0) {
					list.remove(0);
				}
				convertResult += output;
			}
			return convertResult;
		}
	}
}
