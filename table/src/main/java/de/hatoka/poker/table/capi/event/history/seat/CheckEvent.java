package de.hatoka.poker.table.capi.event.history.seat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.history.PublicGameEvent;

public class CheckEvent implements PublicGameEvent, ActionEvent, CoinEvent
{
    @JsonProperty("seat")
    private String seat;

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

    @Override
    public Integer getCoins()
    {
        return 0;
    }
}
