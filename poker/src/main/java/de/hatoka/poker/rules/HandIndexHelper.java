package de.hatoka.poker.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.base.Color;
import de.hatoka.poker.base.Image;

public class HandIndexHelper
{
    private static final Comparator<Image> IMAGE = (a, b) -> a.compareTo(b);

    public static Map<Image, Integer> getHandImages(Collection<Card> cards)
    {
        Map<Image, Integer> result = new HashMap<>();

        // count different cards
        for (Card card : cards)
        {
            Image image = card.getImage();
            Integer current = result.get(image);
            if (current == null)
            {
                result.put(image, 1);
            }
            else
            {
                result.put(image, current + 1);
            }
        }
        return result;
    }

    public static Map<Color, Integer> getHandColors(Collection<Card> cards)
    {
        Map<Color, Integer> result = new HashMap<>();

        // count different cards
        for (Card card : cards)
        {
            Color color = card.getColor();
            Integer current = result.get(color);
            if (current == null)
            {
                result.put(color, 1);
            }
            else
            {
                result.put(color, current + 1);
            }
        }
        return result;
    }

    public static List<Card> filterCardsOfFlush(Collection<Card> cards)
    {
        Color color = getHandColors(cards).entrySet()
                                          .stream()
                                          .filter(e -> e.getValue() > 4)
                                          .map(e -> e.getKey())
                                          .findAny()
                                          .get();
        return cards.stream().filter(c -> color.equals(c.getColor())).toList();
    }

    public static List<Image> getStraightImages(Collection<Image> images)
    {
        LinkedList<Image> sortedImages = new LinkedList<>();
        sortedImages.addAll(images.stream().sorted(IMAGE).toList());
        if (images.contains(Image.ACE))
        {
            sortedImages.addFirst(Image.ACE);
        }
        Image lastImage = sortedImages.get(0);
        List<Image> collectedImages = new ArrayList<>();
        collectedImages.add(lastImage);
        List<Image> result = new ArrayList<>(5);
        for (int i = 1; i < sortedImages.size(); i++)
        {
            Image current = sortedImages.get(i);
            if (!isFollowedBy(lastImage, current))
            {
                if (collectedImages.size() > 4)
                {
                    result.clear();
                    result.addAll(collectedImages);
                }
                collectedImages.clear();
            }
            lastImage = current;
            collectedImages.add(current);
        }
        if (collectedImages.size() > 4)
        {
            result.clear();
            result.addAll(collectedImages);
        }
        Collections.reverse(result);
        return result;
    }

    private static boolean isFollowedBy(Image lastImage, Image current)
    {
        return Image.ACE.equals(lastImage) && Image.TWO.equals(current) || !Image.ACE.equals(lastImage)
                        && current.equals(Image.valueViaIndex(lastImage.getIndex() + 1));
    }

}
