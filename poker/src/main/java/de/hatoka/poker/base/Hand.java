package de.hatoka.poker.base;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Hand
{
    public static Hand valueOf(List<Card> handCards, List<Card> boardCards)
    {
        return new Hand(handCards, boardCards);
    }

    private final List<Card> hole;
    private final List<Card> boardCards;

    private Hand(List<Card> handCards, List<Card> boardCards)
    {
        this.hole = handCards;
        this.boardCards = boardCards;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("h:(");
        for (Card card : hole)
        {
            builder.append(" ").append(card.toString());
        }
        builder.append(")  b:(");
        for (Card card : boardCards)
        {
            builder.append(" ").append(card.toString());
        }
        builder.append(")");
        return builder.toString().trim();
    }

    public List<Card> getHoleCards()
    {
        return hole;
    }

    public List<Card> getBoardCards()
    {
        return boardCards;
    }

    public Set<Card> getCards()
    {
        Set<Card> result = new HashSet<>();
        result.addAll(getBoardCards());
        result.addAll(hole);
        return result;
    }

}
