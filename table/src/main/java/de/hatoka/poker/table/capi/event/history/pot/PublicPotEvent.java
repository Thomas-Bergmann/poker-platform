package de.hatoka.poker.table.capi.event.history.pot;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.hatoka.poker.table.capi.event.history.PublicGameEvent;

public class PublicPotEvent implements PublicGameEvent
{
    public static PublicPotEvent valueOf(Integer coins)
    {
        PublicPotEvent result = new PublicPotEvent();
        result.setPotSize(coins);
        return result;
    }

    /**
     * Current pot structure
     */
    @JsonProperty("pot-size")
    private Integer potSize;

    public Integer getPotSize()
    {
        return potSize;
    }

    public void setPotSize(Integer potSize)
    {
        this.potSize = potSize;
    }
}
