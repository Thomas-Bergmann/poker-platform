package de.hatoka.poker.table.capi.event.history;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface PrivateGameEvent extends GameEvent
{
    /**
     * @return true of content is visible to all players
     */
    @JsonIgnore
    default boolean isPublic()
    {
        return false;
    }
}
