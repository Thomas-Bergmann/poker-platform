package de.hatoka.poker.table.capi.event.history.seat;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.history.PublicGameEvent;

public interface CoinEvent extends PublicGameEvent
{
    @JsonIgnore
    Integer getCoins();
    
    @JsonIgnore
    public SeatRef getSeat();
    
    @JsonIgnore
    default Optional<SeatRef> getOptionalSeat()
    {
        return Optional.ofNullable(getSeat());
    }

    @JsonIgnore
    default boolean isReset()
    {
        return false;
    }
}
