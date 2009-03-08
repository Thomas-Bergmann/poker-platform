package de.hatoka.poker.base;

import java.util.Comparator;
import java.util.NoSuchElementException;

public enum Image
{
    TWO(0, "2", "Two"), THREE(1, "3", "Three"), FOUR(2, "4", "Four"), FIVE(3, "5", "Five"), SIX(4, "6", "Six"),
    SEVEN(5, "7", "Seven"), EIGTH(6, "8", "Eight"), NINE(7, "9", "Nine"), TEN(8, "T", "Ten"), JACK(9, "J", "Jack"),
    QUEEN(10, "Q", "Queen"), KING(11, "K", "King"), ACE(12, "A", "Ace");

    public static final Comparator<Image> IMAGE = (a, b) -> a.compareTo(b);

    public static Image valueViaIndex(int index)
    {
        for (Image aImage : Image.values())
        {
            if (aImage.getIndex() == index) return aImage;
        }
        throw new NoSuchElementException("Image doesn't exist: '" + index + "'");
    }

    public static Image valueViaAbbreviation(String image)
    {
        for (Image aImage : Image.values())
        {
            if (aImage.getAbbreviation().equals(image)) return aImage;
        }
        throw new NoSuchElementException("Image doesn't exist: '" + image + "'");
    }

    private final int index;
    private final String abbreviation;
    private final String englishName;

    private Image(int index, String abbreviation, String englishName)
    {
        this.index = index;
        this.abbreviation = abbreviation;
        this.englishName = englishName;
    }

    public int getIndex()
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
