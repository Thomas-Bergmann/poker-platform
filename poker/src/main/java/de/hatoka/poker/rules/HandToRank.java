package de.hatoka.poker.rules;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.base.Color;
import de.hatoka.poker.base.Image;
import de.hatoka.poker.base.Rank;

/**
 * Calculates the rank for a given list of cards.
 */
public class HandToRank implements Function<Collection<Card>, Rank> 
{
    @Override
    public Rank apply(Collection<Card> cards)
    {
        // all cards allowed in Texas Holdem
        Set<Rank> ranks = new HashSet<>();
        Map<Image, Integer> handImages = HandIndexHelper.getHandImages(cards);
        for (Entry<Image, Integer> entry : handImages.entrySet())
        {
            Integer numberOfImages = entry.getValue();
            // four of kind
            if (numberOfImages == 4)
            {
                ranks.add(Rank.FOUR_OF_KIND);
            }
            if (numberOfImages == 3)
            {
                ranks.add(Rank.THREE_OF_KIND);
            }
            if (numberOfImages == 2)
            {
                if (ranks.contains(Rank.ONE_PAIR))
                {
                    ranks.add(Rank.TWO_PAIR);
                }
                else
                {
                    ranks.add(Rank.ONE_PAIR);
                }
            }
        }
        if (ranks.contains(Rank.THREE_OF_KIND) && ranks.contains(Rank.ONE_PAIR))
        {
            ranks.add(Rank.FULL_HOUSE);
        }
        // test straight
        if (isStraight(handImages.keySet()))
        {
            ranks.add(Rank.STRAIGHT);
        }
        // test flush
        for (Entry<Color, Integer> entry : HandIndexHelper.getHandColors(cards).entrySet())
        {
            if (entry.getValue() > 4)
            {
                ranks.add(Rank.FLUSH);
            }
        }
        // test straight flush
        if (ranks.contains(Rank.FLUSH) && ranks.contains(Rank.STRAIGHT))
        {
            Collection<Card> flushCards = HandIndexHelper.filterCardsOfFlush(cards);
            if (isStraight(HandIndexHelper.getHandImages(flushCards).keySet()))
            {
                ranks.add(Rank.STRAIGHT_FLUSH);
            }
        }

        // find highest rank
        Rank highest = Rank.HIGH_CARD;
        for (Rank i : ranks)
        {
            if (i.isBetterThan(highest))
            {
                highest = i;
            }
        }
        return highest;
    }

    private boolean isStraight(Collection<Image> images)
    {
        if (images.size() < 5)
        {
            return false;
        }
        return HandIndexHelper.getStraightImages(images).size() > 4;
    }

}
