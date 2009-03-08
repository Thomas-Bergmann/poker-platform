package de.hatoka.poker.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck
{
    private static final int DECK_LENGTH = Color.values().length * Image.values().length;
    private static final Random seedGenerator = new Random();
    private static Random generator = new Random();
    private static Long lastSeed = 0L;
    private static boolean manuallySet = false;

    public static void resetRandom()
    {
        lastSeed = seedGenerator.nextLong();
        generator = new Random(lastSeed);
        manuallySet = false;
    }

    public static void initializeRandom(long seed)
    {
        lastSeed = seed;
        generator = new Random(lastSeed);
        manuallySet = true;
    }

    private static List<Card> initializeDeck()
    {
        if (!manuallySet)
        {
            lastSeed = seedGenerator.nextLong();
            generator = new Random(lastSeed);
        }
        List<Card> result = new ArrayList<>();
        for (Color c : Color.values())
        {
            for (Image i : Image.values())
            {
                result.add(Card.valueOf(i, c));
            }
        }
        return result;
    }

    private static void shuffle(int countShuffle, List<Card> cards)
    {
        for (int index = 0; index < countShuffle; index++)
        {
            int firstIndex = generator.nextInt(DECK_LENGTH);
            int secondIndex = generator.nextInt(DECK_LENGTH);
            Card firstCard = cards.get(firstIndex);
            Card secondCard = cards.get(secondIndex);
            cards.set(firstIndex, secondCard);
            cards.set(secondIndex, firstCard);
        }
    }
    
    public static Deck generate()
    {
        List<Card> cards = initializeDeck();
        shuffle(DECK_LENGTH * DECK_LENGTH + generator.nextInt(DECK_LENGTH), cards);
        return Deck.valueOf(cards, 0);
    }

    public static String serialize(Deck deck)
    {
        return deck.toString();
    }

    public static Deck deserialize(String deckAsString)
    {
        String[] parts = deckAsString.split(":");
        String[] seedParts = parts[1].split(" ");
        lastSeed = Long.valueOf(seedParts[1]);
        String[] nextCardsParts = parts[2].split(" ");
        Integer nextCard = Integer.valueOf(nextCardsParts[1]);
        List<Card> cards = Card.deserialize(parts[3].trim());
        return new Deck(cards, nextCard);
    }

    public static Deck valueOf(List<Card> cards, Integer nextCard)
    {
        return new Deck(cards, nextCard);
    }

    private final List<Card> cards;
    private int indexNextCard;

    private Deck(List<Card> cards, Integer nextCard)
    {
        this.cards = Collections.unmodifiableList(cards);
        this.indexNextCard = nextCard;
    }

    protected Card getCard(int position)
    {
        return cards.get(position);
    }

    public Card getNextCard()
    {
        if (indexNextCard == DECK_LENGTH)
        {
            return null;
        }
        return getCard(indexNextCard++);
    }

    public List<Card> getNextCards(int amount)
    {
        List<Card> result = new ArrayList<>(amount);
        for(int i = 0; i < amount; i++)
        {
            result.add(getNextCard());
        }
        return result;
    }

    public int getCountCards()
    {
        return DECK_LENGTH;
    }

    public int getOutCards()
    {
        return indexNextCard;
    }

    @Override
    public String toString()
    {
        StringBuilder out = new StringBuilder();
        out = out.append("seed: ").append(lastSeed).append(" index: ").append(indexNextCard).append(" cards: ").append(Card.serialize(cards));
        return out.toString();
    }
}
