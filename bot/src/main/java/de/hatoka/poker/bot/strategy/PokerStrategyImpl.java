package de.hatoka.poker.bot.strategy;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
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
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
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
        Optional<TableRO> tableOpt = tables.stream()
                                           .filter(t -> "NO_LIMIT".equals(t.getInfo().getLimit()))
                                           .filter(t -> "TEXAS_HOLDEM".equals(t.getInfo().getVariant()))
                                           .findAny();
        if (tableOpt.isPresent())
        {
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
            while(seat != null)
            {
                if (seat.getData().isSittingOut() || seat.getData().getCoinsOnSeat() == 0)
                {
                    if (seat.getData().getCoinsOnSeat() > 0)
                    {
                        client.sitIn(seat);
                    }
                    else
                    {
                        client.rebuy(seat, table);
                    }
                }
                try
                {
                    seat = runOnSeat(seat);
                }
                catch (Exception e) {
                    GameRO game = client.getGame(seat);
                    final SeatRO oldSeatInfo = seat;
                    seat = game.getInfo()
                                    .getSeats()
                                    .stream()
                                    .filter(s -> oldSeatInfo.getRefGlobal().equals(s.getRefGlobal()))
                                    .findAny().orElse(null);
                }
            }
        }
        else
        {
            LOGGER.debug("bot not on seat.");
            client.joinTable(table);
            LOGGER.info("bot joined table.");
        }
    }

    private SeatRO runOnSeat(SeatRO seatOnTable)
    {
        GameRO game = client.getGame(seatOnTable);
        Optional<SeatRO> seatOnGame = game.getInfo()
                        .getSeats()
                        .stream()
                        .filter(s -> seatOnTable.getRefGlobal().equals(s.getRefGlobal()))
                        .findAny();
        if (seatOnGame.isEmpty())
        {
            LOGGER.warn("Can't find seat on game '{}'.", seatOnTable.getRefGlobal());
            return null;
        }
        SeatRO seat = seatOnGame.get();
        if (seat.getGame().isHasAction())
        {
            runOnAction(seat);
        }
        else
        {
            Optional<SeatRO> seatWithAction = game.getInfo()
                                                  .getSeats()
                                                  .stream()
                                                  .filter(s -> s.getGame().isHasAction())
                                                  .findAny();
            if (!seatWithAction.isEmpty())
            {
                LOGGER.debug("player '{}' has the action.", seatWithAction.get().getInfo().getName());
                sleep();
            }
        }
        return seatOnGame.get();
    }

    private void sleep()
    {
        try
        {
            Thread.sleep(4_000);
        }
        catch(InterruptedException e)
        {
            LOGGER.warn("waiting for next move was interrupted.");
            Thread.currentThread().interrupt();
        }
    }

    private void runOnAction(SeatRO seat)
    {
        GameRO game = client.getGame(seat);
        RemotePlayer remotePlayer = remotePlayerFactory.create(seat, client);
        PlayerGameActionRO action = strategyFactory.createDecisionMaker(remotePlayer).calculateAction(game);
        LOGGER.debug("Player did action {} on game {}.", action, game.getRefLocal());
        remotePlayer.submit(action);
    }
}
