package de.hatoka.poker.bot.strategy;

import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.hatoka.poker.bot.remote.client.BotServiceClient;
import de.hatoka.poker.bot.remote.client.RemotePlayer;
import de.hatoka.poker.bot.remote.client.RemotePlayerFactory;
import de.hatoka.poker.remote.GameRO;
import de.hatoka.poker.remote.PlayerGameActionRO;
import de.hatoka.poker.remote.SeatRO;
import de.hatoka.poker.remote.TableRO;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PokerStrategyImpl implements PokerStrategy
{
    @Autowired
    private RemotePlayerFactory remotePlayerFactory;
    @Autowired
    private PokerStrategyFactory strategyFactory;

    private final BotServiceClient client;

    public PokerStrategyImpl(BotServiceClient client)
    {
        this.client = client;
    }

    @Override
    public void run()
    {
        List<TableRO> tables = client.getTables();
        Optional<TableRO> tableOpt = tables.stream().filter(t -> "NO_LIMIT".equals(t.getInfo().getLimit())).filter(t -> "TEXAS_HOLDEM".equals(t.getInfo().getVariant())).findAny();
        if (tableOpt.isPresent()) {
            runOnTable(tableOpt.get());
        }
    }

    private void runOnTable(TableRO table)
    {
        // check if bot is on a seat?
        // select seat
        Optional<SeatRO> seatOpt = client.getBotSeat(table);
        if (seatOpt.isPresent())
        {
            SeatRO seat = seatOpt.get();
            while (seat.getData().getCoinsOnSeat() > 0)
            {
                runOnSeat(seat);
            }
        }
        else
        {
            LoggerFactory.getLogger(getClass()).debug("bot not on seat.");
            client.joinTable(table);
            LoggerFactory.getLogger(getClass()).info("bot joined table.");
        }
    }

    private void runOnSeat(SeatRO seat)
    {
        GameRO game = client.getGame(seat);
        RemotePlayer remotePlayer = remotePlayerFactory.create(seat, client);
        if (remotePlayer.hasAction())
        {
            runOnAction(game, remotePlayer);
        }
        else
        {
            Optional<SeatRO> seatWithAction = game.getInfo().getSeats().stream().filter(s -> s.getInfo().isHasAction()).findAny();
            if (seatWithAction.isEmpty())
            {
                LoggerFactory.getLogger(getClass()).warn("bot has not the action. Bug - no one has action - or only one player at table.");
            }
            else
            {
                LoggerFactory.getLogger(getClass()).debug("player '{}' has the action.", seatWithAction.get().getInfo().getName());
                sleep();
            }
        }
    }

    private void sleep()
    {
        try
        {
            Thread.sleep(4_000);
        }
        catch(InterruptedException e)
        {
            LoggerFactory.getLogger(getClass()).warn("waiting for next move was interrupted.");
            Thread.currentThread().interrupt();
        }
    }

    private void runOnAction(GameRO game, RemotePlayer remotePlayer)
    {
        PlayerGameActionRO action;
        String boardCards = game.getInfo().getBoardCards();
        if (boardCards.isEmpty())
        {
            action = strategyFactory.createFirstRoundStategy(remotePlayer).calculateAction();
        }
        else
        {
            LoggerFactory.getLogger(getClass()).debug("Cards on board {}.", boardCards); 
            action = remotePlayer.call();
        }
        LoggerFactory.getLogger(getClass()).debug("Player did action {}.", action); 
        remotePlayer.submit(action);
    }
}
