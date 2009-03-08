package de.hatoka.poker.base;

import java.util.NoSuchElementException;

public enum Color
{
    DIAMONDS(0, "d", "Diamonds"), HEARTS(1, "h", "Hearts"), SPADES(2, "s", "Spades"), CLUBS(3, "c", "Clubs");

    public static Color valueViaAbbreviation(String color)
    {
        for (Color aColor : Color.values())
        {
            if (aColor.getAbbreviation().equals(color))
            {
                return aColor;
            }
        }
        throw new NoSuchElementException("Color doesn't exist: '" + color + "'");
    };

    private final int index;
    private final String abbreviation;
    private final String englishName;

    private Color(int index, String abbreviation, String englishName)
    {
        this.index = index;
        this.abbreviation = abbreviation;
        this.englishName = englishName;
    }

    protected int getIndex()
    {
        return index;
    }

    public String getAbbreviation()
    {
        return abbreviation;
    }

    public String getDescription()
    {
        return englishName;
    }

    @Override
    public String toString()
    {
        return getAbbreviation();
    }
}
