package de.hatoka.poker.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.hatoka.poker.base.Card;

public class HandToIndexTest
{
    private static final HandToIndex UNDER_TEST = new HandToIndex();

    @Test
    public void testHighCardToHighCard()
    {
        List<Card> cardsHigh = Card.deserialize("Ac Ks Qh Jd 2c");
        List<Card> cardsLow = Card.deserialize("Ks Qh Jd Tc 2c 3s");
        assertTrue(0 < UNDER_TEST.apply(cardsHigh).compareTo(UNDER_TEST.apply(cardsLow)));
    }

    @Test
    public void testHighCardToPair()
    {
        List<Card> cardsHigh = Card.deserialize("Ac Ks Kh Jd 2c");
        List<Card> cardsLow = Card.deserialize("Ac Ks Qh Jd 2c");
        assertTrue(0 < UNDER_TEST.apply(cardsHigh).compareTo(UNDER_TEST.apply(cardsLow)));
    }


    @Test
    public void testComparePairToPair()
    {
        List<Card> cardsLow = Card.deserialize("9d Ks Jd Kd Ac");
        List<Card> cardsHigh = Card.deserialize("Ad Ks Jd 8d Ac");
        assertTrue(0 < UNDER_TEST.apply(cardsHigh).compareTo(UNDER_TEST.apply(cardsLow)));
    }

    @Test
    public void testComparePairToPairWithKicker()
    {
        List<Card> cardsLow = Card.deserialize("Ad Qs Jd 8d Ac");
        List<Card> cardsHigh = Card.deserialize("Ad Ks Jd 8d Ac");
        assertTrue(0 < UNDER_TEST.apply(cardsHigh).compareTo(UNDER_TEST.apply(cardsLow)));
    }

    @Test
    public void testComparePairToPairWithThirdPair()
    {
        List<Card> cardsLow = Card.deserialize("Ad Ac Ts Td 8d 8c");
        List<Card> cardsHigh = Card.deserialize("Ad Ac Ts Td Qc");
        assertTrue(0 < UNDER_TEST.apply(cardsHigh).compareTo(UNDER_TEST.apply(cardsLow)));
    }

    @Test
    public void testComparePairToThreeOfKind()
    {
        List<Card> cardsLow = Card.deserialize("Ad Ks Jd 8d Ac");
        List<Card> cardsHigh = Card.deserialize("8d 8s Jd 8d Ac");
        assertTrue(0 < UNDER_TEST.apply(cardsHigh).compareTo(UNDER_TEST.apply(cardsLow)));
    }

    @Test
    public void testCompareThreeOfKindWithKicker()
    {
        List<Card> cardsLow = Card.deserialize("8d 8s Jd 8d Qc");
        List<Card> cardsHigh = Card.deserialize("8d 8s Jd 8d Ac");
        assertTrue(0 < UNDER_TEST.apply(cardsHigh).compareTo(UNDER_TEST.apply(cardsLow)));
    }

    @Test
    public void testCompareThreeOfKindWithStraight()
    {
        List<Card> cardsLow = Card.deserialize("8d 8s Jd 8d Ac");
        List<Card> cardsHigh = Card.deserialize("3d 4s 5d 7d Ac 2c");
        assertTrue(0 < UNDER_TEST.apply(cardsHigh).compareTo(UNDER_TEST.apply(cardsLow)));
    }

    @Test
    public void testCompareStraightWithStraight()
    {
        List<Card> cardsLow = Card.deserialize("8d 9s Jd 7d Tc 2c");
        List<Card> cardsHigh = Card.deserialize("8d 9s Jd 7d Tc Qc");
        assertEquals("Jd Tc 9s 8d 7d", Card.serialize(UNDER_TEST.getBestCards(cardsLow)));
        assertEquals("Qc Jd Tc 9s 8d", Card.serialize(UNDER_TEST.getBestCards(cardsHigh)));
        assertTrue(0 < UNDER_TEST.apply(cardsHigh).compareTo(UNDER_TEST.apply(cardsLow)));
    }

    @Test
    public void testStraightToFlush()
    {
        List<Card> cardsLow = Card.deserialize("8d 9s Jd 7d Tc Qc 2d");
        List<Card> cardsHigh = Card.deserialize("8d 9s Jd 7d Tc Qd 2d");
        assertTrue(0 < UNDER_TEST.apply(cardsHigh).compareTo(UNDER_TEST.apply(cardsLow)));
    }

    @Test
    public void testFlushVsFlush()
    {
        List<Card> cardsLow = Card.deserialize("8d 9s Jd 7d Tc Qd 2d");
        List<Card> cardsHigh = Card.deserialize("8d 9s Jd 7d Tc Qd Kd");
        assertTrue(0 < UNDER_TEST.apply(cardsHigh).compareTo(UNDER_TEST.apply(cardsLow)));
    }

    @Test
    public void testFlushVsFullHouse()
    {
        // List<Card> board  = Card.deserialize("8d 9s Kh Qd Kd");
        List<Card> cardsLow = Card.deserialize("8d 9s Kh Qd Kd 6d Ad");
        List<Card> cardsHigh = Card.deserialize("8d 9s Kh Qd Kd 8s Ks");
        assertTrue(0 < UNDER_TEST.apply(cardsHigh).compareTo(UNDER_TEST.apply(cardsLow)));
    }

    @Test
    public void testStraightToFullHouse()
    {
        List<Card> cardsLow = Card.deserialize("Ac Kh Qh Jh Th");
        List<Card> cardsHigh = Card.deserialize("Ah Ac Qh Qd As");
        assertTrue(0 < UNDER_TEST.apply(cardsHigh).compareTo(UNDER_TEST.apply(cardsLow)));
    }

    @Test
    public void testFullHouseVsFullHouse()
    {
        List<Card> cardsLow = Card.deserialize("Ah Ac Qh Qd As Ks");
        List<Card> cardsHigh = Card.deserialize("Ah Ac Kh Qd As Ks");
        assertTrue(0 < UNDER_TEST.apply(cardsHigh).compareTo(UNDER_TEST.apply(cardsLow)));
    }

    @Test
    public void testFullHouseVsFour()
    {
        List<Card> cardsLow = Card.deserialize("Ah Ac Kh Qd As Ks");
        List<Card> cardsHigh = Card.deserialize("Qh Ac Qc Qd Qs Ks");
        assertTrue(0 < UNDER_TEST.apply(cardsHigh).compareTo(UNDER_TEST.apply(cardsLow)));
    }

    @Test
    public void testFullFourVsFourWithKicker()
    {
        List<Card> cardsLow = Card.deserialize("Qh 8c Qc Qd Qs 8s");
        List<Card> cardsHigh = Card.deserialize("Qh 7c Qc Qd Qs Ts");
        assertTrue(0 < UNDER_TEST.apply(cardsHigh).compareTo(UNDER_TEST.apply(cardsLow)));
    }

    @Test
    public void testFullFourVsStraightFlush()
    {
        List<Card> cardsLow = Card.deserialize("Qh 7c Qc Qd Qs Ts");
        List<Card> cardsHigh = Card.deserialize("8c 9c Jc 7c Tc Qd");
        assertTrue(0 < UNDER_TEST.apply(cardsHigh).compareTo(UNDER_TEST.apply(cardsLow)));
    }

    @Test
    public void testFullStraightFlushVsStraightFlush()
    {
        List<Card> cardsLow = Card.deserialize("8c 9c Jc 7c Tc Qd");
        List<Card> cardsHigh = Card.deserialize("8c 9c Jc 7c Tc Qc");
        assertTrue(0 < UNDER_TEST.apply(cardsHigh).compareTo(UNDER_TEST.apply(cardsLow)));
    }
}
