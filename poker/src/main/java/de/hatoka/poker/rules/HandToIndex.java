package de.hatoka.poker.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.base.Image;
import de.hatoka.poker.base.Rank;

/**
 * Creates an index "value" for a list of cards
 */
public class HandToIndex implements Function<Collection<Card>, Integer>
{
    private static final Function<Collection<Card>, Rank> RANKER = new HandToRank();

    @Override
    public Integer apply(Collection<Card> cards)
    {
        int ext = 13;
        Rank rank = RANKER.apply(cards);
        int value = rank.getIndex();
        for (Card card : getBestCards(cards))
        {
            value = value * ext + card.getImage().getIndex();
        }
        return value;
    }

    public List<Card> getBestCards(Collection<Card> cards)
    {
        Rank rank = RANKER.apply(cards);
        return defineBestCards(rank, cards);
    }

    private List<Card> defineBestCards(Rank rank, Collection<Card> cards)
    {
        List<Card> result = null;
        switch(rank)
        {
            case STRAIGHT_FLUSH:
                Collection<Card> flushCards = HandIndexHelper.filterCardsOfFlush(cards);
                result = getCardsStraight(flushCards);
                break;
            case STRAIGHT:
                result = getCardsStraight(cards);
                break;
            case FOUR_OF_KIND:
                result = getCardsFourOfKind(cards);
                break;
            case FULL_HOUSE:
                result = getCardsFullHouse(cards);
                break;
            case THREE_OF_KIND:
                result = getCardsThreeOfKind(cards);
                break;
            case TWO_PAIR:
                result = getCardsTwoPair(cards);
                break;
            case ONE_PAIR:
                result = getCardsOnePair(cards);
                break;
            case HIGH_CARD:
                result = getCardsHighCard(cards);
                break;
            case FLUSH:
                result = getCardsFlush(cards);
                break;
            default:
                throw new RuntimeException("Unknown hand rank" + rank.toString());
        }
        return result;
    }

    private List<Card> getCardsStraight(Collection<Card> cards)
    {
        Collection<Image> images = HandIndexHelper.getHandImages(cards).keySet();
        List<Image> straightImages = HandIndexHelper.getStraightImages(images);
        List<Card> result = new ArrayList<>();
        for (Image image : straightImages)
        {
            result.add(cards.stream().filter(c -> image.equals(c.getImage())).findAny().get());
        }
        return removeTooMuchCards(result);
    }

    private List<Card> getCardsFourOfKind(Collection<Card> cards)
    {
        Map<Image, Integer> images = HandIndexHelper.getHandImages(cards);
        // multiple only with more the 7 cards
        List<Image> imagesFourTimes = images.entrySet()
                                            .stream()
                                            .filter(e -> e.getValue() == 4)
                                            .map(e -> e.getKey())
                                            .toList();
        Image bestImage = imagesFourTimes.stream().sorted(Image.IMAGE.reversed()).findFirst().get();
        List<Card> theFourCards = cards.stream().filter(c -> bestImage.equals(c.getImage())).toList();
        List<Card> cardsWithOutTheFour = cards.stream().filter(c -> !theFourCards.contains(c)).toList();
        List<Card> result = new ArrayList<>();
        result.addAll(theFourCards);
        result.add(getHighCard(cardsWithOutTheFour));
        return result;
    }

    private Card getHighCard(List<Card> cards)
    {
        return cards.stream().sorted(Card.BY_IMAGE.reversed()).findFirst().get();
    }

    private List<Card> removeTooMuchCards(List<Card> cards)
    {
        while(cards.size() > 5)
        {
            cards.remove(cards.size() - 1);
        }
        return cards;
    }

    private List<Card> getCardsFlush(Collection<Card> cards)
    {
        List<Card> flushCards = HandIndexHelper.filterCardsOfFlush(cards);
        List<Card> result = new ArrayList<>();
        result.addAll(flushCards);
        result.sort(Card.BY_IMAGE.reversed());
        return removeTooMuchCards(result);
    }

    private List<Card> getCardsFullHouse(Collection<Card> cards)
    {
        Map<Image, Integer> images = HandIndexHelper.getHandImages(cards);
        List<Image> imagesThreeTimes = images.entrySet()
                                             .stream()
                                             .filter(e -> e.getValue() == 3)
                                             .map(e -> e.getKey())
                                             .sorted(Image.IMAGE.reversed())
                                             .toList();
        List<Image> imagesTwoTimes = images.entrySet()
                                           .stream()
                                           .filter(e -> e.getValue() == 2)
                                           .map(e -> e.getKey())
                                           .toList();
        Image bestImageThreeTimes = imagesThreeTimes.get(0);
        // second three could be better as pairs, but does not count
        List<Image> imagesThreeOrTwoTimes = new ArrayList<>();
        imagesThreeOrTwoTimes.addAll(imagesThreeTimes);
        imagesThreeOrTwoTimes.addAll(imagesTwoTimes);
        imagesThreeOrTwoTimes.remove(bestImageThreeTimes);
        imagesThreeOrTwoTimes.sort(Image.IMAGE.reversed());
        Image bestImageTwoTimes = imagesThreeOrTwoTimes.get(0);
        List<Card> result = new ArrayList<>();
        result.addAll(cards.stream().filter(c -> bestImageThreeTimes.equals(c.getImage())).toList());
        result.addAll(cards.stream().filter(c -> bestImageTwoTimes.equals(c.getImage())).toList());
        return removeTooMuchCards(result);
    }

    private List<Card> getCardsThreeOfKind(Collection<Card> cards)
    {
        Map<Image, Integer> images = HandIndexHelper.getHandImages(cards);
        List<Image> imagesThreeTimes = images.entrySet()
                                             .stream()
                                             .filter(e -> e.getValue() == 3)
                                             .map(e -> e.getKey())
                                             .sorted(Image.IMAGE.reversed())
                                             .toList();
        Image bestImageThreeTimes = imagesThreeTimes.get(0);
        List<Card> threeOfKind = cards.stream().filter(c -> bestImageThreeTimes.equals(c.getImage())).toList();
        List<Card> result = new ArrayList<>();
        result.addAll(threeOfKind.stream().sorted(Card.BY_COLOR.reversed()).toList());
        // add other cards by rank
        result.addAll(cards.stream().filter(c -> !threeOfKind.contains(c)).sorted(Card.BY_IMAGE.reversed()).toList());
        return removeTooMuchCards(result);
    }

    private List<Card> getCardsTwoPair(Collection<Card> cards)
    {
        Map<Image, Integer> images = HandIndexHelper.getHandImages(cards);
        List<Image> imagesPairs = images.entrySet()
                                        .stream()
                                        .filter(e -> e.getValue() == 2)
                                        .map(e -> e.getKey())
                                        .sorted(Image.IMAGE.reversed())
                                        .toList();
        Image bestPair = imagesPairs.get(0);
        Image secondPair = imagesPairs.get(1);
        List<Card> result = new ArrayList<>();
        result.addAll(cards.stream().filter(c -> bestPair.equals(c.getImage())).sorted(Card.BY_COLOR.reversed()).toList());
        result.addAll(cards.stream().filter(c -> secondPair.equals(c.getImage())).sorted(Card.BY_COLOR.reversed()).toList());
        // add other cards by rank
        result.addAll(cards.stream().filter(c -> !result.contains(c)).sorted(Card.BY_IMAGE.reversed()).toList());
        // all cards to low
        return removeTooMuchCards(result);
    }

    private List<Card> getCardsOnePair(Collection<Card> cards)
    {
        Map<Image, Integer> images = HandIndexHelper.getHandImages(cards);
        List<Image> imagesPairs = images.entrySet()
                                        .stream()
                                        .filter(e -> e.getValue() == 2)
                                        .map(e -> e.getKey())
                                        .sorted(Image.IMAGE.reversed())
                                        .toList();
        Image bestPair = imagesPairs.get(0);
        List<Card> result = new ArrayList<>();
        result.addAll(cards.stream().filter(c -> bestPair.equals(c.getImage())).sorted(Card.BY_COLOR.reversed()).toList());
        // add other cards by rank
        result.addAll(cards.stream().filter(c -> !result.contains(c)).sorted(Card.BY_IMAGE.reversed()).toList());
        // all cards to low
        return removeTooMuchCards(result);
    }

    private List<Card> getCardsHighCard(Collection<Card> cards)
    {
        List<Card> result = new ArrayList<>();
        result.addAll(cards.stream().sorted(Card.BY_IMAGE.reversed()).toList());
        // all cards to low
        return removeTooMuchCards(result);
    }
}
