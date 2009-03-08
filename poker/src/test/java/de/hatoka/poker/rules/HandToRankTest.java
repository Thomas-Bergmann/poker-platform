package de.hatoka.poker.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.base.Rank;

public class HandToRankTest
{
    private static final Function<Collection<Card>, Rank> UNDER_TEST = new HandToRank();

    @Test
    public void testStraightFlush()
    {
        List<Card> royalFlush = Card.deserialize("Ah Kh Qh Jh Th");
        assertEquals(Rank.STRAIGHT_FLUSH, UNDER_TEST.apply(royalFlush), "StraightFlush AKQJT");

        List<Card> straighFlush = Card.deserialize("Ac 2c 3c 4c 5c");
        assertEquals(Rank.STRAIGHT_FLUSH, UNDER_TEST.apply(straighFlush), "StraightFlush 5432A");

        List<Card> straighFlush2 = Card.deserialize("6c 2c 3c 4c 5c");
        assertEquals(Rank.STRAIGHT_FLUSH, UNDER_TEST.apply(straighFlush2), "StraightFlush 65432");
    }

    @Test
    public void testStraight()
    {
        List<Card> straight = Card.deserialize("Ac Kh Qh Jh Th");
        assertEquals(Rank.STRAIGHT, UNDER_TEST.apply(straight), "Straight AKQJT");
    }

    @Test
    public void testFlush()
    {
        List<Card> flush = Card.deserialize("8h Kh Qh Jh Th");
        assertEquals(Rank.FLUSH, UNDER_TEST.apply(flush), "Flush KQJT8");

        List<Card> flushAddIrrelevantCard = Card.deserialize("8h Kh Qh Jh Th 5c");
        assertEquals(Rank.FLUSH, UNDER_TEST.apply(flushAddIrrelevantCard), "Flush KQJT8");

        // straight and flush, but no straight flush
        List<Card> flushAddRelevantCard = Card.deserialize("9c Kh Qh Jh Th 5h");
        assertEquals(Rank.FLUSH, UNDER_TEST.apply(flushAddRelevantCard), "Flush KQJT5");
    }

    @Test
    public void testOnePair()
    {
        List<Card> cards = Card.deserialize("Ah Ac Qh Jh Th");
        assertEquals(Rank.ONE_PAIR, UNDER_TEST.apply(cards), "OnePair AAQJT");
    }

    @Test
    public void testTwoPair()
    {
        List<Card> cards = Card.deserialize("Ah Ac Qh Qc Th");
        assertEquals(Rank.TWO_PAIR, UNDER_TEST.apply(cards), "TwoPair AAQQT");
    }

    @Test
    public void testThreeOfKind()
    {
        List<Card> cards = Card.deserialize("Ah Ac As Jh Th");
        assertEquals(Rank.THREE_OF_KIND, UNDER_TEST.apply(cards), "ThreeOfKind AAAJT");

        List<Card> cards2 = Card.deserialize("Ah Tc Ts Jh Th");
        assertEquals(Rank.THREE_OF_KIND, UNDER_TEST.apply(cards2), "ThreeOfKind TTTAJ");
    }

    @Test
    public void testFourOfKind()
    {
        List<Card> cards = Card.deserialize("Ah Ac As Jh Ad");
        assertEquals(Rank.FOUR_OF_KIND, UNDER_TEST.apply(cards), "FourOfKind AAAAJ");
    }

    @Test
    public void testFullHouse()
    {
        List<Card> cards = Card.deserialize("Ah Ac Qh Qs As");
        assertEquals(Rank.FULL_HOUSE, UNDER_TEST.apply(cards), "FullHouse AAAQQ");
    }

    @Test
    public void testHighCard()
    {
        List<Card> cards = Card.deserialize("2c Kh Qh Jh Th");
        assertEquals(Rank.HIGH_CARD, UNDER_TEST.apply(cards), "HighCard KQJT2");
    }
}
