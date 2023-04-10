package de.hatoka.poker.table.capi.event.history.lifecycle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.hatoka.poker.table.internal.json.GameEventType;

class ErrorEventTest
{

    @Test
    void test()
    {
        ErrorEvent underTest = ErrorEvent.valueOf(new RuntimeException());
        String serEvent = GameEventType.serializeEvent(underTest);
        // only de.hatoka.poker
        assertEquals(126, serEvent.length());
    }

}
