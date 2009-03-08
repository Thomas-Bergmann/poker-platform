package de.hatoka.poker.table.capi.event.history;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface PublicGameEvent extends GameEvent
{
    /**
     * @return true of content is visible to all players
     */
    @JsonIgnore
    default boolean isPublic()
    {
        return true;
    }

    /**
     * @return public visible even, if the content makes sense
     */
    @JsonIgnore
    default Optional<GameEvent> getPublicEvent()
    {
        return Optional.of(this);
    }
}
