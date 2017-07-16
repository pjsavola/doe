import java.awt.Color;


public enum Type {
	GRASS(0x00FF00),
	PLAINS(0xAAFF00),
	WATER(0x0000FF),
	HILLS(0xFFAA00),
	MOUNTAIN(0x999999),
	FOREST(0x008844),
	DESERT(0xFFFF00);
	
	private final int color;
	
	private Type(final int color) {
		this.color = color;
	}
	
	public Color getColor() {
		return new Color(color);
	}
}