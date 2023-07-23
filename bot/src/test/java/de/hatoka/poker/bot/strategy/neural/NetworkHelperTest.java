package de.hatoka.poker.bot.strategy.neural;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.base.Rank;

class NetworkHelperTest
{
    private final NetworkHelper networkHelper = new NetworkHelper();

    @Test
    void testInputImage()
    {
        List<Double> inputs = networkHelper.getInputImage(Card.deserialize("Ad"));
        assertEquals(1, inputs.size());
        assertEquals(1, inputs.get(0));
        inputs = networkHelper.getInputImage(Card.deserialize("2d"));
        assertEquals(1, inputs.size());
        assertTrue(inputs.get(0) < 0.1d);
    }

    @Test
    void testInputColor()
    {
        List<Double> inputs = networkHelper.getInputColor(Card.deserialize("Ad"));
        assertEquals(1, inputs.size());
        assertEquals(0.25, inputs.get(0));
        inputs = networkHelper.getInputColor(Card.deserialize("Ah"));
        assertEquals(1, inputs.size());
        assertEquals(0.5, inputs.get(0));
    }

    @Test
    void testInputCards()
    {
        List<Double> inputs = networkHelper.getInputCards(Card.deserialize("Ad"));
        assertEquals(2, inputs.size());
        assertEquals(1, inputs.get(0));
        assertEquals(0.25, inputs.get(1));

        assertEquals(4, networkHelper.getInputCards(Card.deserialize("Ad 8d")).size());
        assertEquals(10, networkHelper.getInputCards(Card.deserialize("Ah As Th As Ts")).size());
    }

    @Test
    void testInputRank()
    {
        List<Double> inputs = networkHelper.getInputRank(Rank.FLUSH);
        assertEquals(9, inputs.size());
        assertEquals(0.0, inputs.get(0));
        assertEquals(0.0, inputs.get(1));
        assertEquals(0.0, inputs.get(2));
        assertEquals(0.0, inputs.get(3));
        assertEquals(0.0, inputs.get(4));
        assertEquals(1.0, inputs.get(Rank.FLUSH.getIndex()));
        assertEquals(0.0, inputs.get(6));
        assertEquals(0.0, inputs.get(7));
        assertEquals(0.0, inputs.get(8));
    }
}
