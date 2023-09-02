package de.hatoka.poker.table.capi.event.history.lifecycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

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
import de.hatoka.user.capi.business.UserRef;

public class StartEvent implements ChangedPotEvent, DealerEvent, PrivateGameEvent
{
    /**
     * table reference
     */
    @JsonProperty("table")
    private String table;

    /**
     * Contains seats (seat number) taking part of game
     */
    @JsonProperty("seats-order")
    private List<Integer> seatsNumbers;

    /**
     * Contains coins per seat (seat number)
     */
    @JsonProperty("seat-coins")
    private Map<Integer, Integer> seatCoins;

    /**
     * Contains players of seat (seat number)
     */
    @JsonProperty("seat-player")
    private Map<Integer, String> seatPlayer;

    /**
     * Contains seat (seat number) of dealer
     */
    @JsonProperty("button")
    private Integer onButton;

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
        return SeatRef.localRef(getTableRef(), position);
    }
    
    @JsonIgnore
    public List<SeatRef> getSeats()
    {
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
        seatCoins.forEach((k,v) -> result.put(this.getSeatRef(k), v));
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
        return SeatRef.localRef(getTableRef(), onButton);
    }

    @JsonIgnore
    public void setOnButton(SeatRef onButton)
    {
        setTableRef(onButton.getTableRef());
        this.onButton = onButton.getPosition();
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

    @JsonIgnore
    public TableRef getTableRef()
    {
        return TableRef.globalRef(table);
    }

    @JsonIgnore
    public void setTableRef(TableRef tableRef)
    {
        this.table = tableRef.getGlobalRef();
    }

    @JsonIgnore
    public Map<SeatRef, PlayerRef> getPlayers()
    {
        Map<SeatRef, PlayerRef> result = new HashMap<>();
        seatPlayer.forEach((k,v) -> result.put(this.getSeatRef(k), PlayerRef.globalRef(v)));
        return result;
    }

    @JsonSetter("seats-no")
    @Deprecated
    public void setSeatNo(List<Integer> seats)
    {
        this.seatsNumbers = seats;
    }

    @JsonSetter("seats")
    @Deprecated
    public void setSeatRefs(List<String> seats)
    {
        // ignore for new content
        if (seats == null)
        {
            return;
        }
        seatPlayer = new HashMap<>();
        seatsNumbers = new ArrayList<>();
        for(String s : seats)
        {
            SeatRef seatRef = SeatRef.globalRef(s);
            // fake player - was not in the content
            PlayerRef playerRef = PlayerRef.humanRef(UserRef.localRef(s));
            seatPlayer.put(seatRef.getPosition(), playerRef.getGlobalRef());
            seatsNumbers.add(seatRef.getPosition());
        }
    }
    @JsonSetter("coins-on-seat")
    @Deprecated
    public void setCoinsOnSeats(Map<String, Integer> coins)
    {
        // ignore for new content
        if (coins== null)
        {
            return;
        }
        seatCoins = new HashMap<>();
        coins.forEach((k,v) -> seatCoins.put(SeatRef.globalRef(k).getPosition(), v));
    }

    /** for json loading **/
    @JsonSetter("button")
    @Deprecated
    public void setOnButton(String onButtonRef)
    {
        if (onButtonRef.startsWith("seat:"))
        {
            SeatRef onButton = SeatRef.globalRef(onButtonRef);
            setTableRef(onButton.getTableRef());
            this.onButton = onButton.getPosition();
        }
        else
        {
            this.onButton = Integer.valueOf(onButtonRef);
        }
    }
}
