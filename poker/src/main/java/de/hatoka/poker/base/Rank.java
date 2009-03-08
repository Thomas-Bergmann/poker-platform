package de.hatoka.poker.base;

public enum Rank
{
    HIGH_CARD(0, "HighCard"), ONE_PAIR(1, "OnePair"), TWO_PAIR(2, "TwoPair"), THREE_OF_KIND(3, "ThreeOfKind"),
    STRAIGHT(4, "Straight"), FLUSH(5, "Flush"), FULL_HOUSE(6, "FullHouse"), FOUR_OF_KIND(7, "FourOfKind"),
    STRAIGHT_FLUSH(8, "StraightFlush");

    private final int index;
    private final String englishName;

    private Rank(int index, String englishName)
    {
        this.index = index;
        this.englishName = englishName;
    }

    public int getIndex()
    {
        return index;
    }

    public String getName()
    {
        return englishName;
    }

    @Override
    public String toString()
    {
        return getName();
    }
    
    public boolean isBetterThan(Rank otherRank)
    {
        return this.getIndex() > otherRank.getIndex();
    }
}
