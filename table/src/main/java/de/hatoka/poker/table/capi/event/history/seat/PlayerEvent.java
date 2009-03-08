package de.hatoka.poker.table.capi.event.history.seat;

import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.history.GameEvent;

public interface PlayerEvent extends GameEvent
{
    SeatRef getSeat();

    void setSeat(SeatRef seat);
}
