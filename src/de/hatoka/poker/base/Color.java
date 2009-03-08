package de.hatoka.poker.base;

public class Color {
	public static final int DIAMONDS = 0;
	public static final int HEARTS   = 1;
	public static final int SPADES   = 2;
	public static final int CLUBS    = 3;
	
	public static final int COUNT = 4;

	public static String names[] = { 
		"d","h","s","c"
	};
	
	public static String descriptions[] = { 
		"Diamonds","Hearts","Spades","Clubs"
	};

	public static Color elements[] = { 
		new Color(Color.DIAMONDS),new Color(Color.HEARTS),new Color(Color.SPADES),new Color(Color.CLUBS)
	};

	public static Color GetColor(int index) {
		return elements[index];
	};

	int index = 0;
	Color (int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getName() {
		return names[index];
	}
	
	public String getDescription() {
		return descriptions[index];
	}
	
	public String toString() {
		return getName();
	}
}
