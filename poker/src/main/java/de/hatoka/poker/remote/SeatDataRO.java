package de.hatoka.poker.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SeatDataRO
{
    /**
     * Player sitting on seat, if changed the player leaves or joins the table
     */
    @JsonProperty("player-ref")
    private String playerRef;
    
    /**
     * Coins of seat, if changed/submitted actions buyin, buyout, rebuy are applied 
     */
    @JsonProperty("coins-onseat")
    private Integer coinsOnSeat;

    /**
     * Player is not active, if changed to true player will be part of next game, change to false will immediately fold and out
     */
    @JsonProperty("sitting-out")
    private Boolean sittingOut;

    public String getPlayerRef()
    {
        return playerRef;
    }

    public void setPlayerRef(String playerRef)
    {
        this.playerRef = playerRef;
    }

    public Integer getCoinsOnSeat()
    {
        return coinsOnSeat;
    }

    public void setCoinsOnSeat(Integer coinsOnSeat)
    {
        this.coinsOnSeat = coinsOnSeat;
    }

    public Boolean isSittingOut()
    {
        return sittingOut;
    }

    public void setSittingOut(Boolean sittingOut)
    {
        this.sittingOut = sittingOut;
    }
}
