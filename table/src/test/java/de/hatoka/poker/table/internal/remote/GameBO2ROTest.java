package de.hatoka.poker.table.internal.remote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.remote.GameEventRO;
import de.hatoka.poker.remote.GameEventRO.Action;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.history.lifecycle.ShowdownEvent;
import de.hatoka.poker.table.internal.event.GameInfo;
import de.hatoka.poker.table.internal.event.DealerResourceLoader;
import de.hatoka.poker.table.internal.json.GameEventType;
import tests.de.hatoka.poker.table.TableTestConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TableTestConfiguration.class })
class GameBO2ROTest
{
    private static final DealerResourceLoader LOADER = new DealerResourceLoader();

    @Autowired
    private GameBO2RO underTest;

    private GameInfo getDealerInfo(String resource, int numberOfEvents) throws URISyntaxException, IOException
    {
        return LOADER.load(getResourceURI(resource), numberOfEvents);
    }

    private GameInfo getDealerInfo(String resource) throws URISyntaxException, IOException
    {
        return LOADER.load(getResourceURI(resource));
    }

    private URI getResourceURI(String resourcePath) throws URISyntaxException
    {
        URL resource = getClass().getResource(resourcePath);
        assertNotNull(resource, "Can't load resource " + resourcePath);
        return resource.toURI();
    }

    @Test
    void testHiddenCarsAreNotIn() throws Exception
    {
        GameInfo dealerInfo = getDealerInfo("GameBO2ROTest_TestNo1.csv", 26);
        List<GameEventRO> result = underTest.getEvents(dealerInfo);
        assertEquals(Action.start, result.get(0).getAction());
        assertEquals(Action.blind, result.get(1).getAction());
        assertEquals(Action.blind, result.get(2).getAction());
        // hole cards are not public
        assertEquals(Action.call, result.get(3).getAction());
        assertEquals(Action.bet, result.get(4).getAction());
        assertEquals(Action.call, result.get(5).getAction());
        assertEquals(Action.pot, result.get(6).getAction());
        // flop
        assertEquals(Action.cards, result.get(7).getAction());
        assertEquals("{\"cards\":\"5s 2d Qh\"}", result.get(7).getInfo());
    }

    @Test
    void testShowdownHoleCards() throws Exception
    {
        GameInfo dealerInfo = getDealerInfo("GameBO2ROTest_TestNo2.csv");
        List<GameEventRO> result = underTest.getEvents(dealerInfo);
        // flop
        assertEquals(Action.cards, result.get(7).getAction());
        assertEquals("{\"cards\":\"8s 8h 2d\"}", result.get(7).getInfo());
        // turn
        assertEquals(Action.cards, result.get(11).getAction());
        assertEquals("{\"cards\":\"7h\"}", result.get(11).getInfo());
        // river
        assertEquals(Action.cards, result.get(15).getAction());
        assertEquals("{\"cards\":\"Td\"}", result.get(15).getInfo());
        // pot
        assertEquals(Action.pot, result.get(18).getAction());
        assertEquals("{\"pot-size\":25}", result.get(18).getInfo());
        // showdown
        assertEquals(Action.showdown, result.get(19).getAction());
        ShowdownEvent showdown = (ShowdownEvent) GameEventType.Showdown.deserialize(result.get(19).getInfo());
        assertEquals("6c Kd", Card.serialize(showdown.getCardsHole().get(SeatRef.globalRef("seat:3@table:3c5ededf-b795-4abd-bd18-68ae2a183a7f"))));
        assertEquals("9h Ks", Card.serialize(showdown.getCardsHole().get(SeatRef.globalRef("seat:0@table:3c5ededf-b795-4abd-bd18-68ae2a183a7f"))));
    }
}
