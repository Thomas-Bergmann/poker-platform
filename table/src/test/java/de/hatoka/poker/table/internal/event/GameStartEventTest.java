package de.hatoka.poker.table.internal.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.hatoka.poker.base.Deck;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.business.TableRef;
import de.hatoka.poker.table.capi.event.history.lifecycle.StartEvent;
import de.hatoka.poker.table.internal.json.GameEventType;

class GameStartEventTest
{

    private static final TableRef TABLE_REF = TableRef.localRef("table-one");
    private static final SeatRef DEALER_REF = SeatRef.localRef(TABLE_REF, 0);

    @Test
    void test()
    {
        Deck.initializeRandom(123456L);
        StartEvent event = new StartEvent();
        event.setOnButton(DEALER_REF);
        event.setDeck(Deck.generate());
        
        GameEventType type = GameEventType.valueOf(StartEvent.class);
        String asString = type.serialize(event);
        StartEvent serEvent = (StartEvent) type.deserialize(asString);
        assertEquals(DEALER_REF, serEvent.getOnButton());
    }

}
