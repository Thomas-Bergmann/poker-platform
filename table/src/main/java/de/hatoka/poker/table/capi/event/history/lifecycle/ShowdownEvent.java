package de.hatoka.poker.table.capi.event.history.lifecycle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.history.DealerEvent;
import de.hatoka.poker.table.capi.event.history.PublicGameEvent;

public class ShowdownEvent implements PublicGameEvent, DealerEvent
{
    /**
     * How much coins won seats(player)
     */
    @JsonProperty("winner")
    private Map<String, Integer> winner;

    /**
     * Which cards are shown by seat
     */
    @JsonProperty("cards-hole")
    private Map<String, String> cardsHole;
    
    /**
     * Which cards are shown by seat
     */
    @JsonProperty("cards-hand")
    private Map<String, String> cardsBestHand;

    /**
     * Which player mucks the cards (doesn't show them) 
     */
    @JsonProperty("muck")
    private List<String> mucks;

    @JsonIgnore
    public Map<SeatRef, Integer> getWinner()
    {
        Map<SeatRef, Integer> result = new HashMap<>();
        winner.forEach((k,v) -> result.put(SeatRef.globalRef(k), v));
        return result;
    }

    @JsonIgnore
    public void setWinner(Map<SeatRef, Integer> winner)
    {
        Map<String, Integer> result = new HashMap<>();
        winner.forEach((k,v) -> result.put(k.getGlobalRef(), v));
        this.winner = result;
    }

    @JsonIgnore
    public Map<SeatRef, List<Card>> getCardsBestHand()
    {
        Map<SeatRef, List<Card>> result = new HashMap<>();
        cardsBestHand.forEach((k,v) -> result.put(SeatRef.globalRef(k), Card.deserialize(v)));
        return result;
    }

    @JsonIgnore
    public void setCardsBestHand(Map<SeatRef, List<Card>> cards)
    {
        Map<String, String> result = new HashMap<>();
        cards.forEach((k,v) -> result.put(k.getGlobalRef(), Card.serialize(v)));
        this.cardsBestHand= result;
    }

    @JsonIgnore
    public Map<SeatRef, List<Card>> getCardsHole()
    {
        Map<SeatRef, List<Card>> result = new HashMap<>();
        cardsHole.forEach((k,v) -> result.put(SeatRef.globalRef(k), Card.deserialize(v)));
        return result;
    }

    @JsonIgnore
    public void setCardsHole(Map<SeatRef, List<Card>> cards)
    {
        Map<String, String> result = new HashMap<>();
        cards.forEach((k,v) -> result.put(k.getGlobalRef(), Card.serialize(v)));
        this.cardsHole = result;
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
