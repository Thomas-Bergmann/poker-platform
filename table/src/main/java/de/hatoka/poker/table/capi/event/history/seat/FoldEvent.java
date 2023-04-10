package de.hatoka.poker.table.capi.event.history.seat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.history.PublicGameEvent;

public class FoldEvent implements PublicGameEvent, PlayerEvent
{
    @JsonProperty("seat")
    private String seat;

    @JsonProperty("by-dealer")
    private boolean byDealer = false;

    @JsonIgnore
    public SeatRef getSeat()
    {
        return SeatRef.globalRef(this.seat);
    }

    @JsonIgnore
    public void setSeat(SeatRef seat)
    {
        this.seat = seat.getGlobalRef();
    }

    public boolean isByDealer()
    {
        return byDealer;
    }

    public void setByDealer(boolean byDealer)
    {
        this.byDealer = byDealer;
    }
}
