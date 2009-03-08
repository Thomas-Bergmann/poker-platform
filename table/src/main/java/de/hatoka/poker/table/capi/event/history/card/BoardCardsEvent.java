package de.hatoka.poker.table.capi.event.history.card;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.table.capi.event.history.DealerEvent;
import de.hatoka.poker.table.capi.event.history.PublicGameEvent;

public class BoardCardsEvent implements ChangedDeckEvent, PublicGameEvent, DealerEvent
{
    /**
     * Displayed/Open Cards
     */
    @JsonProperty("cards")
    private String cards;

    @JsonIgnore
    public List<Card> getCards()
    {
        return Card.deserialize(this.cards);
    }

    @JsonIgnore
    public void setCards(List<Card> cards)
    {
        this.cards = Card.serialize(cards);
    }

    @Override
    @JsonIgnore
    public int getNumberOfCards()
    {
        return getCards().size();
    }
}
