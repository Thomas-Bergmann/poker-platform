package de.hatoka.poker.table.capi.event.history.card;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.history.DealerEvent;
import de.hatoka.poker.table.capi.event.history.GameEvent;
import de.hatoka.poker.table.capi.event.history.PrivateGameEvent;

public class HoleCardsEvent implements PrivateGameEvent, ChangedDeckEvent, DealerEvent
{
    public static HoleCardsEvent valueOf(SeatRef seat, List<Card> cards)
    {
        HoleCardsEvent result = new HoleCardsEvent();
        result.setSeat(seat);
        result.setCards(cards);
        return result; 
    }

    /**
     * Closed Cards
     */
    @JsonProperty("cards")
    private String cards;
    @JsonProperty("seat")
    private String seat;

    /**
     * @return seat if event is not public it' visible to this player only
     */
    @JsonIgnore
    public Optional<SeatRef> getOptionalSeat()
    {
        return Optional.of(getSeat());
    }

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

    @Override
    public Optional<GameEvent> getPublicEvent()
    {
        return Optional.empty();
    }

    @Override
    public String toString()
    {
        return "HoleCardsEvent [cards=" + cards + ", seat=" + seat + "]";
    }
}
