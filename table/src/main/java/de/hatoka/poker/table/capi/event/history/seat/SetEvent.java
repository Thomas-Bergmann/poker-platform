package de.hatoka.poker.table.capi.event.history.seat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.hatoka.poker.table.capi.business.SeatRef;

/**
 * Player sets coins from seat to table
 */
public abstract class SetEvent implements CoinEvent, PlayerEvent
{
    @JsonProperty("seat")
    private String seat;
    @JsonProperty("coins")
    private Integer coins;
    @JsonProperty("allin")
    private boolean isAllIn = false;

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

    /**
     * @return player moves this coins to table 
     */
    @JsonIgnore
    public Integer getCoins()
    {
        return coins;
    }

    /**
     * Player wants to move this coins to table
     * @param coins
     */
    @JsonIgnore
    public void setCoins(Integer coins)
    {
        this.coins = coins;
    }

    @JsonIgnore
    public boolean isAllIn()
    {
        return isAllIn;
    }

    @JsonIgnore
    public void setIsAllIn(boolean isAllIn)
    {
        this.isAllIn = isAllIn;
    }
}
