package de.hatoka.poker.table.capi.event.history.lifecycle;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.history.DealerEvent;
import de.hatoka.poker.table.capi.event.history.PublicGameEvent;

public class PublicStartEvent implements PublicGameEvent, DealerEvent
{
    public static final PublicStartEvent valueOf(List<SeatRef> seats, SeatRef onButton)
    {
        PublicStartEvent result = new PublicStartEvent();
        result.setSeats(seats);
        result.setOnButton(onButton);
        return result;
    }
    /**
     * Contains players/seats (seat ref) taking part of game
     */
    @JsonProperty("seats")
    private List<String> seats;

    /**
     * Contains player/seat (seat ref) on button (dealer)
     */
    @JsonProperty("button")
    private String onButton;

    @JsonIgnore
    public List<SeatRef> getSeats()
    {
        return seats.stream().map(SeatRef::globalRef).toList();
    }

    @JsonIgnore
    public void setSeats(List<SeatRef> seats)
    {
        this.seats = seats.stream().map(SeatRef::getGlobalRef).toList();
    }

    @JsonIgnore
    public SeatRef getOnButton()
    {
        return SeatRef.globalRef(onButton);
    }

    @JsonIgnore
    public void setOnButton(SeatRef seat)
    {
        this.onButton = seat.getGlobalRef();
    }
}
