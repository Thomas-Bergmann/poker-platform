package de.hatoka.poker.table.capi.event.history.card;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.table.capi.event.history.DealerEvent;
import de.hatoka.poker.table.capi.event.history.GameEvent;
import de.hatoka.poker.table.capi.event.history.PrivateGameEvent;

public class BurnCardEvent implements ChangedDeckEvent, PrivateGameEvent, DealerEvent
{
    @JsonProperty("card")
    private String card;

    @JsonIgnore
    public Card getCard()
    {
        return Card.valueOf(this.card);
    }

    @JsonIgnore
    public void setCard(Card card)
    {
        this.card = card.toString();
    }

    @Override
    @JsonIgnore
    public int getNumberOfCards()
    {
        return 1;
    }

    @Override
    public Optional<GameEvent> getPublicEvent()
    {
        return Optional.empty();
    }
}
