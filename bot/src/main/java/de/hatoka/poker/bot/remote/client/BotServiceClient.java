package de.hatoka.poker.bot.remote.client;

import java.util.List;
import java.util.Optional;

import de.hatoka.poker.remote.GameRO;
import de.hatoka.poker.remote.PlayerGameActionRO;
import de.hatoka.poker.remote.SeatRO;
import de.hatoka.poker.remote.TableRO;

/**
 * PokerServiceClient has the context of the bot 
 */
public interface BotServiceClient
{
    /**
     * @return all existing tables
     */
    List<TableRO> getTables();

    /**
     * @param table table information
     * @return all seats of a table  (without game information)
     */
    List<SeatRO> getSeats(TableRO table);

    /**
     * Bot joins a table
     * @param table table information
     */
    void joinTable(TableRO table);
    
    /**
     * Bot sits down again
     * @param seat
     */
    void sitIn(SeatRO seat);

    /**
     * Bot sits down again
     * @param seat
     * @param table
     */
    void rebuy(SeatRO seat, TableRO table);

    /**
     * @param seat seat of table
     * @return game information from seat perspective
     */
    GameRO getGame(SeatRO seat);

    /**
     * @param seats seats of table
     * @return seat if one of the seats is assigned to bot
     */
    Optional<SeatRO> getBotSeat(List<SeatRO> seats);
    
    /**
     * @param table
     * @return bot seat of table if bot is placed (without game information)
     */
    default Optional<SeatRO> getBotSeat(TableRO table)
    {
        return getBotSeat(getSeats(table));
    }

    /**
     * Bot does a play action
     * @param seat
     * @param action
     */
    void doAction(SeatRO seat, PlayerGameActionRO action);
}
