package de.hatoka.poker.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeckTest
{
    private static final String DECK_ONE = "seed: 123456 index: 2 cards: 8c 4d 2h 3c Ad 8h";
    private Deck testDeck;

    @BeforeEach
    public void initDeck()
    {
        Deck.initializeRandom(123456L);
        testDeck = Deck.generate();
    }

    @Test
    public void testGetNextCard()
    {
        Card card = testDeck.getNextCard();
        assertEquals("8c", card.toString());
        Card card2 = testDeck.getNextCard();
        assertEquals("4d", card2.toString());
    }

    @Test
    public void testGetCountCards()
    {
        assertEquals(52, testDeck.getCountCards());
    }

    @Test
    public void testShuffle()
    {
        assertTrue(0 == testDeck.getOutCards());
        Card card1 = testDeck.getNextCard();
        testDeck = Deck.generate();
        assertTrue(0 == testDeck.getOutCards());
        Card card2 = testDeck.getNextCard();
        assertNotSame(card2, card1);
    }

    @Test
    public void testGetOutCards()
    {
        assertTrue(0 == testDeck.getOutCards());
        testDeck.getNextCard();
        assertTrue(1 == testDeck.getOutCards());
    }

    @Test
    public void testToString()
    {
        testDeck.getNextCard();
        testDeck.getNextCard();
        assertTrue(testDeck.toString().startsWith(DECK_ONE), "was: '" + testDeck.toString() + "'");
        Deck serialDeck = Deck.deserialize(DECK_ONE);
        assertEquals(2, serialDeck.getOutCards());
        assertTrue(serialDeck.toString().startsWith(DECK_ONE), "was: '" + testDeck.toString() + "'");
    }

    @Test
    public void testPredefinedSeed()
    {
        Deck.initializeRandom(4961115982468162243L);
        Deck specificDeck = Deck.generate();
        assertEquals("Qs", specificDeck.getNextCard().toString());
        assertEquals("2s", specificDeck.getNextCard().toString());
        assertEquals("Jh", specificDeck.getNextCard().toString());
    }
}
