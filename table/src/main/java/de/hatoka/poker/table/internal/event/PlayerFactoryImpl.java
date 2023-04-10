package de.hatoka.poker.table.internal.event;

import org.springframework.stereotype.Component;

import de.hatoka.poker.table.capi.business.GameBO;
import de.hatoka.poker.table.capi.business.SeatBO;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.game.PlayerFactory;

@Component
public class PlayerFactoryImpl implements PlayerFactory
{
    @Override
    public PlayerActions get(GameInfo gameInfo, SeatRef seat)
    {
        return new PlayerImpl(new PlayerGameInfo(gameInfo, seat));
    }

    @Override
    public PlayerActions get(SeatBO seat)
    {
        GameBO game = seat.getTable().getCurrentGame().get();
        return get(new GameInfo(game), seat.getRef());
    }

    @Override
    public PlayerGameInfo getInfo(SeatBO seat)
    {
        GameBO game = seat.getTable().getCurrentGame().get();
        return new PlayerGameInfo(new GameInfo(game), seat.getRef());
    }

    @Override
    public PlayerGameInfo getInfo(GameInfo gameInfo, SeatRef seat)
    {
        return new PlayerGameInfo(gameInfo, seat);
    }
}
