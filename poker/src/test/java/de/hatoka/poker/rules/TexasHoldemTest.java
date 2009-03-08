package de.hatoka.poker.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.base.Hand;

class TexasHoldemTest
{
    @Test
    void test()
    {
        Hand hand = Hand.valueOf(Card.deserialize("8c 2h"), Card.deserialize("8h Jc 7h"));
        Set<Card> expectedCards = new HashSet<>();
        expectedCards.addAll(Card.deserialize("8c 8h Jc 7h 2h"));
        assertEquals(expectedCards, TexasHoldem.getBestHand(hand).getCards());
    }
}
