package test.de.hatoka.poker.base;

import static org.junit.Assert.*;

import org.junit.Test;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.base.Color;
import de.hatoka.poker.base.Image;

public class CardTest {
    Card testCard = new Card(Card.KING,Card.CLUBS);

	@Test
	public void testCard() {
 		assertFalse(null == testCard);
	}

	@Test
	public void testGetImage() {
		assertTrue(Image.KING == testCard.getImage().getIndex());
	}

	@Test
	public void testGetColor() {
		assertTrue(Color.CLUBS == testCard.getColor().getIndex());
	}

	@Test
	public void testCompareTo() {
        Card card1 = new Card(Card.KING, Card.CLUBS);
        Card card2 = new Card(Card.QUEEN,Card.CLUBS);
        Card card3 = new Card(Card.KING, Card.SPADES);
        Card card4 = new Card(Card.ACE,  Card.SPADES);
        
		assertTrue(0 == testCard.compareTo(card1));
		assertTrue(0 <  testCard.compareTo(card2));
		assertTrue(0 <  testCard.compareTo(card3));
		assertTrue(0 >  testCard.compareTo(card4));
	}

	@Test
	public void testToString() {
		assertTrue(testCard.toString().equals("Kc"));
	}

}
