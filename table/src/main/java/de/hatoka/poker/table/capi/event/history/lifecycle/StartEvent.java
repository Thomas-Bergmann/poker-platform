package de.hatoka.poker.table.capi.event.history.lifecycle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.hatoka.poker.base.Deck;
import de.hatoka.poker.base.Pot;
import de.hatoka.poker.table.capi.business.SeatBO;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.history.DealerEvent;
import de.hatoka.poker.table.capi.event.history.GameEvent;
import de.hatoka.poker.table.capi.event.history.PrivateGameEvent;
import de.hatoka.poker.table.capi.event.history.pot.ChangedPotEvent;

public class StartEvent implements ChangedPotEvent, DealerEvent, PrivateGameEvent
{
    /**
     * Contains players/seats (seat ref) taking part of game
     */
    @JsonProperty("seats")
    private List<String> seats;

    /**
     * Contains players/seats (seat ref) taking part of game
     */
    @JsonProperty("coins-on-seat")
    private Map<String, Integer> coinsOnSeat;

    /**
     * Contains player/seat (seat ref) of dealer
     */
    @JsonProperty("button")
    private String onButton;

    /**
     * Contains the whole deck at the end of the event
     */
    @JsonProperty("deck")
    private String deck;
    
    /**
     * Contains the pot after blinds
     */
    @JsonProperty("pot")
    private String pot;

    /**
     * number of coins for small blind
     */
    @JsonProperty("blind-small")
    private Integer smallBlind;

    /**
     * number of coins for big blind
     */
    @JsonProperty("blind-big")
    private Integer bigBlind;

    @JsonIgnore
    public List<SeatRef> getSeats()
    {
        return seats.stream().map(SeatRef::globalRef).toList();
    }

    @JsonIgnore
    public void setSeats(List<SeatBO> seats)
    {
        this.seats = seats.stream().map(SeatBO::getRef).map(SeatRef::getGlobalRef).toList();
        Map<String, Integer> result = new HashMap<>();
        seats.forEach(s -> result.put(s.getRef().getGlobalRef(), s.getAmountOfCoinsOnSeat()));
        this.coinsOnSeat = result;
    }

    @JsonIgnore
    public Map<SeatRef, Integer> getCoinsOnSeats()
    {
        Map<SeatRef, Integer> result = new HashMap<>();
        coinsOnSeat.forEach((k,v) -> result.put(SeatRef.globalRef(k), v));
        return result;
    }

    @JsonIgnore
    public Deck getDeck()
    {
        return Deck.deserialize(deck);
    }

    @JsonIgnore
    public void setDeck(Deck deck)
    {
        this.deck = Deck.serialize(deck);
    }

    @JsonIgnore
    public Pot getPot()
    {
        return Pot.deserialize(pot);
    }

    @JsonIgnore
    public void setPot(Pot pot)
    {
        this.pot = Pot.serialize(pot);
    }

    @JsonIgnore
    public SeatRef getOnButton()
    {
        return SeatRef.globalRef(onButton);
    }

    @JsonIgnore
    public void setOnButton(SeatRef onButton)
    {
        this.onButton = onButton.getGlobalRef();
    }

    @Override
    public Optional<GameEvent> getPublicEvent()
    {
       return Optional.of(PublicStartEvent.valueOf(getSeats(), getOnButton()));
    }

    public Integer getSmallBlind()
    {
        return smallBlind;
    }

    public void setSmallBlind(Integer smallBlind)
    {
        this.smallBlind = smallBlind;
    }

    public Integer getBigBlind()
    {
        return bigBlind;
    }

    public void setBigBlind(Integer bigBlind)
    {
        this.bigBlind = bigBlind;
    }
}
