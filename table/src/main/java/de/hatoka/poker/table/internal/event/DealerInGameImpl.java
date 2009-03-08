package de.hatoka.poker.table.internal.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.base.Deck;
import de.hatoka.poker.base.Hand;
import de.hatoka.poker.base.Pot;
import de.hatoka.poker.rules.TexasHoldem;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.game.DealerInGame;
import de.hatoka.poker.table.capi.event.history.DealerEvent;
import de.hatoka.poker.table.capi.event.history.GameEvent;
import de.hatoka.poker.table.capi.event.history.card.BoardCardsEvent;
import de.hatoka.poker.table.capi.event.history.card.BurnCardEvent;
import de.hatoka.poker.table.capi.event.history.card.HoleCardsEvent;
import de.hatoka.poker.table.capi.event.history.lifecycle.ShowdownEvent;
import de.hatoka.poker.table.capi.event.history.lifecycle.StartEvent;
import de.hatoka.poker.table.capi.event.history.lifecycle.TransferEvent;
import de.hatoka.poker.table.capi.event.history.pot.CollectCoinsEvent;
import de.hatoka.poker.table.capi.event.history.seat.BetEvent;
import de.hatoka.poker.table.capi.event.history.seat.BlindEvent;
import de.hatoka.poker.table.capi.event.history.seat.SetEvent;

public class DealerInGameImpl implements DealerInGame
{
    private final GameInfo game;

    public DealerInGameImpl(GameInfo game)
    {
        this.game = game;
    }

    @Override
    public void blindsAndAnte()
    {
        SeatRef onbutton = game.getOnButton().get();
        List<SeatRef> allSeats = game.getSeats();
        SeatRef smallBlindSeat = getNextSmallBlind(onbutton, allSeats);
        SeatRef bigBlindSeat = getNextBigBlind(smallBlindSeat, allSeats);
        // on heads up the next player is big not small
        if (allSeats.size() == 2)
        {
            SeatRef rem = bigBlindSeat;
            bigBlindSeat = smallBlindSeat;
            smallBlindSeat = rem;
        }
        BlindEvent smallBlindEvent = new BlindEvent();
        smallBlindEvent.setCoins(game.getNextSmallBlind());
        smallBlindEvent.setSeat(smallBlindSeat);
        smallBlindEvent.setIsBigBlind(false);

        BlindEvent bigBlindEvent = new BlindEvent();
        bigBlindEvent.setCoins(game.getNextBigBlind());
        bigBlindEvent.setSeat(bigBlindSeat);
        bigBlindEvent.setIsBigBlind(true);

        game.publishEvent((DealerEvent)smallBlindEvent);
        game.publishEvent((DealerEvent)bigBlindEvent);
    }

    @Override
    public void holeCards()
    {
        List<HoleCardsEvent> events = giveAllPlayersCards(2).entrySet()
                                                            .stream()
                                                            .map(e -> HoleCardsEvent.valueOf(e.getKey(), e.getValue()))
                                                            .toList();
        events.forEach(game::publishEvent);
    }

    @Override
    public void burnCard()
    {
        Card card = game.getDeck().getNextCard();

        BurnCardEvent event = new BurnCardEvent();
        event.setCard(card);
        game.publishEvent(event);
    }

    @Override
    public void giveBoardCards(int amount)
    {
        List<Card> cards = game.getDeck().getNextCards(amount);

        BoardCardsEvent event = new BoardCardsEvent();
        event.setCards(cards);
        game.publishEvent(event);
    }

    @Override
    public void collectCoins()
    {
        Map<SeatRef, Integer> seats = game.getCoinsInPlay();
        Map<String, Integer> coinsInPlay = new HashMap<>();
        for (Entry<SeatRef, Integer> current : seats.entrySet())
        {
            int coins = current.getValue();
            coinsInPlay.put(current.getKey().getGlobalRef(), coins);
        }
        if (coinsInPlay.isEmpty())
        {
            return;
        }
        Pot pot = game.getPot();
        pot.addToPot(coinsInPlay);

        CollectCoinsEvent event = new CollectCoinsEvent();
        event.setPot(pot);
        game.publishEvent(event);
    }

    @Override
    public void payout()
    {
        Pot pot = game.getPot();
        ShowdownEvent event = new ShowdownEvent();
        if (isOneLeft())
        {
            List<SeatRef> seats = game.filterActiveSeats(game.getSeats());
            Map<SeatRef, Integer> seatPayment = new HashMap<>();
            seatPayment.put(seats.get(0), pot.getCoinCount());
            event.setWinner(seatPayment);
        }
        else
        {
            Map<SeatRef, Hand> handsOfPlayer = game.getHands();
            List<List<SeatRef>> placement = getPlacement(handsOfPlayer);
            Map<String, Integer> potPayment = pot.showdown(
                            placement.stream().map(l -> l.stream().map(SeatRef::getGlobalRef).toList()).toList());
            Map<SeatRef, Integer> seatPayment = new HashMap<>();
            potPayment.forEach((k, v) -> seatPayment.put(SeatRef.globalRef(k), v));
            event.setWinner(seatPayment);
            showDownShowMuckCards(event, seatPayment.keySet(), handsOfPlayer);
        }
        game.publishEvent(event);
    }

    private int getSeatPosition(SeatRef seat, List<SeatRef> seats)
    {
        int result = -1;
        for (int i = 0; result < 0 && i < seats.size(); i++)
        {
            if (seats.get(i).equals(seat))
            {
                result = i;
            }
        }
        // can't found dealer
        if (result < 0)
        {
            result = 0;
        }
        return result;
    }

    private SeatRef getNextBigBlind(SeatRef dealer, List<SeatRef> allSeats)
    {
        int newPosition = getSeatPosition(dealer, allSeats) + 1;
        if (newPosition < 0 || newPosition >= allSeats.size())
        {
            newPosition = 0;
        }
        return allSeats.get(newPosition);
    }

    private SeatRef getNextSmallBlind(SeatRef smallBlind, List<SeatRef> allSeats)
    {
        int newPosition = getSeatPosition(smallBlind, allSeats) + 1;
        if (newPosition < 0 || newPosition >= allSeats.size())
        {
            newPosition = 0;
        }
        return allSeats.get(newPosition);
    }

    /**
     * Gives each active player one card
     */
    private Map<SeatRef, List<Card>> giveAllPlayersCards(int numberOfCards)
    {
        Deck deck = game.getDeck();
        Map<SeatRef, List<Card>> result = new HashMap<>();
        // TODO need to start with small blind position
        for (int i = 0; i < numberOfCards; i++)
        {
            for (SeatRef seat : game.getSeats())
            {
                result.computeIfAbsent(seat, (s) -> {
                    return new ArrayList<>();
                }).add(deck.getNextCard());
            }
        }
        return result;
    }

    private void showDownShowMuckCards(ShowdownEvent event, Collection<SeatRef> placedPlayer,
                    Map<SeatRef, Hand> handsOfPlayer)
    {
        Map<SeatRef, List<Card>> showedCards = new HashMap<>();
        List<SeatRef> mucked = new ArrayList<>();
        Hand bestHandShown = null;
        for (SeatRef seat : getShowCardsOrder())
        {
            Hand handOfPlayer = handsOfPlayer.get(seat);
            if (placedPlayer.contains(seat) || isAllIn(seat))
            {
                showedCards.put(seat, getBestCards(handOfPlayer));
                if (worthToShow(handOfPlayer, bestHandShown))
                {
                    bestHandShown = handOfPlayer;
                }
            }
            else
            {
                if (worthToShow(handOfPlayer, bestHandShown))
                {
                    showedCards.put(seat, getBestCards(handOfPlayer));
                    bestHandShown = handOfPlayer;
                }
                else
                {
                    mucked.add(seat);
                }
            }
        }
        Map<SeatRef, List<Card>> showedHoleCards = new HashMap<>();
        showedCards.keySet()
                   .forEach(seatRef -> showedHoleCards.put(seatRef, handsOfPlayer.get(seatRef).getHoleCards()));
        // update event
        event.setCardsBestHand(showedCards);
        event.setCardsHole(showedHoleCards);
        event.setMucks(mucked);
    }

    private boolean isAllIn(SeatRef seat)
    {
        return game.getEvents(SetEvent.class)
                   .filter(SetEvent::isAllIn)
                   .filter(e -> seat.equals(e.getSeat()))
                   .findAny()
                   .isPresent();
    }

    private boolean worthToShow(Hand handOfPlayer, Hand bestHandShown)
    {
        if (bestHandShown == null)
        {
            return true;
        }
        return TexasHoldem.compare(handOfPlayer, bestHandShown) >= 0;
    }

    private List<Card> getBestCards(Hand hand)
    {
        return TexasHoldem.getBestCards(hand);
    }

    /**
     * First all all-ins (in the order of all-ins), afterwards player with last bet, afterwards order on table
     * 
     * @return
     */
    private List<SeatRef> getShowCardsOrder()
    {
        List<SeatRef> result = new ArrayList<>();
        result.addAll(getAllInSeats());
        Optional<SeatRef> startWith = getLastAggressor();
        if (startWith.isEmpty())
        {
            startWith = game.getEvents(BlindEvent.class).findFirst().map(BlindEvent::getSeat);
        }
        List<SeatRef> allSeatsStartsFromAggressorOrSmallBlind = getAllSeats(startWith);
        allSeatsStartsFromAggressorOrSmallBlind.removeAll(result);
        result.addAll(allSeatsStartsFromAggressorOrSmallBlind);
        result.removeIf(game::hasFolded);
        return result;
    }

    /**
     * @param startWith
     * @return all seats starting with given seat
     */
    private List<SeatRef> getAllSeats(Optional<SeatRef> startWith)
    {
        List<SeatRef> result = new ArrayList<>();
        if (startWith.isEmpty())
        {
            result.addAll(game.getSeats());
        } else
        {
            SeatRef aggressor = startWith.get();
            List<SeatRef> allSeats = game.getSeats();
            int posAggressor = allSeats.indexOf(aggressor);
            result.addAll(allSeats.subList(posAggressor, allSeats.size()));
            if (posAggressor > 0)
            {
                result.addAll(allSeats.subList(0, posAggressor - 1));
            }
        }
        return result;
    }

    private List<SeatRef> getAllInSeats()
    {
        return game.getEvents(SetEvent.class).filter(SetEvent::isAllIn).map(SetEvent::getSeat).toList();
    }

    /**
     * @return the last aggressor on the last bet round (after river)
     */
    private Optional<SeatRef> getLastAggressor()
    {
        int boardCardEvents = 0;
        List<GameEvent> allEvents = game.getAllEvents();
        List<GameEvent> afterRiverEvents = Collections.emptyList();
        for (int i = 0; i < allEvents.size(); i++)
        {
            if (allEvents.get(i) instanceof BoardCardsEvent)
            {
                boardCardEvents++;
                // river
                if (boardCardEvents == 3)
                {
                    if (i >= allEvents.size() - 1)
                    {
                        return Optional.empty();
                    }
                    afterRiverEvents = allEvents.subList(i + 1, allEvents.size() - 1);
                    break;
                }
            }
        }
        SeatRef result = null;
        for (int i = 0; i < afterRiverEvents.size(); i++)
        {
            if (afterRiverEvents.get(i) instanceof BetEvent betEvent)
            {
                result = betEvent.getSeat();
            }
        }
        return Optional.ofNullable(result);
    }

    private List<List<SeatRef>> getPlacement(Map<SeatRef, Hand> handsOfPlayer)
    {
        List<List<Hand>> sortedHands = sortHands(handsOfPlayer.values());
        return sortSeats(handsOfPlayer, sortedHands);
    }

    /**
     * sorts seats by given order of hands
     */
    private List<List<SeatRef>> sortSeats(Map<SeatRef, Hand> handsOfPlayer, List<List<Hand>> sortedHands)
    {
        Map<Hand, List<SeatRef>> reverseHands = new HashMap<>();
        for (Entry<SeatRef, Hand> entry : handsOfPlayer.entrySet())
        {
            List<SeatRef> seats = reverseHands.computeIfAbsent(entry.getValue(), (hand) -> {
                return new ArrayList<>();
            });
            seats.add(entry.getKey());
        }
        List<List<SeatRef>> result = new ArrayList<>();
        for (List<Hand> hands : sortedHands)
        {
            List<SeatRef> seats = new ArrayList<>();
            hands.forEach(h -> seats.addAll(reverseHands.get(h)));
            result.add(seats);
        }
        return result;
    }

    /**
     * @param hands
     * @return sorted list of hands best hand comes first
     */
    private List<List<Hand>> sortHands(Collection<Hand> hands)
    {
        Map<Hand, Integer> handIndex = new HashMap<>();
        hands.forEach(h -> {
            // TODO holdem
            handIndex.put(h, TexasHoldem.getIndex(h.getCards()));
        });
        Map<Integer, List<Hand>> reverseHandIndex = new HashMap<>();
        for (Entry<Hand, Integer> entry : handIndex.entrySet())
        {
            List<Hand> handsOfIndex = reverseHandIndex.computeIfAbsent(entry.getValue(), (hand) -> {
                return new ArrayList<>();
            });
            handsOfIndex.add(entry.getKey());
        }
        List<Integer> sortedIndexes = handIndex.values().stream().distinct().sorted((a, b) -> b.compareTo(a)).toList();
        return sortedIndexes.stream().map(reverseHandIndex::get).toList();
    }

    @Override
    public void abort()
    {
        Pot pot = game.getPot();
        Map<SeatRef, Integer> inPlay = game.getCoinsInPlay();
        ShowdownEvent event = new ShowdownEvent();
        Map<SeatRef, Integer> returnCoinsToPlayer = new HashMap<>();
        returnCoinsToPlayer.putAll(inPlay);
        Map<String, Integer> coinPerPlayer = pot.getCoinsOfPlayer();
        coinPerPlayer.forEach((k, v) -> {
            SeatRef ref = SeatRef.globalRef(k);
            Integer old = returnCoinsToPlayer.computeIfAbsent(ref, (a) -> {
                return 0;
            });
            returnCoinsToPlayer.put(ref, old + v);
        });
        event.setWinner(returnCoinsToPlayer);
        game.publishEvent(event);
    }

    @Override
    public boolean isOneLeft()
    {
        List<SeatRef> seats = game.filterActiveSeats(game.getSeats());
        return seats.size() == 1;
    }

    @Override
    public boolean doWhatEverYouNeed()
    {
        if (!game.hasDealerAction())
        {
            // player needs to interact
            return false;
        }
        if (game.getEvents(StartEvent.class).findAny().isEmpty() || canTransfer())
        {
            // dealer on table needs to do something
            return true;
        }
        if (!game.getCoinsInPlay().isEmpty())
        {
            collectCoins();
        }
        int noBoardCards = game.getBoardCards().size();
        if (noBoardCards == 0)
        {
            if (game.getEvents(BlindEvent.class).findAny().isEmpty())
            {
                start();
            }
            else
            {
                flop();
            }
        }
        else if (noBoardCards == 3)
        {
            turn();
        }
        else if (noBoardCards == 4)
        {
            river();
        }
        else if (noBoardCards == 5)
        {
            showDown();
        }
        else if (noBoardCards > 5)
        {
            abort();
        }
        doWhatEverYouNeed();
        // not clear - who needs to react
        return true;
    }

    @Override
    public boolean canTransfer()
    {
        return game.getEvents(ShowdownEvent.class).findAny().isPresent();
    }

    @Override
    public TransferEvent getTransfer()
    {
        if (!canTransfer())
        {
            throw new IllegalStateException("Can't tranfer without showdown");
        }
        StartEvent startEvent = game.getEvents(StartEvent.class).findAny().get();
        Map<SeatRef, Integer> afterGame = new HashMap<>();
        Map<SeatRef, Integer> diffGame = new HashMap<>();
        startEvent.getSeats().forEach(s -> {
            Integer afterGameSeat = game.getCoinsOnSeat(s);
            afterGame.put(s, afterGameSeat);
            diffGame.put(s, afterGameSeat - startEvent.getCoinsOnSeats().get(s));
        });
        TransferEvent result = new TransferEvent();
        result.setCoinsOnSeatBeforeGame(startEvent.getCoinsOnSeats());
        result.setCoinsOnSeatAfterGame(afterGame);
        result.setCoinsOnSeatDiffOfGame(diffGame);
        return result;
    }
}
