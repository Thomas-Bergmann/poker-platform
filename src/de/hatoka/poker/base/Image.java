package de.hatoka.poker.base;

public class Image {

	public static final int TWO   = 0;
	public static final int THREE = 1;
	public static final int FOUR  = 2;
	public static final int FIVE  = 3;
	public static final int SIX   = 4;
	public static final int SEVEN = 5;
	public static final int EIGTH = 6;
	public static final int NINE  = 7;
	public static final int TEN   = 8;
	public static final int JACK  = 9;
	public static final int QUEEN = 10;
	public static final int KING  = 11;
	public static final int ACE   = 12;
	
	public static final int COUNT = 13;

	public static String names[] = {
		"2","3","4","5","6","7","8","9","T","J","Q","K","A"
	};
	
	public static String descriptions[] = {
		"Two","Three","Four","Five","Six","Seven","Eight","Nine","Ten","Jack","Queen","King","Ace"
	};

	public static Image elements[] = { 
		new Image(Image.TWO),new Image(Image.THREE),new Image(Image.FOUR),new Image(Image.FIVE),new Image(Image.SIX),
		new Image(Image.SEVEN),new Image(Image.EIGTH),new Image(Image.NINE),new Image(Image.TEN),
		new Image(Image.JACK),new Image(Image.QUEEN),new Image(Image.KING),new Image(Image.ACE)
	};

	public static Image GetImage(int index) {
		return elements[index];
	};

	int index = 0;
	Image (int index) {
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
