package de.hatoka.poker.table.internal.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.base.Hand;
import de.hatoka.poker.base.Rank;
import de.hatoka.poker.rules.TexasHoldem;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.history.card.BoardCardsEvent;
import de.hatoka.poker.table.capi.event.history.card.HoleCardsEvent;
import de.hatoka.poker.table.capi.event.history.lifecycle.ShowdownEvent;
import de.hatoka.poker.table.capi.event.history.seat.PlayerEvent;
import de.hatoka.poker.table.capi.event.history.seat.SetEvent;

public class PlayerGameInfo
{
    private final SeatRef seat;
    private final GameInfo game;

    public PlayerGameInfo(GameInfo game, SeatRef seat)
    {
        this.game = game;
        this.seat = seat;
    }

    public SeatRef getSeatRef()
    {
        return seat;
    }

    public void publisEvent(PlayerEvent event)
    {
        event.setSeat(seat);
        game.publishEvent(event);
    }

    public int getMaxCoinsInPlay()
    {
        return game.getMaxCoinsInPlay(getCoinsInPlay());
    }

    public int getCoinsInPlayOfSeat()
    {
        return getCoinsInPlay().getOrDefault(seat, 0);
    }

    public int getCoinsOnSeat()
    {
        return game.getCoinsOnSeat(seat);
    }

    @SuppressWarnings("unchecked")
    public <T> Stream<T> getEvents(Class<T> eventClass)
    {
        return game.getEvents(seat).stream().filter(e -> eventClass.isInstance(e)).map(e -> (T)e);
    }

    @SuppressWarnings("unchecked")
    <T> Optional<T> getFirstEvent(Class<T> eventClass)
    {
        return game.getEvents(seat).stream().filter(e -> eventClass.isInstance(e)).map(e -> (T)e).findFirst();
    }

    public Map<SeatRef, Integer> getCoinsInPlay()
    {
        return game.getCoinsInPlay();
    }

    public List<Card> getBoardCards()
    {
        Stream<BoardCardsEvent> boardEvents = getEvents(BoardCardsEvent.class);
        List<Card> result = new ArrayList<>();
        boardEvents.forEach(b -> result.addAll(b.getCards()));
        return result;
    }

    private List<Card> getBestCards(Hand hand)
    {
        return TexasHoldem.getBestCards(hand);
    }

    public List<Card> getCardsOfHand(SeatRef otherSeat)
    {
        if (seat.equals(otherSeat))
        {
            return getBestCards(Hand.valueOf(getHoleCards(), getBoardCards()));
        }
        Optional<ShowdownEvent> optShowdownEvent = getFirstEvent(ShowdownEvent.class);
        if (optShowdownEvent.isEmpty())
        {
            return Collections.emptyList();
        }
        return optShowdownEvent.get().getCardsBestHand().getOrDefault(otherSeat, Collections.emptyList());
    }

    public List<Card> getHoleCards()
    {
        List<HoleCardsEvent> cardEvents = getEvents(HoleCardsEvent.class).toList();
        List<Card> result = new ArrayList<>();
        cardEvents.forEach(b -> result.addAll(b.getCards()));
        return result;
    }

    public List<SeatRef> getAllIns()
    {
        return getEvents(SetEvent.class).filter(SetEvent::isAllIn).map(SetEvent::getSeat).toList();
    }

    public Map<SeatRef, Hand> getHands()
    {
        Map<SeatRef, Hand> result = new HashMap<>();
        Optional<ShowdownEvent> optShowdownEvent = getFirstEvent(ShowdownEvent.class);
        if (!optShowdownEvent.isEmpty())
        {
            optShowdownEvent.get().getCardsHole().forEach((k,v) -> result.put(k, Hand.valueOf(v, getBoardCards())));
        }
        result.put(seat, Hand.valueOf(getHoleCards(), getBoardCards()));
        return result;
    }

    public Rank getRank(Hand hand)
    {
        return TexasHoldem.getRank(hand);
    }
}
