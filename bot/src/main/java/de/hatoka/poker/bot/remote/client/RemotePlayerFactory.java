package de.hatoka.poker.bot.remote.client;

import de.hatoka.poker.remote.SeatRO;

public interface RemotePlayerFactory
{
    /**
     * @param table
     * @param client for bot
     * @return possible actions for player on table
     */
    RemotePlayer create(SeatRO seat, PokerServiceClient client);
}
