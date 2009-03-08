package de.hatoka.poker.table.capi.event.history.seat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.hatoka.poker.table.capi.event.history.DealerEvent;

public class BlindEvent extends SetEvent implements DealerEvent
{
    @JsonProperty("big")
    private Boolean isBigBlind = false;
    
    @JsonIgnore
    public boolean isSmallBlind()
    {
        return !isBigBlind;
    }

    @JsonIgnore
    public boolean isBigBlind()
    {
        return isBigBlind;
    }

    @JsonIgnore
    public void setIsBigBlind(boolean isBig)
    {
        this.isBigBlind = isBig;
    }
}