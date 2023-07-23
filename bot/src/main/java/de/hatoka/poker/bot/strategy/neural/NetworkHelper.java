package de.hatoka.poker.bot.strategy.neural;

import java.util.ArrayList;
import java.util.List;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.base.Color;
import de.hatoka.poker.base.Image;
import de.hatoka.poker.base.Rank;
import de.hatoka.poker.rules.HandToRank;

public class NetworkHelper
{
    private static final HandToRank hand2rank = new HandToRank();
    private static final int COUNT_IMAGES = Image.values().length;
    private static final int COUNT_COLOR = Color.values().length;

    private Double byImage(Card card)
    {
        double d = card.getImage().getIndex() + 1;
        return d / COUNT_IMAGES;
    }

    private Double byColor(Card card)
    {
        double d = card.getColor().getIndex() + 1;
        return d / COUNT_COLOR;
    }

    List<Double> getInputImage(List<Card> cards)
    {
        return cards.stream().map(this::byImage).toList();
    }

    List<Double> getInputColor(List<Card> cards)
    {
        return cards.stream().map(this::byColor).toList();
    }

    List<Double> getInputCards(List<Card> cards)
    {
        List<Double> result = new ArrayList<>();
        result.addAll(getInputImage(cards));
        result.addAll(getInputColor(cards));
        return result;
    }

    List<Double> getInputRank(Rank rank)
    {
        List<Double> result = new ArrayList<>();
        for (Rank aRank : Rank.values())
        {
            result.add(rank.equals(aRank) ? 1.0 : 0.0);
        }
        return result;
    }

    List<Double> getInputHand(Rank rank, List<Card> cards)
    {
        List<Double> result = new ArrayList<>();
        result.addAll(getInputRank(rank));
        result.addAll(getInputCards(cards));
        return result;
    }

    private double[] mapToInput(List<Double> doubles)
    {
        double[] result = new double[doubles.size()];
        for (int i = 0; i < doubles.size(); i++)
        {
            result[i] = doubles.get(i);
        }
        return result;
    }

    double[] getInputs(List<Card> cards)
    {
        if (cards.size() == 2)
        {
            return mapToInput(getInputCards(cards));
        }
        Rank rank = hand2rank.apply(cards);
        return mapToInput(getInputHand(rank, cards));
    }
}
