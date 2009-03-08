package de.hatoka.poker.table.capi.event.history;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.hatoka.poker.table.capi.business.SeatRef;

public interface GameEvent
{
    /**
     * @return true of content is visible to all players
     */
    @JsonIgnore
    public boolean isPublic();

    /**
     * @return seat if event is not public it' visible to this player only
     */
    @JsonIgnore
    default Optional<SeatRef> getOptionalSeat()
    {
        return Optional.empty();
    }

    /**
     * @return public visible even, if the content makes sense
     */
    @JsonIgnore
    Optional<GameEvent> getPublicEvent();
}
