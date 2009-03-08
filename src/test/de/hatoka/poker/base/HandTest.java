package test.de.hatoka.poker.base;

import static org.junit.Assert.*;

import org.junit.Test;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.base.Hand;

public class HandTest {
	static Card AceHearts = new Card(Card.ACE, Card.HEARTS);
	static Card KingHearts = new Card(Card.KING, Card.HEARTS);
	static Card QueenHearts = new Card(Card.QUEEN, Card.HEARTS);
	static Card JackHearts = new Card(Card.JACK, Card.HEARTS);
	static Card TenHearts = new Card(Card.TEN, Card.HEARTS);
	
	Card[] cards = { AceHearts,KingHearts,QueenHearts, JackHearts, TenHearts};
	Hand hand = new Hand(cards);
	
	private void reset() {
		cards[0] = AceHearts;
		cards[1] = KingHearts;
		cards[2] = QueenHearts;
		cards[3] = JackHearts;
		cards[4] = TenHearts;

	}
	@Test
	public void testStraightFlush() {
		reset();
		assertEquals(Hand.STRAIGHT_FLUSH, hand.getRankOfCards(cards));
		assertEquals(Card.ACE, hand.getRank(1));
		assertEquals("StraightFlush A", hand.toString());

		cards[0] = new Card(Card.ACE, Card.CLUBS);
		cards[1] = new Card(Card.TWO, Card.CLUBS);
		cards[2] = new Card(Card.FOUR, Card.CLUBS);
		cards[3] = new Card(Card.THREE, Card.CLUBS);
		cards[4] = new Card(Card.FIVE, Card.CLUBS);
		assertEquals(Hand.STRAIGHT_FLUSH, hand.getRankOfCards(cards));
		assertEquals(Card.FIVE, hand.getRank(1));
		assertEquals(null, hand.getRank(2));
		assertEquals("StraightFlush 5", hand.toString());

		cards[0] = new Card(Card.SIX, Card.CLUBS);
		cards[1] = new Card(Card.TWO, Card.CLUBS);
		cards[2] = new Card(Card.FOUR, Card.CLUBS);
		cards[3] = new Card(Card.THREE, Card.CLUBS);
		cards[4] = new Card(Card.FIVE, Card.CLUBS);
		assertEquals(Hand.STRAIGHT_FLUSH, hand.getRankOfCards(cards));
		assertEquals(Card.SIX, hand.getRank(1));
		assertEquals(null, hand.getRank(2));
	}
	
	@Test
	public void testStraight() {
		reset();
		cards[0] = new Card(Card.ACE, Card.CLUBS);
		assertEquals(Hand.STRAIGHT, hand.getRankOfCards(cards));
	}

	@Test
	public void testFlush() {
		reset();
		cards[0] = new Card(Card.EIGTH, Card.HEARTS);
		assertEquals(Hand.FLUSH, hand.getRankOfCards(cards));
		assertEquals(Card.KING, hand.getRank(1));
		assertEquals(Card.QUEEN, hand.getRank(2));
		assertEquals(Card.JACK, hand.getRank(3));
		assertEquals(Card.TEN, hand.getRank(4));
		assertEquals(Card.EIGTH, hand.getRank(5));
	}

	@Test
	public void testOnePair() {
		reset();
		cards[1] = new Card(Card.ACE, Card.CLUBS);
		assertEquals(Hand.ONE_PAIR, hand.getRankOfCards(cards));
		assertEquals(Card.ACE, hand.getRank(1));
		assertEquals(Card.QUEEN, hand.getRank(2));
		assertEquals("OnePair AQJT", hand.toString());
	}
	
	@Test
	public void testTwoPair() {
		reset();
		cards[1] = new Card(Card.ACE, Card.CLUBS);
		cards[3] = new Card(Card.QUEEN, Card.CLUBS);
		assertEquals(Hand.TWO_PAIR, hand.getRankOfCards(cards));
		assertEquals(Card.ACE, hand.getRank(1));
		assertEquals(Card.QUEEN, hand.getRank(2));
		assertEquals(Card.TEN, hand.getRank(3));
	}

	@Test
	public void testThreeOfKind() {
		reset();
		cards[1] = new Card(Card.ACE, Card.CLUBS);
		cards[2] = new Card(Card.ACE, Card.SPADES);
		assertEquals(Hand.THREE_OF_KIND, hand.getRankOfCards(cards));
		assertEquals(Card.ACE, hand.getRank(1));
		assertEquals(Card.JACK, hand.getRank(2));
		assertEquals(Card.TEN, hand.getRank(3));
		reset();
		cards[1] = new Card(Card.TEN, Card.CLUBS);
		cards[2] = new Card(Card.TEN, Card.SPADES);
		assertEquals(Hand.THREE_OF_KIND, hand.getRankOfCards(cards));
		assertEquals(Card.TEN, hand.getRank(1));
		assertEquals(Card.ACE, hand.getRank(2));
		assertEquals(Card.JACK, hand.getRank(3));
	}
	
	@Test
	public void testFourOfKind() {
		reset();
		cards[1] = new Card(Card.ACE, Card.CLUBS);
		cards[2] = new Card(Card.ACE, Card.SPADES);
		cards[4] = new Card(Card.ACE, Card.DIAMONDS);
		assertEquals(Hand.FOUR_OF_KIND, hand.getRankOfCards(cards));
		assertEquals(Card.ACE, hand.getRank(1));
		assertEquals(Card.JACK, hand.getRank(2));
	}
	
	@Test
	public void testFullHouse() {
		reset();
		cards[1] = new Card(Card.ACE, Card.CLUBS);
		cards[4] = new Card(Card.ACE, Card.SPADES);
		cards[3] = new Card(Card.QUEEN, Card.DIAMONDS);
		assertEquals(Hand.FULL_HOUSE, hand.getRankOfCards(cards));
		assertEquals(Card.ACE, hand.getRank(1));
		assertEquals(Card.QUEEN, hand.getRank(2));
	}	
}
