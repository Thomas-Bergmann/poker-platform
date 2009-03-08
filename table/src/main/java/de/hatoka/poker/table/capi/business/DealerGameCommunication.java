package de.hatoka.poker.table.capi.business;

import java.util.List;

import de.hatoka.poker.table.capi.event.history.DealerEvent;
import de.hatoka.poker.table.capi.event.history.GameEvent;
import de.hatoka.poker.table.capi.event.history.seat.PlayerEvent;

public interface DealerGameCommunication
{
    /**
     * Player creating events
     * @param event was made
     */
    void publishEvent(PlayerEvent event);

    /**
     * Dealer creating events
     * @param event was made
     */
    void publishEvent(DealerEvent event);

    List<GameEvent> getAllEvents();
}
