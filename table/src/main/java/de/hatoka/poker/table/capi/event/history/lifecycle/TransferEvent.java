package de.hatoka.poker.table.capi.event.history.lifecycle;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.history.DealerEvent;
import de.hatoka.poker.table.capi.event.history.PublicGameEvent;

public class TransferEvent implements DealerEvent, PublicGameEvent
{
    @JsonProperty("coins-before")
    private Map<String, Integer> coinsOnSeatBeforeGame;

    @JsonProperty("coins-diff")
    private Map<String, Integer> coinsOnSeatDiffOfGame;

    @JsonProperty("coins-after")
    private Map<String, Integer> coinsOnSeatAfterGame;

    @JsonIgnore
    public Map<SeatRef, Integer> getCoinsOnSeatBeforeGame()
    {
        Map<SeatRef, Integer> result = new HashMap<>();
        coinsOnSeatBeforeGame.forEach((k,v) -> result.put(SeatRef.globalRef(k), v));
        return result;
    }

    @JsonIgnore
    public void setCoinsOnSeatBeforeGame(Map<SeatRef, Integer> coinsOnSeatBeforeGame)
    {
        Map<String, Integer> result = new HashMap<>();
        coinsOnSeatBeforeGame.forEach((k,v) -> result.put(k.getGlobalRef(), v));
        this.coinsOnSeatBeforeGame = result;
    }

    @JsonIgnore
    public Map<SeatRef, Integer> getCoinsOnSeatAfterGame()
    {
        Map<SeatRef, Integer> result = new HashMap<>();
        coinsOnSeatAfterGame.forEach((k,v) -> result.put(SeatRef.globalRef(k), v));
        return result;
    }

    @JsonIgnore
    public void setCoinsOnSeatAfterGame(Map<SeatRef, Integer> coinsOnSeatAfterGame)
    {
        Map<String, Integer> result = new HashMap<>();
        coinsOnSeatAfterGame.forEach((k,v) -> result.put(k.getGlobalRef(), v));
        this.coinsOnSeatAfterGame = result;
    }

    @JsonIgnore
    public Map<SeatRef, Integer> getCoinsOnSeatDiffOfGame()
    {
        Map<SeatRef, Integer> result = new HashMap<>();
        coinsOnSeatDiffOfGame.forEach((k,v) -> result.put(SeatRef.globalRef(k), v));
        return result;
    }

    @JsonIgnore
    public void setCoinsOnSeatDiffOfGame(Map<SeatRef, Integer> coinsOnSeatDiffOfGame)
    {
        Map<String, Integer> result = new HashMap<>();
        coinsOnSeatDiffOfGame.forEach((k,v) -> result.put(k.getGlobalRef(), v));
        this.coinsOnSeatDiffOfGame = result;
    }
}
