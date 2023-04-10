package de.hatoka.poker.table.capi.event.history.lifecycle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.hatoka.poker.base.Deck;
import de.hatoka.poker.base.Pot;
import de.hatoka.poker.player.capi.business.PlayerBO;
import de.hatoka.poker.player.capi.business.PlayerRef;
import de.hatoka.poker.table.capi.business.SeatBO;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.business.TableRef;
import de.hatoka.poker.table.capi.event.history.DealerEvent;
import de.hatoka.poker.table.capi.event.history.GameEvent;
import de.hatoka.poker.table.capi.event.history.PrivateGameEvent;
import de.hatoka.poker.table.capi.event.history.pot.ChangedPotEvent;

public class StartEvent implements ChangedPotEvent, DealerEvent, PrivateGameEvent
{
    /**
     * Contains players/seats (seat ref) taking part of game
     */
    @JsonProperty("table")
    private String table;

    /**
     * Contains players/seats (seat ref) taking part of game
     */
    @JsonProperty("seats")
    @Deprecated
    private List<String> seats;

    /**
     * Contains players/seats (seat ref) taking part of game
     */
    @JsonProperty("seats-no")
    private List<Integer> seatsNumbers;

    /**
     * Contains players/seats (seat ref) taking part of game
     */
    @JsonProperty("coins-on-seat")
    @Deprecated
    private Map<String, Integer> coinsOnSeat;

    /**
     * Contains players/seats (seat ref) taking part of game
     */
    @JsonProperty("seat-coins")
    private Map<Integer, Integer> seatCoins;

    /**
     * Contains players/seats (seat ref) taking part of game
     */
    @JsonProperty("seat-player")
    private Map<Integer, String> seatPlayer;

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


    private SeatRef getSeatRef(Integer position)
    {
        return SeatRef.localRef(TableRef.globalRef(table), position);
    }
    
    @JsonIgnore
    public List<SeatRef> getSeats()
    {
        if(this.seats != null)
        {
            return seats.stream().map(SeatRef::globalRef).toList();
        }
        return seatsNumbers.stream().map(this::getSeatRef).toList();
    }

    @JsonIgnore
    public void setSeats(List<SeatBO> seats)
    {
        this.table = seats.get(0).getRef().getTableRef().getGlobalRef();
        this.seatsNumbers = seats.stream().map(SeatBO::getPosition).toList();
        Map<Integer, Integer> seatCoins = new HashMap<>();
        seats.forEach(s -> seatCoins.put(s.getPosition(), s.getAmountOfCoinsOnSeat()));
        this.seatCoins = seatCoins;
        Map<Integer, String> seatPlayer = new HashMap<>();
        seats.forEach(s -> seatPlayer.put(s.getPosition(), s.getPlayer().map(PlayerBO::getRef).map(PlayerRef::getGlobalRef).orElse(null)));
        this.seatPlayer = seatPlayer;
    }

    @JsonIgnore
    public Map<SeatRef, Integer> getCoinsOnSeats()
    {
        Map<SeatRef, Integer> result = new HashMap<>();
        if (this.coinsOnSeat != null)
        {
            coinsOnSeat.forEach((k,v) -> result.put(SeatRef.globalRef(k), v));
        }
        else
        {
            seatCoins.forEach((k,v) -> result.put(this.getSeatRef(k), v));
        }
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
        this.table = onButton.getTableRef().getGlobalRef();
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

    public String getTable()
    {
        return table;
    }

    public void setTable(String table)
    {
        this.table = table;
    }

    @JsonIgnore
    public Map<SeatRef, PlayerRef> getPlayers()
    {
        Map<SeatRef, PlayerRef> result = new HashMap<>();
        seatPlayer.forEach((k,v) -> result.put(this.getSeatRef(k), PlayerRef.globalRef(v)));
        return result;
    }

}
