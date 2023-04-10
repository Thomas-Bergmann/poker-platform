package de.hatoka.poker.table.capi.event.history.lifecycle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.business.TableRef;
import de.hatoka.poker.table.capi.event.history.DealerEvent;
import de.hatoka.poker.table.capi.event.history.PublicGameEvent;

public class ShowdownEvent implements PublicGameEvent, DealerEvent
{
    /**
     * How much coins won seats(player)
     */
    @JsonProperty("table")
    private String table;

    /**
     * How much coins won seats(player)
     */
    @JsonProperty("winner")
    @Deprecated
    private Map<String, Integer> winner;

    /**
     * How much coins won seats(player)
     */
    @JsonProperty("seat-wins")
    private Map<Integer, Integer> seatWins;

    /**
     * Which cards are shown by seat
     */
    @JsonProperty("cards-hole")
    @Deprecated
    private Map<String, String> cardsHole;

    /**
     * Which cards are shown by seat
     */
    @JsonProperty("seat-cards-hole")
    private Map<Integer, String> seatCardsHole;
    
    /**
     * Which hand are shown by seat
     */
    @JsonProperty("cards-hand")
    @Deprecated
    private Map<String, String> cardsBestHand;

    /**
     * Which hand are shown by seat
     */
    @JsonProperty("seat-hands")
    private Map<Integer, String> seatHands;

    /**
     * Which player mucks the cards (doesn't show them) 
     */
    @JsonProperty("muck")
    private List<String> mucks;

    private SeatRef getSeatRef(Integer position)
    {
        return SeatRef.localRef(TableRef.globalRef(table), position);
    }

    @JsonIgnore
    public Map<SeatRef, Integer> getWinner()
    {
        Map<SeatRef, Integer> result = new HashMap<>();
        if (winner != null)
        {
            winner.forEach((k,v) -> result.put(SeatRef.globalRef(k), v));
        }
        else
        {
            seatWins.forEach((k,v) -> result.put(getSeatRef(k), v));
        }
        return result;
    }

    @JsonIgnore
    public void setWinner(Map<SeatRef, Integer> winner)
    {
        this.table = winner.keySet().iterator().next().getTableRef().getGlobalRef();
        Map<Integer, Integer> result = new HashMap<>();
        winner.forEach((k,v) -> result.put(k.getPosition(), v));
        this.seatWins = result;
    }

    @JsonIgnore
    public Map<SeatRef, List<Card>> getCardsBestHand()
    {
        Map<SeatRef, List<Card>> result = new HashMap<>();
        if (cardsBestHand != null)
        {
            cardsBestHand.forEach((k,v) -> result.put(SeatRef.globalRef(k), Card.deserialize(v)));
        }
        else
        {
            seatHands.forEach((k,v) -> result.put(getSeatRef(k), Card.deserialize(v)));
        }
        return result;
    }

    @JsonIgnore
    public void setCardsBestHand(Map<SeatRef, List<Card>> cards)
    {
        this.table = cards.keySet().iterator().next().getTableRef().getGlobalRef();
        Map<Integer, String> result = new HashMap<>();
        cards.forEach((k,v) -> result.put(k.getPosition(), Card.serialize(v)));
        this.seatHands = result;
    }

    @JsonIgnore
    public Map<SeatRef, List<Card>> getCardsHole()
    {
        Map<SeatRef, List<Card>> result = new HashMap<>();
        if (cardsHole != null)
        {
            cardsHole.forEach((k,v) -> result.put(SeatRef.globalRef(k), Card.deserialize(v)));
        }
        else
        {
            seatCardsHole.forEach((k,v) -> result.put(getSeatRef(k), Card.deserialize(v)));
        }
        return result;
    }

    @JsonIgnore
    public void setCardsHole(Map<SeatRef, List<Card>> cards)
    {
        Map<Integer, String> result = new HashMap<>();
        cards.forEach((k,v) -> result.put(k.getPosition(), Card.serialize(v)));
        this.seatCardsHole = result;
    }

    @JsonIgnore
    public List<SeatRef> getMucks()
    {
        return mucks.stream().map(SeatRef::globalRef).toList();
    }

    @JsonIgnore
    public void setMucks(List<SeatRef> mucks)
    {
        this.mucks = mucks.stream().map(SeatRef::getGlobalRef).toList();
    }
}
