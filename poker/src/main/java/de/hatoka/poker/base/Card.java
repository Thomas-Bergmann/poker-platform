package de.hatoka.poker.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Card implements Comparable<Card>
{
    public static final Comparator<Card> BY_IMAGE = (a, b) -> a.getImage().compareTo(b.getImage());
    public static final Comparator<Card> BY_COLOR = (a, b) -> a.getColor().compareTo(b.getColor());

    public static List<Card> deserialize(String cards)
    {
        if (cards == null || cards.isBlank())
        {
            return Collections.emptyList();
        }
        String[] parts = cards.split(" ");
        List<Card> result = new ArrayList<>(parts.length);
        for (String cardPart : parts)
        {
            result.add(Card.valueOf(cardPart.trim()));
        }
        return Collections.unmodifiableList(result);
    }

    public static String serialize(List<Card> cards)
    {
        StringBuilder out = new StringBuilder();
        for (int index = 0; index < cards.size(); index++)
        {
            if (index > 0) out = out.append(" ");
            out = out.append(cards.get(index).toString());
        }
        return out.toString();
    }

    public static Card valueOf(String image, String color)
    {
        return new Card(Image.valueViaAbbreviation(image), Color.valueViaAbbreviation(color));
    }

    public static Card valueOf(String card)
    {
        return Card.valueOf(card.substring(0, 1), card.substring(1, 2));
    }

    public static Card valueOf(Image image, Color color)
    {
        return new Card(image, color);
    }

    private final Color color;
    private final Image image;

    private Card(Image image, Color color)
    {
        this.color = color;
        this.image = image;
    }

    public Color getColor()
    {
        return color;
    }

    public Image getImage()
    {
        return image;
    }

    @Override
    public int compareTo(Card aCard) throws ClassCastException
    {
        int compare = this.image.compareTo(aCard.getImage());
        if (compare != 0)
        {
            return compare;
        }
        return this.color.compareTo(aCard.color);
    }

    @Override
    public String toString()
    {
        return image.getAbbreviation().concat(color.getAbbreviation());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(color, image);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Card other = (Card)obj;
        return color == other.color && image == other.image;
    }
}
