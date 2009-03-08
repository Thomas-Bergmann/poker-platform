package de.hatoka.poker.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.base.Hand;
import de.hatoka.poker.base.Rank;

public class TexasHoldem
{
    private static final Function<Collection<Card>, Rank> RANKER = new HandToRank();
    private static final HandToIndex HAND_TO_INDEX = new HandToIndex();

    public static int compare(Hand a, Hand b)
    {
        if (a == null && b == null) return 0;
        if (a == null)
        {
            return -1;
        }
        if (b == null)
        {
            return -1;
        }
        return HAND_TO_INDEX.apply(a.getCards()).compareTo(HAND_TO_INDEX.apply(b.getCards()));
    }

    /**
     * @param hand with all available cards
     * @return the used cards from the hand
     */
    public static Hand getBestHand(Hand hand)
    {
        List<Card> bestCards = HAND_TO_INDEX.getBestCards(hand.getCards());
        List<Card> usedHandCards = new ArrayList<>();
        List<Card> usedBoardCards = new ArrayList<>();
        for(Card card : bestCards)
        {
            if (hand.getHoleCards().contains(card))
            {
                usedHandCards.add(card);
            }
            else
            {
                usedBoardCards.add(card);
            }
        }
        return Hand.valueOf(usedHandCards, usedBoardCards);
    }

    public static List<Card> getBestCards(Hand hand)
    {
        return HAND_TO_INDEX.getBestCards(getBestHand(hand).getCards());
    }

    public static Integer getIndex(Set<Card> cards)
    {
        return HAND_TO_INDEX.apply(cards);
    }

    public static Rank getRank(Hand hand)
    {
        return RANKER.apply(hand.getCards());
    }
}
