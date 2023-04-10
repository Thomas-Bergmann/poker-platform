package de.hatoka.poker.bot.remote.client;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.remote.GameRO;
import de.hatoka.poker.remote.PlayerGameActionRO;
import de.hatoka.poker.remote.PlayerGameActionRO.Action;
import de.hatoka.poker.remote.SeatInfoRO;
import de.hatoka.poker.remote.SeatRO;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RemotePlayerImpl implements RemotePlayer
{
    private final BotServiceClient client;
    private final SeatRO seat;
    private final String seatRef;

    public RemotePlayerImpl(SeatRO seat, BotServiceClient client)
    {
        this.client = client;
        this.seat = seat;
        this.seatRef = seat.getRefGlobal();
    }

    @Override
    public PlayerGameActionRO check()
    {
        return PlayerGameActionRO.valueOf(Action.check);
    }

    @Override
    public PlayerGameActionRO call()
    {
        return PlayerGameActionRO.valueOf(Action.call);
    }

    @Override
    public PlayerGameActionRO betTo(int coins)
    {
        return PlayerGameActionRO.valueOf(Action.bet, coins);
    }

    public PlayerGameActionRO raiseTo(int coins)
    {
        return PlayerGameActionRO.valueOf(Action.raise, coins);
    }

    @Override
    public PlayerGameActionRO allIn()
    {
        return PlayerGameActionRO.valueOf(Action.allin);
    }

    @Override
    public PlayerGameActionRO fold()
    {
        return PlayerGameActionRO.valueOf(Action.fold);
    }

    @Override
    public void submit(PlayerGameActionRO action)
    {
        client.doAction(seat, action);
    }

    @Override
    public boolean hasAction()
    {
        return getCurrentSeat().filter(s -> s.getInfo().isHasAction()).isPresent();
    }

    private Optional<SeatRO> getCurrentSeat()
    {
        return getSeats().stream().filter(s -> seatRef.equals(s.getRefGlobal())).findAny();
    }

    private GameRO getGame()
    {
        return client.getGame(seat);
    }

    @Override
    public List<Card> getHoleCards()
    {
        return getCurrentSeat().map(SeatRO::getInfo)
                               .map(SeatInfoRO::getHoleCards)
                               .map(Card::deserialize)
                               .orElse(Collections.emptyList());
    }

    @Override
    public Integer getPotSize()
    {
        return getGame().getInfo().getPotSize();
    }

    @Override
    public List<SeatRO> getSeats()
    {
        return getGame().getInfo().getSeats();
    }
}
