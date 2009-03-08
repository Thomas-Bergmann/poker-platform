package de.hatoka.poker.bot.remote.client;

import java.util.List;
import java.util.Optional;

import de.hatoka.poker.remote.GameRO;
import de.hatoka.poker.remote.PlayerGameActionRO;
import de.hatoka.poker.remote.SeatRO;
import de.hatoka.poker.remote.TableRO;

public interface PokerServiceClient
{
    List<TableRO> getTables();

    List<SeatRO> getSeats(TableRO table);

    GameRO getGame(SeatRO seatRO);

    /**
     * @param seats seats of table
     * @return seat if one of the seats is assigned to bot
     */
    Optional<SeatRO> getBotSeat(List<SeatRO> seats);
    
    /**
     * @param table
     * @return bot seat of table if bot is placed
     */
    default Optional<SeatRO> getBotSeat(TableRO table)
    {
        return getBotSeat(getSeats(table));
    }

    void doAction(SeatRO seat, PlayerGameActionRO action);
}
