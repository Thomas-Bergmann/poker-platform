package de.hatoka.poker.table.internal.event;

import de.hatoka.poker.table.PlayerActions;
import de.hatoka.poker.table.capi.event.history.seat.BetEvent;
import de.hatoka.poker.table.capi.event.history.seat.CallEvent;
import de.hatoka.poker.table.capi.event.history.seat.CheckEvent;
import de.hatoka.poker.table.capi.event.history.seat.FoldEvent;
import de.hatoka.poker.table.capi.event.history.seat.RaiseEvent;

public class PlayerImpl implements PlayerActions
{
    private PlayerGameInfo game;

    public PlayerImpl(PlayerGameInfo game)
    {
        this.game = game;
    }

    @Override
    public void check()
    {
        CheckEvent event = new CheckEvent();
        game.publisEvent(event);
    }

    @Override
    public void call()
    {
        CallEvent event = new CallEvent();
        int coinsToCall = game.getMaxCoinsInPlay() - game.getCoinsInPlayOfSeat();
        int coinsOnSeat = game.getCoinsOnSeat();
        if (coinsOnSeat <= coinsToCall)
        {
            coinsToCall = coinsOnSeat;
            event.setIsAllIn(true);
        }
        event.setCoins(coinsToCall);
        game.publisEvent(event);
    }

    @Override
    public void betTo(int coins)
    {
        BetEvent event = new BetEvent();
        event.setCoins(coins - game.getCoinsInPlayOfSeat());
        game.publisEvent(event);
    }

    @Override
    public void raiseTo(int coins)
    {
        RaiseEvent event = new RaiseEvent();
        event.setCoins(coins - game.getCoinsInPlayOfSeat());
        game.publisEvent(event);
    }

    @Override
    public void allIn()
    {
        RaiseEvent event = new RaiseEvent();
        event.setCoins(game.getCoinsOnSeat());
        event.setIsAllIn(true);
        game.publisEvent(event);
    }

    @Override
    public void fold()
    {
        FoldEvent event = new FoldEvent();
        game.publisEvent(event);
    }
}
