package test.de.hatoka.poker.base;

import static org.junit.Assert.*;

import org.junit.Test;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.base.Deck;

public class DeckTest {
	Deck testDeck = new Deck((long)123456);
	
	@Test
	public void testGetNextCard() {
		Card card = testDeck.getNextCard();
		assertEquals("8c",card.toString());
		Card card2 = testDeck.getNextCard();
		assertEquals("4d",card2.toString());
	}

	@Test
	public void testGetCountCards() {
		assertEquals(52,testDeck.getCountCards());
	}

	@Test
	public void testShuffle() {
		testDeck.shuffle();
		assertTrue(0== testDeck.getOutCards());
		Card card1 = testDeck.getNextCard();
		testDeck.shuffle();
		assertTrue(0== testDeck.getOutCards());
		Card card2 = testDeck.getNextCard();
		assertNotSame(card2, card1);
	}

	@Test
	public void testGetOutCards() {
		testDeck.shuffle();
		assertTrue(0== testDeck.getOutCards());
		testDeck.getNextCard();
		assertTrue(1== testDeck.getOutCards());
	}

	@Test
	public void testToString() {
		testDeck.shuffle();
		testDeck.getNextCard();
		testDeck.getNextCard();
		assertEquals(testDeck.toString(), "9d 8s");
	}

}
