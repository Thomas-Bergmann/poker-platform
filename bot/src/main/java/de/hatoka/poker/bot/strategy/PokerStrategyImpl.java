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
        LoggerFactory.getLogger(getClass()).info("got tables {}.", tables.size());
        Optional<TableRO> tableOpt = tables.stream().filter(t -> "NO_LIMIT".equals(t.getInfo().getLimit())).filter(t -> "TEXAS_HOLDEM".equals(t.getInfo().getVariant())).findAny();
        if (tableOpt.isPresent()) {
            TableRO table = tableOpt.get();
            List<SeatRO> seats = client.getSeats(table);
            LoggerFactory.getLogger(getClass()).info("got seats {}.", seats.size());
            // check if bot is on a seat?
            // select seat
            Optional<SeatRO> seatOpt = client.getBotSeat(table);
            if (seatOpt.isPresent())
            {
                GameRO game = client.getGame(seatOpt.get());
                LoggerFactory.getLogger(getClass()).debug("bot is on game {}.", game.getRefGlobal());
                RemotePlayer remotePlayer = remotePlayerFactory.create(seatOpt.get(), client);
                if (remotePlayer.hasAction())
                {
                    LoggerFactory.getLogger(getClass()).debug("bot has action.");
                    if (game.getInfo().getBoardCards().isEmpty())
                    {
                        strategyFactory.createFirstRoundStategy(remotePlayer).run();
                    }
                    else
                    {
                        remotePlayer.call();
                    }
                }
                else
                {
                    Optional<SeatRO> seatWithAction = game.getInfo().getSeats().stream().filter(s -> s.getInfo().isHasAction()).findAny();
                    if (seatWithAction.isEmpty())
                    {
                        LoggerFactory.getLogger(getClass()).debug("bot has not the action. Bug - no one has action - or only one player at table.");
                    }
                    else
                    {
                        LoggerFactory.getLogger(getClass()).debug("player {} has the action.", seatWithAction.get().getInfo().getName());
                    }
                }
            }
            else
            {
                LoggerFactory.getLogger(getClass()).warn("bot not on seat.");
                client.joinTable(table);
                LoggerFactory.getLogger(getClass()).warn("bot joined table.");
            }
        }
    }
}
