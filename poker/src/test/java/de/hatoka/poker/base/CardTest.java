package de.hatoka.poker.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class CardTest
{
    Card testCard = Card.valueOf(Image.KING, Color.CLUBS);

    @Test
    public void testCardStringString()
    {
        Card testCard = Card.valueOf("K", "c");
        assertEquals(Image.KING,testCard.getImage());
        assertEquals(Color.CLUBS,testCard.getColor());
    }

    @Test
    public void testCardString()
    {
        Card testCard = Card.valueOf("Kc");
        assertEquals(Image.KING, testCard.getImage());
        assertEquals(Color.CLUBS, testCard.getColor());
    }

    @Test
    public void testCompareTo()
    {
        Card card1 = Card.valueOf(Image.KING, Color.CLUBS);
        Card card2 = Card.valueOf(Image.QUEEN, Color.CLUBS);
        Card card3 = Card.valueOf(Image.KING, Color.SPADES);
        Card card4 = Card.valueOf(Image.ACE, Color.SPADES);

        assertTrue(0 == testCard.compareTo(card1));
        assertTrue(0 < testCard.compareTo(card2));
        assertTrue(0 < testCard.compareTo(card3));
        assertTrue(0 > testCard.compareTo(card4));
    }

    @Test
    public void testToString()
    {
        assertTrue(testCard.toString().equals("Kc"));
    }

    @Test
    public void testListOfCards()
    {
        List<Card> cards = Card.deserialize("Kc Qc");
        assertEquals(Card.valueOf(Image.KING, Color.CLUBS), cards.get(0));
        assertEquals(Card.valueOf(Image.QUEEN, Color.CLUBS), cards.get(1));
        assertEquals("Kc Qc", Card.serialize(cards));
    }
}
