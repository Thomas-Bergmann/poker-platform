package de.hatoka.poker.table.internal.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.base.Deck;
import de.hatoka.poker.base.Hand;
import de.hatoka.poker.base.Pot;
import de.hatoka.poker.table.capi.business.DealerGameCommunication;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.IllegalGameEventException;
import de.hatoka.poker.table.capi.event.history.DealerEvent;
import de.hatoka.poker.table.capi.event.history.GameEvent;
import de.hatoka.poker.table.capi.event.history.card.BoardCardsEvent;
import de.hatoka.poker.table.capi.event.history.card.ChangedDeckEvent;
import de.hatoka.poker.table.capi.event.history.card.HoleCardsEvent;
import de.hatoka.poker.table.capi.event.history.lifecycle.ShowdownEvent;
import de.hatoka.poker.table.capi.event.history.lifecycle.StartEvent;
import de.hatoka.poker.table.capi.event.history.lifecycle.TransferEvent;
import de.hatoka.poker.table.capi.event.history.pot.ChangedPotEvent;
import de.hatoka.poker.table.capi.event.history.seat.BetEvent;
import de.hatoka.poker.table.capi.event.history.seat.BlindEvent;
import de.hatoka.poker.table.capi.event.history.seat.CallEvent;
import de.hatoka.poker.table.capi.event.history.seat.CheckEvent;
import de.hatoka.poker.table.capi.event.history.seat.CoinEvent;
import de.hatoka.poker.table.capi.event.history.seat.FoldEvent;
import de.hatoka.poker.table.capi.event.history.seat.PlayerEvent;
import de.hatoka.poker.table.capi.event.history.seat.SetEvent;
import de.hatoka.poker.table.internal.json.GameEventType;

public class GameInfo
{
    private final DealerGameCommunication game;

    public GameInfo(DealerGameCommunication game)
    {
        this.game = game;
    }

    public Pot getPot()
    {
        List<ChangedPotEvent> potEvents = getEvents(ChangedPotEvent.class).toList();
        return potEvents.get(potEvents.size() - 1).getPot();
    }

    @SuppressWarnings("unchecked")
    public <T> Stream<T> getEvents(Class<T> eventClass)
    {
        return getAllEvents().stream().filter(e -> eventClass.isInstance(e)).map(e -> (T)e);
    }

    @SuppressWarnings("unchecked")
    private <T> T getFirstEvent(Class<T> eventClass)
    {
        return getAllEvents().stream().filter(e -> eventClass.isInstance(e)).map(e -> (T)e).findFirst().get();
    }

    public Deck getDeck()
    {
        Deck deck = getFirstEvent(StartEvent.class).getDeck();
        int removedFromDeck = getEvents(ChangedDeckEvent.class).mapToInt(ChangedDeckEvent::getNumberOfCards).sum();
        deck.getNextCards(removedFromDeck);
        return deck;
    }

    public void publishEvent(PlayerEvent event)
    {
        checkEvent(event);
        game.publishEvent(event);
    }
    
    private void checkEvent(PlayerEvent event)
    {
        checkSeatHasAction(event);
        switch(GameEventType.valueOf(event.getClass()))
        {
            case Bet:
            case Raise:
                checkBetEvent((BetEvent) event);
                checkAllInAndCoins((BetEvent)event);
                break;
            case Check:
                checkCheckEvent((CheckEvent) event);
                break;
            case Call:
                checkCallEvent((CallEvent) event);
                checkAllInAndCoins((CallEvent)event);
                break;
            case Fold:
                // fold is possible every time
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + GameEventType.valueOf(event.getClass()));
        }
    }

    private void checkCallEvent(CallEvent event)
    {
        if (getEvents(BetEvent.class).filter(e -> event.getSeat().equals(e.getSeat())).filter(e -> e.isAllIn()).findAny().isPresent())
        {
            throw new IllegalGameEventException("User is all in", event);
        }
    }

    private void checkAllInAndCoins(SetEvent event)
    {
        // user can call, but not call more than he has
        int coinsOnSeat = getCoinsOnSeat(event.getSeat());
        if (event.getCoins() > coinsOnSeat)
        {
            throw new IllegalGameEventException("User can't set more than he has.", event);
        }
        // user can call, but needs to mark all in
        if (event.getCoins() == coinsOnSeat && !event.isAllIn())
        {
            throw new IllegalGameEventException("User can't go all in without marker.", event);
        }
        // user can call, but needs to mark all in
        if (event.getCoins() < coinsOnSeat && event.isAllIn())
        {
            throw new IllegalGameEventException("User can't set marker without putting all coins on table.", event);
        }
    }

    private void checkSeatHasAction(PlayerEvent event)
    {
        // no action from other seats
        Optional<SeatRef> seatHasActionOpt = getSeatHasAction();
        if (seatHasActionOpt.isEmpty())
        {
            throw new IllegalGameEventException("Dealer has action", event);
        }
        if (!event.getSeat().equals(seatHasActionOpt.get()))
        {
            throw new IllegalGameEventException("Seat has no action", event);
        }
    }

    private void checkCheckEvent(CheckEvent event)
    {
        Map<SeatRef, Integer> coinsInPlay = getCoinsInPlay();
        int maxCoinsInPlay = getMaxCoinsInPlay(coinsInPlay);
        int coinsInPlayOfSeat = coinsInPlay.getOrDefault(event.getSeat(), 0);
        if (coinsInPlayOfSeat < maxCoinsInPlay)
        {
            throw new IllegalGameEventException("Seat can check, because bet or raise happen", event);
        }
    }

    private void checkBetEvent(BetEvent event)
    {
        // no negative raise
        int additionalCoins = event.getCoins();
        if (additionalCoins <= 0)
        {
            throw new IllegalGameEventException("Bet can't remove coins", event);
        }
        if (!event.isAllIn())
        {
            Map<SeatRef, Integer> coinsInPlay = getCoinsInPlay();
            int raiseTo = additionalCoins + coinsInPlay.getOrDefault(event.getSeat(), 0);
            int maxCoinsInPlay = getMaxCoinsInPlay(coinsInPlay);
            int realRaise = raiseTo - maxCoinsInPlay;
            int minRaise = getMinRaise(coinsInPlay, maxCoinsInPlay);
            if (realRaise < minRaise)
            {
                throw new IllegalGameEventException("Bet can't be less than min raise", event);
            }
        }
    }

    private int getMinRaise(Map<SeatRef, Integer> coinsInPlay, int maxCoinsInPlay)
    {
        Map<SeatRef, Integer> lessThanMax = new HashMap<>();
        coinsInPlay.forEach((k,v) -> {
            if (v < maxCoinsInPlay)
            {
                lessThanMax.put(k, v);
            }
        });
        int secondMax = getMaxCoinsInPlay(lessThanMax);
        return maxCoinsInPlay - secondMax;
    }

    public void publishEvent(DealerEvent event)
    {
        checkEvent(event);
        game.publishEvent(event);
    }

    private void checkEvent(DealerEvent event)
    {
        switch(GameEventType.valueOf(event.getClass()))
        {
            case Start:
                checkStartEvent((StartEvent) event);
                break;
            case Blind:
                checkAllInAndCoins((BlindEvent) event);
                break;
            case TransferEvent:
                checkTransferEvent((TransferEvent) event);
                break;
            case BurnCard:
            case HoleCard:
            case BoardCards:
            case Showdown:
            case CollectCoins:
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + GameEventType.valueOf(event.getClass()));
        }
    }

    private void checkTransferEvent(TransferEvent event)
    {
        if (getEvents(TransferEvent.class).findAny().isPresent())
        {
            throw new IllegalGameEventException("Game can't be transferred twice.", event);
        }
    }

    private void checkStartEvent(StartEvent event)
    {
        // no seat can start negative or zero
        if (event.getCoinsOnSeats().values().stream().filter(i -> i<=0).findAny().isPresent())
        {
            throw new IllegalGameEventException("No player can start without coins", event);
        }
    }

    public Optional<SeatRef> getOnButton()
    {
        return getEvents(StartEvent.class).map(StartEvent::getOnButton).findAny();
    }

    private Optional<SeatRef> getLastActiveSeatWithAction()
    {
        List<SeatRef> seats = getSeats();
        List<SeatRef> allIns = getAllIns();
        Optional<SeatRef> button = getOnButton();
        Optional<SeatRef> result = button;
        while(result.isPresent() && (hasFolded(result.get()) || allIns.contains(result.get())))
        {
            int idx = seats.indexOf(result.get());
            result = Optional.of(seats.get(idx == 0 ? seats.size() - 1 : idx - 1));
            if (result.get().equals(button.get()))
            {
                return Optional.empty();
            }
        }
        return result;
    }

    public int getMaxCoinsInPlay(Map<SeatRef, Integer> coinsInplay)
    {
        return coinsInplay.values().stream().mapToInt(v -> v).max().orElse(0);
    }

    public Map<SeatRef, Integer> getCoinsInPlay()
    {
        Map<SeatRef, Integer> result = new HashMap<>();
        for (CoinEvent event : getEvents(CoinEvent.class).toList())
        {
            if (event.isReset())
            {
                result.clear();
            }
            else
            {
                Integer old = result.computeIfAbsent(event.getSeat(), (a) -> {
                    return 0;
                });
                result.put(event.getSeat(), old + event.getCoins());
            }
        }
        return result;
    }

    public boolean hasFolded(SeatRef seatRef)
    {
        return getEvents(FoldEvent.class).filter(e -> seatRef.equals(e.getSeat())).findAny().isPresent();
    }

    List<Card> getHoleCards(SeatRef seatRef)
    {
        Optional<HoleCardsEvent> event = getEvents(HoleCardsEvent.class).filter(s -> seatRef.equals(s.getSeat()))
                                                                        .findAny();
        return event.map(HoleCardsEvent::getCards).orElse(Collections.emptyList());
    }

    public int getPotSize()
    {
        return getPot().getCoinCount();
    }

    public List<Integer> getPotSizes()
    {
        return getPot().getCoinsPerPod();
    }

    public Map<SeatRef, Hand> getHands()
    {
        Map<SeatRef, Hand> result = new HashMap<>();
        for (SeatRef seatRef : getSeats())
        {
            if (!hasFolded(seatRef))
            {
                Hand hand = Hand.valueOf(getHoleCards(seatRef), getBoardCards());
                result.put(seatRef, hand);
            }
        }
        return result;
    }

    public List<SeatRef> getSeats()
    {
        return getFirstEvent(StartEvent.class).getSeats();
    }

    public List<SeatRef> filterActiveSeats(List<SeatRef> all)
    {
        List<SeatRef> result = new ArrayList<>();
        result.addAll(all);
        // remove folded
        result.removeAll(getEvents(FoldEvent.class).map(FoldEvent::getSeat).toList());
        // remove all in
        result.removeAll(getAllIns());
        return result;
    }

    public List<Card> getBoardCards()
    {
        Stream<BoardCardsEvent> boardEvents = getEvents(BoardCardsEvent.class);
        List<Card> result = new ArrayList<>();
        boardEvents.forEach(b -> result.addAll(b.getCards()));
        return result;
    }

    public List<SeatRef> getAllIns()
    {
        return getEvents(SetEvent.class).filter(SetEvent::isAllIn).map(SetEvent::getSeat).toList();
    }

    public Optional<SeatRef> getSeatHasAction()
    {
        // check that game started
        if (getEvents(StartEvent.class).findAny().isEmpty())
        {
            return Optional.empty();
        }
        List<PlayerEvent> playerEvents = getEvents(PlayerEvent.class).toList();
        // no player events -> no blinds yet
        if (playerEvents.isEmpty())
        {
            return Optional.empty();
        }
        List<SeatRef> seats = getSeats();
        List<SeatRef> activeSeats = filterActiveSeats(seats);
        if (activeSeats.isEmpty())
        {
            return Optional.empty();
        }
        if (activeSeats.size() < 2 && getAllIns().isEmpty())
        {
            return Optional.empty();
        }
        SeatRef playerWithLastAction = getLastActiveSeatWithAction().get();
        // last was cards, so left from button
        if (lastWasBoardCardsEvent())
        {
            return getNextActiveSeat(seats, playerWithLastAction, playerWithLastAction, activeSeats);
        }
        // get last player, which has done something (call, check, raise)
        PlayerEvent lastPlayer = playerEvents.get(playerEvents.size() - 1);
        SeatRef lastSeat = lastPlayer.getSeat();
        // get last player, which has done an action (bet or raise)
        SeatRef lastAction = getLastActionSeat().orElse(null);

        // if first round blinds have actions
        if (getBoardCards().isEmpty() && lastAction == null)
        {
            if (isBigBlind(lastPlayer.getSeat()))
            {
                // if had action than dealer else go
                if (lastPlayer instanceof BlindEvent)
                {
                    if (seats.size() == 2)
                    {
                        return Optional.of(seats.get(0));
                    }
                }
                else
                {
                    return Optional.empty();
                }
            }
        } else if (lastAction == null && lastSeat.equals(playerWithLastAction)) {
            // if nobody did bet - button had last action
            return Optional.empty();
        }
        return getNextActiveSeat(seats, lastSeat, lastAction, activeSeats);
    }

    private Optional<SeatRef> getLastActionSeat()
    {
        List<GameEvent> all = getAllEvents();
        if (all.isEmpty())
        {
            return Optional.empty();
        }
        SeatRef lastAction = null;
        for (GameEvent event : all)
        {
            if (event instanceof BoardCardsEvent)
            {
                lastAction = null;
            }
            else if (event instanceof BetEvent)
            {
                lastAction = ((BetEvent)event).getSeat();
            }
        }
        return Optional.ofNullable(lastAction);
    }

    private boolean lastWasBoardCardsEvent()
    {
        List<GameEvent> all = getAllEvents();
        if (all.isEmpty())
        {
            return false;
        }
        GameEvent last = all.get(all.size() - 1);
        return last instanceof BoardCardsEvent;
    }

    /**
     * @param seats all seats in play (incl. fold and all-in)
     * @param lastAction seat did last action (inc. call, check)
     * @param lastBet seat did last action (can't be the next) 
     * @param activeSeats seats which are not allin or fold
     * @return seat which has next action (definitely not last action)
     */
    private Optional<SeatRef> getNextActiveSeat(List<SeatRef> seats, SeatRef lastAction, SeatRef lastBet,
                    Collection<SeatRef> activeSeats)
    {
        int idxLastAction= seats.indexOf(lastAction);
        int idxLastBet = seats.indexOf(lastBet);
        int newPos = idxLastAction;
        SeatRef newSeat;
        do
        {
            newPos++;
            if (newPos == seats.size())
            {
                newPos = 0;
            }
            if (newPos == idxLastBet)
            {
                return Optional.empty();
            }
            newSeat = seats.get(newPos);
        }
        while(!activeSeats.contains(newSeat));
        return Optional.of(newSeat);
    }

    boolean isBigBlind(SeatRef seat)
    {
        return getEvents(BlindEvent.class).filter(BlindEvent::isBigBlind)
                                          .map(BlindEvent::getSeat)
                                          .filter(s -> seat.equals(s))
                                          .findAny()
                                          .isPresent();
    }

    boolean isSmallBlind(SeatRef seat)
    {
        return getEvents(BlindEvent.class).filter(BlindEvent::isSmallBlind)
                                          .map(BlindEvent::getSeat)
                                          .filter(s -> seat.equals(s))
                                          .findAny()
                                          .isPresent();
    }

    public boolean hasDealerAction()
    {
        return getSeatHasAction().isEmpty();
    }

    /**
     * @return current big blind to calculated bet size steps
     */
    public int getBigBlind()
    {
        return getEvents(BlindEvent.class).filter(BlindEvent::isBigBlind).findAny().map(BlindEvent::getCoins).orElse(0);
    }

    public int getNextSmallBlind()
    {
        return getEvents(StartEvent.class).findAny().map(StartEvent::getSmallBlind).orElse(0);
    }

    public int getNextBigBlind()
    {
        return getEvents(StartEvent.class).findAny().map(StartEvent::getBigBlind).orElse(0);
    }

    public int getCoinsOnSeat(SeatRef seat)
    {
        int initial = getFirstEvent(StartEvent.class).getCoinsOnSeats().getOrDefault(seat, 0);
        int moved = getEvents(CoinEvent.class).filter(e -> !e.isReset()).filter(e -> e.getSeat().equals(seat)).mapToInt(CoinEvent::getCoins).sum();
        int showDown = getEvents(ShowdownEvent.class).map(e -> e.getWinner().getOrDefault(seat, 0)).mapToInt(i -> i).sum();
        return initial - moved + showDown;
    }

    public List<GameEvent> getEvents(SeatRef seatRef)
    {
        return getEvents(GameEvent.class)
                             .map(e -> this.mapForSeat(e, seatRef))
                             .filter(Optional::isPresent)
                             .map(Optional::get)
                             .toList();
    }

    private Optional<GameEvent> mapForSeat(GameEvent event, SeatRef seatRef)
    {
        if (!event.isPublic() && seatRef.equals(event.getOptionalSeat().orElse(null)))
        {
            return Optional.of(event);
        }
        return event.getPublicEvent();
    }

    public void logEvents()
    {
        Logger logger = LoggerFactory.getLogger(getClass());
        getEvents(GameEvent.class).forEach(e -> logger.info("{} : {}.", e.getClass().getSimpleName(), GameEventType.serializeEvent(e)));
        
    }

    public List<GameEvent> getAllEvents()
    {
        return game.getAllEvents();
    }
}
