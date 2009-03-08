package de.hatoka.poker.base;

public class Card 
	extends Object 
	implements Comparable<Object> 
{
	public static final Color DIAMONDS = Color.GetColor(Color.DIAMONDS);
	public static final Color HEARTS   = Color.GetColor(Color.HEARTS);
	public static final Color SPADES   = Color.GetColor(Color.SPADES);
	public static final Color CLUBS    = Color.GetColor(Color.CLUBS);

	public static final Image TWO   = Image.GetImage(Image.TWO);
	public static final Image THREE = Image.GetImage(Image.THREE);
	public static final Image FOUR  = Image.GetImage(Image.FOUR);
	public static final Image FIVE  = Image.GetImage(Image.FIVE);
	public static final Image SIX   = Image.GetImage(Image.SIX);
	public static final Image SEVEN = Image.GetImage(Image.SEVEN);
	public static final Image EIGTH = Image.GetImage(Image.EIGTH);
	public static final Image NINE  = Image.GetImage(Image.NINE);
	public static final Image TEN   = Image.GetImage(Image.TEN);
	public static final Image JACK  = Image.GetImage(Image.JACK);
	public static final Image QUEEN = Image.GetImage(Image.QUEEN);
	public static final Image KING  = Image.GetImage(Image.KING);
	public static final Image ACE   = Image.GetImage(Image.ACE);
	
	private Color color = null;
	private Image image = null;
	
	public Card (Image image, Color color) {
		this.color = color;
		this.image = image;
	}
	
	public Color getColor () {
		return color;
	}
	
	public Image getImage () {
		return image;
	}

	public int getIndex() {
		return image.getIndex() * Image.COUNT + color.getIndex();
	}
	
	public int compareTo(Object aCard) throws ClassCastException {
	    if (!(aCard instanceof Card))
	      throw new ClassCastException("A Card object expected.");
	    Card card = (Card) aCard;
	    return getIndex() - card.getIndex();    
	}
	
	public String toString () {
		return image.getName().concat(color.getName());
	}

}
