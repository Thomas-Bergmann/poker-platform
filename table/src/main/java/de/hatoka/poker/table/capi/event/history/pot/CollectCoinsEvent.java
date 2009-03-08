package de.hatoka.poker.table.capi.event.history.pot;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.hatoka.poker.base.Pot;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.history.DealerEvent;
import de.hatoka.poker.table.capi.event.history.GameEvent;
import de.hatoka.poker.table.capi.event.history.seat.CoinEvent;

/**
 * Specific event to collect coins from seats in play
 * @implSpec SetCoinEvent because all previous SetCoinEvent are not longer relevant 
 */
public class CollectCoinsEvent implements ChangedPotEvent, CoinEvent, DealerEvent
{
    /**
     * Current pot structure
     */
    @JsonProperty("pot")
    private String pot;

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

    @Override
    @JsonIgnore
    public Integer getCoins()
    {
        return 0;
    }

    @Override
    @JsonIgnore
    public boolean isReset()
    {
        return true;
    }
    
    @Override
    @JsonIgnore
    public SeatRef getSeat()
    {
        return null;
    }
    
    @Override
    public Optional<GameEvent> getPublicEvent()
    {
        return Optional.of(PublicPotEvent.valueOf(this.getPot().getCoinCount()));
    }
}
